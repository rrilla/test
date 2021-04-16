package kr.co.eventroad.admin.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kr.co.eventroad.admin.common.CommonController;
import kr.co.eventroad.admin.common.Constant;
import kr.co.eventroad.admin.common.FileUploadForm;
import kr.co.eventroad.admin.common.ResultCode;
import kr.co.eventroad.admin.common.RootController;
import kr.co.eventroad.admin.common.ServicesUtils;
import kr.co.eventroad.admin.service.CarService;
import kr.co.eventroad.admin.service.NoticeService;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 차량 컨트롤러
 * @author jwdoe
 * @since 2018. 02. 06.
 * @version 1.0.0
 */
@Controller
public class CarController extends RootController {

	/**
	 * 차량 서비스(자동 할당)
	 */
	@Autowired
	public CarService carService;
	
	@Autowired
	public NoticeService noticeService;
	
	/**
	 * 차량 등원&하원 가능 자녀 목록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectKindergardenKidsStationList.do")
	public @ResponseBody Map<?, ?> selectKindergardenKidsStationList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectKindergardenKidsStationList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("seq_kindergarden_class")){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			if( paramMap.get("seq_kindergarden_class").toString().equals("0") ) {
				paramMap.remove("seq_kindergarden_class");
			}
			
			if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1) {
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			
			retMap.put("kindergarden_kids_station_list", carService.selectKindergardenKidsStationList(paramMap));
			
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
	 * 차량 자녀 상세 정보
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectKidsStationOne.do")
	public @ResponseBody Map<?, ?> selectKidsStationOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectKidsStationOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("seq_kids") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("kids_station_list", carService.selectKidsStationList(paramMap));
			
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
	 * 유치원 차량 정보
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectCarList.do")
	public @ResponseBody Map<?, ?> selectCarList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectCarList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kindergarden") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("car_list", carService.selectCarList(paramMap));
			
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
	 * 차량 지점 정보
	 * station_flag : g : 등원, h : 하원
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectCarStationList.do")
	public @ResponseBody Map<?, ?> selectCarStationList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectCarStationList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_car") || !paramMap.containsKey("station_flag") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("car_station_list", carService.selectCarStationList(paramMap));
			
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
	 * 자녀 지점 등록
	 * station_flag : g : 등원, h : 하원
	 * @param paramMap
	 * @param model
	 * @return 
	 */
	@RequestMapping(value = "/insertKidsStation.do")
	public @ResponseBody Map<?, ?> insertKidsStation(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req insertKidsStation.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( paramMap.containsKey("seq_kids_station") || !paramMap.containsKey("seq_car_station") || !paramMap.containsKey("seq_kids") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("station_flag") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			// 해당 유치원 해당 요일에 등록이 되있는지 확인
			int survey_vote_count = carService.selectKidsStationCheck(paramMap);
			
			if(survey_vote_count == 0) {
//				System.out.println(""+survey_vote_count);
				carService.insertKidsStation(paramMap);
			} else {
//				System.out.println("111이상"+survey_vote_count);
				throw new Exception(ResultCode.AREADY_INSERT.getCode());
			}
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			model.clear();
			log.error(e.getMessage(), e);
			procException(model, e);
		}
		
		model.addAttribute("retMap", retMap);
		return model;
	}
	
