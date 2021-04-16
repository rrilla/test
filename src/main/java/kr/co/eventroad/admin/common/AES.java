package kr.co.eventroad.admin.common;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;


public class AES {

	private Key getKey() throws Exception {
//		Key secureKey = new SecretKeySpec("com.kt.goodfood".getBytes(), "AES");
		Key secureKey = new SecretKeySpec("com.kt.goodfood.com.kt.goodfood.".getBytes(), "AES");
		return secureKey;
	}

	public String encrypt(String str) throws Exception {
		if (str == null || str.length() == 0)
			return "";

		String instance = "AES/ECB/PKCS5Padding";
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(instance);
		cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, getKey());

		byte[] outputBytes1 = cipher.doFinal(str.getBytes("UTF8"));
		
		Encoder encoder = Base64.getEncoder();
		String ret = new String(encoder.encode(outputBytes1));
		return ret;

//		BASE64Encoder encoder = new BASE64Encoder();
//		String ret = encoder.encode(outputBytes1);
//		return ret;
	}
	
//	 encrypmap 과 decrypmap 을 할 때 변수 하나 더 받아서 처리	

	public String decrypt(String str) throws Exception {
		if (str == null || str.length() == 0)
			return "";

		String instance = "AES/ECB/PKCS5Padding";
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(instance);
		cipher.init(javax.crypto.Cipher.DECRYPT_MODE, getKey());

		Decoder decoder = Base64.getDecoder();
		byte[] inputBytes1 = decoder.decode(str);
		
		byte[] outputBytes1 = cipher.doFinal(inputBytes1);
		
//		BASE64Decoder decoder = new BASE64Decoder();
//		byte[] inputBytes1 = decoder.decodeBuffer(str);
//
//		byte[] outputBytes1 = cipher.doFinal(inputBytes1);

		String strResult = new String(outputBytes1, "UTF8");
		return strResult;
	}

	public String encryptMap(Map map) throws Exception {
		String transformation = "AES/ECB/PKCS5Padding";
		Cipher cipher = Cipher.getInstance(transformation);
		cipher.init(Cipher.ENCRYPT_MODE, getKey());
		byte[] mapByte = getObjectAsBytes(map);
		byte[] encrypt = cipher.doFinal(mapByte);

		Encoder encoder = Base64.getEncoder();
		String outputStr1 = new String(encoder.encode(encrypt));
		
//		BASE64Encoder encoder = new BASE64Encoder();
//		String outputStr1 = encoder.encode(encrypt);
		return outputStr1;
	}

	public Map decryptMap(String codedID) throws Exception {
		String transformation = "AES/ECB/PKCS5Padding";
		Cipher cipher = Cipher.getInstance(transformation);
		cipher.init(Cipher.DECRYPT_MODE, getKey());
		
		Decoder decoder = Base64.getDecoder();
		byte[] inputBytes1 = decoder.decode(codedID);
		
//		BASE64Decoder decoder = new BASE64Decoder();
//		byte[] inputBytes1 = decoder.decodeBuffer(codedID);

		byte[] decrypt = cipher.doFinal(inputBytes1);
		Map map = (Map) toObject(decrypt);
		return map;
	}

	public Object toObject(byte[] bytes) {
		Object object = null;
		try {
			object = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes)).readObject();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		} catch (java.lang.ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		return object;
	}

	public byte[] getObjectAsBytes(final Object obj) {
		java.io.ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			bos.close();
			return bos.toByteArray();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*public static void main(String[] args) {
		AES as = new AES();
		
		String str = "안녕하세요";
		try {
			str = as.encrypt(str);
		
		System.out.println(str);
		
		str = as.decrypt(str);
		System.out.println(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
