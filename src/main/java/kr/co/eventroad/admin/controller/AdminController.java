package kr.co.eventroad.admin.controller;

import java.net.URLEncoder;
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
import kr.co.eventroad.admin.service.AdminService;
import kr.co.eventroad.admin.service.KidsService;
import kr.co.eventroad.admin.service.TeacherService;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

/**
 * 관리자 컨트롤러
 * @author jwdoe
 * @since 2016. 12. 12.
 * @version 1.0.0
 * 마지막 수정 : jwdoe, 2017. 02. 20
 */
@Controller
@RequestMapping("/admin/*")
//@SessionAttributes({"id","seq_admin"})
public class AdminController extends RootController {

	@Autowired
	public AdminService adminService;
	
	@Autowired
	public KidsService agencyService;
	
	@Autowired
	public TeacherService dealerService;
	
	String url = "web/admin/";
	
	
	/**
	 * 메인 - 관리자 로그인 했을 시 권한에 따라 main 연결
	 * @param model
	 * @param params
	 * @param req
	 * @return 메인
	 */
	@RequestMapping(value = "/admin.do")
	public String admin(Model model, @RequestParam Map<String, Object> params, HttpServletRequest req, Authentication authentication) {
		log.debug("req admin.do :" + params);
		Map<String, Object> retMap = new HashMap<String, Object>();
		String url = "";
		if ( authentication != null ) {
//			User user = (User) authentication.getPrincipal();
			
			if( authentication.getAuthorities().contains( new SimpleGrantedAuthority("ROLE_ADMIN") ) ){
				url = "admin/noticeConditions.do";
			}else {
			}
		}
		return "redirect:/" + url;
	}
	
	/**
	 * 비콘 업로드 양식 엑셀파일 다운로드
	 * @param paramMap
	 * @param model
	 * @param req
	 * @return rep
	 */
	@RequestMapping(value = "/getExcelBeaconTemplete.do")
	public String getExcelBeaconTemplete(@RequestParam Map<String, Object> paramMap, ModelMap model, HttpServletRequest req, Authentication authentication) {
		log.debug("req getExcelBeaconTemplete.do :" + paramMap);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		try {
			if ( authentication != null ) {
				User user = (User) authentication.getPrincipal();
				paramMap.put("seq_user", user.getUsername() );
			}
			ServicesUtils.setResultCode(ResultCode.RESULT_OK, model);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			procException(retMap, e);
		}
		model.addAllAttributes(retMap);
		return "ExcelBeaconTemplete";
	}
}