package kr.co.eventroad.admin.controller;

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
import kr.co.eventroad.admin.service.NoticeService;

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
 * 공지, 설문지 관리자 컨트롤러
 * @author jwdoe
 * @since 2014. 10. 16.
 * @version 1.0.0
 */
@Controller
public class NoticeController extends RootController {

	/**
	 * 공지, 설문지 서비스(자동 할당)
	 */
	@Autowired
	public NoticeService noticeService;

	/**
	 * 공지사항 등록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/insertNotice.do")
	public @ResponseBody Map<?, ?> insertNotice(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req insertNotice.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			if( !paramMap.containsKey("seq_user") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("seq_kindergarden_class") || !paramMap.containsKey("is_reply") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			noticeService.insertNotice(paramMap);
			
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
	        		
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "file_notice/" + paramMap.get("seq_notice").toString() + "/" + fileName, multipartFile);
	        		
	        		paramMap.put("file_path", Constant.IMAGE_PROFILE_IMAGE_RETURN + "file_notice/" + paramMap.get("seq_notice").toString() + "/" + fileName);
	        		cnt++;
	            }
	        }
			
			// 회원정보 update
			noticeService.updateNotice(paramMap);
			
			
			// news insert
			paramMap.put("msg_type", "공지사항 등록");
			paramMap.put("seq", paramMap.get("seq_notice").toString());
			paramMap.put("subtitle", "공지사항");
			paramMap.put("title", paramMap.get("title").toString());
			paramMap.put("seq_user", paramMap.get("seq_user").toString());
			paramMap.put("seq_kids", null);
			paramMap.put("year", null);
			paramMap.put("month", null);
			paramMap.put("day", null);
						
			noticeService.insertNews(paramMap);
			
			// push token list
			List<Object> temp_token_list = (List<Object>) noticeService.selectPushKindergardenClassList(paramMap);
			System.out.println(temp_token_list);
			
			// push
			if(temp_token_list.size() > 0) {
				// param pushParamMap
				Map<String, Object> pushDataMap = new HashMap<String, Object>();
				
				pushDataMap.put("msg_type", "공지사항 등록");
				pushDataMap.put("seq", paramMap.get("seq_notice").toString());
				pushDataMap.put("seq_kindergarden", paramMap.get("seq_kindergarden").toString());
				pushDataMap.put("seq_kindergarden_class", paramMap.get("seq_kindergarden_class").toString());
				pushDataMap.put("seq_kids", null);
				pushDataMap.put("age", null);
				pushDataMap.put("subtitle", "공지사항");
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
	 * 공지사항 수정
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateNotice.do")
	public @ResponseBody Map<?, ?> updateNotice(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req updateNotice.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_notice") ){
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
	        		
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "file_notice/" + paramMap.get("seq_notice").toString() + "/" + fileName, multipartFile);
	        		
	        		paramMap.put("file_path", Constant.IMAGE_PROFILE_IMAGE_RETURN + "file_notice/" + paramMap.get("seq_notice").toString() + "/" + fileName);
	        		cnt++;
	            }
	        }
			
			// 회원정보 update
			noticeService.updateNotice(paramMap);
			
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
	 * 공지사항 삭제
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/deleteNotice.do")
	public @ResponseBody Map<?, ?> deleteNotice(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req deleteNotice.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_notice") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			noticeService.deleteNotice(paramMap);
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
	@RequestMapping(value = "/selectNoticeList.do")
	public @ResponseBody Map<?, ?> selectNoticeList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectNoticeList.do :" + paramMap);
		
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
			
			retMap.put("notice_list", noticeService.selectNoticeList(paramMap));
			
			// 상단 노출
			retMap.put("top_notice_list", noticeService.selectTopNoticeList(paramMap));
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
	 * 수쉰확인시 자녀가 여러명일 수도 있어서 seq_kids 는 있지만 본인 수신확인시 체크안하는걸로 해야함
	 * 원랜 이렇게 해야하는데 이런경우가 거의 없으니 패스
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectNoticeOne.do")
	public @ResponseBody Map<?, ?> selectNoticeOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectNoticeOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_notice") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("notice", noticeService.selectNoticeOne(paramMap));
			
			// 유치원, 유치원 반 시퀀스
			/*paramMap.put("seq_kindergarden", ((Map)retMap.get("notice")).get("seq_kindergarden").toString() );
			paramMap.put("seq_kindergarden_class", ((Map)retMap.get("notice")).get("seq_kindergarden_class").toString() );
			
			// 본인 수신확인 체크
			paramMap.put("seq", paramMap.get("seq_notice").toString());
			paramMap.put("confirm_flag", "n");
			
			if ( paramMap.get("seq_kindergarden_class").toString().equals("0") ) {
				paramMap.remove("seq_kindergarden_class");
			}
			
			// 학부모 수신확인 입력
			if( paramMap.containsKey("seq_kids") && paramMap.containsKey("seq_user") ) {
				Map<String, Object> confirmMap = noticeService.selectConfirmCheck(paramMap);
				
				if(confirmMap == null) {
					noticeService.insertConfirm(paramMap);				
				}
			}
			
			// 전체 학부모&현재 확인한 학부모 여부
			retMap.put("confirm", noticeService.selectConfirmAll(paramMap));*/
			
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
	 * 설문지 등록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/insertSurvey.do")
	public @ResponseBody Map<?, ?> insertSurvey(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req insertSurvey.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("title") || !paramMap.containsKey("content") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			String seq_survey = noticeService.insertSurvey(paramMap);
			
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
//	        		 + "." + strSplit[strSplit.length-1]
	        				 
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_survey/" + paramMap.get("seq_survey").toString() + "/" + fileName, multipartFile);
	        		
	        		paramMap.put("survey_vote_item_img"+cnt, Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_survey/" + paramMap.get("seq_survey").toString() + "/" + fileName);
	        		cnt++;
	            }
	        }
			
			
			/*
			 * 총갯수 : 5
			 * vote 1, vote 2, vote 3, vote 4, vote 5
			 * 투표1,   img,   투표3,    img,   투표5     값이 img일 경우 위에 저장했던 img_count 해서  경로 넣기 끝~
			 * vote[0] , "투표1" 
			 * vote[1] , "image"
			 * vote[2] , "image"
			 */
			
			// c_survey_vote : 투표 총 몇개인지
			// vote_item : 이미지일 경우만 survey_vote_item_img 입력
			
			
			
