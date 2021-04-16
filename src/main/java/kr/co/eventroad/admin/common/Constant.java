package kr.co.eventroad.admin.common;

public class Constant {
	// 테스트용
	public static final String SEQ_USER = "131";
	public static final String PW = "1111";
	public static final String EMAIL = "test";
	public static final String NICKNAME = "testNickname";
	public static final String SEQ_BOARD = "21";
	public static final boolean IS_TEST = true;
	
	// API key
//	public static final String API_KEY = "AIzaSyDDodNu65XgwUNzwr0U-Kw64UFEQIy6qiA";
	public static final String API_KEY = "AIzaSyA56QRjQKV0uXdO7Fs2wDGOZWfcJ2kk3EM";
	
	// 리턴 메시지/코드 정의
	public static final String CODE_OK = "100";
	public static final String CODE_UNKNOWN = "300";
	public static final String CODE_INVALID_PARAMS = "301";
	public static final String CODE_INVALID_EMAIL = "302";
	public static final String CODE_INVALID_PASSWD = "303";
	public static final String CODE_NO_RETURN = "305";
	public static final String CODE_SESSION_EXPIRED = "306";
	public static final String CODE_MISSING_PARAMS = "307";
	public static final String CODE_ACCOUNT_ROCK_PARAMS = "308";
	public static final String CODE_INSERT_FAIL = "309";

	public static final String MSG_OK = "OK";
	public static final String MSG_UNKNOWN = "네트워크가 불안정합니다. 조금 후에 재시도 해주세요.";//300
	public static final String MSG_INVALID_PARAMS = "매배변수가 올바르지 않습니다.정확한 정보를 입력해 주세요.";//301
	public static final String MSG_INVALID_EMAIL = "이미 가입된 EMAIL 입니다.";//302
	public static final String MSG_INVALID_PASSWD = "";//303
	public static final String MSG_NO_RETURN = "찾는 값이 없습니다.";//305
	public static final String MSG_SESSION_EXPIRED = "세션이 없습니다.다시 로그인해주세요.";//305
	public static final String MSG_MISSING_PARAMS = "매개변수가 누락되었습니다.";//307
	public static final String MSG_ACCOUNT_ROCK_PARAMS = "308"; // 308
	public static final String MSG_INSERT_FAIL = "입력에 실패했습니다"; // 309
	
	
	//realserver resin
//	public static final String IMAGE_PROFILE_IMAGE_UPLOAD = "E:/resin-pro-4.0.36/webapps/img/upload/"; //업로드 되는 회원 프로필 이미지 위치 (서버)
//	public static final String IMAGE_PROFILE_IMAGE_RETURN = "http://218.145.27.16:8080/img/upload/"; //리턴 되는 회원 프로필 이미지 위치 (클라이언트)
//	public static final String IMAGE_PROFILE_IMAGE_UPLOAD = "/resin-4.0.40/webapps/img/upload/"; //업로드 되는 회원 프로필 이미지 위치 (서버)
//	public static final String IMAGE_PROFILE_IMAGE_RETURN = "http://112.175.230.9:8080/img/upload/"; //리턴 되는 회원 프로필 이미지 위치 (클라이언트)
//	public static final String IMAGE_PROFILE_IMAGE_UPLOAD = "/tomcat/webapps/img/res/upload/"; //업로드 되는 회원 프로필 이미지 위치 (서버)
//	public static final String IMAGE_PROFILE_IMAGE_RETURN = "http://115.68.135.11:8080/img/res/upload/"; //리턴 되는 회원 프로필 이미지 위치 (클라이언트)
	
	
//	public static final String IMAGE_PROFILE_IMAGE_UPLOAD = "c:/img/upload/"; //업로드 되는 회원 프로필 이미지 위치 (서버)
//	public static final String IMAGE_PROFILE_IMAGE_RETURN = "http://test-ilbang.iptime.org:8080/img/res/"; //리턴 되는 회원 프로필 이미지 위치 (클라이언트)
	
	// mmcp test server
//	public static final String IMAGE_PROFILE_IMAGE_UPLOAD = "C:/Program Files/Apache Software Foundation/Tomcat 8.5/webapps/img/resources/"; //업로드 되는 회원 프로필 이미지 위치 (서버)
//	public static final String IMAGE_PROFILE_IMAGE_RETURN = "http://test-ilbang.iptime.org:8080/img/res/"; //리턴 되는 회원 프로필 이미지 위치 (클라이언트)
	
	// test serbver
//	public static final String IMAGE_PROFILE_IMAGE_UPLOAD = "C:/img/resources/"; //업로드 되는 회원 프로필 이미지 위치 (서버)
//	public static final String IMAGE_PROFILE_IMAGE_RETURN = "http://58.229.208.246/img/res/ububa/"; //리턴 되는 회원 프로필 이미지 위치 (클라이언트)
		
	// real serbver
	public static final String IMAGE_PROFILE_IMAGE_UPLOAD = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/webapps/img/resources/ububa/"; //업로드 되는 회원 프로필 이미지 위치 (서버)
	public static final String IMAGE_PROFILE_IMAGE_RETURN = "http://58.229.208.246/img/res/ububa/"; //리턴 되는 회원 프로필 이미지 위치 (클라이언트)
	
	
	//4server resin
	/*public static final String IMAGE_PROFILE_IMAGE_UPLOAD = "D:/apps/resin-4.0.40/webapps/img/upload/"; //업로드 되는 회원 프로필 이미지 위치 (서버)
	public static final String IMAGE_PROFILE_IMAGE_RETURN = "http://58.123.178.202:48080/img/upload/"; //리턴 되는 회원 프로필 이미지 위치 (클라이언트)
	 */
	// 30server tomcat 7
	/*
	public static final String IMAGE_PROFILE_IMAGE_UPLOAD = "D:/apps/tomcat7/webapps/img/upload/"; //업로드 되는 회원 프로필 이미지 위치 (서버)
	public static final String IMAGE_PROFILE_IMAGE_RETURN = "http://58.123.178.202:18080/img/upload/"; //리턴 되는 회원 프로필 이미지 위치 (클라이언트)
*/}
