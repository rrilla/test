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
import kr.co.eventroad.admin.service.AreaService;
import kr.co.eventroad.admin.service.TeacherService;
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
 *  유치원 컨트롤러
 * @author jwdoe
 * @since 2017. 12. 14.
 * @version 1.0.0
 */
@Controller
public class KindergardenController extends RootController {

	/**
	 * 유치원 서비스(자동 할당)
	 */
	@Autowired
	public KindergardenService kindergardenService;
	
	/**
	 * 유치원 목록(검색)
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectKindergardenList.do")
	public @ResponseBody Map<?, ?> selectKindergardenList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req selectKindergardenList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1) {
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			
			if(paramMap.containsKey("kindergarden_name")) {
				paramMap.put("kindergarden_name", "%" + paramMap.get("kindergarden_name").toString().trim() + "%");
			}
			
			retMap.put( "kindergarden_list", kindergardenService.selectKindergardenList(paramMap) );
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 유치원 반 목록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectKindergardenClassList.do")
	public @ResponseBody Map<?, ?> selectKindergardenClassList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req selectKindergardenClassList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1) {
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}
			
			if( !paramMap.containsKey("seq_kindergarden") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put( "kindergarden_class_list", kindergardenService.selectKindergardenClassList(paramMap) );
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	/**
	 * 회원가입
	 * @param paramMap
	 * @param model
	 * @param req, req_flag: t: 선생, p: 부모
	 * @return 
	 */
	@RequestMapping(value = "/inserReqKindergardenApply.do")
	public @ResponseBody Map<?, ?> inserReqKindergardenApply(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req inserReqKindergardenApply.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
//		System.out.println(paramMap.get("seq_user").toString());
		try {
			if( !paramMap.containsKey("seq_user") || !paramMap.containsKey("seq_kindergarden") || !paramMap.containsKey("req_flag") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			} else {
				
				// 이미 신청중인 유치원 확인 seq_kindergarden
				Map<String, Object> kindergardenMap = kindergardenService.selectReqKindergardenApplyCheck(paramMap);
				
				if(kindergardenMap == null) {
					// 신청정보 insert
					kindergardenService.inserReqKindergardenApply(paramMap);
				} else if(kindergardenMap.get("rep_flag").toString().equals("n") ){
					// 신청정보 update
					paramMap.put("seq_apply", kindergardenMap.get("seq_apply").toString());
					paramMap.put("rep_flag", "w");
					kindergardenService.updateReqKindergardenApply(paramMap);
				} else if(kindergardenMap.get("rep_flag").toString().equals("y") || kindergardenMap.get("rep_flag").toString().equals("w")){
					throw new Exception(ResultCode.RESULT_ALREADY_RESGISTERD_USER.getCode());
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
	 * 기관정보(유치원 상세조회)
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectKindergardenOne.do")
	public @ResponseBody Map<?, ?> selectKindergardenOne(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req selectKindergardenOne.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if( !paramMap.containsKey("seq_kindergarden") ){
				throw new Exception(ResultCode.RESULT_MISSED_MANDATORY.getCode());
			}
			
			retMap.put( "kindergarden", kindergardenService.selectKindergardenOne(paramMap) );
			
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
}