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
import kr.co.eventroad.admin.service.KindergardenService;
import kr.co.eventroad.admin.service.TeacherService;
import kr.co.eventroad.admin.service.MainService;
import kr.co.eventroad.admin.service.AlbumService;
import kr.co.eventroad.admin.service.UserService;
import scala.annotation.meta.param;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedSingleColumnRowMapper;
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
 *  딜러 컨트롤러
 * @author jwdoe
 * @since 2016. 12. 14.
 * @version 1.0.0
 */
@Controller
public class TeacherController extends RootController {

	/**
	 * 딜러 서비스(자동 할당)
	 */
	@Autowired
	public TeacherService teacherService;

	@Autowired
	public UserService userService;
	
	@Autowired
	public AlbumService storeService;
	
	@Autowired
	public KidsService agencyService;
	
	@Autowired
	public MainService mainService;
	
	String url = "web/dealer/";
	
	/**
	 * 선생님 전체
	 * @param model
	 * @param params
	 * @param req
	 * @return 선생님 유치원 신청&등록 목록
	 */
	@RequestMapping(value = "/selectMyKindergardenList.do")
	public @ResponseBody Map<?, ?> selectMyKindergardenList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req selectMyKindergardenList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			if(!paramMap.containsKey("seq_user")){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1) {
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
//			retMap.put("page", paramMap.get("page") );
			
			/*if( paramMap.containsKey("keyword") ){
				retMap.put("keyword", paramMap.get("keyword").toString());
				paramMap.put("keyword", "%" + paramMap.get("keyword").toString().trim() + "%");
			}*/
			
			// 원장도 이용가능 하게
			/*if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user", user.getUsername() );
				
				temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") );
			}*/
			
			retMap.put("my_kindergarden_list", teacherService.selectMyKindergardenList(paramMap));
//			retMap.put("page_count", agencyService.selectBeaconConditionsCount(paramMap));
			
			
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 딜러 전체  비콘현황
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
				paramMap.put("seq_user", user.getUsername() );
				
				temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") );
			}
			
