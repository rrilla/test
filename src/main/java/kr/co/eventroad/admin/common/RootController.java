package kr.co.eventroad.admin.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;

public class RootController {
	/**
	 * 전역 로그
	 */
	protected static final org.slf4j.Logger log = LoggerFactory.getLogger("ahsol.d");

	/**
	 * md5 해쉬함수
	 * @param str plain text
	 * @return md5 text
	 */
	protected String getMD5(String str) {
		String MD5 = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();

		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);

			MD5 = null;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			MD5 = null;
		}
		return MD5;
	}

	/**
	 * sha256 해쉬함수
	 * @param str plain text
	 * @return sha256 text
	 */
	protected String getSHA256(String str) {
		String SHA = "";
		try {
			MessageDigest sh = MessageDigest.getInstance("SHA-256");
			sh.update(str.getBytes());
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();

		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			SHA = null;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			SHA = null;
		}
		return SHA;
	}
	
	/**
	 * param obj check
	 * @param obj param
	 * @return is null or empty
	 */
	protected boolean isNull(Object obj) {
		return (obj == null || "".equals(obj))? true:false;
	}
	
	protected void procException(Map<String, Object> retMap, Exception e) {
		if(ResultCode.RESULT_ALREADY_USER_DUPLICATE_ID.getCode().equals(e.getMessage())) {
			retMap.put("resultMsg", ResultCode.RESULT_ALREADY_USER_DUPLICATE_ID.getMsg());
			retMap.put("resultCode", ResultCode.RESULT_ALREADY_USER_DUPLICATE_ID.getCode());
		} else if(ResultCode.RESULT_ALREADY_RESGISTERD_USER.getCode().equals(e.getMessage())) {
			retMap.put("resultMsg", ResultCode.RESULT_ALREADY_RESGISTERD_USER.getMsg());
			retMap.put("resultCode", ResultCode.RESULT_ALREADY_RESGISTERD_USER.getCode());
		} else if(ResultCode.RESULT_ALREADY_USER_DUPLICATE_EMAIL.getCode().equals(e.getMessage())) {
			retMap.put("resultMsg", ResultCode.RESULT_ALREADY_USER_DUPLICATE_EMAIL.getMsg());
			retMap.put("resultCode", ResultCode.RESULT_ALREADY_USER_DUPLICATE_EMAIL.getCode());
		} else if(ResultCode.RESULT_MISSED_MANDATORY.getCode().equals(e.getMessage())) {
			retMap.put("resultMsg", ResultCode.RESULT_MISSED_MANDATORY.getMsg());
			retMap.put("resultCode", ResultCode.RESULT_MISSED_MANDATORY.getCode());
		} else if(ResultCode.RESULT_WRONG_VALUE.getCode().equals(e.getMessage())) {
			retMap.put("resultMsg", ResultCode.RESULT_WRONG_VALUE.getMsg());
			retMap.put("resultCode", ResultCode.RESULT_WRONG_VALUE.getCode());
		} else if(ResultCode.RESULT_NOT_FOUND_VALUE.getCode().equals(e.getMessage())) {
			retMap.put("resultMsg", ResultCode.RESULT_NOT_FOUND_VALUE.getMsg());
			retMap.put("resultCode", ResultCode.RESULT_NOT_FOUND_VALUE.getCode());
		} else if(ResultCode.RESULT_ALREADY_PUSH.getCode().equals(e.getMessage())) {
			retMap.put("resultMsg", ResultCode.RESULT_ALREADY_PUSH.getMsg());
			retMap.put("resultCode", ResultCode.RESULT_ALREADY_PUSH.getCode());
		} else if(ResultCode.AREADY_INSERT.getCode().equals(e.getMessage())) {
			retMap.put("resultMsg", ResultCode.AREADY_INSERT.getMsg());
			retMap.put("resultCode", ResultCode.AREADY_INSERT.getCode());
		} else {
			retMap.put("resultMsg", Constant.MSG_UNKNOWN);
			retMap.put("resultCode", Constant.CODE_UNKNOWN);
		}
		
		
		
		
		/*if (Constant.CODE_INVALID_PARAMS.equals(e.getMessage())) {
			retMap.put("resultMsg", Constant.MSG_INVALID_PARAMS);
			retMap.put("resultCode", Constant.CODE_INVALID_PARAMS);
		} else if (Constant.CODE_MISSING_PARAMS.equals(e.getMessage())) {
			retMap.put("resultMsg", Constant.MSG_MISSING_PARAMS);
			retMap.put("resultCode", Constant.CODE_MISSING_PARAMS);
		} else if (Constant.CODE_SESSION_EXPIRED.equals(e.getMessage())) {
			retMap.put("resultMsg", Constant.MSG_SESSION_EXPIRED);
			retMap.put("resultCode", Constant.CODE_SESSION_EXPIRED);
		} else if (Constant.CODE_ACCOUNT_ROCK_PARAMS.equals(e.getMessage())) {
//			retMap.put("resultMsg", Constant.);
			retMap.put("resultCode", Constant.CODE_ACCOUNT_ROCK_PARAMS);
		} else if (Constant.CODE_INVALID_EMAIL.equals(e.getMessage())) {
			retMap.put("resultMsg", Constant.MSG_INVALID_EMAIL);
			retMap.put("resultCode", Constant.CODE_INVALID_EMAIL);
		} else */
			
			
	}
	
	public void send_push(List<?> list, Map<String, Object> pushDataMap) {
		String AUTH_KEY_FCM = "AAAA0vT60M8:APA91bGhjkuD7CRBpPukLLfo3AkI3xc2REjAu-82_jnHS-qKSCvsRc6g8oNzlywaAbjqI8CgTwkB30EW0NiFXL8nvLLpljdiEn1KOjhoZIT-5kfScP6AOYTAyydvlZc8kAF9T-y48zno";
		String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
		
		ArrayList<String> token = new ArrayList<String>();
//        token.add("");
		
		for(int i=0;i<list.size();i++) {
			token.add(((Map)list.get(i)).get("token").toString());
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
        JSONObject data = new JSONObject();
       /* JSONObject vSomeDictionary = new JSONObject();
        vSomeDictionary.put("content-available", 1);
        vSomeDictionary.put("alert", "SomeMessage");
        vSomeDictionary.put("sound", "default");
        json.put("aps", vSomeDictionary);*/
        
        for( String key : pushDataMap.keySet() ){
        	if(pushDataMap.get(key) != null) {
        		data.put( key , pushDataMap.get(key) );
        	}
        }	// 출처: http://stove99.tistory.com/96 [스토브 훌로구]
        
     // ios용
        data.put("sound", "");
        json.put("priority", "high");
        json.put("content_available", true);
        
//        data.put("content-available", 1);
        json.put("notification", data);
        
        
        /*data.put("subtitle", subtitle); // Notification body
        data.put("seq", seq); // Notification body
        data.put("msg_type", msg_type); // Notification body
*/        
//        json.put("notification", info);
        json.put("data", data);
        json.put("registration_ids", token); // deviceID
//        json.put("to", "dDPdIV7cRG4:APA91bHITaLgZSspU6RG7odnZQ3liIC5wnTNKGVFDoh26UWlY2OkL3VrEy0M3lJPWqrkVTNzXEs4AIOM0vLeEBZ81MC0eRR8dLy7FFmilmiST4mjU-Xsubg3M6Gtm3RzEg9SXluBMv20"); // deviceID
        
        System.out.println("================================");
        System.out.println(json);
        System.out.println("================================");
        
        boolean SHOW_ON_IDLE = false;    //옙 활성화 상태일때 보여줄것인지

//      try(OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())){
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
		
	}
}