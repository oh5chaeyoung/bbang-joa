package com.sweetievegan.util.exception;

public class GlobalException extends RuntimeException {
	private final int code;
	public int getCode() {
		return code;
	}

	public GlobalException(GlobalErrorCode errorCode) {
		super(errorCode.getDescription());
		this.code = errorCode.getCode();
	}
}
