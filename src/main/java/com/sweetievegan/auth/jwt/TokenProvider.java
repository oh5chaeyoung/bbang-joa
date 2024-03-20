package com.sweetievegan.auth.jwt;

import com.sweetievegan.auth.domain.entity.Member;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
	private final JwtProperties jwtProperties;

	public String generateToken(Member member, Duration expiredAt) {
		Date now = new Date();
		return makeToken(new Date(now.getTime() + expiredAt.toMillis()), member);
	}

	private String makeToken(Date expiry, Member member) {
		Date now = new Date();

		log.info("member = {}", member);

		Claims claims = Jwts.claims().setSubject(member.getId());

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expiry)
//				.claim("id", member.getId())
				.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
				.compact();
	}

	public boolean validToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(jwtProperties.getSecretKey())
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);
		Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

		return new UsernamePasswordAuthenticationToken(new User(claims.getSubject(), "", authorities), token, authorities);
	}

	public String getUserId(String token) {
		Claims claims = getClaims(token);
		return claims.get("id", String.class);
	}

	private Claims getClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(jwtProperties.getSecretKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
}