	/**
	 * 자녀 지점 수정
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateKidsStation.do")
	public @ResponseBody Map<?, ?> updateKidsStation(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req updateKidsStation.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_kids_station") || !paramMap.containsKey("seq_kids") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("station_flag") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			// 해당 유치원 해당 요일에 등록이 되있는지 확인
			int survey_vote_count = carService.selectKidsStationCheck(paramMap);
			
			if(survey_vote_count == 0) {
//				System.out.println(""+survey_vote_count);
				carService.updateKidsStation(paramMap);
			} else {
				throw new Exception(ResultCode.AREADY_INSERT.getCode());
			}
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			model.clear();
			log.error(e.getMessage(), e);
			procException(model, e);
		}
		
		model.addAttribute("retMap", retMap);
		return model;
	}
	
	/**
	 * 자녀 지점 삭제
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/deleteKidsStation.do")
	public @ResponseBody Map<?, ?> deleteKidsStation(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req deleteKidsStation.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kids_station") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			carService.deleteKidsStation(paramMap);
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 등원&하원 지도 차량 정보
	 * station_flag : g : 등원, h : 하원
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectGuideCarList.do")
	public @ResponseBody Map<?, ?> selectGuideCarList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectGuideCarList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_car") || !paramMap.containsKey("station_flag") || !paramMap.containsKey("day_of_the_week") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			paramMap.put(paramMap.get("day_of_the_week").toString(), "y");
//			System.out.println(paramMap.toString());
			retMap.put("guide_car_list", carService.selectGuideCarList(paramMap));
			
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
	 * 등원&하원 지점별 자녀목록
	 * station_flag : g : 등원, h : 하원
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectGuideKidsStationKidsList.do")
	public @ResponseBody Map<?, ?> selectGuideKidsStationKidsList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectGuideKidsStationKidsList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_car_station") || !paramMap.containsKey("day_of_the_week") || !paramMap.containsKey("year") || !paramMap.containsKey("month") || !paramMap.containsKey("day") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			paramMap.put(paramMap.get("day_of_the_week").toString(), "y");
			retMap.put("guide_kids_station_kids_list", carService.selectGuideKidsStationKidsList(paramMap));
			
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
	 * 학부모 - 자녀차량 정보
	 * cm.state_flag : e :운행종료 s:운행시작
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectMyKidsCarRaceInfoOne.do")
	public @ResponseBody Map<?, ?> selectMyKidsCarRaceInfoOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectMyKidsCarRaceInfoOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> tempMap;
		
		try {
			if( !paramMap.containsKey("seq_kids") || !paramMap.containsKey("day_of_the_week") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("station_flag") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			paramMap.put(paramMap.get("day_of_the_week").toString(), "y");
			
			tempMap = carService.selectMyKidsCarRaceInfoOne(paramMap);
			
			if(tempMap == null) {
				tempMap = new HashMap<String, Object>();
			}
			
			retMap.put("my_kids_car_race_info_one", tempMap);
			
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
	 * 차량운행(등원&하원) 시작&종료
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateCarRace.do")
	public @ResponseBody Map<?, ?> updateCarRace(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req updateCarRace.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("state_flag") || !paramMap.containsKey("seq_user") || !paramMap.containsKey("seq_car") || !paramMap.containsKey("day_of_the_week") || !paramMap.containsKey("station_flag") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			carService.updateCarRace(paramMap);
			
			Map<String, Object> selectCarMap = carService.selectCarOne(paramMap);
			
			
			// fcm
			if(paramMap.get("state_flag").toString().equals("e") && paramMap.get("station_flag").toString().equals("h")) {
			} else {
				paramMap.put(paramMap.get("day_of_the_week").toString(), "y");
				// push token list
				List<Object> temp_token_list = (List<Object>) carService.selectPushCarRaceKidsUserList(paramMap);
				System.out.println(temp_token_list);
				
				// push
				if(temp_token_list.size() > 0) {
					// param pushParamMap
					Map<String, Object> pushDataMap = new HashMap<String, Object>();
					
					pushDataMap.put("msg_type", "차량운행 " + (paramMap.get("state_flag").toString().equals("s")?"시작":"종료"));
					pushDataMap.put("seq", null);
					pushDataMap.put("seq_kindergarden", null);
					pushDataMap.put("seq_kindergarden_class", null);
					pushDataMap.put("seq_kids", null);
					pushDataMap.put("age", null);
					pushDataMap.put("subtitle", paramMap.get("state_flag").toString().equals("s")?(selectCarMap.get("car_name").toString() + "가 출발했습니다."):"자녀가 유치원에 도착했습니다.");
					pushDataMap.put("year", null);
					pushDataMap.put("month", null);
					pushDataMap.put("day", null);
					
					send_push(temp_token_list, pushDataMap);
				}
			}
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			model.clear();
			log.error(e.getMessage(), e);
			procException(model, e);
		}
		
		model.addAttribute("retMap", retMap);
		return model;
	}
	
	/**
	 * 푸시테스트
	 * @param model
	 * @param params
	 * @param req
	 * @return 로그인
	 */
	@RequestMapping(value = "/push_test.do")
	public @ResponseBody Map<?, ?> pushsss(@RequestParam Map<String, Object> paramMap, ModelMap model) {
		log.debug("req pushsss.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> temp_map;
		List<Object> sub_temp_list;
		try {
			System.out.println( paramMap.toString() );
			
			String AUTH_KEY_FCM = "AAAA0vT60M8:APA91bGhjkuD7CRBpPukLLfo3AkI3xc2REjAu-82_jnHS-qKSCvsRc6g8oNzlywaAbjqI8CgTwkB30EW0NiFXL8nvLLpljdiEn1KOjhoZIT-5kfScP6AOYTAyydvlZc8kAF9T-y48zno";
			String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

			ArrayList<String> token = new ArrayList<String>();
//	        token.add("ck1K6vpxQNk:APA91bFF4IstUVw7ZrSyNQMy6I6wqoZ-SHjhubRwS_TMARwWLuat7M64YOTKy9FukQJYjwUW6hyM_aTLsEhOcfntqHyDlYK_rO58IsDD5hbfQ4WzOgV4HQolYDTKhknxNyTFHhsLoNqY");
	        token.add(paramMap.get("token").toString());
	        
		    // userDeviceIdKey is the device id you will query from your database
			
			URL url = new URL(API_URL_FCM);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
	        conn.setDoInput(true); // ++
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
            conn.setUseCaches(false); // ++
            
            conn.setDoOutput(true);
			
            JSONObject json = new JSONObject();
	        JSONObject info = new JSONObject();
	        JSONObject data = new JSONObject();
	        
//	        info.put("body", "새로운 주문이 도착했습니다."); // Notification body
	        
	        data.put("title", "제목");
	        data.put("content", "본문"); // Notification body
//	        data.put("ss12", "s3하하"); // Notification body
	        
	        info.put("body", "This is a test body");
	        info.put("title", "This is a test title");
	        info.put("badge", "0");
	        info.put("sound", "default");
	        json.put("content-available", 1);
	        json.put("notification", info);
	        
//	        json.put("notification", info);
//	        json.put("data", data);
	        json.put("registration_ids", token); // deviceID
//	        json.put("to", "dDPdIV7cRG4:APA91bHITaLgZSspU6RG7odnZQ3liIC5wnTNKGVFDoh26UWlY2OkL3VrEy0M3lJPWqrkVTNzXEs4AIOM0vLeEBZ81MC0eRR8dLy7FFmilmiST4mjU-Xsubg3M6Gtm3RzEg9SXluBMv20"); // deviceID
	        
	        
	        System.out.println("================================");
	        System.out.println(json);
	        System.out.println("================================");
	        
	        
	        boolean SHOW_ON_IDLE = false;    //옙 활성화 상태일때 보여줄것인지

//	        try(OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())){
				//혹시나 한글 깨짐이 발생하면 
			try(OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")){ //인코딩을 변경해준다.

	            wr.write(json.toString());
	            wr.flush();
	        }catch(Exception e){
	        }

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		        String output;
		        System.out.println("Output from Server .... \n");
		        while ((output = br.readLine()) != null) {
		            System.out.println(output);
		        }

		    conn.disconnect();
			
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(model, e);
		}
		
		model.addAttribute("retMap", retMap);
		return model;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * RealStory 관리자 페이지
	 * @param model
	 * @param params
	 * @param req
	 * @return 리얼스토리, 메인동영상 List
	 */
	@RequestMapping(value = "/adminRealStory.fnd")
	public String adminRealStory(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req adminRealStory.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("user") == null) {
				throw new Exception(Constant.CODE_SESSION_EXPIRED);
			}
			
			carService.selectRealStoryBroadCast(params);
			
			retMap.put("RET", params.get("RET"));
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return "admin/adminRealStory";
	}
	
	/**
	 * RealStory 등록 페이지
	 * @param model
	 * @param params
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/regRealStory.fnd")
	public String regRealStory(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req regRealStory.fnd :" + params);

		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.putAll(params);
		
		return "regRealStory";
	}
	
	/**
	 * 업체 리스트(이름순 정렬)
	 * @param model
	 * @param params
	 * @param req
	 * @return 업체 List
	 */
	@RequestMapping(value = "/selectRealStoryStoreList.fnd")
	public String selectRealStoryStoreList(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req selectRealStoryStoreList.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("user") == null) {
				throw new Exception(Constant.CODE_SESSION_EXPIRED);
			}
			if(params.get("STORE_NAME") != null){
				params.put("STORE_NAME", "%"+params.get("STORE_NAME")+"%");
			}
			
			carService.selectRealStoryStoreList(params);
			
			retMap.put("RET", params.get("RET"));
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return "json";
	}
	
