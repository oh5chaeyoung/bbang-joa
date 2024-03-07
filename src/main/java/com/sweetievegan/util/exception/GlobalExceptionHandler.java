package com.sweetievegan.util.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<String> handlePostException(GlobalException ex) {
		return ResponseEntity.status(ex.getCode()).body(ex.getMessage());
	}
}