			Map<String, Object> tempMap;
			int img_cnt = 0;
			for(int i=0;i<Integer.valueOf(paramMap.get("c_survey_vote").toString());i++ ) {  // 설문지 투표 갯수
				if(paramMap.containsKey("vote_item["+ i +"]") ){
					if( paramMap.get("vote_item["+ i +"]") != null && !paramMap.get("vote_item["+ i +"]").equals("") ){
						tempMap = new HashMap<String, Object>();
						if( paramMap.get("vote_item["+ i +"]").equals("survey_vote_item_img") ) {
							tempMap.put("vote_item", paramMap.get("survey_vote_item_img"+img_cnt).toString());
							tempMap.put("vote_flag", "image");
							img_cnt++;
						} else {
							tempMap.put("vote_item", paramMap.get("vote_item["+ i +"]").toString());
							tempMap.put("vote_flag", "text");
						}
						tempMap.put("seq_survey", seq_survey);
						
						noticeService.insertSurveyVoteItem(tempMap);
					}
				}
			}
			
//			noticeService.updateSurvey(paramMap);
			
			// news insert
			paramMap.put("msg_type", "설문지 등록");
			paramMap.put("seq", seq_survey);
			paramMap.put("subtitle", "설문지");
			paramMap.put("title", paramMap.get("title").toString());
			paramMap.put("seq_user", paramMap.get("seq_user").toString());
			paramMap.put("seq_kids", null);
			paramMap.put("year", paramMap.get("year").toString());
			paramMap.put("month", paramMap.get("month").toString());
			paramMap.put("day", paramMap.get("day").toString());
						
			noticeService.insertNews(paramMap);
			
			// push token list
			List<Object> temp_token_list = (List<Object>) noticeService.selectPushKindergardenClassList(paramMap);
			System.out.println(temp_token_list);
			
			// push
			if(temp_token_list.size() > 0) {
				// param pushParamMap
				Map<String, Object> pushDataMap = new HashMap<String, Object>();
				
				pushDataMap.put("msg_type", "설문지 등록");
				pushDataMap.put("seq", seq_survey);
				pushDataMap.put("seq_kindergarden", paramMap.get("seq_kindergarden").toString());
				pushDataMap.put("seq_kindergarden_class", paramMap.get("seq_kindergarden_class").toString());
				pushDataMap.put("seq_kids", null);
				pushDataMap.put("age", null);
				pushDataMap.put("subtitle", "설문지");
				pushDataMap.put("title", paramMap.get("title").toString());
				pushDataMap.put("year", paramMap.get("year").toString());
				pushDataMap.put("month", paramMap.get("month").toString());
				pushDataMap.put("day", paramMap.get("day").toString());
				
				send_push(temp_token_list, pushDataMap);
			}
			
			model.clear();
			retMap.put("seq_kindergarden_class", paramMap.get("seq_kindergarden_class"));
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
	 * 설문지 수정
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateSurvey.do")
	public @ResponseBody Map<?, ?> updateSurvey(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req updateSurvey.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_survey") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			Map<String, Object> survey_vote_map = new HashMap<String, Object>();
			survey_vote_map.put("seq_survey", paramMap.get("seq_survey").toString());
			
			// 설문지 투표가 1개라도 있는지 확인
			int survey_vote_count = noticeService.selectSurveyVoteCheck(survey_vote_map);
			if(survey_vote_count >= 1) {
				throw new Exception(ResultCode.AREADY_INSERT.getCode());
			}
			
			noticeService.updateSurvey(paramMap);
			
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
	        				 
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_survey/" + paramMap.get("seq_survey").toString() + "/" + fileName, multipartFile);
	        		
	        		paramMap.put("survey_vote_item_img"+cnt, Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_survey/" + paramMap.get("seq_survey").toString() + "/" + fileName);
	        		cnt++;
	            }
	        }
			// c_survey_vote : 투표 총 몇개인지
			// vote_item : 이미지일 경우만 survey_vote_item_img 입력
			
			// insert 설문지 투표 항목
			
			noticeService.deleteSurveyVoteItem(paramMap);
			
