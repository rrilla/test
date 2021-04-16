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
 *  총판 컨트롤러
 * @author jwdoe
 * @since 2016. 12. 14.
 * @version 1.0.0
 */
@Controller
public class AreaController extends RootController {

	/**
	 * 매거진 서비스(자동 할당)
	 */
	@Autowired
	public AreaService areaService;
	
	/**
	 * 캠페인 목록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectAreaList.do")
	public @ResponseBody Map<?, ?> selectAreaList(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req selectAreaList.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			retMap.put( "area_list", areaService.selectAreaList(paramMap) );
			
//			retMap.put("l_notice", mainService.getMainNotice(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 캠페인 목록
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/selectArea.do")
	public @ResponseBody Map<?, ?> selectArea(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req selectArea.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			/*if(paramMap.get("page") == null || Integer.parseInt(paramMap.get("page").toString()) == -1){
				paramMap.put("page", 1);
			} else {
				paramMap.put("page", Integer.parseInt( paramMap.get("page").toString()) );
			}*/
			
			/*if( StringUtils.isEmpty( paramMap.get("seq_user").toString() ) ){
				// 로그인한 경우 판단
				throw new Exception(Constant.CODE_MISSING_PARAMS);
			}*/
			
			retMap.put( "area_list", areaService.selectArea(paramMap) );
			
//			retMap.put("l_notice", mainService.getMainNotice(paramMap));
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return model;
	}
	
}