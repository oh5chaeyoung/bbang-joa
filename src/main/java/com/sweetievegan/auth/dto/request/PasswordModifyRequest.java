package com.sweetievegan.auth.dto.request;

import lombok.Data;

@Data
public class PasswordModifyRequest {
	private String exPassword;
	private String newPassword;
}