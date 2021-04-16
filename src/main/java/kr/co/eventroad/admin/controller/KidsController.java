package kr.co.eventroad.admin.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.co.eventroad.admin.common.CommonController;
import kr.co.eventroad.admin.common.Constant;
import kr.co.eventroad.admin.common.FileUploadForm;
import kr.co.eventroad.admin.common.ResultCode;
import kr.co.eventroad.admin.common.RootController;
import kr.co.eventroad.admin.common.ServicesUtils;
import kr.co.eventroad.admin.service.AdminService;
import kr.co.eventroad.admin.service.AreaService;
import kr.co.eventroad.admin.service.KidsService;
import kr.co.eventroad.admin.service.NoticeService;
import kr.co.eventroad.admin.service.TeacherService;
import kr.co.eventroad.admin.service.UserService;
import scala.annotation.meta.param;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
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
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.ibm.icu.util.ChineseCalendar;

/**
 *  자녀 컨트롤러
 * @author jwdoe
 * @since 2016. 12. 14.
 * @version 1.0.0
 */
@Controller
//@RequestMapping("/job/*")
public class KidsController extends RootController {

	/**
	 * 자녀 서비스(자동 할당)
	 */
	@Autowired
	public KidsService kidsService;
	
	@Autowired
	public UserService userService;
	
	@Autowired
	public NoticeService noticeService;
	
	String url = "web/agency/";
	
	/**
	 * 자녀 입력
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/insertKids.do")
	public @ResponseBody Map<?, ?> insertKids(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req insertKids.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			System.out.println(paramMap.toString());
			// seq_user : 부모 seq
			if( !paramMap.containsKey("seq_user") || !paramMap.containsKey("kids_name") || !paramMap.containsKey("sex")){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			} else {
				// 자녀정보 insert
				String seq_kids = kidsService.insertKids(paramMap);
				System.out.println(seq_kids);
				
				List<MultipartFile> files = uploadForm.getFiles();
				
				int cnt = 0;
				if(null != files && files.size() > 0) {
					for (MultipartFile multipartFile : files) {
						if(multipartFile == null || multipartFile.getSize() == 0) {
	 	            		cnt++;
		            		continue;
		            	}
	 	            	
		 	            String fileName = multipartFile.getOriginalFilename();
//			            fileNames.add(fileName);
		        		String strSplit[] = multipartFile.getOriginalFilename().split("[.]");
		        		
		        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_kids/" + paramMap.get("seq_kids").toString() + "/" + fileName, multipartFile);
		        		
		        		paramMap.put("kids_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_kids/" + paramMap.get("seq_kids").toString() + "/" + fileName);
		        		cnt++;
		        	}
		        }
				
				kidsService.updateKids(paramMap);
				
				userService.insertTitle(paramMap);
				
				paramMap.clear();
				retMap.put("seq_kids", seq_kids);
			}
			model.clear();
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
	 * 자녀 업데이트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateKids.do")
	public @ResponseBody Map<?, ?> updateKids(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req updateKids.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_kids") || !paramMap.containsKey("seq_user") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			} else {
				List<MultipartFile> files = uploadForm.getFiles();
				
				int cnt = 0;
				if(null != files && files.size() > 0) {
					for (MultipartFile multipartFile : files) {
						if(multipartFile == null || multipartFile.getSize() == 0) {
	 	            		cnt++;
		            		continue;
		            	}
	 	            	
		 	            String fileName = multipartFile.getOriginalFilename();
	//		            fileNames.add(fileName);
		        		String strSplit[] = multipartFile.getOriginalFilename().split("[.]");
		        		
		        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_kids/" + paramMap.get("seq_kids").toString() + "/" + fileName, multipartFile);
		        		
		        		paramMap.put("kids_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_kids/" + paramMap.get("seq_kids").toString() + "/" + fileName);
		        		cnt++;
		        	}
		        }
				
				
				// 자녀정보 update
				kidsService.updateKids(paramMap);
				userService.updateTitle(paramMap);
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
	 * 자녀 전체
	 * @param model
	 * @param params
	 * @param req
	 * @return 자녀 유치원 신청&등록 목록
	 */
	@RequestMapping(value = "/selectMyKidsList.do")
	public @ResponseBody Map<?, ?> selectMyKidsList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req selectMyKidsList.do :" + paramMap);
		
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
			