	/**
	 * 지역구분 option 지역들
	 * @param model
	 * @param params
	 * @param req
	 * @return 지역 List
	 */
	@RequestMapping(value = "/selectRealStorySearchArea.fnd")
	public String selectRealStorySearchArea(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req selectRealStorySearchArea.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			carService.selectRealStorySearchArea(params);
			
			retMap.put("RET", params.get("RET"));
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return "json";
	}
	
	/**
	 * 검색 이벤트
	 * @param model
	 * @param params
	 * @param req SEARCH_TEXT=업체, AREA_TYPE=덕진동, FOOD_TYPE=피자
	 * @return 검색 된 업체 List
	 */
	@RequestMapping(value = "/selectRealStorySearchStore.fnd")
	public String selectRealStorySearchStore(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req selectRealStorySearchStore :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		params.put("SEARCH_TEXT", "%"+params.get("SEARCH_TEXT")+"%");
		
		try {
			carService.selectRealStorySearchStore(params);
			
			retMap.put("RET", params.get("RET"));
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return "json";
	}
	
	/**
	 * 리얼스토리 insert
	 * @param model
	 * @param params BOARD_DETAIL, START_DATE, END_DATE, VIDEO_FILE, BROADCAST_DETAIL(BROADCAST_TITLE 로 저장 됨)
	 * @param req
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/insertRealStory.fnd")
	public String insertRealStory(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req, @RequestParam MultipartFile file) {
		log.debug("req insertRealStory.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		String cd = null;
		/*
		BOARD_DETAIL  // 방송 제목 board 에 저장
		START_DATE
		END_DATE
		VIDEO_FILE  // 비디오 파일 경로
		  // ADMIN 설정 업체들
		*/		
		try {
			if (!file.isEmpty()) {
				CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + file.getOriginalFilename(), file);
			}
			// 섬네일 파일 경로  - file
			params.put("SCREEN_FILE", Constant.IMAGE_PROFILE_IMAGE_RETURN + file.getOriginalFilename());

			String str="";
			if(params.get("BROADCAST_DETAIL") != null){
				cd = (String) params.get("BROADCAST_DETAIL");
				String[] li = cd.split(",");
				// 업체가 여러개일 경우 목록 보여주기 위해 BROADCAST_DETAIL 에 정보넣음
//				String str="";  원래 Position
				for(int i=0;i<li.length;i++){
					str+=li[i] ;
					if(i<li.length-1){
						str += "," ;
					}
				}
				params.put("BROADCAST_DETAIL", str);
			}
			
			
			
			
			if(params.get("BOARD_TYPE").equals("BM")){
				str="";
				if(Integer.parseInt(params.get("START_TIME").toString()) <= Integer.parseInt(params.get("START_TIME").toString())){
					for(int i=Integer.parseInt(params.get("START_TIME").toString());i<=Integer.parseInt(params.get("END_TIME").toString());i++){
						str+=String.valueOf(i);
						if(i < Integer.parseInt(params.get("END_TIME").toString())) {
							str += "," ;
						}
					}
				} else {
					for(int i=Integer.parseInt(params.get("START_TIME").toString()) ; i<=23 ; i++){
						str+=String.valueOf(i);
							str += "," ;
					}
					
					for(int i=0 ; i<=Integer.parseInt(params.get("END_TIME").toString()) ; i++){
						str+=String.valueOf(i);
						if(i < Integer.parseInt(params.get("END_TIME").toString())) {
							str += "," ;
						}
					}
				}
				params.put("BROADCAST_TITLE", str);
			} else if(params.get("BOARD_TYPE").equals("BT")){
				params.remove("START_TIME");
				params.remove("END_TIME");
			} else if(params.get("BOARD_TYPE").equals("BL")){
				params.remove("START_TIME");
				params.remove("END_TIME");
			}
			
			carService.insertRealStoryVideo(params);
			carService.insertRealStoryBroadCast(params);
			carService.insertRealStoryBoard(params);
			
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		
		return "redirect:/adminRealStory.fnd";
	}
	
