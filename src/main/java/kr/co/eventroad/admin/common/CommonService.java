package kr.co.eventroad.admin.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

@Service
@Transactional
public class CommonService extends RootService {

	@Autowired
	private SqlSession sqlSession;

	/**
	 * gcm 을 위한 apikey
	 */
//	private String apiKey = "AIzaSyByQLkzrH9TKIIKm4d5jf-F99nrl3xErFw";
//	private String apiKey = "AIzaSyA56QRjQKV0uXdO7Fs2wDGOZWfcJ2kk3EM"; // sample app
	private String apiKey = "AIzaSyA1QDcn9z42L41XR4SiOgpAAzbtwG_tozY"; // sonagi dev app
//	private String apiKey = "AIzaSyDUAX6n9rV9MAfTvvJr6B1Vx9LvQ_YMvS8"; // sonagi capp
	
	private Sender gcmSender; // GCM Sender
	private Message gcmMessage; // GCM Message

	public void send(Map<String, String> modelMap) throws Exception {
		emptyRemoveMap(modelMap);
		
		log.debug(">>>>>>>>>>>>>>>>send : "+modelMap);
		
		gcmSender = new Sender(apiKey);

		String pushCode = (String) modelMap.get("USER_PUSH_CODE");
		
		/*상단 텍스트변수 : PushTitle
		하단 텍스트변수를 PushContent 
		하단 이미지 변수는 PushImageUrl
		이동하고 싶은 url은 PushContentUrl*/
		
		
//		modelMap.remove("USER_PUSH_CODE");
		
		modelMap.put("PushTitle", "title");
		modelMap.put("PushContent", "content");
		modelMap.put("PushImageUrl", "http://coushot.com:8080/img/res/store_img/2/img_store0.jpg");
//		modelMap.put("PushImageUrl", "");
		modelMap.put("PushProfileImageUrl", "http://coushot.com:8080/img/res/store_img/2/img_store1.jpg");
		modelMap.put("PushContentUrl", "http://naver.com");
		
		gcmMessage = new Message.Builder().collapseKey("collapseKey" + System.currentTimeMillis()).timeToLive(3).delayWhileIdle(true).setData(modelMap).build();

		Result result = null;
		Map m = null;
		try {
			List<String> resList = new ArrayList<String>();
//			resList.add("APA91bE-Y0OfPwQiOYR-XggsY_NDmhicnLDPF4AdhSAq0LNFlqXUkvNBiquHfUUls_hbtaQaIBJ60KMoA06gciTyD5vBoBylH97282PguwsEFALKDLqW2bmS0LygNe5DO22o8SZtxDJ4");
//			resList.add("APA91bFPcd1gcG3dEbr5UsSf7Vinm8_LRqavQ2TT4sc6t9E8IMx5I4-K5XdBDf7FaSq_MzXNQB9rG-7qKMOdnmFYyYpy753M0bYjOBa1zhilpnwKsIqOLaFkSBLQTy6h1Rh1HJyw_-9Y");
			
			// sample app
//			resList.add("APA91bH44nF_PU8g_gLQzCmdnMMnd08e8Yh0CHpu9uXDAqFQxK6A-34Tz0SQF25oxB_HScqsjZFS7mlzaxW8Rhrlv-Fhuh1P7zXM1OzsfnnUFWX0pcZpCmMf1-jg2Mu5ok6hWFAB6Tuy");
			// sonagi dev app
			resList.add("APA91bEZkL3-zaTbqx4whWJnwDmahchxmwDgSsKZUl6cjRtDSgE5ZqZpdNgTjewKPfb8PIUTUoUVH_zwQq_gdltAIlpNirj7sE2pNp6zhsdZD9EJnFlfwMjbZOt1MSjrpb5lDMTToKJ6");
			// sonagi app
//			resList.add("APA91bFdfQUgqiR11zBSCFh25_oJmfUyhfOLLO9Qvp6Rih-0-dOu_2nv9urZ0n8VeL4q20T16_7hCscCjAk5I3xzNvP7hLZt4X8POC000olBXaT45OskBSSRiJ2Zz7qMtW4DTv4M87xU");
			
			MulticastResult ret = gcmSender.send(gcmMessage, resList, 5);
			System.out.println( ret.getResults() );

			/*if (modelMap.get("TYPE").equals("M")) {
				modelMap.put("sender", modelMap.get("SENDER"));
				modelMap.put("receiver", modelMap.get("RECEIVER"));
				m = sqlSession.selectOne("msg_reject.select", modelMap);
//			}
			if (m != null && m.size() > 0) {
				return;
			}
			if (pushCode == null || "".equals(pushCode)) {
				log.debug("invalid pushcode!!");
			} else {
				result = gcmSender.send(gcmMessage, pushCode, 5);
				log.debug("send to "+ pushCode);
//			}
			if (result != null && result.getMessageId() != null) {
				modelMap.clear();
				modelMap.put("MSG", "OK");
				modelMap.put("CODE", "200");
			} else {
				String error = result.getErrorCodeName();
				modelMap.clear();
				modelMap.put("MSG", "ERROR");
				modelMap.put("CODE", "1001");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*public void send(Map<String, String> modelMap, String pushCode) throws Exception {
		emptyRemoveMap(modelMap);
		
		log.debug(">>>>>>>>>>>>>>>>send : "+modelMap);
		
		gcmSender = new Sender(apiKey);
		gcmMessage = new Message.Builder().collapseKey("collapseKey" + System.currentTimeMillis()).timeToLive(3).delayWhileIdle(true).setData(modelMap).build();

		Result result = null;
		Map m = null;
		try {
			if (modelMap.get("TYPE").equals("M")) {
				modelMap.put("sender", modelMap.get("SENDER"));
				modelMap.put("receiver", modelMap.get("RECEIVER"));
				m = sqlSession.selectOne("msg_reject.select", modelMap);
			}
			if (m != null && m.size() > 0) {
				return;
			}
			if (pushCode == null || "".equals(pushCode)) {
				log.debug("invalid pushcode!!");
			} else {
				result = gcmSender.send(gcmMessage, pushCode, 5);
				log.debug("send to "+pushCode);
			}
			if (result != null && result.getMessageId() != null) {
				modelMap.clear();
				modelMap.put("MSG", "OK");
				modelMap.put("CODE", "200");
			} else {
				String error = result.getErrorCodeName();
				modelMap.clear();
				modelMap.put("MSG", "ERROR");
				modelMap.put("CODE", "1001");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void send(Map<String, String> modelMap, List<String> pushCode) throws Exception {
		emptyRemoveMap(modelMap);
		
		log.debug(">>>>>>>>>>>>>>>>send : "+modelMap);
		
		gcmSender = new Sender(apiKey);
		gcmMessage = new Message.Builder().collapseKey("collapseKey" + System.currentTimeMillis()).timeToLive(3).delayWhileIdle(true).setData(modelMap).build();

		MulticastResult result = null;
		Map m = null;
		try {
			if (modelMap.get("TYPE").equals("M")) {
				modelMap.put("sender", modelMap.get("SENDER"));
				modelMap.put("receiver", modelMap.get("RECEIVER"));
				m = sqlSession.selectOne("msg_reject.select", modelMap);
			}
			if (m != null && m.size() > 0) {
				return;
			}
			if (pushCode == null || "".equals(pushCode)) {
				log.debug("invalid pushcode!!");
			} else {
				result = gcmSender.send(gcmMessage, pushCode, 5);
				log.debug("send to "+pushCode);
			}
			for (int i = 0; i < result.getResults().size(); i++) {
				Result rst = result.getResults().get(i);
				if (rst != null && rst.getMessageId() != null) {
				} else {
					String error = rst.getErrorCodeName();
					modelMap.clear();
					modelMap.put("MSG", "ERROR");
					modelMap.put("CODE", "1001");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public void writeLog(Map<String, String> sqlMap) throws Exception {
		sqlSession.insert("setLog", sqlMap);
	}
}