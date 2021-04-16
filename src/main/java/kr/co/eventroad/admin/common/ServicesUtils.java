package kr.co.eventroad.admin.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;


public class ServicesUtils {
	/**
	 * Mandatory parameter value check
	 * 
	 * @param param
	 *            Mandatory param value
	 * @param model
	 *            Return model object
	 * @return Result code added model
	 */
	public static boolean checkParameter(String param, ModelMap model) {
		if (param != null && !param.isEmpty()) {
			setResultCode(ResultCode.RESULT_OK, model);
			return true;
		}
		setResultCode(ResultCode.RESULT_MISSED_MANDATORY, model);
		return false;
	}

	/**
	 * Set result code
	 * 
	 * @param code
	 *            {@link ResultCode}
	 * @param model
	 *            Return model object
	 */
	public static void setResultCode(ResultCode code, ModelMap model) {
		if (model != null) {
			model.put("resultCode", code.getCode());
			model.put("resultMsg", code.getMsg());
		}
	}

	/**
	 * Make random value
	 * 
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value
	 * @return A value between the minimum and the maximum.
	 */
	public static int getRandomInt(int min, int max) {
		return (int) (Math.random() * max) + min;
	}
	
	public static void setLanguage(@RequestParam Map<String, Object> paramMap, HttpServletRequest request) {
		
		//http://daeng2c.blog.me/220069520868 (다국어처리)
		HttpSession session = request.getSession();
		Locale locales = Locale.CHINA;
			
		if(paramMap.get("locale") == null){
			//쿠키에있는거로 
			String cookie_locale = ServicesUtils.getCookies(request, "lang_type");
			if(cookie_locale!=null) {
				paramMap.put("locale", cookie_locale);
				if(cookie_locale.equals("en")) locales = Locale.ENGLISH;
				else if(cookie_locale.equals("ko")) locales = Locale.KOREA;
			}
			
		} else {
			//파라미터 넘어온거로 
			String l = paramMap.get("locale").toString();			
			if(l!=null) {
				paramMap.put("locale", l);
				if(l.equals("en")) locales = Locale.ENGLISH;
				else if(l.equals("ko")) locales = Locale.KOREA;
			}
		}
		
		//세션에 존재하는 Locale을 새로운 언어로 변경해준다 
		session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locales);

	}
	
	public static String getCookies(HttpServletRequest request, String name) {
		
		String value = null;
		
		Cookie[] cookies = request.getCookies();
		if( cookies != null ) {
			for( int i=0 ; i<cookies.length ; i++ )	{
				Cookie cookie = cookies[i];
				if (cookie.getName().equals(name)) {
					value = cookie.getValue();
				}							
			}
		}
		return value;
	}
	
	
	public static String isMobileCheck(HttpServletRequest request) {
		String UA_ANDROID = ".*Android.*|.*CUPCAKE.*|.*dream.*";
		String UA_IOS = ".*iPhone.*|.*iPod.*|.*iPad.*|.*webmate.*";

		String userAgent = request.getHeader("user-agent");

		boolean isAndroid = userAgent.matches(UA_ANDROID);
		boolean isIOS = userAgent.matches(UA_IOS);
		boolean isMobile = isAndroid || isIOS;
		
		return isMobile?"mobile":"web";
	}
	
	public static String isMobileDealer(HttpServletRequest request, boolean auth) {
		String UA_ANDROID = ".*Android.*|.*CUPCAKE.*|.*dream.*";
		String UA_IOS = ".*iPhone.*|.*iPod.*|.*iPad.*|.*webmate.*";

		String userAgent = request.getHeader("user-agent");

		boolean isAndroid = userAgent.matches(UA_ANDROID);
		boolean isIOS = userAgent.matches(UA_IOS);
		boolean isMobile = isAndroid || isIOS;
		
//		System.out.println(is_dealer==true?"dealer":is_store==true?"store":""  );
		
		return (isMobile && auth)?"mobile/dealer/":"web/dealer/";
	}
	
	public static String isMobileStore(HttpServletRequest request, boolean auth) {
		String UA_ANDROID = ".*Android.*|.*CUPCAKE.*|.*dream.*";
		String UA_IOS = ".*iPhone.*|.*iPod.*|.*iPad.*|.*webmate.*";

		String userAgent = request.getHeader("user-agent");

		boolean isAndroid = userAgent.matches(UA_ANDROID);
		boolean isIOS = userAgent.matches(UA_IOS);
		boolean isMobile = isAndroid || isIOS;
		
//		System.out.println(is_dealer==true?"dealer":is_store==true?"store":""  );
		
		return (isMobile && auth)?"mobile/store/":"web/store/";
	}
	
	public static String testMD5(String str){
		String MD5 = ""; 
		try{
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(str.getBytes()); 
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5;
	}
	
	// ==========================================================================
	// WebServer의 System Date와 System Time을 가져온다.
	// chrGubun = 'D': 오늘날짜, 'T': 현재 시간
	// ==========================================================================
	private static String getSysDateTimeFromWebServer(char chrGubun, String strDelim) {
		Calendar cal = Calendar.getInstance();
		String strResult = "";

		if (chrGubun == 'D') {
			String strYear = cal.get(Calendar.YEAR) + "";

			String strMonth = (cal.get(Calendar.MONTH) + 1) + "";
			if (strMonth.length() < 2)
				strMonth = "0" + strMonth;

			String strDate = cal.get(Calendar.DATE) + "";
			if (strDate.length() < 2)
				strDate = "0" + strDate;

			strResult = strYear + strDelim + strMonth + strDelim + strDate;
		} else if (chrGubun == 'T') {
			String strHours = cal.get(Calendar.HOUR) + "";
			if (cal.get(Calendar.AM_PM) == 1)
				strHours = Integer.parseInt(strHours) + 12 + "";
			if (strHours.length() < 2)
				strHours = "0" + strHours;

			String strMinutes = cal.get(Calendar.MINUTE) + "";

			if (strMinutes.length() < 2)
				strMinutes = "0" + strMinutes;

			String strSecond = cal.get(Calendar.SECOND) + "";
			if (strSecond.length() < 2)
				strSecond = "0" + strSecond;

			strResult = strHours + strDelim + strMinutes + strDelim + strSecond;
		}
//logger.debug("webserver time is "+strResult+"/ gubun :"+chrGubun);
		return strResult;
	}
	
	// ==========================================================================
	/**
	 * 오늘 날짜를 가져온다.
	 * 
	 * @param strDelim
	 *            날짜의 년, 월, 일 구분자
	 * @return yyyy + 구분자 + MM + 구분자 + dd
	 * @throws Exception 
	 */
	// ==========================================================================
	public static String getToday(String strDelim) throws Exception {
		String strToday = "";

		if (strDelim.trim().equals("'"))
			strDelim = ".";
		strToday = getSysDateTimeFromWebServer('D', strDelim); // WebServer의 System Date를가져온다.
		
		return strToday;
	}
	
	
	// ==========================================================================
	/**
	 * 현재 시간(24시간제) 을 가져온다.
	 * 
	 * @param strDelim
	 *            시간의 시, 분 구분자
	 * @return hh + 구분자 + mm
	 * @throws Exception 
	 */
	// ==========================================================================
	public static String getCurrentTime(String strDelim) throws Exception {
		String strCurrTime = "";
		strCurrTime = getSysDateTimeFromWebServer('T', strDelim);
		if (strDelim.trim().equals("'"))
			strDelim = ":";
		
		strCurrTime = getSysDateTimeFromWebServer('T', strDelim); // WebServer의 System Time을 가져온다.
		strCurrTime.trim();

		return strCurrTime;
	}
	
	/**
	 * 폴더 이름 생성
	 * @return 폴더 경로
	 * @throws Exception
	 */
	public static String autoFoldername() throws Exception {
		Calendar cal = Calendar.getInstance();  // 오늘 날짜시간에 대한 객체 얻기

		String yStr = ""+cal.get(Calendar.YEAR);  // 올해년도 얻기
		String mStr = ""+(cal.get(Calendar.MONTH) + 1);  // 현재 월 얻기 (월은 + 1 해줘야함)
		String dStr = ""+(cal.get(Calendar.DATE));  // 현재 월 얻기 (월은 + 1 해줘야함)
		
		if((cal.get(Calendar.MONTH)+1) < 10 ) {
			   mStr = "0"+mStr;  // 현재월이 1자리 숫자인경우 앞에 0을붙여준다.
		}
		if((cal.get(Calendar.DATE)) < 10 ) {
				dStr = "0"+dStr;
		}
		System.out.println(yStr+"\\"+mStr+"\\"+mStr+dStr);
		
		return yStr+"\\"+mStr+"\\"+mStr+dStr;
	}
	
}