	/**
	 * RealStory 수정 페이지
	 * @param model
	 * @param params
	 * @param req
	 * @return RealStory One
	 */
	@RequestMapping(value = "/editRealStory.fnd")
	public String editRealStory(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req editRealStory.fnd :" + params);

		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try{
			carService.selectOneRealStory(params);

			retMap.put("RET", params.get("RET"));
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		retMap.putAll(params);
		model.addAttribute("retMap", retMap);
		
		return "editRealStory";
	}
	
	/**
	 * 메인동영상 수정
	 * @param model
	 * @param params SEQ_USER, PW, PW_CURRENT, PROFILE_IMAGE
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/updateRealStory.fnd")
	public String updateRealStory(Model model, @RequestParam Map<String, Object> params, @RequestParam MultipartFile file) {
		log.debug("req updateRealStory.fnd : " + params);

		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if (!file.isEmpty()) {
				CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + file.getOriginalFilename(), file);
				
				// 섬네일 파일 경로  - file
				params.put("SCREEN_FILE", Constant.IMAGE_PROFILE_IMAGE_RETURN + file.getOriginalFilename());
			}
			
			String str="";
			if(params.get("BOARD_TYPE").equals("BM")){
				if(Integer.parseInt(params.get("START_TIME").toString()) <= Integer.parseInt(params.get("START_TIME").toString())){
					for(int i=Integer.parseInt(params.get("START_TIME").toString());i<=Integer.parseInt(params.get("END_TIME").toString());i++){
						str+=String.valueOf(i);
						if(i < Integer.parseInt(params.get("END_TIME").toString())) {
							str += "," ;
						}
					}
				} else {
					for(int i=Integer.parseInt(params.get("START_TIME").toString()) ; i<=23 ; i++){
						str+=String.valueOf(i);
							str += "," ;
					}
					
					for(int i=0 ; i<=Integer.parseInt(params.get("END_TIME").toString()) ; i++){
						str+=String.valueOf(i);
						if(i < Integer.parseInt(params.get("END_TIME").toString())) {
							str += "," ;
						}
					}
				}
				params.put("BROADCAST_TITLE", str);
			} else if(params.get("BOARD_TYPE").equals("BT")){
				params.remove("START_TIME");
				params.remove("END_TIME");
			} else if(params.get("BOARD_TYPE").equals("BL")){
				params.remove("START_TIME");
				params.remove("END_TIME");
			}
			
			
			String cd;
			if( params.get("BROADCAST_DETAIL") != null){
				cd = (String) params.get("BROADCAST_DETAIL");
				params.remove("BROADCAST_DETAIL");
				String[] li = cd.split(",");
				
				// 업체가 여러개일 경우 목록 보여주기 위해 BROADCAST_DETAIL 에 정보넣음
				str="";
				for(int i=0;i<li.length;i++){
					str+=li[i] ;
					if(i<li.length-1){
						str += "," ;
					}
				}
				params.put("BROADCAST_DETAIL", str);
			}
			
			
			
			carService.updateRealStoryBroadCast(params);
			//수정할 떄 멈출 떄 있었음... 확인 필요
			carService.updateRealStoryVideo(params);
			carService.updateRealStoryBoard(params);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}

		model.addAttribute("retMap", retMap);
		log.debug("rep updateRealStory.fnd : " + retMap);
		return "redirect:/adminRealStory.fnd";
	}
	
	/**
	 * 리얼스토리 삭제
	 * @param model
	 * @param params
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/deleteRealStory.fnd")
	public String deleteRealStory(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req deleteRealStory.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			carService.deleteRealStoryVideo(params);
			carService.deleteRealStoryBroadCast(params);
			carService.deleteRealStoryBoard(params);
			
			retMap.put("RET", params.get("RET"));
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		
		return "json";
	}
	
	/**
	 * 리얼스토리 전체댓글
	 * @param model
	 * @param params
	 * @param req
	 * @return 리얼스토리 전체댓글 List
	 */
	@RequestMapping(value = "/selectReplyRealStory.fnd")
	public String selectReplyRealStory(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req selectReplyRealStory :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("user") == null) {
				throw new Exception(Constant.CODE_SESSION_EXPIRED);
			}
			
			carService.selectReplyRealStory(params);
			
			retMap.put("RET", params.get("RET"));
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return "selectReplyRealStory";
	}
}
