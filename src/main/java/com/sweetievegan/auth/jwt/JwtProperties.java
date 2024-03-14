package com.sweetievegan.auth.jwt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtProperties {
	@Value("${jwt.issuer}")
	private String issuer;
	@Value("${jwt.secret_key}")
	private String secretKey;
}