			retMap.put("beacon_list", agencyService.selectBeaconConditionsList(paramMap));
			retMap.put("page_count", agencyService.selectBeaconConditionsCount(paramMap));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileDealer(req, temp_auth) + "beaconConditions";
	}*/
	
	/**
	 * 딜러 비콘현황
	 * @param model
	 * @param params
	 * @param req
	 * @return 메인
	 */
	@RequestMapping(value = "/storeBeaconConditions.do")
	public String storeBeaconConditions(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req storeBeaconConditions.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		boolean temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") );
		
		try {
			if(paramMap.get("page") == null || Integer.parseInt( paramMap.get("page").toString()) < 1){
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			retMap.put("page", paramMap.get("page") );
			
			if(!paramMap.containsKey("seq_user")){
				if ( authentication != null ) {
					User user = (User) authentication.getPrincipal();
					paramMap.put("seq_user", user.getUsername() );
				}
			}
			
			if(paramMap.containsKey("seq_store")){
				retMap.put("seq_store", paramMap.get("seq_store").toString() );
			}
			
			retMap.put("dealer_beacon_req_list", teacherService.selectDealerBeaconReqList(paramMap));
			retMap.put("page_count", teacherService.selectDealerBeaconReqCount(paramMap));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileDealer(req, temp_auth) + "storeBeaconConditions";
	}
	
	/**
	 * 비콘 리스트 (매장등록 후 비콘 등록할 때 검색 기능 포함)
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectBeaconList.do")
	public @ResponseBody Map<?, ?> selectBeaconList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req selectBeaconList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(!paramMap.containsKey("seq_user")){
			if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user", user.getUsername() );
			}
		}
		
		try {
			retMap.put("beacon_list", teacherService.selectBeaconList(paramMap) );
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 딜러 > 매장현황 > 비콘 등록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/insertBeaconReq.do")
	public @ResponseBody Map<?, ?> insertBeaconReq(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req insertBeaconReq.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user", user.getUsername() );
			}
			
			teacherService.insertBeaconReq(paramMap);
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 비콘 업데이트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/updateBeaconReq.do")
	public @ResponseBody Map<?, ?> updateBeaconReq(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req updateBeaconReq.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			teacherService.updateBeaconReq(paramMap);
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 비콘요청 정보 상세
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectBeaconReq.do")
	public @ResponseBody Map<?, ?> selectBeaconReq(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectBeaconReq.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			retMap.put("beacon_req", teacherService.selectBeaconReq(paramMap) );
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 매장 현황
	 * @param model
	 * @param paramMap
	 * @param req
	 * @return 매장 현황
	 */
	@RequestMapping(value = "/storeConditions.do")
	public String storeConditions(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req storeConditions.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		boolean temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") );
		
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
			
			// seq_user 로 올경우에 처리 필요 ( ex - 본사, 총판  에서 직접 검색할 경우    상하관계 어떻게 할건지?
			// 매장명 등을 검색을 하려고 접근할 때    본사 : 총판, 딜러, 매장  다 join,   총판 : 딜러, 매장만 조인 , 딜러 : 딜러 매장 조인
			if( temp_auth ){
				if ( authentication != null ) {
					User user = (User) authentication.getPrincipal();
					paramMap.put("seq_user_prt", user.getUsername() );
				}
			}
			
			retMap.put("store_list", teacherService.selectStoreList(paramMap));
			retMap.put("page_count", teacherService.selectStoreCount(paramMap));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileDealer(req, temp_auth) + "storeConditions";
	}
	
	/**
	 * 매장 기본 정보 등록 페이지
	 * @param model
	 * @param params
	 * @param req
	 * @return 매장 등록 페이지
	 */
	@RequestMapping(value = "/storeBaseReg.do")
	public String storeReg(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req storeReg.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		boolean temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") );
		
		try {
			if( !paramMap.isEmpty() ){
				retMap.put("store", teacherService.selectStoreOne(paramMap));
				retMap.put("user", userService.selectUserEx(paramMap));
			}
			paramMap.put("comn_cd", "category_cd");
			retMap.put("category_cd_list", mainService.selectCategoryList(paramMap));
			
			paramMap.put("comn_cd", "bsins_day_cd");
			retMap.put("bsins_day_cd_list", mainService.selectCategoryList(paramMap));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileDealer(req, temp_auth) + "storeBaseReg";
	}
	
	/**
	 * 딜러 gps현황
	 * @param model
	 * @param params
	 * @param req
	 * @return 딜러 gps현황
	 */
	/*@RequestMapping(value = "/gpsConditions.do")
	public String gpsConditions(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req gpsConditions.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		boolean temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") );
		
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
			
			if( authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") ) ){
				if ( authentication != null ) {
					User user = (User) authentication.getPrincipal();
					paramMap.put("seq_user", user.getUsername() );
				}
			}
			
			retMap.put("dealer_gps_req_list", teacherService.selectDealerGpsReqList(paramMap));
			retMap.put("page_count", teacherService.selectDealerGpsReqCount(paramMap));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
//		retMap.put("user_ex", userService.selectUserEx(paramMap) );
//		retMap.put("beacon_list", dealerService.selectAgencyBeaconList(paramMap));
		
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileDealer(req, temp_auth) + "gpsConditions";
	}*/
	
	/**
	 * 매장 등록 페이지
	 * @param model
	 * @param params
	 * @param req
	 * @return 매장 등록 페이지
	 */
	@RequestMapping(value = "/beaconReg.do")
	public String beaconReg(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req beaconReg.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		boolean temp_auth = authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") );
		
		if(!paramMap.containsKey("seq_user")){
			if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user", user.getUsername() );
			}
		}
		System.out.println(paramMap.toString());
		try {
			if( !paramMap.isEmpty() ){
//				retMap.put("store", dealerService.selectStoreOne(paramMap));
//				retMap.put("user", userService.selectUserEx(paramMap));
				retMap.put("beacon_req", teacherService.selectBeaconReq(paramMap));
				System.out.println(retMap.toString());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileDealer(req, temp_auth) + "beaconReg";
	}
	
	/**
	 * 딜러 > 매장현황 > 비콘현황 > gps 등록/수정
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/insertGpsReq.do")
	public @ResponseBody Map<?, ?> insertGpsReq(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req insertGpsReq.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap);
		try {
			if(paramMap.get("cont_type").toString().equals("NW")){
				teacherService.insertGpsReq(paramMap);
				if( authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_ADMIN") ) ){
					retMap.put("authority", "admin");
				} else if( authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") ) ) {
					retMap.put("authority", "dealer");
				}
			} else {
				if( authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_ADMIN") ) ){
					paramMap.put("proc_stat", "RA");
					
					retMap.put("authority", "admin");
				} else if( authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") ) ) {
					paramMap.put("proc_stat", "ED");
					
					paramMap.put("gps_addr_ed", paramMap.get("gps_addr").toString());
					paramMap.put("gps_ar1_ed", paramMap.get("gps_ar1").toString());
					paramMap.put("gps_ar2_ed", paramMap.get("gps_ar2").toString());
					paramMap.put("gps_ar3_ed", paramMap.get("gps_ar3").toString());
					paramMap.put("lat_ed", paramMap.get("lat").toString());
					paramMap.put("lng_ed", paramMap.get("lng").toString());
					
					paramMap.remove("gps_addr");
					paramMap.remove("gps_ar1");
					paramMap.remove("gps_ar2");
					paramMap.remove("gps_ar3");
					paramMap.remove("lat");
					paramMap.remove("lng");
					
					retMap.put("authority", "dealer");
				}
				teacherService.updateGpsReq(paramMap);
			}
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * GPS 업데이트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/updateGpsReq.do")
	public @ResponseBody Map<?, ?> updateGpsReq(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req updateGpsReq.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user_prt", user.getUsername() );
			}
			
			teacherService.updateGpsReq(paramMap);
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
}