package com.sweetievegan.auth.dto.request;

import lombok.Data;

@Data
public class EmailValidCodeRequest {
	private String email;
	private String validCode;
}
