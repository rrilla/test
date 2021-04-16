package kr.co.eventroad.admin.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import kr.co.eventroad.admin.common.CommonController;
import kr.co.eventroad.admin.common.Constant;
import kr.co.eventroad.admin.common.FileUploadForm;
import kr.co.eventroad.admin.common.ResultCode;
import kr.co.eventroad.admin.common.RootController;
import kr.co.eventroad.admin.common.ServicesUtils;
import kr.co.eventroad.admin.service.AlbumService;
import kr.co.eventroad.admin.service.UserService;

/**
 * 유저 컨트롤러
 * @author jwdoe
 * @since 2017. 12. 07.
 * @version 1.0.0
 * 마지막 수정 : jwdoe, 2017. 12. 07
 */
@Controller
@SessionAttributes({"id","seq_user"})
//@SessionAttributes("mb_id")

public class UserController extends RootController {

	/**
	 * 유저 서비스
	 */
	@Autowired
	public UserService userService;
	
	@Autowired
	public AlbumService storeService;
	
	/**
	 * 회원가입
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/insertUser.do")
	public @ResponseBody Map<?, ?> insertUser(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req setUserSign.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( paramMap.containsKey("seq_user") || !paramMap.containsKey("authority") || !paramMap.containsKey("password") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			} else {
				
				// 이미 가입한 아이디 확인 seq_user, id 택1
				Map<String, Object> userMap = userService.selectUserIdCheck(paramMap);
				
				if(userMap == null) {
					BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
					// 비대칭 키 암호
					paramMap.put("password", passwordEncoder.encode( paramMap.get("password").toString() ));
					
					// 회원정보 insert
//					 paramMap.put("authority", "ROLE_USER");
					userService.insertSign(paramMap);
			        // 권한 insert
					userService.insertAuth(paramMap);
					
					// 이미지 등록
					List<MultipartFile> files = uploadForm.getFiles();
//					List<String> fileNames = new ArrayList<String>();
					int cnt = 0;
					if(null != files && files.size() > 0) {
						for (MultipartFile multipartFile : files) {
							if(multipartFile == null || multipartFile.getSize() == 0) {
		 	            		cnt++;
			            		continue;
			            	}
		 	            	
			 	            String fileName = multipartFile.getOriginalFilename();
//				            fileNames.add(fileName);
			        		String strSplit[] = multipartFile.getOriginalFilename().split("[.]");
			        		
			        		System.out.println(fileName);
			        		System.out.println(strSplit[strSplit.length-1]);
			        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_user/" + paramMap.get("seq_user").toString() + "/" + fileName, multipartFile);
//			        		exifSaveImage(multipartFile, paramMap, cnt, strSplit);
			        		
			        		paramMap.put("user_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_user/" + paramMap.get("seq_user").toString() + "/" + fileName);
			        		cnt++;
			            }
			        }
					
					// 회원정보 update
					userService.updateUser(paramMap);
					
//			        retMap.put("user", paramMap);
				} else {
					throw new Exception(ResultCode.RESULT_ALREADY_RESGISTERD_USER.getCode());
				}
				
				/*ROLE_DIRECTOR_TEACHER
				ROLE_TEACHER
				ROLE_PARENTS
				ROLE_KIDS*/
			}
			
			model.clear();
