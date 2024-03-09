package com.sweetievegan.auth.dto.request;

import lombok.Data;

@Data
public class MemberPasswordModifyRequest {
	private String exPassword;
	private String newPassword;
}