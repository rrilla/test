package kr.co.eventroad.admin.common;

/**
 * @author kihyuk.yoon
 * @since 2014. 11. 10.
 */
public enum ResultCode {
	RESULT_OK("200", "OK"),
	RESULT_MISSED_MANDATORY("101", "Missed mandatory parameter"),
	RESULT_WRONG_VALUE("102", "Wrong or does not exist data"),
	
	RESULT_ALREADY_RESGISTERD_USER("301", "This user has already been registered"),
	RESULT_ACCOUNT_DELETED("302", "This account has been deleted"),
	
	RESULT_ALREADY_USER_DUPLICATE_ID("230", "This id has already"),
	RESULT_ALREADY_USER_DUPLICATE_EMAIL("235", "This email has already"),
	RESULT_ALREADY_USER_DUPLICATE_NICKNAME("250", "This nickname has already"),
	
	RESULT_ALREADY_PUSH("260", "This push has already"),
	
	RESULT_REPLY_FAIL("307", "Not Reply"),
	
	RESULT_NOT_FOUND_VALUE("460", "Not found data"),
	
	RESULT_USER_NOT_UPDATE("601", "Update fail"),
	
	ADMIN_NOT_INSERT_BOARD("701", "Board Insert fail"),
	AREADY_INSERT("311", "AREADY INSERT"),
	;
	
	private String code;
	private String msg;

	private ResultCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}