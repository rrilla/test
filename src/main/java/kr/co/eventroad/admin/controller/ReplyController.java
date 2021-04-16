package kr.co.eventroad.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kr.co.eventroad.admin.common.Constant;
import kr.co.eventroad.admin.common.ResultCode;
import kr.co.eventroad.admin.common.RootController;
import kr.co.eventroad.admin.common.ServicesUtils;
import kr.co.eventroad.admin.service.AlbumService;
import kr.co.eventroad.admin.service.NoticeService;
import kr.co.eventroad.admin.service.ReplyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 리뷰 관리자 컨트롤러
 * @author jwdoe
 * @since 2014. 10. 16.
 * @version 1.0.0
 */
@Controller
public class ReplyController extends RootController {

	/**
	 * 리뷰 서비스
	 */
	@Autowired
	public ReplyService replyService;
	
	@Autowired
	public NoticeService noticeService;
	
	/**
	 * 댓글 등록
	 * @param paramMap
	 * @param model
	 * @param req, req_flag: t: 선생, p: 부모
	 * @return 
	 */
	@RequestMapping(value = "/insertReply.do")
	public @ResponseBody Map<?, ?> insertReply(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req insertReply.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_user") || !paramMap.containsKey("seq") || !paramMap.containsKey("flag") || !paramMap.containsKey("seq_user_teacher")){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			} else {
				replyService.insertReply(paramMap);
				
				// news insert
				paramMap.put("msg_type", "댓글 등록");
				paramMap.put("seq", paramMap.get("seq").toString());
				paramMap.put("subtitle", (paramMap.get("flag").toString().equals("n")?"공지사항":"앨범") + " 댓글이 등록되었습니다.");
				paramMap.put("title", paramMap.get("content").toString());
				paramMap.put("seq_user", paramMap.get("seq_user").toString());
				paramMap.put("seq_kids", paramMap.containsKey("seq_kids")?paramMap.get("seq_kids").toString():null );
				paramMap.put("year", null);
				paramMap.put("month", null);
				paramMap.put("day", null);
				
				paramMap.put("is_personal", "y");
				paramMap.put("seq_user_teacher", paramMap.get("seq_user_teacher").toString());
				
				noticeService.insertNews(paramMap);
				
				// push token list
				List<Object> temp_token_list = null;
				if( paramMap.get("flag").toString().equals("n") ) {
					temp_token_list = (List<Object>) replyService.selectPushNoticeUserOne(paramMap);
				} else if( paramMap.get("flag").toString().equals("a") ) {
					temp_token_list = (List<Object>) replyService.selectPushAlbumUserOne(paramMap);
				}
				
				System.out.println(temp_token_list);
				
				// push
				if(temp_token_list.size() > 0) {
					// param pushParamMap
					Map<String, Object> pushDataMap = new HashMap<String, Object>();
					
					pushDataMap.put("msg_type", "댓글 등록");
					pushDataMap.put("seq", paramMap.get("seq").toString());
					pushDataMap.put("seq_kindergarden", paramMap.get("seq_kindergarden").toString());
					pushDataMap.put("seq_kindergarden_class", null);
					pushDataMap.put("seq_kids", paramMap.containsKey("seq_kids")?paramMap.get("seq_kids").toString():null );
					pushDataMap.put("age", null);
					pushDataMap.put("subtitle",  (paramMap.get("flag").toString().equals("n")?"공지사항":"앨범") + "댓글이 등록되었습니다.");
					pushDataMap.put("title", paramMap.get("content").toString());
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
	 * 댓글 삭제
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/deleteReply.do")
	public @ResponseBody Map<?, ?> deleteReply(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req deleteReply.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_reply") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			replyService.deleteReply(paramMap);
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 댓글 리스트
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectReplyList.do")
	public @ResponseBody Map<?, ?> selectReplyList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req) {
		log.debug("req selectReplyList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq") || !paramMap.containsKey("flag") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1) {
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			
			retMap.put("reply_list", replyService.selectReplyList(paramMap));
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
	 * 리뷰 관리자 페이지
	 * @param model
	 * @param params
	 * @param req
	 * @return 리뷰 List
	 */
	@RequestMapping(value = "/adminReview.fnd")
	public String adminReview(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req adminReview.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("user") == null) {
				throw new Exception(Constant.CODE_SESSION_EXPIRED);
			}
			
			if(params.get("STORE_NAME") != null){
				params.put("STORE_NAME", "%" + params.get("STORE_NAME") + "%");
			}
			
			if(params.get("START_DATE") != null){
				replyService.selectAdminReviewInfo(params);
			}

			retMap.putAll(params);
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		
		if(params.get("STORE_NAME") == null && params.get("START_DATE") == null && params.get("END_DATE") == null){
			return "admin/adminReview";
		} else {
			return "json";
		}
	}
	
	/**
	 * 관리자 댓글 insert
	 * @param model
	 * @param params
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/insertReply.fnd")
	public String insertReview(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req insertReply.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("user") == null) {
				throw new Exception(Constant.CODE_SESSION_EXPIRED);
			}
			
			replyService.insertReply(params);
			
			if(params.get("STORE_NAME") != null){
				params.put("STORE_NAME", "%" + params.get("STORE_NAME") + "%");
			}
			
			replyService.selectAdminReviewInfo(params);

			retMap.putAll(params);
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
	 * 관리자 댓글 삭제
	 * @param model
	 * @param params
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/deleteReply.fnd")
	public String deleteReply(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req deleteReply.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("user") == null) {
				throw new Exception(Constant.CODE_SESSION_EXPIRED);
			}
			
			if(params.get("STORE_NAME") != null){
				params.put("STORE_NAME", "%" + params.get("STORE_NAME") + "%");
			}
			replyService.deleteReply(params);
			replyService.selectAdminReviewInfo(params);
			
			retMap.putAll(params);
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
	 * 리뷰 차단
	 * @param model
	 * @param params
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/updateBoard.fnd")
	public String updateBoard(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req) {
		log.debug("req updateBoard.fnd :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			HttpSession ses = req.getSession(false);
			if (ses == null || ses.getAttribute("user") == null) {
				throw new Exception(Constant.CODE_SESSION_EXPIRED);
			}
			
			if(params.get("STORE_NAME") != null){
				params.put("STORE_NAME", "%" + params.get("STORE_NAME") + "%");
			}
			
			replyService.updateBoard(params);
			replyService.selectAdminReviewInfo(params);

			retMap.putAll(params);
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAttribute("retMap", retMap);
		return "json";
	}
}