			retMap.put("my_kids_list", kidsService.selectMyKidsList(paramMap));
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 출결 목록
	 * attendance_flag : o : 출석목록, x : 결석목록, w : 체크 안한 자녀
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectAttendanceBookList.do")
	public @ResponseBody Map<?, ?> selectAttendanceBookList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectAttendanceBookList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("year") || !paramMap.containsKey("month") || !paramMap.containsKey("day") || !paramMap.containsKey("attendance_flag") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("attendance_book_list", kidsService.selectAttendanceBookList(paramMap));
			
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
	 * 출석등록
	 * attendance_flag : o : 출석, x : 결석
	 * @param paramMap
	 * @param model
	 * @return 
	 */
	@RequestMapping(value = "/insertAttendanceBook.do")
	public @ResponseBody Map<?, ?> insertAttendanceBook(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req insertAttendanceBook.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_kids[0]") || !paramMap.containsKey("attendance_flag[0]") || !paramMap.containsKey("year") || !paramMap.containsKey("month") || !paramMap.containsKey("day") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("c_attendance_book") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			// select 로 출&결 확인
			String temp_seq_kids = "";
			for(int i=0;i<Integer.valueOf(paramMap.get("c_attendance_book").toString());i++) {
				if(paramMap.containsKey("seq_kids["+ i +"]") ) {
					temp_seq_kids += paramMap.get("seq_kids["+ i +"]").toString() + ",";
				}
			}
			
			temp_seq_kids = temp_seq_kids.substring( 0, temp_seq_kids.length()-1 );
			
			System.out.println("입력받은 seq_kids list");
			System.out.println(temp_seq_kids);
			
			paramMap.put("seq_kids_arr", temp_seq_kids );
			
			List<Object>data_attendance_book_list = (List<Object>) kidsService.selectAttendanceBookCheckList(paramMap);
			
			System.out.println("입력받은 seq_kids list 가 db에 있는지 확인");
			System.out.println(data_attendance_book_list.toString());
			
			// 다중insert 에서 이미 출석 체크를 한 자녀를 분리하기 위한 key 로 찾을 수 있게 제작
			Map<String, Object> temp_map;
			Map<String, Object> temp_attendance_book_map = new HashMap<String, Object>();
			for(int i=0;i<data_attendance_book_list.size();i++) {
				temp_map = (Map<String, Object>) data_attendance_book_list.get(i);
				temp_attendance_book_map.put(temp_map.get("seq_kids").toString(), "in");
			}
			
			System.out.println("이미 출석한 자녀");
			System.out.println(temp_attendance_book_map.toString());
			
			// 다중insert 를 하기 위한 array 배열 만들기
			ArrayList<Map<String, Object>> attendance_book_list = new ArrayList<Map<String, Object>>(); 
			for(int i=0;i<Integer.valueOf(paramMap.get("c_attendance_book").toString());i++ ) {
				if(temp_attendance_book_map.containsKey( paramMap.get("seq_kids["+ i +"]") )) {
					continue;
				}
				
				temp_map = new HashMap<String, Object>();
				
				if(temp_attendance_book_map.containsKey(paramMap.get("seq_kids["+ i +"]"))) {
					System.out.println("이미 등록된 원아");
					continue;
				}
				
				temp_map.put("seq_kindergarden", paramMap.get("seq_kindergarden"));
				temp_map.put("year", paramMap.get("year"));
				temp_map.put("month", paramMap.get("month"));
				temp_map.put("day", paramMap.get("day"));
				temp_map.put("seq_kids", paramMap.get("seq_kids["+ i +"]"));
				temp_map.put("attendance_flag", paramMap.get("attendance_flag["+ i +"]"));
				
				if(paramMap.containsKey("memo")) {
					temp_map.put("memo", paramMap.get("memo"));
				} else {
					temp_map.put("memo", " ");
				}
				
				attendance_book_list.add(temp_map);
			}
			
//			System.out.println(attendance_book_list.toString());
			
			if(attendance_book_list.size()>0){
				System.out.println("size > 0 insert");
				kidsService.insertAttendanceBook(attendance_book_list);
			}
			
			// 미탑승일 경우 해당부모에게 push 전송
			
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
	 * 출석 수정
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateAttendanceBook.do")
	public @ResponseBody Map<?, ?> updateAttendanceBook(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req updateAttendanceBook.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
//			if( !paramMap.containsKey("seq_attendance_book_arr") || !paramMap.containsKey("attendance_flag") ) {
			if( !paramMap.containsKey("seq_kids_arr") || !paramMap.containsKey("attendance_flag") || !paramMap.containsKey("year") || !paramMap.containsKey("month") || !paramMap.containsKey("day") || !paramMap.containsKey("seq_kindergarden") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			String[] kids_array = paramMap.get("seq_kids_arr").toString().split(",");
			
			// 수정해야하는 seq_kids 값만 남게됨
			Map<String, Object> param_kids_map = new HashMap<String, Object>();
			
			// 원아 string[] -> map 형식 만들기
	        for(int i=0;i<kids_array.length;i++) {
	        	param_kids_map.put( kids_array[i], "");
	        }
	        
	        System.out.println("seq_kids_arr");
	        System.out.println(param_kids_map);
	              
			// select attendance
			List<Object>data_insert_kids_list = (List<Object>) kidsService.selectCheckInsertAttendanceBook(paramMap);
			
			System.out.println("db select - insert 여부체크");
			System.out.println(data_insert_kids_list);
			
			// seq_kids_arr 에서 db select 된 seq_kids 값 제외
			for(int i=0;i<data_insert_kids_list.size();i++) {
				param_kids_map.remove( ((Map)data_insert_kids_list.get(i)).get("seq_kids").toString() );
			}
			
			System.out.println("update 처리 할 목록");
			System.out.println(data_insert_kids_list);
			
			if(data_insert_kids_list.size() > 0) {
				System.out.println("update 처리");
				String arr_update = "";
				for(int i=0;i<data_insert_kids_list.size();i++) {
					arr_update += ((Map)data_insert_kids_list.get(i)).get("seq_kids").toString() + ",";
				}
				arr_update = arr_update.substring(0, arr_update.length()-1);
				
				System.out.println(arr_update);
				paramMap.put("seq_kids_arr", arr_update);
				kidsService.updateAttendanceBook(paramMap);
			}
			
			
			System.out.println("insert 처리 할 목록");
			System.out.println(param_kids_map);
//			List<Object> update_kids_list = new ArrayList<Object>(param_kids_map.keySet());
			
			if(param_kids_map.size() > 0) {
				// 다중insert 를 하기 위한 array 배열 만들기
				ArrayList<Map<String, Object>> attendance_book_list = new ArrayList<Map<String, Object>>(); 
				
				Iterator<Object> iter = new HashSet<Object>(param_kids_map.keySet()).iterator();
				Map<String, Object> tempMap;
				if(param_kids_map.size() > 0) {
					String key = null;
					while (iter.hasNext()) {
						key = (String) iter.next();
						tempMap = new HashMap<String, Object>();
						
						tempMap.put("seq_kids", key);
						tempMap.put("seq_kindergarden", paramMap.get("seq_kindergarden"));
						tempMap.put("year", paramMap.get("year"));
						tempMap.put("month", paramMap.get("month"));
						tempMap.put("day", paramMap.get("day"));
						tempMap.put("attendance_flag", paramMap.get("attendance_flag"));
						if(paramMap.containsKey("memo")) {
							tempMap.put("memo", paramMap.get("memo"));
						} else {
							tempMap.put("memo", " ");
						}
						attendance_book_list.add(tempMap);
					}
				}
				System.out.println(attendance_book_list);
				kidsService.insertAttendanceBook(attendance_book_list);
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
	 * 원아 월별 출결 목록
	 * attendance_flag : o : 출석목록, x : 결석목록, w : 체크 안한 자녀
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectKidsAttendanceBookList.do")
	public @ResponseBody Map<?, ?> selectKidsAttendanceBookList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectKidsAttendanceBookList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kids") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("year") || !paramMap.containsKey("month") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("kids_attendance_book_list", kidsService.selectKidsAttendanceBookList(paramMap));
			
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
	 * 지점 도착 푸시 전송
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/send_station_push.do")
	public @ResponseBody Map<?, ?> send_station_push(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req send_station_push.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_car_station") || !paramMap.containsKey("day_of_the_week") || !paramMap.containsKey("year") || !paramMap.containsKey("month") || !paramMap.containsKey("day") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			paramMap.put(paramMap.get("day_of_the_week").toString(), "y");
			
			List<Object> token_list = (List<Object>) kidsService.selectPushStationKidsList(paramMap);
			
			if(token_list.size() == 0) {
				throw new Exception(ResultCode.RESULT_NOT_FOUND_VALUE.getCode());
			}
			
			String AUTH_KEY_FCM = "AAAA0vT60M8:APA91bGhjkuD7CRBpPukLLfo3AkI3xc2REjAu-82_jnHS-qKSCvsRc6g8oNzlywaAbjqI8CgTwkB30EW0NiFXL8nvLLpljdiEn1KOjhoZIT-5kfScP6AOYTAyydvlZc8kAF9T-y48zno";
			String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
			
			ArrayList<String> token = new ArrayList<String>();
			
			for(int i=0;i<token_list.size();i++) {
				token.add(((Map)token_list.get(i)).get("token").toString());
			}
			
			// userDeviceIdKey is the device id you will query from your database
			try {
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
	        
	        if( paramMap.containsKey("station_flag") ) {
	        	if(paramMap.get("station_flag").toString().equals("g")) {
	        		paramMap.put("msg_type", "등원 승차 알림");
					paramMap.put("subtitle", "자녀가 " + ((Map)token_list.get(0)).get("car_name").toString() + "에 승차하였습니다");
	        	} else if(paramMap.get("station_flag").toString().equals("h")) {
	        		paramMap.put("msg_type", "하원 하차 알림");
					paramMap.put("subtitle", "자녀가 " + ((Map)token_list.get(0)).get("car_name").toString() + "에 하차하였습니다");
	        	}
	        } else {
	        	paramMap.put("msg_type", "차량도착");
				paramMap.put("subtitle", ((Map)token_list.get(0)).get("car_name").toString() + " 곧 도착합니다");
	        }
	        
	        data.put("subtitle", paramMap.get("subtitle").toString()); // Notification body
	        data.put("msg_type", paramMap.get("msg_type").toString()); // Notification body
	        
//	        json.put("notification", info);
	        json.put("data", data);
	        json.put("registration_ids", token); // deviceID
//	        json.put("to", "dDPdIV7cRG4:APA91bHITaLgZSspU6RG7odnZQ3liIC5wnTNKGVFDoh26UWlY2OkL3VrEy0M3lJPWqrkVTNzXEs4AIOM0vLeEBZ81MC0eRR8dLy7FFmilmiST4mjU-Xsubg3M6Gtm3RzEg9SXluBMv20"); // deviceID
	        
	        System.out.println("================================");
	        System.out.println(json);
	        System.out.println("================================");
	        
	        boolean SHOW_ON_IDLE = false;    //옙 활성화 상태일때 보여줄것인지

//	      try(OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())){
			try(OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")){ //혹시나 한글 깨짐이 발생하면
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
		    
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
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
	 * 원아  정보 조회
	 * attendance_flag : o : 출석목록, x : 결석목록, w : 체크 안한 자녀
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectKidsOne.do")
	public @ResponseBody Map<?, ?> selectKidsOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectKidsOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kids") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("kids", kidsService.selectKidsOne(paramMap));
			
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
	 * 결석신청  -- &취소
	 * attendance_flag : x : 결석
	 * @param paramMap
	 * @param model
	 * @return 
	 */
	@RequestMapping(value = "/setAttendanceBookAbsent.do")
	public @ResponseBody Map<?, ?> setAttendanceBookAbsent(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req setAttendanceBookAbsent.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_kids") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("seq_kindergarden_class") || !paramMap.containsKey("kids_name") || !paramMap.containsKey("start_year") || !paramMap.containsKey("start_month") || !paramMap.containsKey("start_day") || !paramMap.containsKey("end_year") || !paramMap.containsKey("end_month") || !paramMap.containsKey("end_day") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			String start_date = paramMap.get("start_year").toString() + "-" + paramMap.get("start_month").toString() + "-" + paramMap.get("start_day").toString();
			String end_date = paramMap.get("end_year").toString() + "-" + paramMap.get("end_month").toString() + "-" + paramMap.get("end_day").toString();
			
			System.out.println(start_date);
			System.out.println(end_date);
			
			// start 날짜
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			Calendar start_cal = Calendar.getInstance();   
			start_cal.setTime(dateFormat.parse( start_date ));
			
			Calendar end_cal = Calendar.getInstance();   
			end_cal.setTime(dateFormat.parse( end_date ));
			
			/*
			 * 1. cal1 == cal2 일 경우 return 0
    		 * 2. cal1 > cal2 일 경우 return 1
    		 * 3. cal1 < cal2 일 경우 return -1
			 * [출처] [예제로 보는 함수 - java]Calendar.compareTo() - 날짜 비교하기, Calendar - 두 날짜 차이 구하기 , Calendar.getTimeInMillis() - 시간 받아오기|작성자 광레기
			 */
			
			if( start_cal.compareTo(end_cal) == 1) {
				throw new Exception(ResultCode.RESULT_WRONG_VALUE.getCode());
			}
			
			// 다중insert 를 하기 위한 array 배열 만들기
			ArrayList<Map<String, Object>> attendance_book_list = new ArrayList<Map<String, Object>>(); 
			Map<String, Object> tempMap;
			
	        String year;
	        String month;
	        String day;
	        String now;
	        
	        
	        // 2018 년 공휴일 구해서 key value 만들기
	        	// 법정 휴일 array
			String[] solar = {"01-01", "03-01", "05-05", "06-06", "08-15", "10-03", "10-09", "12-25"};
	        String[] lunar = {"01-01", "01-02", "04-08", "08-14", "08-15", "08-16", "12-31"};
	        
	        // 법정 휴일 map
	        	// 양력
	        Map<String, Object> lunarHolidayMap = new HashMap<String, Object>();
	        
	        lunarHolidayMap.put("01-01", "lunar");
	        lunarHolidayMap.put("01-02", "lunar");
	        lunarHolidayMap.put("04-08", "lunar");
	        lunarHolidayMap.put("08-14", "lunar");
	        lunarHolidayMap.put("08-15", "lunar");
	        lunarHolidayMap.put("08-16", "lunar");
	        lunarHolidayMap.put("12-31", "lunar");
	        	// 음력
	        Map<String, Object> solarHolidayMap = new HashMap<String, Object>();
	        solarHolidayMap.put("01-01", "solar");
	        solarHolidayMap.put("03-01", "solar");
	        solarHolidayMap.put("05-05", "solar");
	        solarHolidayMap.put("06-06", "solar");
	        solarHolidayMap.put("08-15", "solar");
	        solarHolidayMap.put("10-03", "solar");
	        solarHolidayMap.put("10-09", "solar");
	        solarHolidayMap.put("12-25", "solar");
	        
			while(true) {
				// 주말
				if(start_cal.get(Calendar.DAY_OF_WEEK) != 1 && start_cal.get(Calendar.DAY_OF_WEEK) != 7) {
					year = "" + start_cal.get(Calendar.YEAR);
					month = "" + ((start_cal.get(Calendar.MONTH)+1)<10?("0" + (start_cal.get(Calendar.MONTH)+1)):(start_cal.get(Calendar.MONTH)+1)) ;
					day = "" + (start_cal.get(Calendar.DATE)<10?("0" + start_cal.get(Calendar.DATE)):start_cal.get(Calendar.DATE));
					
					now = year+"-"+month+"-"+day;
					// 공휴일 체크
					if( !lunarHolidayMap.containsKey(isHoliday(now)) && !solarHolidayMap.containsKey(month+"-"+day) ) {
						tempMap = new HashMap<String, Object>();
						
						tempMap.put("seq_kids", paramMap.get("seq_kids").toString());
						tempMap.put("seq_kindergarden", paramMap.get("seq_kindergarden"));
						tempMap.put("year", year);
						tempMap.put("month",  month);
						tempMap.put("day", day);
						tempMap.put("attendance_flag", "x");
						if(paramMap.containsKey("memo")) {
							tempMap.put("memo", paramMap.get("memo"));
						} else {
							tempMap.put("memo", " ");
						}
						attendance_book_list.add(tempMap);
					}
				}
				
				if(start_cal.compareTo(end_cal) == 0) {
					break;
				} else {
					start_cal.add(Calendar.DATE, 1);
				}
			}
			
			
			// 등록 된 기간별 데이터 조회 (list)
			paramMap.put("list", attendance_book_list);
			List<Object>attendance_book_kids_term_list = (List<Object>) kidsService.selectAttendanceBookKidsTermList(paramMap);
			Map<String, Object> aready_attendance_book_kids_map = new HashMap<String, Object>();
			
			System.out.println("기존 출석 체크 된 원아 목록");
			System.out.println(attendance_book_kids_term_list);
			
			// 등록 된 기간별 데이터 map 형식 만들기
	        for(int i=0;i<attendance_book_kids_term_list.size();i++) {
	        	aready_attendance_book_kids_map.put( ((Map)attendance_book_kids_term_list.get(i)).get("year").toString() + ((Map)attendance_book_kids_term_list.get(i)).get("month").toString() + ((Map)attendance_book_kids_term_list.get(i)).get("day").toString(), "");
	        }
	        
	        System.out.println("기존 출석 체크 된 원아 목록 map 버전");
			System.out.println(aready_attendance_book_kids_map);
	        
			// 등록 된 비콘 제거 하기 위한 iter
	        Iterator<Map<String, Object>> iter = attendance_book_list.iterator();
	        
	        // 등록 된 비콘 제거 후 iterator to list
	        ArrayList<Map<String, Object>> insert_attendance_book_kids_term_list = new ArrayList<Map<String, Object>>(); 
//	        excel_list = new ArrayList<Object>();
	        
	        while (iter.hasNext()) {
	        	Map<String, Object> s = (Map<String, Object>) iter.next();
	        	
	        	if( aready_attendance_book_kids_map.containsKey( s.get("year").toString() + s.get("month").toString() + s.get("day").toString() ) ) {
	        		iter.remove();
	        	} else {
	        		insert_attendance_book_kids_term_list.add(s);
	        	}
	        }
			
	        System.out.println("입력 가능 출석 일정");
			System.out.println(insert_attendance_book_kids_term_list);
			
			if(insert_attendance_book_kids_term_list.size() > 0) {
				kidsService.insertAttendanceBook(insert_attendance_book_kids_term_list);
			}
			
			// news insert
			/*paramMap.put("msg_type", "결석신청");
			paramMap.put("seq", null);
			paramMap.put("subtitle", paramMap.get("kids_name").toString() + " 귀가동의서가 등록되었습니다.");
			paramMap.put("seq_user", paramMap.get("seq_user_parent").toString());
			paramMap.put("seq_kids", paramMap.get("seq_kids").toString());
			paramMap.put("year", null);
			paramMap.put("month", null);
			paramMap.put("day", null);
			
			noticeService.insertNews(paramMap);*/
			
			// push token list
			List<Object> temp_token_list = (List<Object>) noticeService.selectPushKidsTeacherList(paramMap);
			System.out.println(temp_token_list);
			
			// push
			if(temp_token_list.size() > 0) {
				// param pushParamMap
				Map<String, Object> pushDataMap = new HashMap<String, Object>();
				
				pushDataMap.put("msg_type", "결석신청");
				pushDataMap.put("seq", null);
				pushDataMap.put("seq_kindergarden", null);
				pushDataMap.put("seq_kindergarden_class", null);
				pushDataMap.put("seq_kids", paramMap.get("seq_kids").toString());
				pushDataMap.put("age", null);
				pushDataMap.put("subtitle", paramMap.get("kids_name").toString() + " 결석 신청이 등록되었습니다.");
				pushDataMap.put("year", null);
				pushDataMap.put("month", null);
				pushDataMap.put("day", null);
				
				send_push(temp_token_list, pushDataMap);
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
	 * 공휴일 체크
	 * @param now
	 * @return
	 */
	private String isHoliday(String now) {
		
		System.out.println("isHoliday");
		System.out.println(now);
		
        // 변수 날짜 format
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		ChineseCalendar chinaCal =new ChineseCalendar();
		try {
	        chinaCal.setTime(dateFormat.parse( now ));
	        
	        // 음력 체크 연
	        System.out.println("음력 변환 연");
	        System.out.println(String.format("%02d", chinaCal.get(Calendar.MONTH)+1) + "-" + String.format("%02d", chinaCal.get(ChineseCalendar.DAY_OF_MONTH) ) );
        
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return String.format("%02d", chinaCal.get(Calendar.MONTH)+1) + "-" + String.format("%02d", chinaCal.get(ChineseCalendar.DAY_OF_MONTH) );
	}
	
	/**
	 * 출석체크 여부 조회
	 * attendance_flag : o : 출석목록, x : 결석목록, w : 체크 안한 자녀
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectAttendanceBookKidsCheckOne.do")
	public @ResponseBody Map<?, ?> selectAttendanceBookKidsCheckOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectAttendanceBookKidsCheckOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kids") || !paramMap.containsKey("year") || !paramMap.containsKey("month") || !paramMap.containsKey("day") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("attendance_book", kidsService.selectAttendanceBookKidsCheckOne(paramMap));
			
//			System.out.println( retMap.toString() );
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	private void exifSaveImage(MultipartFile multipartFile, Map<String, Object> paramMap, int cnt, String[] strSplit) throws Exception {
		int orientation;
		Metadata metadata = ImageMetadataReader.readMetadata(multipartFile.getInputStream());
     	ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
     	
     	if( directory==null || directory.getString(ExifSubIFDDirectory.TAG_ORIENTATION) == null  ){
     		orientation=0;
     	} else {
     		orientation=directory.getInt(ExifSubIFDDirectory.TAG_ORIENTATION);
     	}
     	
     	/*System.out.println(""+directory.getTagName(ExifSubIFDDirectory.TAG_ORIENTATION));
     	System.out.println(""+directory.getDescription(ExifSubIFDDirectory.TAG_ORIENTATION));*/
     	
     	int radians;
     	if(orientation == 6){ //정위치
     		radians =  90;
     	} else if (orientation == 1){ //왼쪽으로 눞였을때
     		radians =  0;
     	} else if (orientation == 3){//오른쪽으로 눞였을때
     		radians =  180;
     	} else if (orientation == 8){//180도
     		radians =  270;
     	} else{
     		radians =  0;
     	}
     	
     	BufferedImage oldImage = ImageIO.read(multipartFile.getInputStream());
//     	if (oldImage.getWidth() > 2448 || oldImage.getHeight() > 3264) {
     	BufferedImage newImage;
     	if(radians==90 || radians==270){
     		newImage = new BufferedImage(oldImage.getHeight(),oldImage.getWidth(),oldImage.getType());
     	} else if (radians==180){
     		newImage = new BufferedImage(oldImage.getWidth(),oldImage.getHeight(),oldImage.getType());
     	} else {
     		newImage = oldImage;
     	}
     	
     	Graphics2D graphics = (Graphics2D) newImage.getGraphics();
     	graphics.rotate(Math.toRadians(radians), newImage.getWidth() / 2, newImage.getHeight() / 2);
     	graphics.translate((newImage.getWidth() - oldImage.getWidth()) / 2, (newImage.getHeight() - oldImage.getHeight()) / 2);
     	
     	graphics.drawImage(oldImage, 0, 0, oldImage.getWidth(), oldImage.getHeight(), null);
//     	ByteArrayOutputStream bos = new ByteArrayOutputStream();
     	
     	File dir = new File(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "store_img/" + paramMap.get("seq_store").toString());
     	if(!dir.exists()){
     		dir.mkdirs();
     	}
     	
     	ImageIO.write(newImage, "JPG", new File(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "store_img/" + paramMap.get("seq_store").toString() + "/" + "img_store" + cnt + "."+ strSplit[strSplit.length-1]));
//     	}
     	/*for (Directory directory : metadata.getDirectories()) {
     	    for (Tag tag : directory.getTags()) {
     	        System.out.format("[%s] - %s = %s",
     	            directory.getName(), tag.getTagName(), tag.getDescription());
     	    }
     	    if (directory.hasErrors()) {
     	        for (String error : directory.getErrors()) {
     	            System.err.format("ERROR: %s", error);
     	        }
     	    }
     	}*/
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 일자리 검색
	 * @param model
	 * @param paramMap
	 * @param req
	 * @return 딜러 현황
	 */
	@RequestMapping(value = "/job_list.do")
	public String job_list(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req job_list.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
//			area_category_cd 1차 주소
//			dtl_area_category_cd 2차 주소
//			job_category_cd 1차 직업
//			dtl_job_category_cd 2차 직업
			
			//유효성 검사
			if(paramMap.get("dtl_area_category_cd").toString().equals("")) {
				
			} else {
				
			}
			
			retMap.put("job_list", kidsService.selectJobList(paramMap));
			System.out.println(kidsService.selectJobList(paramMap) );
			
			
			if(paramMap.get("comn").toString().equals("all")) {
				
			} else {
				if(paramMap.get("page") == null || Integer.parseInt( paramMap.get("page").toString()) < 1){
					paramMap.put("page", 1);
				} else {
					paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
				}
			}
			
			
			
			
			
			
			
			
			
			/*
			retMap.put("page", paramMap.get("page") );
			
			if( paramMap.containsKey("keyword") ){
				retMap.put("keyword", paramMap.get("keyword").toString());
				paramMap.put("keyword", "%" + paramMap.get("keyword").toString().trim() + "%");
			}
			
			if(!paramMap.containsKey("seq_user")){
				if ( authentication != null ) {
					User user = (User) authentication.getPrincipal();
					paramMap.put("seq_user", user.getUsername() );
				}
			}
	//		retMap.put("user_ex", userService.selectUserEx(paramMap) );
			retMap.put("dealer_list", agencyService.selectDealerList(paramMap));
			retMap.put("page_count", agencyService.selectDealerCount(paramMap));*/
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		model.addAttribute("retMap", retMap);
		return url + "job_list";
	}
	
	@RequestMapping(value = "/selectDtlJobCategory.do")
	public @ResponseBody Map<?, ?> selectDtlJobCategory(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req selectDtlJobCategory.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			/*if(paramMap.get("")) {
				
			}*/
			
			retMap.put("l_dtl_job_category", kidsService.selectDtlJobCategory(paramMap) );
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	@RequestMapping(value = "/selectDtlAreaCategory.do")
	public @ResponseBody Map<?, ?> selectDtlAreaCategory(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req selectDtlAreaCategory.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			/*if(paramMap.get("")) {
				
			}*/
			
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 딜러 현황
	 * @param model
	 * @param paramMap
	 * @param req
	 * @return 딜러 현황
	 */
	@RequestMapping(value = "/dealerConditions.do")
	public String dealerConditions(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req dealerConditions.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
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
			
			if(!paramMap.containsKey("seq_user")){
				if ( authentication != null ) {
					User user = (User) authentication.getPrincipal();
					paramMap.put("seq_user", user.getUsername() );
				}
			}
	//		retMap.put("user_ex", userService.selectUserEx(paramMap) );
			retMap.put("dealer_list", kidsService.selectDealerList(paramMap));
			retMap.put("page_count", kidsService.selectDealerCount(paramMap));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		model.addAttribute("retMap", retMap);
		return url + "dealerConditions";
	}
	
	
	/**
	 * new btn 업데이트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectCheckNews.do")
	public @ResponseBody Map<?, ?> selectCheckNews(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req selectCheckNews.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if(authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_ADMIN") )){
//				retMap.put("news", adminService.selectCheckNews(paramMap) );
			} else {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user", user.getUsername() );
				
				retMap.put("news", kidsService.selectCheckNews(paramMap) );
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
	 * 총판 비콘현황
	 * @param model
	 * @param params
	 * @param req
	 * @return 총판 비콘현황
	 */
	@RequestMapping(value = "beaconConditions.do")
	public String beaconConditions(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req beaconConditions.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
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
				paramMap.put("seq_user_agency", user.getUsername() );
			}
			
			retMap.put("beacon_list", kidsService.selectBeaconConditionsList(paramMap));
			retMap.put("page_count", kidsService.selectBeaconConditionsCount(paramMap));
			
			retMap.put("dealer_name_list", kidsService.selectDealerNameList(paramMap));
			retMap.put("seq_user_agency",paramMap.get("seq_user_agency").toString()); // agency seq
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		model.addAttribute("retMap", retMap);
		return url + "beaconConditions";
	}
	
	/**
	 * 딜러 등록 페이지
	 * @param model
	 * @param params
	 * @param req
	 * @return 딜러 등록 페이지
	 */
	@RequestMapping(value = "/dealerReg.do")
	public String dealerReg(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req dealerReg.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.isEmpty() ){
//				retMap.put("dealer", dealerService.selectDealerOne(paramMap));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return url + "dealerReg";
	}
	
	/**
	 * 비콘 업데이트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/updateBeacon.do")
	public @ResponseBody Map<?, ?> updateBeacon(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req updateBeacon.do :" + paramMap);
		System.out.println(paramMap.toString());
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user_prt", user.getUsername() );
				if(paramMap.get("seq_user_prt").toString().equals( paramMap.get("seq_user").toString() )){
					paramMap.put("seq_user_prt", 1 );
				}
			}
			System.out.println( paramMap );
			kidsService.updateBeacon(paramMap);
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 본사 gps현황
	 * @param model
	 * @param params
	 * @param req
	 * @return 딜러 gps현황
	 */
	/*@RequestMapping(value = "/gpsConditions.do")
	public String gpsConditions(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req gpsConditions.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
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
			
	//		retMap.put("seq_beacon_req", paramMap.get("seq_beacon_req").toString() );
//			retMap.put("dealer_gps_req_list", dealerService.selectDealerGpsReqList(paramMap));
//			retMap.put("page_count", dealerService.selectDealerGpsReqCount(paramMap));
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		model.addAttribute("retMap", retMap);
		return url + "gpsConditions";
	}
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 캠페인 현황
	 * @param model
	 * @param paramMap
	 * @param req
	 * @return 캠페인 Edit
	 */
	@RequestMapping(value = "/campaignEdit.do")
	public String campaignEdit(Model model, @RequestParam Map<String, Object> paramMap, HttpServletRequest req, Authentication authentication) {
		log.debug("req campaignEdit.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		if ( authentication != null ) {
			User user = (User) authentication.getPrincipal();
			paramMap.put("seq_user", user.getUsername() );
//			model.addAttribute("id","test Doe");
		}
		
		retMap.put("seq_campaign_prdct" ,kidsService.selectSeqCampaignPrdct(paramMap));
		
		if( !StringUtils.isEmpty( (String)retMap.get("seq_campaign_prdct") ) ){
			List<Object> list = new ArrayList<Object>();
			Map<String, Object> temp;
			String prdct_name[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
			Map<String, Object> tt = kidsService.selectCampaignPrdct(paramMap);
			for(int i=0; i<prdct_name.length;i++){
				if(tt.containsKey("title_"+prdct_name[i])){
					temp = new HashMap<String, Object>();
					temp.put("key", prdct_name[i]);
					temp.put("title", tt.get("title_"+prdct_name[i]));
					temp.put("percent", tt.get("percent_"+prdct_name[i]));
					list.add(temp);
					// img percent 추가
				}
			}
			retMap.put("campaign_prdct_list", list);
		}
		
		retMap.put("campaign", kidsService.selectCampaign(paramMap) );
		
		// 업체 보유 동영상 목록 조회
//		retMap.put( "video_list",  agencyService.selectCorpVideo(paramMap));
		 
		
		// arr_video 값 가져와서 tokennize 로 값 나누기 및 count 확인
		// for문으로   video_list 를 List()로 만들면서 containkey 로  arr_video key 값 확인
		// video_list 로  값 내려주고 front 에서 확인
		
		model.addAttribute("retMap", retMap);
		return "web/" + "campaignEdit";
	}
	
	/**
	 * 캠페인 등록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/deleteCampaign.do")
	public @ResponseBody Map<?, ?> deleteCampaign(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req deleteCampaign.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user", user.getUsername() );
//				throw new Exception(Constant.CODE_MISSING_PARAMS);
			}
			
			File file = new File(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + paramMap.get("seq_campaign").toString());
			CommonController.DeleteDir(file);
			
			kidsService.deleteCampaign( paramMap );
			// 캠페인과 관련된 것들 삭제 해야함
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 엑셀 상품 상세내역 확인
	 * @param model
	 * @param params
	 * @param uploadForm
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/excellFileUpload.do")
//	public String noticeImageUpload(Model model, @RequestParam Map<String, Object> params, @RequestParam MultipartFile file) {
	public @ResponseBody Map<?, ?> excellFileUpload(ModelMap model, @RequestParam Map<String, Object> params, @ModelAttribute("uploadForm") FileUploadForm uploadForm, HttpServletRequest req) {
		log.debug("req excellFileUpload.do :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
//			HttpSession ses = req.getSession(false);
//			params.put("mb_no", ses.getAttribute("mb_no"));
			
			List<MultipartFile> files = uploadForm.getFiles();
	        List<String> fileNames = new ArrayList<String>();
	        
	        Map<String, Object> tempMap;
	        Map<String,Integer> arr_prdctSum = new HashMap<String, Integer>();
	        
	        String prdct_name[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
	        for(int i=0;i<=9;i++){
	        	arr_prdctSum.put(prdct_name[i], 0);
	        }
	        
	        if(null != files && files.size() > 0) {
	            for (MultipartFile multipartFile : files) {
 	            	if(multipartFile == null || multipartFile.getSize() == 0) {
	            		continue;
	            	}
// 	            	Workbook wb = new HSSFWorkbook(multipartFile.getInputStream());
 	            	Workbook wb = new XSSFWorkbook(multipartFile.getInputStream());
 	            	Sheet sheet = wb.getSheetAt(0);
 	            	
 	            	int last = sheet.getLastRowNum();
// 	            	System.out.println("last : "+last);
 	            	for(int i=1; i<=last; i++){
 	            		Row row = sheet.getRow(i);
 	            		
 	            		for(int j=0;j<=9;j++){
 	            			arr_prdctSum.put(prdct_name[j], arr_prdctSum.get(prdct_name[j]).intValue() + (int)row.getCell(j+3, Row.CREATE_NULL_AS_BLANK).getNumericCellValue() );
 	            		}
 	            		/*tempMap = new HashMap<String, Object>();
// 	            		System.out.println("test : "+ row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue() );
 	            		tempMap.put("a", row.getCell(3, Row.CREATE_NULL_AS_BLANK).getNumericCellValue());
 	            		list.add(tempMap);*/
 	            	}
 	            	
// 	            	int test = mainService.testExcel(list);
// 	            	System.out.println("test 결과 : "+test);
// 	            	retMap.put("test", list);
	            }
	        }
	        retMap.put("arrSum", arr_prdctSum);
	        model.clear();
	        ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		model.addAttribute("retMap", retMap);
		log.debug("rep setFileUpload.do : " + retMap);
		return model;
	}
	
	/**
	 * 엑셀파일 다운로드
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/getBrchListExcel.do")
	public String getBrchListExcel(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req getBrchListExcel.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user", user.getUsername() );
			}
			retMap.put( "brch_list", kidsService.getBrchListExcel(paramMap) );
			
//			System.out.println(test.toString());
			
//			for(int i=0;i<test.size();i++){}
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return "ExcelView";
	}
	
	
	/** 캠페인 수량 update
	 * @param model
	 * @param paramMap
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/setCampaignUpload.do")
	public @ResponseBody Map<?, ?> setCampaignUpload(ModelMap model, @RequestParam Map<String, Object> paramMap, @ModelAttribute("uploadForm") FileUploadForm uploadForm, HttpServletRequest req) {
		log.debug("req setCampaignUpload.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			List<MultipartFile> files = uploadForm.getFiles();
	        List<String> fileNames = new ArrayList<String>();
	        
			int cnt = 1;
			int spare = 0;
			String prdct_name[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
	        if(null != files && files.size() > 0) {
	            for (MultipartFile multipartFile : files) {
 	            	if(multipartFile == null || multipartFile.getSize() == 0) {
	            		cnt++;
	            		continue;
	            	}
	                String fileName = multipartFile.getOriginalFilename();
	                fileNames.add(fileName);
	                //Handle file content - multipartFile.getInputStream()
	                
	                String strSplit[] = multipartFile.getOriginalFilename().split("[.]");
	                
					if(cnt == 1){
						CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + paramMap.get("seq_campaign") + "/" + "img_campaign" +"."+ strSplit[strSplit.length-1], multipartFile);
						paramMap.put("img_campaign", Constant.IMAGE_PROFILE_IMAGE_RETURN + paramMap.get("seq_campaign") + "\\" + "img_campaign" + "." + strSplit[strSplit.length-1]);
					} else if(cnt == 2){
						CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + paramMap.get("seq_campaign") + "/" + "img_prdct" +"."+ strSplit[strSplit.length-1], multipartFile);
						paramMap.put("img_prdct", Constant.IMAGE_PROFILE_IMAGE_RETURN + paramMap.get("seq_campaign") + "\\" + "img_prdct" + "." + strSplit[strSplit.length-1]);
					} else if(cnt > 2){
						prdctLoop : for(int i=(cnt-3<=spare?spare:cnt-3);i<prdct_name.length;i++ ) {
							if(paramMap.containsKey("title_"+ prdct_name[i]) ){
								CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + paramMap.get("seq_campaign") + "/" + prdct_name[i] +"."+ strSplit[strSplit.length-1], multipartFile);
								paramMap.put("img_"+prdct_name[i], Constant.IMAGE_PROFILE_IMAGE_RETURN + paramMap.get("seq_campaign") + "\\" + prdct_name[i] + "." + strSplit[strSplit.length-1] );
								spare = i+1;
								break prdctLoop;
							}
						}
					}
					cnt++;
	            }
	        }
	        
	        kidsService.updateCampaign(paramMap);
	        
        	if( StringUtils.isEmpty( (String)paramMap.get("seq_campaign_prdct") ) ){
        		kidsService.deleteCampaignPrdct(paramMap);
        		kidsService.insertCampaignPrdct(paramMap);
			} else {
				kidsService.updateCampaignPrdct(paramMap);
			}
        	
	        model.clear();
	        ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		retMap.putAll(paramMap);
		
		model.addAllAttributes(retMap);
		log.debug("rep setCampaignUpload.do : " + retMap);
		return model;
	}
}