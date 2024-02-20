package com.sweetievegan.util.exception;

public enum GlobalErrorCode {
	/* POST */
	NOT_FOUND_INFO(404,"찾을 수 없는 글입니다."),
	NOT_FOUND_LIST(404,"목록을 불러오는데 실패했습니다."),
	NOT_REGISTER_POST(500,"등록에 실패했습니다."),
	NOT_FOUND_EXCEPTION(500,"알수 없는 에러가 발생했습니다."),
	/* MEMBER */
	NOT_AUTHORIZED_USER(401,"권한이 없습니다."),
	NOT_FOUND_USER(401,"다시 로그인해주세요."),
	EXIST_EMAIL(400, "이미 가입된 이메일입니다."),
	NOT_MATCH_PASSWORD(400,"비밀번호가 일치하지 않습니다.");
	private int code;
	private String description;
	private GlobalErrorCode(int code, String description) {
		this.code = code;
		this.description = description;
	}
	public int getCode() {
		return code;
	}
	public String getDescription() {
		return description;
	}
}
