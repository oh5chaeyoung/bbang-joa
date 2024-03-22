package com.sweetievegan.auth.jwt;

import com.sweetievegan.auth.domain.entity.Member;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
	private final JwtProperties jwtProperties;
	private static final String AUTHORITIES_KEY = "auth";

	public String generateToken(Member member, Duration expiredAt) {
		Date now = new Date();
		return makeToken(new Date(now.getTime() + expiredAt.toMillis()), member);
	}

	private String makeToken(Date expiry, Member member) {
		Date now = new Date();

		return Jwts.builder()
				.setSubject(member.getId())
				.claim(AUTHORITIES_KEY, member.getAuthority())
				.setIssuedAt(now)
				.setExpiration(expiry)
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
		Collection<? extends GrantedAuthority> authorities =
				Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());
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
