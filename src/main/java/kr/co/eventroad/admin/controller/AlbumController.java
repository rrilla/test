package kr.co.eventroad.admin.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.co.eventroad.admin.common.CommonController;
import kr.co.eventroad.admin.common.Constant;
import kr.co.eventroad.admin.common.FileUploadForm;
import kr.co.eventroad.admin.common.ResultCode;
import kr.co.eventroad.admin.common.RootController;
import kr.co.eventroad.admin.common.ServicesUtils;
import kr.co.eventroad.admin.service.KidsService;
import kr.co.eventroad.admin.service.TeacherService;
import kr.co.eventroad.admin.service.MainService;
import kr.co.eventroad.admin.service.NoticeService;
import kr.co.eventroad.admin.service.AlbumService;
import kr.co.eventroad.admin.service.UserService;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.document.AbstractExcelView;

/**
 *  총판 컨트롤러
 * @author jwdoe
 * @since 2016. 12. 14.
 * @version 1.0.0
 */
@Controller
//@RequestMapping("/store/*")
public class AlbumController extends RootController {

	/**
	 * 매거진 서비스(자동 할당)
	 */
	@Autowired
	public AlbumService albumService;

	@Autowired
	public UserService userService;
	
	@Autowired
	public TeacherService dealerService;

	@Autowired
	public MainService mainService;
	
	@Autowired
	public KidsService agencyService;
	
	@Autowired
	public NoticeService noticeService;
	
	String url = "web/store/";
	