//			retMap.putAll(paramMap);
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
	 * 로그인
	 * @param model
	 * @param params
	 * @param req
	 * @return 로그인
	 */
	@RequestMapping(value = "/login.do")
	public @ResponseBody Map<?, ?> login(@RequestParam Map<String, Object> paramMap, ModelMap model) {
		log.debug("req login.do :" + paramMap);
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( paramMap.containsKey("seq_user") || !paramMap.containsKey("password") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			} else {
				// 유효성 체크 확인 로그인시 클라이언트에서 했던 부분중 중요한 부분...
				Map<String, Object> userMap = userService.login(paramMap);
				
				if(userMap == null) {
					throw new Exception(ResultCode.RESULT_WRONG_VALUE.getCode());
				}
				
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				if( !passwordEncoder.matches( paramMap.get("password").toString(), userMap.get("password").toString() ) ){
					throw new Exception(ResultCode.RESULT_WRONG_VALUE.getCode());
				}
				
				userMap.remove("password");
				retMap.put("user", userMap);
			}
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(model, e);
		}
		
		model.addAttribute("retMap", retMap);
		return model;
	}
	
	/**
	 * 회원정보수정(자녀제외)  이미지는 어떻게 처리할 것인지 확인 후
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateUser.do")
	public @ResponseBody Map<?, ?> updateUser(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req updateUser.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_user") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			// 이미지 등록
			List<MultipartFile> files = uploadForm.getFiles();
//			List<String> fileNames = new ArrayList<String>();
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
	        		
//	        		System.out.println(fileName);
//	        		System.out.println(strSplit[strSplit.length-1]);
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_user/" + paramMap.get("seq_user").toString() + "/" + fileName, multipartFile);
//	        		exifSaveImage(multipartFile, paramMap, cnt, strSplit);
	        		
	        		paramMap.put("user_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_user/" + paramMap.get("seq_user").toString() + "/" + fileName);
	        		cnt++;
	            }
	        }
			
			// 회원정보 update
			userService.updateUser(paramMap);
				
				/*File file = new File(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + paramMap.get("seq_campaign").toString());
				CommonController.DeleteDir(file);
				*/
				
				/*storeService.updateStore(paramMap);
				
				storeService.deleteMenu(paramMap);
				
				ArrayList<Map<String, Object>> menu_list = new ArrayList<Map<String, Object>>(); 
				Map<String, Object> tempMap;
				for(int i=0;i<Integer.valueOf(paramMap.get("c_menu").toString());i++ ) {
					tempMap = new HashMap<String, Object>();
					tempMap.put("seq_store", paramMap.get("seq_store"));
					tempMap.put("menu_name", paramMap.get("menu_name["+ i +"]"));
					tempMap.put("menu_price", paramMap.get("menu_price["+ i +"]"));
					
					menu_list.add(tempMap);
				}
				if(menu_list.size()>0){
					storeService.insertMenu(menu_list);
				}*/
				
			
			model.clear();
