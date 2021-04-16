package kr.co.eventroad.admin.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.co.eventroad.admin.common.CommonController;
import kr.co.eventroad.admin.common.CommonService;
import kr.co.eventroad.admin.common.Constant;
import kr.co.eventroad.admin.common.FileUploadForm;
import kr.co.eventroad.admin.common.ResultCode;
import kr.co.eventroad.admin.common.RootController;
import kr.co.eventroad.admin.common.ServicesUtils;
import kr.co.eventroad.admin.service.AreaService;
import kr.co.eventroad.admin.service.KidsService;
import kr.co.eventroad.admin.service.MainService;
import kr.co.eventroad.admin.service.NoticeService;
import kr.co.eventroad.admin.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * 메인 컨트롤러
 * @author jwdoe
 * @since 2018. 04. 03.
 * @version 1.0.0
 */
@Controller
@SessionAttributes({"id"})
public class MainController extends RootController {

	@Autowired
	public MainService mainService;
	
	@Autowired
	public UserService userService;
	
	@Autowired
	public KidsService jobService;
	
	@Autowired
	public CommonService commonService;
	
	@Autowired
	public AreaService areaService;
	
	
	/**
	 * 버전
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectVersion.do")
	public @ResponseBody Map<?, ?> selectVersion(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("selectVersion.do :" + paramMap);
		System.out.println(paramMap.toString());
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> userMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("device") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("version", mainService.selectVersion(paramMap));
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}

	/**
	 * 친구 관리 목록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/main.do")
	public @ResponseBody Map<?, ?> main(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("main.do :" + paramMap);
		System.out.println(paramMap.toString());
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> userMap = new HashMap<String, Object>();
		
		try {
			if(!paramMap.containsKey("authority") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("year") || !paramMap.containsKey("month") || !paramMap.containsKey("day")) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			if(paramMap.get("authority").toString().equals("ROLE_PARENTS")) {
				// 학부모
				if(!paramMap.containsKey("seq_kids")) {
					throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
				}

				/*
				 * authority=ROLE_PARENTS&seq_kindergarden=3&year=2018&month=01&day=23&seq_kids=5
				 * 금일 자녀의 투약의뢰서 작성 여부
				 * 금일 자녀의 귀가동의서 작성 여부
				*/
				
