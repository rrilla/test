package kr.co.eventroad.admin.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @제작자 jwdoe
 * @마지막 수정날자 2016. 12. 30.
 * @클래스 설명 : 공통 컨트롤러
 */
@Controller
public class CommonController extends RootController implements HandlerInterceptor {

	/**
	 * 공통 서비스
	 */
	@Autowired
	public CommonService commonService;

	/**
	 * 파일 업로드
	 * @param model
	 * @param name 파일이름(경로포함)
	 * @param file 파일
	 * @return json
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload.fnd")
	public String upload(Model model, @RequestParam String name, @RequestParam MultipartFile file) throws Exception {
		log.debug("uploading.fnd");
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			if (!file.isEmpty()) {
				log.debug("file is not empty. : " + file.getOriginalFilename());

				if (name != null && !"".equals(name)) {
					saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + name, file);
				} else {
					saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + file.getOriginalFilename(), file);
				}
				retMap.put("MSG", Constant.MSG_OK);
				retMap.put("CODE", Constant.CODE_OK);
			} else {
				log.debug("file is empty");

				throw new Exception(Constant.CODE_MISSING_PARAMS);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			procException(retMap, e);
		}

		model.addAttribute("retMap", retMap);
		return "json";
	}

	/**
	 * 이미지 저장
	 * @param name 파일이름(경로포함)
	 * @param image 이미지
	 * @throws Exception
	 */
	public static void saveImage(String name, MultipartFile image) throws Exception {
		try {
			File file = new File(name);
			if (!file.exists()) {
				FileUtils.writeByteArrayToFile(file, image.getBytes());
			} else {
				FileUtils.writeByteArrayToFile(file, image.getBytes());
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	public boolean preHandle(HttpServletRequest req, HttpServletResponse rep, Object handle) throws Exception {
//		System.out.println("preHandle");
		/*log.debug("test login redirect. : " );
		HttpSession session = req.getSession(false);
		String path = (String) req.getRequestURI().replace("/do/", "").replace("/ktcs/", "").replace("/admin/", "").replace(".do", "").replace("/Ahsol_Web/", "").trim();
		if (!"testtest".equals(path) &&  !"kcpcert_proc_req".equals(path) && !"kcpcert_proc_res".equals(path) &&!"getQuestionList".equals(path) &&  !"mobile".equals(path) && !"testlaon2".equals(path) && !"getSingo".equals(path) && !"getNoticeList".equals(path) && !"getNoticeDetail".equals(path) && !"getMainNotice".equals(path) && !"getLocalm".equals(path) && !"deleteNotice".equals(path) && !"updateNotice".equals(path) && !"insertNotice".equals(path) && !"getNotice".equals(path) && !"getCoupon".equals(path) && !"insertCoupon".equals(path) && !"deleteCoupon".equals(path) && !"updateCoupon".equals(path) && !"tree_left".equals(path) && !"cmsTop".equals(path) && !"adminMain".equals(path) && !"adminViewMember".equals(path) && !"getUserMember".equals(path) && !"getAdminLogin".equals(path) && !"admin".equals(path) && !"adminMain".equals(path) && !"adminLogin".equals(path) && !"setUserSign".equals(path) && !"getCheckUserId".equals(path) && !"notice".equals(path) && !"sign".equals(path) && !"lolmain".equals(path) && !"testlaon".equals(path) && !"testlaonUploadTest".equals(path) && !"getUserLogin".equals(path) && !"taxchoice".equals(path) && !"login".equals(path) && !"adminRegRock".equals(path) && !"main".equals(path) && !path.contains("res/css/") && !path.contains("res/images/") && !path.contains("res/js/") && !"web/main".equals(path) && !path.contains("woff") && !path.contains("ttf") ) {
			// session검사
//			log.debug("test login redirect. : " + path);
//			HttpSession session = req.getSession(false);

			if (session == null) {
				// 처리를 끝냄 - 컨트롤로 요청이 가지 않음.
//				rep.sendRedirect("login.fnd");
			   StringBuffer sb=new StringBuffer();
               sb.append("<head>\n");
               sb.append("<SCRIPT>\n");
               sb.append("window.top.location.replace('main.do');\n"); 
               sb.append("\n</SCRIPT>\n</head>");
               System.out.println(sb.toString());
               rep.getWriter().println(sb.toString());
               rep.getWriter().flush();
//               rep.getWriter().close();
               return false;
			}

//			Map user = (Map) session.getAttribute("mb_id");
			if (session.getAttribute("mb_id") == null) {
				StringBuffer sb=new StringBuffer();
	               sb.append("<head>\n");
	               sb.append("<SCRIPT>\n");       
	               sb.append("alert('login please')\n");
	               sb.append("window.top.location.replace('main.do');\n"); 
	               sb.append("\n</SCRIPT>\n</head>");
	               System.out.println(sb.toString());
	               rep.getWriter().println(sb.toString());
	               rep.getWriter().flush();
//	               rep.sendRedirect("main.do");
	               rep.getWriter().close();
				return false;
			}
		}*/

		return true;
	}
	
	public static boolean DeleteDir(File path) throws Exception {
		if(!path.exists()) {
			return false;
		}
		
		File[] files = path.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				DeleteDir(file);
			} else {
				file.delete();
			}
		}
		
		return path.delete();
	}
	
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
	 */
	public void postHandle(HttpServletRequest req, HttpServletResponse rep, Object handle, ModelAndView mav) throws Exception {
//		System.out.println(""+req.getRequestURI());
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
	 */
	public void afterCompletion(HttpServletRequest req, HttpServletResponse rep, Object handle, Exception e) throws Exception {
//		System.out.println("afterCompletion");
		if (req.getRequestURI().contains("do")) {
			Map<String, String> sqlMap = new HashMap<String, String>();
			try {
				String path = (String) req.getRequestURI().replace("/Ububa/", "").replace("/", "").replace(".do", "").trim();
				if (path == null || "".equals(path.trim())) {
					path = "null";
				}
				sqlMap.put("path", path);
				String uuid = (String) req.getParameter("i");
				if (uuid == null || "".equals(uuid.trim())) {
					uuid = "null";
				}
				sqlMap.put("uuid", uuid);
				String platform = (String) req.getParameter("p");
				if (platform == null || "".equals(platform.trim())) {
					platform = "null";
				}
				sqlMap.put("platform", platform);
				String version = (String) req.getParameter("v");
				if (version == null || "".equals(version.trim())) {
					version = "n";
				}
				sqlMap.put("version", version);
				String device = (String) req.getParameter("d");
				if (device == null || "".equals(device.trim())) {
					device = "0";
				}
				sqlMap.put("device", device);
				log.debug("sqlMap:" + sqlMap);
				commonService.writeLog(sqlMap);
			} catch (Exception e1) {
				log.debug(e1.getMessage(), e1);
				log.error(e1.getMessage());
			}
		}
	}
}