	/**
	 * 앨범 등록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/insertAlbum.do")
	public @ResponseBody Map<?, ?> insertAlbum(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.error("req insertAlbum.do :" + paramMap);
		log.error("req insertAlbum.do :" + paramMap.toString());
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_user") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("is_reply") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			albumService.insertAlbum(paramMap);
			
			// 이미지 등록
			List<MultipartFile> files = uploadForm.getFiles();
			int cnt = 0;
			if(null != files && files.size() > 0) {
				for (MultipartFile multipartFile : files) {
					if(multipartFile == null || multipartFile.getSize() == 0) {
 	            		cnt++;
	            		continue;
	            	}
 	            	
	 	            String fileName = multipartFile.getOriginalFilename();
//	 	            log.error("req insertAlbum.do fileName " + cnt + " : " + fileName);
	        		String strSplit[] = multipartFile.getOriginalFilename().split("[.]");
	        		
//	        		System.out.println(fileName);
//	        		System.out.println(strSplit[strSplit.length-1]);
	        		
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "file_album/" + paramMap.get("seq_album").toString() + "/" + fileName, multipartFile);
	        		
	        		paramMap.put("album_image_"+(cnt+1), Constant.IMAGE_PROFILE_IMAGE_RETURN + "file_album/" + paramMap.get("seq_album").toString() + "/" + fileName);
	        		cnt++;
	            }
	        }
			
			// 회원정보 update
			albumService.updateAlbum(paramMap);
			
			
			// news insert
			paramMap.put("msg_type", "앨범 등록");
			paramMap.put("seq", paramMap.get("seq_album").toString());
			paramMap.put("subtitle", "앨범이 등록되었습니다.");
			paramMap.put("title", paramMap.get("title").toString());
			paramMap.put("seq_user", paramMap.get("seq_user").toString());
			paramMap.put("seq_kids", null);
			paramMap.put("year", null);
			paramMap.put("month", null);
			paramMap.put("day", null);
						
			noticeService.insertNews(paramMap);
			
			// push token list
			List<Object> temp_token_list = (List<Object>) noticeService.selectPushKindergardenClassList(paramMap);
//			System.out.println(temp_token_list);
			
			// push
			if(temp_token_list.size() > 0) {
				// param pushParamMap
				Map<String, Object> pushDataMap = new HashMap<String, Object>();
				
				pushDataMap.put("msg_type", "앨범 등록");
				pushDataMap.put("seq", paramMap.get("seq_album").toString());
				pushDataMap.put("seq_kindergarden", paramMap.get("seq_kindergarden").toString());
				pushDataMap.put("seq_kindergarden_class", paramMap.get("seq_kindergarden_class").toString());
				pushDataMap.put("seq_kids", null);
				pushDataMap.put("age", null);
				pushDataMap.put("subtitle", "앨범이 등록되었습니다.");
				pushDataMap.put("title", paramMap.get("title").toString());
				pushDataMap.put("year", null);
				pushDataMap.put("month", null);
				pushDataMap.put("day", null);
				
				send_push(temp_token_list, pushDataMap);
			}
			
			model.clear();
//			retMap.putAll(paramMap);
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			model.clear();
			procException(model, e);
		}
		
		model.addAttribute("retMap", retMap);
		return model;
	}
	
	/**
	 * 앨범 수정
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateAlbum.do")
	public @ResponseBody Map<?, ?> updateAlbum(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req updateAlbum.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_album") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			// 이미지 등록
			List<MultipartFile> files = uploadForm.getFiles();
			int cnt = 0;
			if(null != files && files.size() > 0) {
				for (MultipartFile multipartFile : files) {
					if(multipartFile == null || multipartFile.getSize() == 0) {
 	            		cnt++;
	            		continue;
	            	}
 	            	
	 	            String fileName = multipartFile.getOriginalFilename();
	        		String strSplit[] = multipartFile.getOriginalFilename().split("[.]");
	        		
//	        		System.out.println(fileName);
//	        		System.out.println(strSplit[strSplit.length-1]);
	        		
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "file_album/" + paramMap.get("seq_album").toString() + "/" + fileName, multipartFile);
	        		
	        		paramMap.put("album_image_"+(cnt+1), Constant.IMAGE_PROFILE_IMAGE_RETURN + "file_album/" + paramMap.get("seq_album").toString() + "/" + fileName);
	        		cnt++;
	            }
				
				// 이미지가 10개가 안됬을 경우
				// 0~8까지 유효하니까 9개
				if(cnt<9) {
					for(int i=(cnt+1);i<=9;i++) {
						paramMap.put("album_image_"+(i+1), "");
					}
				}
	        }
			
			// 앨범 update
			albumService.updateAlbum(paramMap);
			
			model.clear();
//			retMap.putAll(paramMap);
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			model.clear();
			procException(model, e);
		}
		
		model.addAttribute("retMap", retMap);
		return model;
	}
	
	/**
	 * 앨범 삭제
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/deleteAlbum.do")
	public @ResponseBody Map<?, ?> deleteAlbum(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req deleteAlbum.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_album") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			albumService.deleteAlbum(paramMap);
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 공지사항 리스트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectAlbumList.do")
	public @ResponseBody Map<?, ?> selectAlbumList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectAlbumList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kindergarden") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1) {
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			
			retMap.put("album_list", albumService.selectAlbumList(paramMap));
//			System.out.println( retMap.toString() );
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 공지사항 상세
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectAlbumOne.do")
	public @ResponseBody Map<?, ?> selectAlbumOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectAlbumOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_album") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("album", albumService.selectAlbumOne(paramMap));
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Store 상세
	 * @param model
	 * @param paramMap
	 * @param req
	 * @return Store 상세
	 */
	@RequestMapping(value = "/storeDetail.do")
	public String storeDetail(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req storeDetail.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		boolean temp_auth = false;
		if ( authentication != null ) {
			User user = (User) authentication.getPrincipal();
			paramMap.put("seq_user", user.getUsername() );
			
			temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_STORE") );
		}
		
		retMap.put("store", albumService.selectStoreInfo(paramMap) );
		retMap.put("dealer", albumService.selectDealerInfo(paramMap));
		
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileStore(req, temp_auth) + "storeDetail";
	}
	
	
	/**
	 * 매장 등록 페이지
	 * @param model
	 * @param params
	 * @param req
	 * @return 매장 등록 페이지
	 */
	@RequestMapping(value = "/storeReg.do")
	public String storeReg(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req storeReg.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		boolean temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_STORE") ) || authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") );
		
		try {
			if( !paramMap.isEmpty() ){
				retMap.put("store", dealerService.selectStoreOne(paramMap));
				paramMap.put("seq_store", ((Map)retMap.get("store")).get("seq_store").toString() );
				retMap.put("menu", albumService.selectMenuList(paramMap));
				System.out.println(retMap.toString());
				retMap.put("user", userService.selectUserEx(paramMap));
			}
			paramMap.put("comn_cd", "category_cd");
			retMap.put("category_cd_list", mainService.selectCategoryList(paramMap));
			
			paramMap.put("comn_cd", "bsins_day_cd");
			retMap.put("bsins_day_cd_list", mainService.selectCategoryList(paramMap));
			
//			System.out.println( retMap.toString() );
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileStore(req, temp_auth) + "storeReg";
	}
	
	/**
	 * 매장 전체  비콘현황
	 * @param model
	 * @param params
	 * @param req
	 * @return 딜러 비콘현황
	 */
	/*@RequestMapping(value = "/beaconConditions.do")
	public String beaconConditions(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req beaconConditions.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		boolean temp_auth = false;
		try {
			if(paramMap.get("page") == null || Integer.parseInt( paramMap.get("page").toString()) < 1){
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			retMap.put("page", paramMap.get("page") );
			
			if( paramMap.containsKey("keyword") ){
				retMap.put("keyword", paramMap.get("keyword").toString());
				paramMap.put("keyword", "%" + paramMap.get("keyword").toString().trim() + "%");
			}
			
			if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user_store", user.getUsername() );
				
				temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_STORE") );
			}
			
			retMap.put("beacon_list", agencyService.selectBeaconConditionsList(paramMap));
			retMap.put("page_count", agencyService.selectBeaconConditionsCount(paramMap));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileStore(req, temp_auth) + "beaconConditions";
	}*/
	
	/**
	 * 매장 gps현황
	 * @param model
	 * @param params
	 * @param req
	 * @return 딜러 gps현황
	 */
	@RequestMapping(value = "/gpsConditions.do")
	public String gpsConditions(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req gpsConditions.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		boolean temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_STORE") );
		
		try {
			if(paramMap.get("page") == null || Integer.parseInt( paramMap.get("page").toString()) < 1){
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			retMap.put("page", paramMap.get("page") );
			
			if( paramMap.containsKey("keyword") ){
				retMap.put("keyword", paramMap.get("keyword").toString());
				paramMap.put("keyword", "%" + paramMap.get("keyword").toString().trim() + "%");
			}
			
			if( paramMap.containsKey("seq_beacon_req") ){
				retMap.put("seq_beacon_req", paramMap.get("seq_beacon_req").toString() );
			}
			
			if( authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_STORE") ) ){
				if ( authentication != null ) {
					User user = (User) authentication.getPrincipal();
					paramMap.put("seq_user_store", user.getUsername() );
				}
			}
			
			retMap.put("dealer_gps_req_list", dealerService.selectDealerGpsReqList(paramMap));
			retMap.put("page_count", dealerService.selectDealerGpsReqCount(paramMap));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
//		retMap.put("user_ex", userService.selectUserEx(paramMap) );
//		retMap.put("beacon_list", dealerService.selectAgencyBeaconList(paramMap));
		
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileStore(req, temp_auth) + "gpsConditions";
	}
	
	/**
	 * gps 등록 페이지
	 * @param model
	 * @param params
	 * @param req
	 * @return gps 등록 페이지
	 */
	@RequestMapping(value = "/gpsReg.do")
	public String gpsReg(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req gpsReg.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		boolean temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_STORE") ) || authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") );
		
		try {
//			if( !paramMap.isEmpty() ){
			if( paramMap.containsKey("seq_gps_req") ){
//				retMap.put("store", dealerService.selectStoreOne(paramMap));
//				retMap.put("user", userService.selectUserEx(paramMap));
				retMap.put("gps", dealerService.selectGpsReq(paramMap));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileStore(req, temp_auth) + "gpsReg";
	}
}