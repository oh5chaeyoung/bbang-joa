package com.sweetievegan.auth.dto.request;

import lombok.Data;

@Data
public class NicknameModifyRequest {
	private String email;
	private String nickname;
}
