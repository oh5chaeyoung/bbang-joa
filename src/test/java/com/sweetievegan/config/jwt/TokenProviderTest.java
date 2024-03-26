package com.sweetievegan.config.jwt;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import com.sweetievegan.auth.jwt.JwtProperties;
import com.sweetievegan.auth.jwt.TokenProvider;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
class TokenProviderTest {
	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private JwtProperties jwtProperties;

	@DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 생성한다.")
	@Test
	void generateToken() {
		Member testMember = memberRepository.findMemberById("test");

		String token = tokenProvider.generateToken(testMember, Duration.ofDays(14));

		String memberId = Jwts.parserBuilder()
				.setSigningKey(jwtProperties.getSecretKey())
				.build()
				.parseClaimsJws(token)
				.getBody()
						.getSubject();

		log.info("token = {}", token);

		assertThat(memberId).isEqualTo(testMember.getId());
	}

	@DisplayName("validToken(): 만료된 토큰이면 유효성 검증에 실패한다.")
	@Test
	void validToken_invalidToken() {
		String token = JwtFactory.builder()
				.expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
				.build().createToken(jwtProperties);

		boolean result = tokenProvider.validToken(token);

		assertThat(result).isFalse();
	}

	@DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다.")
	@Test
	void getAuthentication() {
		String memberId = "test";
		String role = "ROLE_USER";
		String token = JwtFactory.builder()
				.subject(memberId)
				.claims(Map.of("auth", role))
				.build()
				.createToken(jwtProperties);

		Authentication authentication = tokenProvider.getAuthentication(token);

		assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(memberId);
	}

	@DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
	@Test
	void getUserId() {
		String memberId = "test";
		String role = "ROLE_USER";
		String token = JwtFactory.builder()
				.subject(memberId)
				.claims(Map.of("auth", role))
				.build()
				.createToken(jwtProperties);

		String userIdByToken = tokenProvider.getUserId(token);

		assertThat(userIdByToken).isEqualTo(memberId);
	}
}
