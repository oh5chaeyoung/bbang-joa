package com.sweetievegan.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenResponse {
	private String accessToken;
	private long tokenExpiresIn;
}