				retMap.put("main", mainService.selectParentMain(paramMap));
				
			} else if(paramMap.get("authority").toString().equals("ROLE_TEACHER")) {
				// 교사
				if(!paramMap.containsKey("seq_kindergarden_class")) {
					throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
				}
				
				/*
				 * authority=ROLE_TEACHER&seq_kindergarden=3&seq_kindergarden_class=2&year=2018&month=01&day=23&seq_kids=5
				 * 금일 담당 반의 처리되지 않은 투약의뢰서
				 * 금일 담당 반의 처리되지 않은 귀가동의서
				 * 금일 담당 반의 rep_flag가 "y", "w"인 인원의 합
				 * 금일 담당 반의 전체 인원
				*/
				
				retMap.put("main", mainService.selectTeacherMain(paramMap));
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
	 * 약속 목록
	 * @param paramMap
	 * @param model
	 * @param req (약속 seq)
	 * @return rep
	 */
	@RequestMapping(value = "/getVirtualNumber.do")
	public @ResponseBody Map<?, ?> selectVirtualNumber(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectVirtualNumber.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if(!paramMap.containsKey("ActionType") || !paramMap.containsKey("CallPhone") || !paramMap.containsKey("ReceivePhone") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			// start 날짜
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");
			
			Calendar start_cal = Calendar.getInstance();   
			
			start_cal.add(Calendar.HOUR, 1);
			
			String year = start_cal.get(Calendar.YEAR) + "";
			String month = (start_cal.get(Calendar.MONTH)+1)<10?("0" + (start_cal.get(Calendar.MONTH)+1)):(start_cal.get(Calendar.MONTH)+1) + "";
			String day = start_cal.get(Calendar.DATE)<10?("0" + start_cal.get(Calendar.DATE)):start_cal.get(Calendar.DATE) + "";
			String hour = start_cal.get(Calendar.HOUR)<10?("0" + start_cal.get(Calendar.HOUR)):start_cal.get(Calendar.HOUR) + "";
			
			String PhoneEND = year + month + day + hour;
			
			
			String AUTH_KEY_FCM = "";
			String API_URL_FCM = "https://www.korea-ssl.com/twoway/?COMPANYID=compay01&ActionType="+ paramMap.get("ActionType").toString() +"&CallPhone=" + paramMap.get("CallPhone").toString() +"&ReceivePhone=" + paramMap.get("ReceivePhone").toString() + "&PhoneEND=" + PhoneEND;
			
			URL url = new URL(API_URL_FCM);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setDoInput(true); // ++
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/json");
//	        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
	        conn.setUseCaches(false); // ++
	        conn.setDoOutput(true);
	        
	        JSONObject json = new JSONObject();
	        
	        /*json.put("COMPANYID", "compay01");
	        json.put("ActionType", "1");
	        json.put("CallPhone", paramMap.get("CallPhone").toString());
	        json.put("ReceivePhone", paramMap.get("ReceivePhone").toString());
	        json.put("PhoneEND", PhoneEND);*/
	        
	        try(OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")){ //혹시나 한글 깨짐이 발생하면
	            wr.write(json.toString());
	            wr.flush();
	        }catch(Exception e){
	        }
	        
	        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
	        
	        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	        
	        String jsonText = readAll(br);
	        
	        JSONParser jsonParser = new JSONParser();
	        JSONObject tempjson = (JSONObject) jsonParser.parse(jsonText);
	        
	        retMap.put("test", tempjson);
	        retMap.put("Result", tempjson.get("Result").toString());
        	retMap.put("VirtualNumber", tempjson.get("VirtualNumber").toString());
	        
            /*String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
            	JSONParser jsonParser = new JSONParser();
            	String test = output.replaceAll("\\\\", "");
            	retMap.put("test", test);
            	JSONObject tempjson = (JSONObject) jsonParser.parse(test);
            	retMap.put("Result", tempjson.get("Result").toString());
            	retMap.put("VirtualNumber", tempjson.get("VirtualNumber").toString());
            }*/
            
    	    conn.disconnect();
			
//			retMap.put("l_promise", mainService.selectPromiseList(paramMap));
    	    
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
			
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 회원가입&로그인
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	/*@RequestMapping(value = "/selectUser.do")
	public @ResponseBody Map<?, ?> selectUser(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("selectUser.do :" + paramMap);
		System.out.println(paramMap.toString());
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> userMap = new HashMap<String, Object>();
		
		try {
			mainService.getCheckUserId(paramMap);
			
			retMap.put("user", mainService.selectUser(paramMap));
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}*/
	
	/**
	 * 내위치 저장
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/insertLocation.do")
	public @ResponseBody Map<?, ?> insertLocation(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req insertLocation.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			mainService.insertLocation(paramMap);
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 친구 신청&수락&거절&취소
	 * @param paramMap
	 * @param model
	 * @param req (약속 seq)
	 * @return rep
	 */
	@RequestMapping(value = "/setFriend.do")
	public @ResponseBody Map<?, ?> setFriend(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req setFriend.do :" + paramMap);
		System.out.println(paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			// 신청 : apply, 수락 : accept, 거절 : reject, 취소 : cancel
			if(paramMap.get("flag").toString().equals("ap")) {
				// 현재 신청한게 이미 있는지 select
				Map<String, Object> friendMap = mainService.checkReqFriend(paramMap);
				// if 값이 없으면 insert
				// if 값이 있으면 pass
				if(friendMap == null) {
					mainService.insertReqFriend(paramMap);
				} else {
					throw new Exception(ResultCode.AREADY_INSERT.getCode());
				}
			} else if(paramMap.get("flag").toString().equals("ac")) { // rep update
				paramMap.put("is_yn", "y");
				mainService.updateReqFriend(paramMap);
			} else if(paramMap.get("flag").toString().equals("re")) { // delete
				mainService.deleteReqFriend(paramMap);
			} else if(paramMap.get("flag").toString().equals("ca")) { // delete
				mainService.deleteReqFriend(paramMap);
			} else if(paramMap.get("flag").toString().equals("de")) { // delete
				mainService.deleteReqFriend(paramMap);
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
	 * 약속 만들기&수정
	 * @param paramMap
	 * @param model
	 * @param req (약속 seq)
	 * @return rep
	 */
	@RequestMapping(value = "/setPromise.do")
	public @ResponseBody Map<?, ?> setPromise(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req setPromise.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		System.out.println(paramMap);
		try {
			if(paramMap.get("flag").toString().equals("i")) {
				mainService.insertPromise(paramMap);
			} else if(paramMap.get("flag").toString().equals("e")) {
				mainService.updatePromise(paramMap);
			} else if(paramMap.get("flag").toString().equals("d")) {
				mainService.updatePromise(paramMap);
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
	 * 약속보기시 약속 관련 내위치, 친구위치 불러오기
	 * @param paramMap
	 * @param model
	 * @param req (약속 seq)
	 * @return rep
	 */
	@RequestMapping(value = "/selectLocationList.do")
	public @ResponseBody Map<?, ?> selectLocationList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectLocationList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		System.out.println("selectLocationList.do : " + paramMap.toString());
		try {
			// 종료된 약속인지 아닌지 확인
			retMap.put("l_location", mainService.selectLocationList(paramMap));
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*@RequestMapping(value = "/mobile.do")
	public String mobile(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req mobile.do :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("user") == null) {
				throw new Exception(Constant.CODE_SESSION_EXPIRED);
			}
			
			mainService.selectBattleInfo(params);
			
			retMap.putAll(params);
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		model.addAttribute("retMap", retMap);
		return "mobile";
	}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value = "/yoon_main.do")
	public String yoon_main(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req) {
//		log.debug("req yoon_main.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		List<Object> data_title_list;
		List<Object> data_content_list;
		List<Object> result_list;
		Map<String, Object> result_map;
		Map<String, Object> temp_map;
		
		paramMap.put("comn_cd", "dtl_category_cremation_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_cremation", result_list );
		
		paramMap.put("comn_cd", "dtl_category_burial_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_burial", result_list );
		
		
		model.addAttribute("retMap", retMap);
		return ServicesUtils.isMobileCheck(req) + "/yoon_main";
	}
	
	
	@RequestMapping(value = "/cost_estimate.do")
	public String cost_estimate(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req) {
//		log.debug("req yoon_main.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		List<Object> data_title_list;
		List<Object> data_content_list;
		List<Object> result_list;
		Map<String, Object> result_map;
		Map<String, Object> temp_map;
		
		paramMap.put("comn_cd", "dtl_category_cremation_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_cremation", result_list );
		
		paramMap.put("comn_cd", "dtl_category_burial_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_burial", result_list );
		
		
		model.addAttribute("retMap", retMap);
//		System.out.println( ServicesUtils.isMobileCheck(req) );
		return ServicesUtils.isMobileCheck(req) + "/cost_estimate";
	}
	
	@RequestMapping(value = "/intro.do")
	public String intro(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req) {
//		log.debug("req yoon_main.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		List<Object> data_title_list;
		List<Object> data_content_list;
		List<Object> result_list;
		Map<String, Object> result_map;
		Map<String, Object> temp_map;
		
		paramMap.put("comn_cd", "dtl_category_cremation_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_cremation", result_list );
		
		paramMap.put("comn_cd", "dtl_category_burial_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_burial", result_list );
		
		
		model.addAttribute("retMap", retMap);
//		System.out.println( ServicesUtils.isMobileCheck(req) );
		return ServicesUtils.isMobileCheck(req) + "/intro";
	}
	
	@RequestMapping(value = "/terms1.do")
	public String terms1(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req) {
//		log.debug("req yoon_main.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
//		System.out.println( ServicesUtils.isMobileCheck(req) );
		return ServicesUtils.isMobileCheck(req) + "/terms1";
	}
	
	@RequestMapping(value = "/terms2.do")
	public String terms2(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req) {
//		log.debug("req yoon_main.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
//		System.out.println( ServicesUtils.isMobileCheck(req) );
		return ServicesUtils.isMobileCheck(req) + "/terms2";
	}
	
	@RequestMapping(value = "/notice.do")
	public String notice(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req) {
//		log.debug("req yoon_main.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		List<Object> data_title_list;
		List<Object> data_content_list;
		List<Object> result_list;
		Map<String, Object> result_map;
		Map<String, Object> temp_map;
		
		paramMap.put("comn_cd", "dtl_category_cremation_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_cremation", result_list );
		
		paramMap.put("comn_cd", "dtl_category_burial_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_burial", result_list );
		
		
		
		
		
		
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
		
//		retMap.put("notice_list", adminService.selectNoticeList(paramMap) );
//		retMap.put("page_count", adminService.selectNoticeCount(paramMap) );
		
		model.addAttribute("retMap", retMap);
//		System.out.println( ServicesUtils.isMobileCheck(req) );
		return ServicesUtils.isMobileCheck(req) + "/notice";
	}
	
	@RequestMapping(value = "/notice_detail.do")
	public String notice_detail(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req) {
//		log.debug("req yoon_main.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		List<Object> data_title_list;
		List<Object> data_content_list;
		List<Object> result_list;
		Map<String, Object> result_map;
		Map<String, Object> temp_map;
		
		paramMap.put("comn_cd", "dtl_category_cremation_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_cremation", result_list );
		
		paramMap.put("comn_cd", "dtl_category_burial_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_burial", result_list );
		
//		retMap.put("notice", adminService.selectNoticeOne(paramMap));
		
		model.addAttribute("retMap", retMap);
//		System.out.println( ServicesUtils.isMobileCheck(req) );
		return ServicesUtils.isMobileCheck(req) + "/notice_detail";
	}
	
	@RequestMapping(value = "/nanum.do")
	public String nanum(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req) {
//		log.debug("req yoon_main.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		List<Object> data_title_list;
		List<Object> data_content_list;
		List<Object> result_list;
		Map<String, Object> result_map;
		Map<String, Object> temp_map;
		
		paramMap.put("comn_cd", "dtl_category_cremation_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_cremation", result_list );
		
		paramMap.put("comn_cd", "dtl_category_burial_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_burial", result_list );
		
		
		
		
		
		
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
		
//		retMap.put("nanum_list", adminService.selectNanumList(paramMap) );
//		retMap.put("page_count", adminService.selectNanumCount(paramMap) );
		
		model.addAttribute("retMap", retMap);
//		System.out.println( ServicesUtils.isMobileCheck(req) );
		return ServicesUtils.isMobileCheck(req) + "/nanum";
	}
	
	@RequestMapping(value = "/nanum_detail.do")
	public String nanum_detail(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req) {
//		log.debug("req yoon_main.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		List<Object> data_title_list;
		List<Object> data_content_list;
		List<Object> result_list;
		Map<String, Object> result_map;
		Map<String, Object> temp_map;
		
		paramMap.put("comn_cd", "dtl_category_cremation_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_cremation", result_list );
		
		paramMap.put("comn_cd", "dtl_category_burial_cd");
		data_title_list = (List<Object>) mainService.selectTitleList(paramMap);
		data_content_list = (List<Object>) mainService.selectContentList(paramMap);
		
		result_list = new ArrayList<Object>();
		result_map = new HashMap<String, Object>();
		
		for(int i=0; i<data_content_list.size();i++) {
			temp_map = (Map<String, Object>) data_content_list.get(i); 
			if(result_map.containsKey( temp_map.get("up_dtl_item_cd") )) {
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			} else {
				result_map.put(temp_map.get("up_dtl_item_cd").toString(), new ArrayList<Object>() );
				((List)result_map.get(temp_map.get("up_dtl_item_cd"))).add(temp_map);
			}
		}
		
		for(int i=0;i<data_title_list.size();i++) {
			temp_map = new HashMap<String, Object>();
			temp_map.put("title", ((Map)data_title_list.get(i)).get("dtl_item_nm") );
			temp_map.put("dtl_item_cd", ((Map)data_title_list.get(i)).get("dtl_item_cd") );
			temp_map.put("is_required", ((Map)data_title_list.get(i)).get("is_required") );
			temp_map.put("list", result_map.get(((Map)data_title_list.get(i)).get("dtl_item_cd")));
			result_list.add(temp_map);
		}
		
		retMap.put("l_burial", result_list );
		
//		retMap.put("nanum", adminService.selectNanumOne(paramMap));
		
		model.addAttribute("retMap", retMap);
//		System.out.println( ServicesUtils.isMobileCheck(req) );
		return ServicesUtils.isMobileCheck(req) + "/nanum_detail";
	}
	
	/**
	 * 이메일 보내기
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/email_send.do")
	public @ResponseBody Map<?, ?> email_send(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req email_send.do :" + paramMap);		
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> tempMap;
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			String temp_arr_params = "";
			
			for( String key : paramMap.keySet() ) {
				temp_arr_params += "'" + paramMap.get(key) + "',"; 
			}
			
			temp_arr_params = temp_arr_params.substring( 0, temp_arr_params.length()-1 );
			paramMap.put("arr_params", temp_arr_params);
			
			List<Object> l_result = (List<Object>) mainService.getCostEstimateResult(paramMap);
//			System.out.println(l_result);
			
			// 중복체크 (중복체크할지 ... 기획확인 필요)
//			userService.getCheckUserId(paramMap);
			// 신청서 입력 insert
//			paramMap.put("transfer_start", paramMap.get( "transfer_year").toString() + " / " + paramMap.get("transfer_month").toString());
//			mainService.insertContact(paramMap);
			// userService.insertAuth(paramMap);
			
//			model.clear();
//			retMap.putAll(paramMap);
			
			
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("smtp.gmail.com");
			mailSender.setPort(587);
			mailSender.setUsername("golfeunho@gmail.com");
//			mailSender.setUsername("doejungwoo@gmail.com");
			mailSender.setPassword("ssarang0316!");
//			mailSender.setPassword("rzaccvbucqzzdchn");
//			
			Properties javaMailProperties = new Properties();
			javaMailProperties.put("mail.smtp.auth", true);
			javaMailProperties.put("mail.smtp.starttls.enable", true);
			javaMailProperties.put("mail.smtp.timeout", 8500);
			javaMailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
			mailSender.setJavaMailProperties(javaMailProperties );
			  
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
//			messageHelper.setTo("minjs0463@naver.com");
//			messageHelper.setTo("rstoislove@naver.com");
			messageHelper.setTo("golfeunho@gmail.com");
//			messageHelper.setTo("tkdalsgp@naver.com");
			
//			messageHelper.setText("안녕하세요 content");
			
			String temp = "<html>";
//			temp += "";
			temp += "<head><style>";
			temp += "</style></head>";
			temp += "<body>";
			
//			temp += "<p style='font-size:15px; font-weight:bold; margin:0 0 12px;'>윤라이프</p>";
//			temp += "<p style='font-size:15px; font-weight:bold; margin:0 0 12px;'>견적받기 요청이 접수되었습니다.</p>";
			
			temp += "<p style='font-size:15px; font-weight:bold; margin:0 0 12px;'>고객 이름 : " + paramMap.get("c_name").toString() + "</p>";
			temp += "<p style='font-size:15px; font-weight:bold; margin:0 0 12px;'>고객 연락처 : " + paramMap.get("phone_no").toString() + "</p>";
			temp += "<p style='font-size:15px; font-weight:bold; margin:0 0 12px;'>고객 이메일 : " + paramMap.get("email").toString() + "</p>";
			
//			temp += "<hr style='border: 1px solid #B3B3B3; margin-bottom: 20px;'>";
			
			temp += "<table style='border-collapse: collapse;'>";
			
			
			for(int i=0; i<l_result.size();i++) {
				tempMap = (Map<String, Object>) l_result.get(i);
				temp += "<tr>";
				temp += "<td style='width:250px; height:38px; border: 1px solid #E6E6E6; text-align: center; background: #F8F8F8; font-weight:bold;'>";
				temp += tempMap.get("up_dtl_item_nm").toString();
				temp += "</td>";
				temp += "<td style='width:680px; height:38px; border: 1px solid #E6E6E6; padding-left: 13px;'>";
				temp += tempMap.get("comn_cdnm").toString();
				temp += "</td>";
				temp += "<td style='width:150px; height:38px; border: 1px solid #E6E6E6; padding-right: 13px; text-align: right;'>";
				temp += Comma_won(tempMap.get("dtl_item_nm").toString()) + "원";
				temp += "</td>";
				temp += "</tr>";
			}
			
			temp += "<tr>";
			temp += "<td style='width:250px; height:38px; border: 1px solid #E6E6E6; text-align: center; background: #F8F8F8; font-weight:bold;'>";
			temp += "합계 금액";
			temp += "</td>";
			temp += "<td style='width:680px; height:38px; border: 1px solid #E6E6E6; padding-left: 13px;'>";
			temp += paramMap.get("product_type").toString();
			temp += "</td>";
			temp += "<td style='width:150px; height:38px; border: 1px solid #E6E6E6; padding-right: 13px; text-align: right;'>";
			temp += paramMap.get("sum_cash").toString();
			temp += "</td>";
			temp += "</tr>";
			
			temp += "</table>";
			temp += "</body></html>";
			
			messageHelper.setText(temp, true);
			
			messageHelper.setFrom("golfeunho@gmail.com");
			messageHelper.setSubject("[" + paramMap.get("product_type").toString() + "] " + paramMap.get("c_name").toString() + "님 견적신청서");
			
			// 시간지연 필요
			mailSender.send(message);
			
			messageHelper.setTo(paramMap.get("email").toString());
			mailSender.send(message);
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			model.clear();
			procException(model, e);
		}
		
		model.addAttribute("retMap", retMap);
		
		return model;
	}
	
	
	public static String Comma_won(String junsu) {
		 int inValues = Integer.parseInt(junsu);
		 DecimalFormat Commas = new DecimalFormat("#,###");
		 String result_int = (String)Commas.format(inValues);
		 return result_int;
	}

	
	
	
	@RequestMapping("favicon.ico")
    public String favicon() {
        return "forward:/res/images/favicon.ico";
    }
	
	// ====================
	// 추후작업
	// post 작업
	// mobile access 작업
	// ====================
	
	
	/**
	 * 유저정보 확인/변경 페이지
	 * @param model
	 * @param paramMap
	 * @param req
	 * @return 유저정보 확인/변경 페이지
	 */
	@RequestMapping(value = "/userEdit.do")
	public String userEdit(ModelMap model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req userEdit.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
//		new ArrayList<String>(paramMap.keySet()).indexOf("r_taxchoice");
		try {
			
			if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user", user.getUsername() );
//				throw new Exception(Constant.CODE_MISSING_PARAMS);
			}
			retMap.put("user", userService.selectUserEx(paramMap));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		
		model.addAttribute("retMap", retMap);
		
		return ((  (authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_DEALER") ) || authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_STORE") ) ) && ServicesUtils.isMobileCheck(req).equals("mobile") )?"mobile/":"web/") + "userEdit";
	}
	
	
	/**
	 * 비콘요청 정보 상세
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectCategoryList.do")
	public @ResponseBody Map<?, ?> selectCategoryList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectCategoryList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		System.out.println( paramMap );
		try {
			retMap.put("category_list", mainService.selectCategoryList(paramMap) );
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
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
		
		retMap.put("seq_beacon_req", paramMap.get("seq_beacon_req").toString() );
		retMap.put("dealer_gps_req_list", dealerService.selectDealerGpsReqList(paramMap));
		
		
//		retMap.put("user_ex", userService.selectUserEx(paramMap) );
//		retMap.put("beacon_list", dealerService.selectAgencyBeaconList(paramMap));
//		retMap.put("agency_name_list", dealerService.selectAgencyNameList(paramMap));
		
		model.addAttribute("retMap", retMap);
		return "web/admin/" + "gpsConditions";
	}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value = "/testtest.do")
	public String testtest(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req testtest.do :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		HttpSession ses = req.getSession(false);
		
		model.addAttribute("retMap", retMap);
		return "testtest";
	}
	
	
	@RequestMapping(value = "/testcash.do")
	public String testcash(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req main.do :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		/*try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("user") == null) {
				throw new Exception(Constant.CODE_SESSION_EXPIRED);
			}
			
			mainService.selectBattleInfo(params);
			
			retMap.putAll(params);
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}*/
		
		model.addAttribute("retMap", retMap);
		return "cash/order";
	}
	
	@RequestMapping(value = "/pp_cli_hub.do")
	public String pp_cli_hub(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req main.do :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		model.addAllAttributes(params);
		return "cash/pp_cli_hub";
	}
	
	
	
	
	/**
	 * 메인-공지 공지 List
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getNoticeList.do")
	public @ResponseBody Map<?, ?> getNoticeList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req getNoticeList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1){
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			
			retMap.put("l_notice", mainService.getNoticeList(paramMap));
			retMap.put("c_notice", mainService.getNoticeListCount(paramMap));
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	
	/**
	 * 메인-공지 공지 List
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/setQuestion.do")
	public @ResponseBody Map<?, ?> setQuestion(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req setQuestion.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("mb_id") == null) {
				ServicesUtils.setResultCode(ResultCode.RESULT_MISSED_MANDATORY, model);
			} else {
				
				if(paramMap.get("seq_question") == null){
					paramMap.put("mb_no", ses.getAttribute("mb_no"));
					mainService.insertQuestion(paramMap);
				} else {
					mainService.updateQuestion(paramMap);
				}
				ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	
	/**
	 * 메인-공지 공지 List
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getQuestionList.do")
	public @ResponseBody Map<?, ?> getQuestionList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req getQuestionList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1){
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			HttpSession ses = req.getSession(false);
			paramMap.put("mb_no", ses.getAttribute("mb_no"));
			retMap.put("l_question", mainService.getQuestionList(paramMap));
			retMap.put("c_question", mainService.getQuestionListCount(paramMap));
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	
	/**
	 * 메인-공지 공지 List
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/deleteQuestion.do")
	public @ResponseBody Map<?, ?> deleteQuestion(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req deleteQuestion.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("mb_id") == null) {
			} else {
				mainService.deleteQuestion(paramMap);
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
	 * 단일단순, 일반신고  선택
	 * @param model
	 * @param params
	 * @param req
	 * @return 배틀 Info List
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = "/taxchoice.do")
	public String taxchoice(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req taxchoice.do :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("mb_id") == null) {
			} else {
				params.put("mb_no", ses.getAttribute("mb_no"));
			}
			
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		
		
		model.addAttribute("retMap", retMap);
		return "taxchoice";
	}
	
	
	/**
	 * 엑셀 데이터 저장 List
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/excell_test.do")
	public @ResponseBody Map<?, ?> excell_test(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req excell_test.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1){
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			HttpSession ses = req.getSession(false);
			paramMap.put("mb_no", ses.getAttribute("mb_no"));
			retMap.put("l_question", mainService.getQuestionList(paramMap));
			retMap.put("c_question", mainService.getQuestionListCount(paramMap));
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	
	
	/*@RequestMapping(value = "/excellFileUpload.do")
//	public String noticeImageUpload(Model model, @RequestParam Map<String, Object> params, @RequestParam MultipartFile file) {
	public @ResponseBody Map<?, ?> excellFileUpload(ModelMap model, @RequestParam Map<String, Object> params, @ModelAttribute("uploadForm") FileUploadForm uploadForm, HttpServletRequest req) {
		log.debug("req excellFileUpload.do :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			HttpSession ses = req.getSession(false);
			params.put("mb_no", ses.getAttribute("mb_no"));
			
			List<MultipartFile> files = uploadForm.getFiles();
	        List<String> fileNames = new ArrayList<String>();
	        
	        Map<String, Object> tempMap;
	        List<Object> list = new ArrayList<Object>();
	        
	        if(null != files && files.size() > 0) {
	            for (MultipartFile multipartFile : files) {
 	            	if(multipartFile == null || multipartFile.getSize() == 0) {
	            		continue;
	            	}
// 	            	XSSFWorkbook
// 	            	Workbook wb = new HSSFWorkbook(multipartFile.getInputStream());
 	            	Workbook wb = new XSSFWorkbook(multipartFile.getInputStream());
 	            	Sheet sheet = wb.getSheetAt(0);
 	            	
 	            	int last = sheet.getLastRowNum();
 	            	System.out.println("last : "+last);
 	            	for(int i=0; i<=last; i++){
 	            		Row row = sheet.getRow(i);
 	            		tempMap = new HashMap<String, Object>();
// 	            		System.out.println("test : "+ row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue() );
 	            		tempMap.put("test1", row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
 	            		tempMap.put("test2", row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
 	            		
 	            		list.add(tempMap);
 	            	}
 	            	
 	            	int test = mainService.testExcel(list);
 	            	System.out.println("test 결과 : "+test);
// 	            	retMap.put("test", list);
 	            	
	            }
	        }
	        ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		model.addAttribute("retMap", retMap);
		log.debug("rep setFileUpload.do : " + retMap);
		return model;
	}*/
}