//			retMap.putAll(paramMap);
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			model.clear();
			procException(model, e);
		}
		
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 유저 비밀번호 변경(암호화 때문에 분리)
	 * @param paramMap
	 * @param model
	 * @param authentication
	 * @return
	 */
	@RequestMapping(value = "/updateUserPassword.do")
	public @ResponseBody Map<?, ?> updateUserPassword(@RequestParam Map<String, Object> paramMap, ModelMap model, Authentication authentication) {
		log.debug("req updateUserPassword.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if(!paramMap.containsKey("seq_user") || !paramMap.containsKey("password") || !paramMap.containsKey("new_password") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			/*if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user", user.getUsername() );
			}*/
			
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
 	        if ( !passwordEncoder.matches(paramMap.get("password").toString(), userService.selectUserPassword(paramMap) ) ){
 	        	throw new Exception(ResultCode.RESULT_WRONG_VALUE.getCode());
 	        }
 	        
 	        paramMap.put("password", passwordEncoder.encode( paramMap.get("new_password").toString() ) );
 	        userService.updateUser(paramMap);
 	        
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 아이디중복체크
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/selectUserReduplicationIdCheck.do")
	public @ResponseBody Map<?, ?> selectUserReduplicationIdCheck(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectUserReduplicationIdCheck.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if(!paramMap.containsKey("id")){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			// 유효성 검사(이미 등록한 cart가 있는지?)
			String userCount = userService.selectUserReduplicationIdCheck(paramMap);
			
			if(!userCount.equals("0")) {
				throw new Exception( ResultCode.RESULT_ALREADY_RESGISTERD_USER.getCode() );
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
	 * 아이디&비밀번호 찾기 이메일 보내기
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 이메일 보내기
	 */
	@RequestMapping(value = "/send_email_forgot_user_info.do")
	public @ResponseBody Map<?, ?> send_email_forgot_user_info(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req send_email_forgot_user_info.do :" + paramMap);		
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if(!paramMap.containsKey("email")){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			List<Object> userList;
			
			/*String temp_arr_params = "";
			
			for( String key : paramMap.keySet() ) {
				temp_arr_params += "'" + paramMap.get(key) + "',"; 
			}
			
			temp_arr_params = temp_arr_params.substring( 0, temp_arr_params.length()-1 );
			paramMap.put("arr_params", temp_arr_params);*/
			
			// list로 해야함 이유: 아이디가 여러개 일 수도 있기 때문
//			Map<String, Object> temp_user_map = userService.selectUserReduplicationIdCheck(paramMap);
			
			System.out.println("");
//			if(size 가 0 일경우 exception  해당정보가 없다)
			
			String temp = "";
			
			if(paramMap.containsKey("id")) {
				// 비밀번호 찾기
				userList = (List<Object>) userService.selectUserInfoList(paramMap);
				
				if(userList.size() == 0) {
					throw new Exception(ResultCode.RESULT_WRONG_VALUE.getCode());
				}
				
				paramMap.put("seq_user", ((Map)userList.get(0)).get("seq_user").toString() );
				
//				랜덤 암호
//				출처: http://seongilman.tistory.com/189 [SEONG]
				StringBuffer buffer = new StringBuffer();
				Random random = new Random();
				String chars[] = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,0,1,2,3,4,5,6,7,8,9".split(",");
				for (int i = 0; i < 4; i++) {
					buffer.append(chars[random.nextInt(chars.length)]);
				}
				
				System.out.println(buffer);
				
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				// 비대칭 키 암호
				paramMap.put("password", passwordEncoder.encode( buffer ));
				
				System.out.println( paramMap );
				userService.updateUser(paramMap);
				
				temp += "<html>";
				temp += "<head><style>";
				temp += "</style></head>";
				temp += "<body>";
				
				temp += "<p style='font-size:15px; font-weight:bold; margin:0 0 12px;'>변경 요청 아이디 : " + paramMap.get("id").toString() + "</p>";
				temp += "<p style='font-size:15px; font-weight:bold; margin:0 0 12px;'>변경 된 비밀번호 : " + buffer + "</p>";
				
				temp += "</body></html>";
			} else {
				// 아이디 찾기
				userList = (List<Object>) userService.selectUserInfoList(paramMap);
				
				if(userList.size() == 0) {
					throw new Exception(ResultCode.RESULT_WRONG_VALUE.getCode());
				}
				
				System.out.println( paramMap );
				
				temp += "<html>";
				temp += "<head><style>";
				temp += "</style></head>";
				temp += "<body>";
				
				for(int i=0; i<userList.size();i++) {
					temp += "<p style='font-size:15px; font-weight:bold; margin:0 0 12px;'>아이디 : " + ((Map)userList.get(i)).get("id").toString() + "</p>";
				}
				
				temp += "</body></html>";
			}
			
			List<Object> l_result;
//			List<Object> l_result = (List<Object>) mainService.getCostEstimateResult(paramMap);
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
			mailSender.setUsername("doejungwoo@gmail.com");
			mailSender.setPassword("rzaccvbucqzzdchn");
//			mailSender.setUsername("golfeunho@gmail.com");
//			mailSender.setPassword("ssarang0316!");
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
			messageHelper.setTo("doejungwoo@gmail.com");
//			messageHelper.setTo("tkdalsgp@naver.com");
			
//			messageHelper.setText("안녕하세요 content");
			messageHelper.setText(temp, true);
			
//			messageHelper.setFrom("golfeunho@gmail.com");
			messageHelper.setFrom("doejungwoo@gmail.com");
			
			if(paramMap.containsKey("id")) {
				messageHelper.setSubject("어부바 비밀번호 초기화");
			} else {
				messageHelper.setSubject("어부바 아이디 찾기");
			}
			
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
	
	/**
	 * 유저 정보 조회
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/selectUserOne.do")
	public @ResponseBody Map<?, ?> selectUserOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectUserOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if(!paramMap.containsKey("seq_user")){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			// 유효성 검사(이미 등록한 cart가 있는지?)
			retMap.put("user", userService.selectUserOne(paramMap));
						
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 탈퇴 처리
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/setMemberLeave.do")
	public @ResponseBody Map<?, ?> setMemberLeave(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req setMemberLeave.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if(!paramMap.containsKey("seq_user") || !paramMap.containsKey("req_flag")) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			if(paramMap.get("req_flag").toString().equals("t")) {
				// tb_apply_m 삭제
				userService.deleteApply(paramMap);
				
				// 차량 seq_user 초기화
				userService.updateCarInit(paramMap);
				
			} else if(paramMap.get("req_flag").toString().equals("p")) {
				if(!paramMap.containsKey("seq_kids")) {
					throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
				}
				// 비콘초기화
				userService.updateBeaconInit(paramMap);
				
				// tb_kids_station_d 삭제
				userService.deleteKidsStationAll(paramMap);
				
				// tb_news_m 삭제
				userService.deleteNewsAll(paramMap);
				
				// tb_apply_m 삭제
				userService.deleteApply(paramMap);
			}
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
}