			Map<String, Object> tempMap;
			int img_cnt = 0;
			for(int i=0;i<Integer.valueOf(paramMap.get("c_survey_vote").toString());i++ ) {  // 설문지 투표 갯수
				if(paramMap.containsKey("vote_item["+ i +"]") ){
					if( paramMap.get("vote_item["+ i +"]") != null && !paramMap.get("vote_item["+ i +"]").equals("") ){
						tempMap = new HashMap<String, Object>();
						if( paramMap.get("vote_item["+ i +"]").equals("survey_vote_item_img") ) {
							tempMap.put("vote_item", paramMap.get("survey_vote_item_img"+img_cnt).toString());
							tempMap.put("vote_flag", "image");
							img_cnt++;
						} else {
							tempMap.put("vote_item", paramMap.get("vote_item["+ i +"]").toString());
							tempMap.put("vote_flag", "text");
						}
						tempMap.put("seq_survey", paramMap.get("seq_survey").toString());
						
						noticeService.insertSurveyVoteItem(tempMap);
					}
				}
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
	 * 투약의뢰서 삭제
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/deleteSurvey.do")
	public @ResponseBody Map<?, ?> deleteSurvey(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req deleteSurvey.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_survey") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			noticeService.deleteSurvey(paramMap);
			// 설문 아이템, 투표 했던 것들 삭제해야함
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 설문지 리스트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectSurveyList.do")
	public @ResponseBody Map<?, ?> selectSurveyList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectSurveyList.do :" + paramMap);
		
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
			
			retMap.put("survey_list", noticeService.selectSurveyList(paramMap));
			
			// 상단 노출 설문지 리스트
			retMap.put("top_survey_list", noticeService.selectTopSurveyList(paramMap));
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 설문지 상세
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectSurveyOne.do")
	public @ResponseBody Map<?, ?> selectSurveyOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectSurveyOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
//			if( !paramMap.containsKey("seq_survey") || !paramMap.containsKey("seq_kids") || !paramMap.containsKey("seq_user") ){
			if( !paramMap.containsKey("seq_survey") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			
			Map<String, Object> surveyMap = noticeService.selectSurveyOne(paramMap);
			
			retMap.put("survey", surveyMap);
			retMap.put("survey_vote_item_list", noticeService.selectSurveyVoteItemList(paramMap));
			
			if(surveyMap != null) {
			
				// 유치원, 유치원 반 시퀀스
				paramMap.put("seq_kindergarden", ((Map)retMap.get("survey")).get("seq_kindergarden").toString() );
				paramMap.put("seq_kindergarden_class", ((Map)retMap.get("survey")).get("seq_kindergarden_class").toString() );
				
				// 본인 수신확인 체크
				paramMap.put("seq", paramMap.get("seq_survey").toString());
				paramMap.put("confirm_flag", "s");
				
				if ( paramMap.get("seq_kindergarden_class").toString().equals("0") ) {
					paramMap.remove("seq_kindergarden_class");
				}
				
				// 학부모 권한
				if( paramMap.containsKey("seq_kids") && paramMap.containsKey("seq_user") ) {
					Map<String, Object> confirmMap = noticeService.selectConfirmCheck(paramMap);
					
					if(confirmMap == null) {
						noticeService.insertConfirm(paramMap);				
					}
					
					// 설문지 투표 여부
					/*int survey_vote_count = noticeService.selectSurveyVoteCheck(paramMap);
					if(survey_vote_count == 0) {
						((Map)retMap.get("survey")).put("survey_vote_yn", 'n');
					} else {
						((Map)retMap.get("survey")).put("survey_vote_yn", 'y');
					}*/
				}
				
				// 전체 학부모&현재 확인한 학부모 여부
				retMap.put("confirm", noticeService.selectConfirmAll(paramMap));
				
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
	 * 교육계획안 등록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 * 
	 * plan_flag : w: 주 , m : 월
	 */
	@RequestMapping(value = "/insertEducationalPlan.do")
	public @ResponseBody Map<?, ?> insertEducationalPlan(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req insertEducationalPlan.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
//			!paramMap.containsKey("seq_kindergarden_class") || 
			if( !paramMap.containsKey("seq_user") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("plan_flag") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			noticeService.insertEducationalPlan(paramMap);
			
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
	        		
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "file_educational_plan/" + paramMap.get("seq_educational_plan").toString() + "/" + fileName, multipartFile);
	        		
	        		paramMap.put("file_path", Constant.IMAGE_PROFILE_IMAGE_RETURN + "file_educational_plan/" + paramMap.get("seq_educational_plan").toString() + "/" + fileName);
	        		cnt++;
	            }
	        }
			
			// 회원정보 update
			noticeService.updateEducationalPlan(paramMap);
			
			
			// news insert
			paramMap.put("msg_type", "교육계획안 등록");
			paramMap.put("seq", paramMap.get("seq_educational_plan").toString());
			paramMap.put("subtitle", "교육계획안");
			paramMap.put("title", paramMap.get("title").toString());
			paramMap.put("seq_user", paramMap.get("seq_user").toString());
			paramMap.put("seq_kids", null);
			paramMap.put("year", null);
			paramMap.put("month", null);
			paramMap.put("day", null);
						
			noticeService.insertNews(paramMap);
			
			// push token list
			List<Object> temp_token_list = (List<Object>) noticeService.selectPushKindergardenClassList(paramMap);
			System.out.println(temp_token_list);
			
			// push
			if(temp_token_list.size() > 0) {
				// param pushParamMap
				Map<String, Object> pushDataMap = new HashMap<String, Object>();
				
				pushDataMap.put("msg_type", "교육계획안 등록");
				pushDataMap.put("seq", paramMap.get("seq_educational_plan").toString());
				pushDataMap.put("seq_kindergarden", paramMap.get("seq_kindergarden").toString());
				pushDataMap.put("seq_kindergarden_class", paramMap.get("seq_kindergarden_class").toString());
				pushDataMap.put("seq_kids", null);
				pushDataMap.put("age", null);
				pushDataMap.put("subtitle", "교육계획안");
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
	 * 교육계획안 수정
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateEducationalPlan.do")
	public @ResponseBody Map<?, ?> updateEducationalPlan(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req updateEducationalPlan.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_educational_plan") ){
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
	        		
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "file_educational_plan/" + paramMap.get("seq_educational_plan").toString() + "/" + fileName, multipartFile);
	        		
	        		paramMap.put("file_path", Constant.IMAGE_PROFILE_IMAGE_RETURN + "file_educational_plan/" + paramMap.get("seq_educational_plan").toString() + "/" + fileName);
	        		cnt++;
	            }
	        }
			
			// 회원정보 update
			noticeService.updateEducationalPlan(paramMap);
			
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
	 * 교육계획안 삭제
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/deleteEducationalPlan.do")
	public @ResponseBody Map<?, ?> deleteEducationalPlan(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req deleteEducationalPlan.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_educational_plan") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			noticeService.deleteEducationalPlan(paramMap);
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 교육계획안 리스트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectEducationalPlanList.do")
	public @ResponseBody Map<?, ?> selectEducationalPlanList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectEducationalPlanList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("plan_flag") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1) {
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			
			retMap.put("educational_plan_list", noticeService.selectEducationalPlanList(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 교육계획안 상세
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectEducationalPlanOne.do")
	public @ResponseBody Map<?, ?> selectEducationalPlanOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectEducationalPlanOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_educational_plan") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("educational_plan", noticeService.selectEducationalPlanOne(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 투약의뢰서 등록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 * 
	 * seq_user_parent : 신청학부모 , seq_user_teacher : 수락 선생님
	 */
	@RequestMapping(value = "/insertMedicationRequest.do")
	public @ResponseBody Map<?, ?> insertMedicationRequest(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req insertMedicationRequest.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("seq_kindergarden_class") || !paramMap.containsKey("seq_user_parent") || !paramMap.containsKey("seq_kids") || !paramMap.containsKey("year") || !paramMap.containsKey("month") || !paramMap.containsKey("day") || !paramMap.containsKey("kids_name") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			noticeService.insertMedicationRequest(paramMap);
			
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
	        		
	        		if(cnt == 0) {
		        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_medication_request/" + paramMap.get("seq_medication_request").toString() + "/" + fileName, multipartFile);
		        		paramMap.put("sign_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_medication_request/" + paramMap.get("seq_medication_request").toString() + "/" + fileName);
		        	} else if(cnt == 1) {
		        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_medication_request/" + paramMap.get("seq_medication_request").toString() + "/" + fileName, multipartFile);
		        		paramMap.put("append_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_medication_request/" + paramMap.get("seq_medication_request").toString() + "/" + fileName);
		        	}
		        	cnt++;
	        		
//	        		System.out.println(fileName);
//	        		System.out.println(strSplit[strSplit.length-1]);
	            }
	        }
			
			// 회원정보 update
			noticeService.updateMedicationRequest(paramMap);
			
			// 알림등록 & 푸쉬
			/*paramMap.put("news_title", "투약의뢰서가 등록되었습니다");
			paramMap.put("seq", paramMap.get("seq_medication_request").toString());
			paramMap.put("seq_name", "seq_medication_request");
			paramMap.put("api", "selectMedicationRequestOne");*/
			
			// news insert
			paramMap.put("msg_type", "투약의뢰서 등록");
			paramMap.put("seq", paramMap.get("seq_medication_request").toString());
			paramMap.put("subtitle", paramMap.get("kids_name").toString() + "의 투약의뢰서가 등록되었습니다.");
			paramMap.put("seq_user", paramMap.get("seq_user_parent").toString());
			paramMap.put("seq_kids", paramMap.get("seq_kids").toString());
			paramMap.put("year", null);
			paramMap.put("month", null);
			paramMap.put("day", null);
						
			noticeService.insertNews(paramMap);
			
			// push token list
			List<Object> temp_token_list = (List<Object>) noticeService.selectPushKidsTeacherList(paramMap);
			System.out.println(temp_token_list);
			
			// push
			if(temp_token_list.size() > 0) {
				// param pushParamMap
				Map<String, Object> pushDataMap = new HashMap<String, Object>();
				
				pushDataMap.put("msg_type", "투약의뢰서 등록");
				pushDataMap.put("seq", paramMap.get("seq_medication_request").toString());
				pushDataMap.put("seq_kindergarden", null);
				pushDataMap.put("seq_kindergarden_class", null);
				pushDataMap.put("seq_kids", null);
				pushDataMap.put("age", null);
				pushDataMap.put("subtitle", paramMap.get("kids_name").toString() + "의 투약의뢰서가 등록되었습니다.");
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
	 * 투약의뢰서 수정
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateMedicationRequest.do")
	public @ResponseBody Map<?, ?> updateMedicationRequest(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req updateMedicationRequest.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_medication_request") || !paramMap.containsKey("seq_kids") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("seq_kindergarden_class") ){
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
	        		
	        		if(cnt == 0) {
		        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_medication_request/" + paramMap.get("seq_medication_request").toString() + "/" + fileName, multipartFile);
		        		paramMap.put("sign_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_medication_request/" + paramMap.get("seq_medication_request").toString() + "/" + fileName);
		        	} else if(cnt == 1) {
		        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_medication_request/" + paramMap.get("seq_medication_request").toString() + "/" + fileName, multipartFile);
		        		paramMap.put("append_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_medication_request/" + paramMap.get("seq_medication_request").toString() + "/" + fileName);
		        	}
		        	cnt++;
	        		
//				        		System.out.println(fileName);
//				        		System.out.println(strSplit[strSplit.length-1]);
	            }
	        }
			
			// 투약의뢰서 update
			noticeService.updateMedicationRequest(paramMap);
			
			if(paramMap.containsKey("seq_user_teacher")) {
				/*// news insert
				paramMap.put("msg_type", "교사 투약의뢰서 처리");
				paramMap.put("seq", paramMap.get("seq_medication_request").toString());
				paramMap.put("subtitle", paramMap.get("kids_name").toString() + "의 투약을 완료 하였습니다.");
				paramMap.put("seq_user", paramMap.get("seq_user_teacher").toString());
				paramMap.put("seq_kids", paramMap.get("seq_kids").toString());
				paramMap.put("year", null);
				paramMap.put("month", null);
				paramMap.put("day", null);
							
				noticeService.insertNews(paramMap);
				
				// push token list
				List<Object> temp_token_list = (List<Object>) noticeService.selectPushKidsOneToList(paramMap);
				System.out.println(temp_token_list);
				
				// push
				if(temp_token_list.size() > 0) {
					// param pushParamMap
					Map<String, Object> pushDataMap = new HashMap<String, Object>();
					
					pushDataMap.put("msg_type", "교사 투약의뢰서 처리");
					pushDataMap.put("seq", paramMap.get("seq_medication_request").toString());
					pushDataMap.put("seq_kindergarden", null);
					pushDataMap.put("seq_kindergarden_class", null);
					pushDataMap.put("seq_kids", paramMap.get("seq_kids").toString());
					pushDataMap.put("age", null);
					pushDataMap.put("subtitle", paramMap.get("kids_name").toString() + "의 투약을 완료 하였습니다.");
					pushDataMap.put("year", null);
					pushDataMap.put("month", null);
					pushDataMap.put("day", null);
					
					send_push(temp_token_list, pushDataMap);
				}*/
			} else { // 학부모 수정
				// news insert
				paramMap.put("msg_type", "투약의뢰서 수정");
				paramMap.put("seq", paramMap.get("seq_medication_request").toString());
				paramMap.put("subtitle", paramMap.get("kids_name").toString() + " 투약의뢰서가 수정되었습니다.");
				paramMap.put("seq_user", paramMap.get("seq_user_parent").toString());
				paramMap.put("seq_kids", paramMap.get("seq_kids").toString());
				paramMap.put("year", null);
				paramMap.put("month", null);
				paramMap.put("day", null);
							
				noticeService.insertNews(paramMap);
				
				// push token list
				List<Object> temp_token_list = (List<Object>) noticeService.selectPushKidsTeacherList(paramMap);
				System.out.println(temp_token_list);
				
				// push
				if(temp_token_list.size() > 0) {
					// param pushParamMap
					Map<String, Object> pushDataMap = new HashMap<String, Object>();
					
					pushDataMap.put("msg_type", "투약의뢰서 수정");
					pushDataMap.put("seq", paramMap.get("seq_medication_request").toString());
					pushDataMap.put("seq_kindergarden", null);
					pushDataMap.put("seq_kindergarden_class", null);
					pushDataMap.put("seq_kids", null);
					pushDataMap.put("age", null);
					pushDataMap.put("subtitle", paramMap.get("kids_name").toString() + " 투약의뢰서가 수정되었습니다.");
					pushDataMap.put("year", null);
					pushDataMap.put("month", null);
					pushDataMap.put("day", null);
					
					send_push(temp_token_list, pushDataMap);
				}
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
	 * 투약의뢰서 삭제
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/deleteMedicationRequest.do")
	public @ResponseBody Map<?, ?> deleteMedicationRequest(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req deleteMedicationRequest.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_medication_request") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			// select medicationRequestMap
			Map<String, Object> medicationRequestMap = noticeService.selectMedicationRequestOne(paramMap);
			paramMap.put("seq_kindergarden", medicationRequestMap.get("seq_kindergarden").toString());
			paramMap.put("seq_kindergarden_class", medicationRequestMap.get("seq_kindergarden_class").toString());
			
			noticeService.deleteMedicationRequest(paramMap);
			
			// news insert
			paramMap.put("msg_type", "투약의뢰서 삭제");
			paramMap.put("seq", null);
			paramMap.put("subtitle", medicationRequestMap.get("kids_name").toString() + " 투약의뢰서가 취소되었습니다.");
			paramMap.put("seq_user", medicationRequestMap.get("seq_user_parent").toString());
			paramMap.put("seq_kids", medicationRequestMap.get("seq_kids").toString());
			paramMap.put("year", null);
			paramMap.put("month", null);
			paramMap.put("day", null);
						
			noticeService.insertNews(paramMap);
			
			// push token list
			List<Object> temp_token_list = (List<Object>) noticeService.selectPushKidsTeacherList(paramMap);
			System.out.println(temp_token_list);
			
			// push
			if(temp_token_list.size() > 0) {
				// param pushParamMap
				Map<String, Object> pushDataMap = new HashMap<String, Object>();
				
				pushDataMap.put("msg_type", "투약의뢰서 삭제");
				pushDataMap.put("seq", null);
				pushDataMap.put("seq_kindergarden", null);
				pushDataMap.put("seq_kindergarden_class", null);
				pushDataMap.put("seq_kids", null);
				pushDataMap.put("age", null);
				pushDataMap.put("subtitle", medicationRequestMap.get("kids_name").toString() + " 투약의뢰서가 취소되었습니다.");
				pushDataMap.put("year", null);
				pushDataMap.put("month", null);
				pushDataMap.put("day", null);
				
				send_push(temp_token_list, pushDataMap);
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
	 * 투약의뢰서 리스트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectMedicationRequestList.do")
	public @ResponseBody Map<?, ?> selectMedicationRequestList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectMedicationRequestList.do :" + paramMap);
		
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
			
			retMap.put("medication_request_list", noticeService.selectMedicationRequestList(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 투약의뢰서 상세
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectMedicationRequestOne.do")
	public @ResponseBody Map<?, ?> selectMedicationRequestOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectMedicationRequestOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_medication_request") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("medication_request", noticeService.selectMedicationRequestOne(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 귀가동의서 등록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 * 
	 * seq_user_parent : 신청학부모 , seq_user_teacher : 수락 선생님
	 */
	@RequestMapping(value = "/insertHomeRequest.do")
	public @ResponseBody Map<?, ?> insertHomeRequest(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req insertHomeRequest.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("seq_kindergarden_class") || !paramMap.containsKey("seq_user_parent") || !paramMap.containsKey("seq_kids") || !paramMap.containsKey("year") || !paramMap.containsKey("month") || !paramMap.containsKey("day")|| !paramMap.containsKey("kids_name") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			int home_request_count = noticeService.checkHomeRequestCount(paramMap);
			if(home_request_count > 0) {
				throw new Exception(ResultCode.AREADY_INSERT.getCode());
			}
			
			noticeService.insertHomeRequest(paramMap);
			
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
	        		
	        		if(cnt == 0) {
		        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_home_request/" + paramMap.get("seq_home_request").toString() + "/" + fileName, multipartFile);
		        		paramMap.put("sign_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_home_request/" + paramMap.get("seq_home_request").toString() + "/" + fileName);
		        	} else if(cnt == 1) {
		        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_home_request/" + paramMap.get("seq_home_request").toString() + "/" + fileName, multipartFile);
		        		paramMap.put("append_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_home_request/" + paramMap.get("seq_home_request").toString() + "/" + fileName);
		        	}
		        	cnt++;
	        		
//	        		System.out.println(fileName);
//	        		System.out.println(strSplit[strSplit.length-1]);
	            }
	        }
			
			// 귀가동의서 update
			noticeService.updateHomeRequest(paramMap);
			
			
			// news insert
			paramMap.put("msg_type", "귀가동의서 등록");
			paramMap.put("seq", paramMap.get("seq_home_request").toString());
			paramMap.put("subtitle", paramMap.get("kids_name").toString() + " 귀가동의서가 등록되었습니다.");
			paramMap.put("seq_user", paramMap.get("seq_user_parent").toString());
			paramMap.put("seq_kids", paramMap.get("seq_kids").toString());
			paramMap.put("year", null);
			paramMap.put("month", null);
			paramMap.put("day", null);
						
			noticeService.insertNews(paramMap);
			
			// push token list
			List<Object> temp_token_list = (List<Object>) noticeService.selectPushKidsTeacherList(paramMap);
			System.out.println(temp_token_list);
			
			// push
			if(temp_token_list.size() > 0) {
				// param pushParamMap
				Map<String, Object> pushDataMap = new HashMap<String, Object>();
				
				pushDataMap.put("msg_type", "귀가동의서 등록");
				pushDataMap.put("seq", paramMap.get("seq_home_request").toString());
				pushDataMap.put("seq_kindergarden", null);
				pushDataMap.put("seq_kindergarden_class", null);
				pushDataMap.put("seq_kids", null);
				pushDataMap.put("age", null);
				pushDataMap.put("subtitle", paramMap.get("kids_name").toString() + " 귀가동의서가 등록되었습니다.");
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
	 * 귀가동의서 수정
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateHomeRequest.do")
	public @ResponseBody Map<?, ?> updateHomeRequest(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req updateHomeRequest.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_home_request") || !paramMap.containsKey("seq_kids") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("seq_kindergarden_class") ){
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
	        		
	        		if(cnt == 0) {
		        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_home_request/" + paramMap.get("seq_home_request").toString() + "/" + fileName, multipartFile);
		        		paramMap.put("sign_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_home_request/" + paramMap.get("seq_home_request").toString() + "/" + fileName);
		        	} else if(cnt == 1) {
		        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "img_home_request/" + paramMap.get("seq_home_request").toString() + "/" + fileName, multipartFile);
		        		paramMap.put("append_image", Constant.IMAGE_PROFILE_IMAGE_RETURN + "img_home_request/" + paramMap.get("seq_home_request").toString() + "/" + fileName);
		        	}
		        	cnt++;
	        		
//				    System.out.println(fileName);
//				    System.out.println(strSplit[strSplit.length-1]);
	            }
	        }
			
			// 귀가동의서 update
			noticeService.updateHomeRequest(paramMap);
			
			if(paramMap.containsKey("seq_user_teacher")) {
				/*// news insert
				paramMap.put("msg_type", "교사 귀가동의서 처리");
				paramMap.put("seq", paramMap.get("seq_home_request").toString());
				paramMap.put("subtitle", paramMap.get("kids_name").toString() + "의 귀가를 완료 하였습니다.");
				paramMap.put("seq_user", paramMap.get("seq_user_teacher").toString());
				paramMap.put("seq_kids", paramMap.get("seq_kids").toString());
				paramMap.put("year", null);
				paramMap.put("month", null);
				paramMap.put("day", null);
				
				noticeService.insertNews(paramMap);
				
				// push token list
				List<Object> temp_token_list = (List<Object>) noticeService.selectPushKidsOneToList(paramMap);
				System.out.println(temp_token_list);
				
				// push
				if(temp_token_list.size() > 0) {
					// param pushParamMap
					Map<String, Object> pushDataMap = new HashMap<String, Object>();
					
					pushDataMap.put("msg_type", "교사 귀가동의서 처리");
					pushDataMap.put("seq", paramMap.get("seq_home_request").toString());
					pushDataMap.put("seq_kindergarden", null);
					pushDataMap.put("seq_kindergarden_class", null);
					pushDataMap.put("seq_kids", paramMap.get("seq_kids").toString());
					pushDataMap.put("age", null);
					pushDataMap.put("subtitle", paramMap.get("kids_name").toString() + "의 귀가를 완료 하였습니다.");
					pushDataMap.put("year", null);
					pushDataMap.put("month", null);
					pushDataMap.put("day", null);
					
					send_push(temp_token_list, pushDataMap);
				}*/
			} else { // 학부모 수정
				// news insert
				paramMap.put("msg_type", "귀가동의서 수정");
				paramMap.put("seq", paramMap.get("seq_home_request").toString());
				paramMap.put("subtitle", paramMap.get("kids_name").toString() + " 귀가동의서가 수정되었습니다.");
				paramMap.put("seq_user", paramMap.get("seq_user_parent").toString());
				paramMap.put("seq_kids", paramMap.get("seq_kids").toString());
				paramMap.put("year", null);
				paramMap.put("month", null);
				paramMap.put("day", null);
							
				noticeService.insertNews(paramMap);
				
				// push token list
				List<Object> temp_token_list = (List<Object>) noticeService.selectPushKidsTeacherList(paramMap);
				System.out.println(temp_token_list);
				
				// push
				if(temp_token_list.size() > 0) {
					// param pushParamMap
					Map<String, Object> pushDataMap = new HashMap<String, Object>();
					
					pushDataMap.put("msg_type", "귀가동의서 수정");
					pushDataMap.put("seq", paramMap.get("seq_home_request").toString());
					pushDataMap.put("seq_kindergarden", null);
					pushDataMap.put("seq_kindergarden_class", null);
					pushDataMap.put("seq_kids", null);
					pushDataMap.put("age", null);
					pushDataMap.put("subtitle", paramMap.get("kids_name").toString() + " 귀가동의서가 수정되었습니다.");
					pushDataMap.put("year", null);
					pushDataMap.put("month", null);
					pushDataMap.put("day", null);
					
					send_push(temp_token_list, pushDataMap);
				}
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
	 * 귀가동의서 삭제
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/deleteHomeRequest.do")
	public @ResponseBody Map<?, ?> deleteHomeRequest(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req deleteHomeRequest.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_home_request") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			// selectHomeRequestMap
			Map<String, Object> homeRequestMap = noticeService.selectHomeRequestOne(paramMap);
			paramMap.put("seq_kindergarden", homeRequestMap.get("seq_kindergarden").toString());
			paramMap.put("seq_kindergarden_class", homeRequestMap.get("seq_kindergarden_class").toString());
			
			noticeService.deleteHomeRequest(paramMap);
			
			// news insert
			paramMap.put("msg_type", "귀가동의서 삭제");
			paramMap.put("seq", null);
			paramMap.put("subtitle", homeRequestMap.get("kids_name").toString() + " 귀가동의서가 취소되었습니다.");
			paramMap.put("seq_user", homeRequestMap.get("seq_user_parent").toString());
			paramMap.put("seq_kids", homeRequestMap.get("seq_kids").toString());
			paramMap.put("year", null);
			paramMap.put("month", null);
			paramMap.put("day", null);
						
			noticeService.insertNews(paramMap);
			
			// push token list
			List<Object> temp_token_list = (List<Object>) noticeService.selectPushKidsTeacherList(paramMap);
			System.out.println(temp_token_list);
			
			// push
			if(temp_token_list.size() > 0) {
				// param pushParamMap
				Map<String, Object> pushDataMap = new HashMap<String, Object>();
				
				pushDataMap.put("msg_type", "귀가동의서 삭제");
				pushDataMap.put("seq", null);
				pushDataMap.put("seq_kindergarden", null);
				pushDataMap.put("seq_kindergarden_class", null);
				pushDataMap.put("seq_kids", null);
				pushDataMap.put("age", null);
				pushDataMap.put("subtitle", homeRequestMap.get("kids_name").toString() + " 귀가동의서가 취소되었습니다.");
				pushDataMap.put("year", null);
				pushDataMap.put("month", null);
				pushDataMap.put("day", null);
				
				send_push(temp_token_list, pushDataMap);
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
	 * 귀가동의서 리스트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectHomeRequestList.do")
	public @ResponseBody Map<?, ?> selectHomeRequestList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectHomeRequestList.do :" + paramMap);
		
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
			
			if ( paramMap.get("seq_kindergarden_class").toString().equals("0") ) {
				paramMap.remove("seq_kindergarden_class");
			}
			
			retMap.put("home_request_list", noticeService.selectHomeRequestList(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 귀가동의서 상세
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectHomeRequestOne.do")
	public @ResponseBody Map<?, ?> selectHomeRequestOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectHomeRequestOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_home_request") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("home_request", noticeService.selectHomeRequestOne(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 일정관리 등록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/insertScheduleManagement.do")
	public @ResponseBody Map<?, ?> insertScheduleManagement(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req insertScheduleManagement.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
//			!paramMap.containsKey("seq_kindergarden_class") || 
			if( !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("seq_user") || !paramMap.containsKey("start_year") || !paramMap.containsKey("end_year") || !paramMap.containsKey("end_day") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			noticeService.insertScheduleManagement(paramMap);
			
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
	        		
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "file_schedule_management/" + paramMap.get("seq_schedule_management").toString() + "/" + fileName, multipartFile);
	        		paramMap.put("file_path", Constant.IMAGE_PROFILE_IMAGE_RETURN + "file_schedule_management/" + paramMap.get("seq_schedule_management").toString() + "/" + fileName);
		        	cnt++;
	        		
//	        		System.out.println(fileName);
//	        		System.out.println(strSplit[strSplit.length-1]);
	            }
	        }
			
			// 회원정보 update
			noticeService.updateScheduleManagement(paramMap);
			
			
			// news insert
			paramMap.put("msg_type", "일정관리 등록");
			paramMap.put("seq", paramMap.get("seq_schedule_management").toString());
			paramMap.put("subtitle", "일정표");
			paramMap.put("title", paramMap.get("title").toString());
			paramMap.put("seq_user", paramMap.get("seq_user").toString());
			paramMap.put("seq_kids", null);
			paramMap.put("year", null);
			paramMap.put("month", null);
			paramMap.put("day", null);
						
			noticeService.insertNews(paramMap);
			
			// push token list
			List<Object> temp_token_list = (List<Object>) noticeService.selectPushKindergardenClassList(paramMap);
			System.out.println(temp_token_list);
			
			// push
			if(temp_token_list.size() > 0) {
				// param pushParamMap
				Map<String, Object> pushDataMap = new HashMap<String, Object>();
				
				pushDataMap.put("msg_type", "일정관리 등록");
				pushDataMap.put("seq", paramMap.get("seq_schedule_management").toString());
				pushDataMap.put("seq_kindergarden", paramMap.get("seq_kindergarden").toString());
				pushDataMap.put("seq_kindergarden_class", null);
				pushDataMap.put("seq_kids", null);
				pushDataMap.put("age", null);
				pushDataMap.put("subtitle", "일정표");
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
	 * 일정관리 수정
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateScheduleManagement.do")
	public @ResponseBody Map<?, ?> updateScheduleManagement(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req updateScheduleManagement.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_schedule_management") ){
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
	        		
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "file_schedule_management/" + paramMap.get("seq_schedule_management").toString() + "/" + fileName, multipartFile);
	        		paramMap.put("file_path", Constant.IMAGE_PROFILE_IMAGE_RETURN + "file_schedule_management/" + paramMap.get("seq_schedule_management").toString() + "/" + fileName);
		        	cnt++;
	            }
	        }
			
			// 일정관리 update
			noticeService.updateScheduleManagement(paramMap);
			
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
	 * 일정관리 삭제
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/deleteScheduleManagement.do")
	public @ResponseBody Map<?, ?> deleteScheduleManagement(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req deleteScheduleManagement.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_schedule_management") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			noticeService.deleteScheduleManagement(paramMap);
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 일정관리 리스트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectScheduleManagementList.do")
	public @ResponseBody Map<?, ?> selectScheduleManagementList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectScheduleManagementList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("year") || !paramMap.containsKey("month") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			paramMap.put("yearmonth", paramMap.get("year").toString()+paramMap.get("month").toString());
			
			retMap.put("schedule_management_list", noticeService.selectScheduleManagementList(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 일정관리 상세
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectScheduleManagementOne.do")
	public @ResponseBody Map<?, ?> selectScheduleManagementOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectScheduleManagementOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_schedule_management") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("schedule_management", noticeService.selectScheduleManagementOne(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 식단표 등록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/insertMenuManagement.do")
	public @ResponseBody Map<?, ?> insertMenuManagement(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req insertMenuManagement.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
//			!paramMap.containsKey("seq_kindergarden_class") || 
			if( !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("seq_user") || !paramMap.containsKey("year") || !paramMap.containsKey("month") || !paramMap.containsKey("day") ) {
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			noticeService.insertMenuManagement(paramMap);
			
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
	        		
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "file_menu_management/" + paramMap.get("seq_menu_management").toString() + "/" + fileName, multipartFile);
	        		paramMap.put("file_path", Constant.IMAGE_PROFILE_IMAGE_RETURN + "file_menu_management/" + paramMap.get("seq_menu_management").toString() + "/" + fileName);
		        	cnt++;
	        		
//	        		System.out.println(fileName);
//	        		System.out.println(strSplit[strSplit.length-1]);
	            }
	        }
			
			// 회원정보 update
			noticeService.updateMenuManagement(paramMap);
			
			
			// news insert
			paramMap.put("msg_type", "식단표 등록");
			paramMap.put("seq", paramMap.get("seq_menu_management").toString());
			paramMap.put("subtitle", "식단표가 등록되었습니다.");
			paramMap.put("seq_user", paramMap.get("seq_user").toString());
			paramMap.put("seq_kids", null);
			paramMap.put("year", null);
			paramMap.put("month", null);
			paramMap.put("day", null);
						
			noticeService.insertNews(paramMap);
			
			paramMap.put("seq_kindergarden_class", "0");
			// push token list
			List<Object> temp_token_list = (List<Object>) noticeService.selectPushKindergardenClassList(paramMap);
			System.out.println(temp_token_list);
			
			// push
			if(temp_token_list.size() > 0) {
				// param pushParamMap
				Map<String, Object> pushDataMap = new HashMap<String, Object>();
				
				pushDataMap.put("msg_type", "식단표 등록");
				pushDataMap.put("seq", paramMap.get("seq_menu_management").toString());
				pushDataMap.put("seq_kindergarden", paramMap.get("seq_kindergarden").toString());
				pushDataMap.put("seq_kindergarden_class", null);
				pushDataMap.put("seq_kids", null);
				pushDataMap.put("age", null);
				pushDataMap.put("subtitle", "식단표가 등록되었습니다.");
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
	 * 식단표 수정
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return 
	 */
	@RequestMapping(value = "/updateMenuManagement.do")
	public @ResponseBody Map<?, ?> updateMenuManagement(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req updateMenuManagement.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_menu_management") ){
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
	        		
	        		CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + "file_menu_management/" + paramMap.get("seq_menu_management").toString() + "/" + fileName, multipartFile);
	        		paramMap.put("file_path", Constant.IMAGE_PROFILE_IMAGE_RETURN + "file_menu_management/" + paramMap.get("seq_menu_management").toString() + "/" + fileName);
		        	cnt++;
	            }
	        }
			
			// 일정관리 update
			noticeService.updateMenuManagement(paramMap);
			
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
	 * 식단표 삭제
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/deleteMenuManagement.do")
	public @ResponseBody Map<?, ?> deleteMenuManagement(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req deleteMenuManagement.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_menu_management") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			noticeService.deleteMenuManagement(paramMap);
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 식단표 리스트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectMenuManagementList.do")
	public @ResponseBody Map<?, ?> selectMenuManagementList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectMenuManagementList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("year") || !paramMap.containsKey("month") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			paramMap.put("yearmonth", paramMap.get("year").toString()+paramMap.get("month").toString());
			
			retMap.put("menu_management_list", noticeService.selectMenuManagementList(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 식단표 상세
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectMenuManagementOne.do")
	public @ResponseBody Map<?, ?> selectMenuManagementOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectMenuManagementOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_menu_management") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put("menu_management", noticeService.selectMenuManagementOne(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 투표 등록
	 * @param paramMap
	 * @param model
	 * @return 
	 */
	@RequestMapping(value = "/insertSurveyVote.do")
	public @ResponseBody Map<?, ?> insertSurveyVote(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req insertSurveyVote.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_user") || !paramMap.containsKey("seq_survey") || !paramMap.containsKey("seq_kids") || !paramMap.containsKey("seq_survey_vote_item") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			} else {
				int survey_vote_count = noticeService.selectSurveyVoteCheck(paramMap);
				if(survey_vote_count == 0) {
					noticeService.insertSurveyVote(paramMap);			
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
	 * 알림 조회(목록)
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectNewsList.do")
	public @ResponseBody Map<?, ?> selectNewsList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectNewsList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_user") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("seq_kindergarden_class") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1) {
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			
			retMap.put("news_list", noticeService.selectNewsList(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 수정 페이지
	 * @param model
	 * @param params
	 * @param req
	 * @return 공지, 이벤트 One
	 */
	@RequestMapping(value = "/editNoticeEvent.fnd")
	public String editNoticeEvent(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req editNoticeEvent.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			noticeService.selectOneNoticeEvent(params);

			retMap.put("RET", params.get("RET"));
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		
		return "editNotice";
	}
	
	/** 공지, 이벤트 update
	 * @param model
	 * @param params
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/updateNoticeEvent.fnd")
	public String updateNoticeEvent(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req updateNoticeEvent.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			noticeService.updateNoticeEvent(params);

			retMap.put("RET", params.get("RET"));
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		
		return "redirect:/adminNotice.fnd";
	}
	
	
	/**
	 * 메인 노출 체크/체크해제
	 * @param model
	 * @param params
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/editCheckExpose.fnd")
	public String editCheckExpose(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req editCheckExpose.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			// count
			noticeService.checkNoticeCount(params);
			if(Integer.parseInt((params.get("NOTICE_COUNT").toString())) < 5){
				if(params.get("SELECT_TYPE").equals("NO")){
					noticeService.editCheckExpose(params);
					retMap.put("CODE", Constant.CODE_OK);
				} else if(params.get("SELECT_TYPE").equals("EV")) {
					noticeService.editCheckExpose(params);
					retMap.put("CODE", Constant.CODE_OK);
				}
				
			} else {
				retMap.put("CODE", "-1");
			}
			retMap.put("MSG", Constant.MSG_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return "json";
	}
	
	/** 공지, 이벤트 update
	 * @param model
	 * @param params
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/noticeImageUpload.do")
//	public String noticeImageUpload(Model model, @RequestParam Map<String, Object> params, @RequestParam MultipartFile file) {
	public String noticeImageUpload(Model model, @RequestParam Map<String, Object> params, @ModelAttribute("uploadForm") FileUploadForm uploadForm) {
		log.debug("req noticeImageUpload.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
//			HttpSession ses = req.getSession(false);
//			if (ses == null || ses.getAttribute("user") == null) {
//				throw new Exception(Constant.CODE_SESSION_EXPIRED);
//			}
			
			List<MultipartFile> files = uploadForm.getFiles();
	        List<String> fileNames = new ArrayList<String>();
	        
			int cnt = 0;
	        if(null != files && files.size() > 0) {
	            for (MultipartFile multipartFile : files) {
 	            	if(multipartFile == null || multipartFile.getSize() == 0) {
	            		cnt++;
	            		continue;
	            	}
	                String fileName = multipartFile.getOriginalFilename();
	                fileNames.add(fileName);
	                //Handle file content - multipartFile.getInputStream()
					CommonController.saveImage("/resin-4.0.40/webapps/img/notice/" + multipartFile.getOriginalFilename(), multipartFile);
//	                CommonController.saveImage("/usr/local/tomcat/webapps/img/notice/" + multipartFile.getOriginalFilename(), multipartFile);
					
					
					
					
					/*params.put("IMAGE_FILE", Constant.IMAGE_PROFILE_IMAGE_RETURN + multipartFile.getOriginalFilename());
					if(cnt==0){
						params.put("IMAGE_TYPE","TP");
						magazineService.insertMagazineImage(params);
					} else if(cnt==1){
						params.put("IMAGE_TYPE","TM");
						magazineService.insertMagazineImage(params);
					} else {
						params.put("IMAGE_TYPE","TI");
						magazineService.insertMagazineImage(params);
					}*/
					
					cnt++;
	            }
	        }
			Map<String,String> myMap = new HashMap<String, String>();
	        myMap.put("a", "1");
	        myMap.put("b", "2");
			
//	        mainService.testlaon(paramMap);
//			retMap.putAll(paramMap);
			
//			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
//			retMap.put("MSG", Constant.MSG_OK);
//			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		
		/*try {
			if (!file.isEmpty()) {
				CommonController.saveImage("/resin-4.0.40/webapps/img/notice/" + file.getOriginalFilename(), file);
//				retMap.put("NOTICE_IMAGE", "http://112.175.230.9:8080/img/notice/" + file.getOriginalFilename());
			} else {
				log.debug("file is empty");
				throw new Exception(Constant.CODE_MISSING_PARAMS);
			}
			
			retMap.put("RET", params.get("RET"));
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}*/
		model.addAttribute("retMap", retMap);
		log.debug("rep noticeImageUpload.fnd : " + retMap);
		return "json";
	}
}