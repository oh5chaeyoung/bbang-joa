package com.sweetievegan.auth.service.jwt;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import com.sweetievegan.auth.dto.request.MemberLoginRequest;
import com.sweetievegan.auth.dto.request.MemberRegisterRequest;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.jwt.TokenDto;
import com.sweetievegan.auth.jwt.TokenProvider;
import com.sweetievegan.util.exception.GlobalErrorCode;
import com.sweetievegan.util.exception.GlobalException;
import com.sweetievegan.util.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
	private final ImageService imageService;
	private final AuthenticationManagerBuilder managerBuilder;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	public MemberResponse signup(MemberRegisterRequest request) {
		if (memberRepository.existsByEmail(request.getEmail())) {
			throw new GlobalException(GlobalErrorCode.EXIST_EMAIL);
		}

		/* nickname ****************************/
		if(request.getNickname() == null) {
			request.setNickname("HelloUser_" + generateSevenDigitRandomNumber());
		}
		/* nickname */

		Member member = request.toMember(passwordEncoder);
		member.setId(createMemberId());
		return MemberResponse.of(memberRepository.save(member));
	}

	public TokenDto login(MemberLoginRequest requestDto) {
		UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication();
		Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);
		return tokenProvider.generateTokenDto(authentication);
	}

	public String createMemberId() {
		UUID uuid = UUID.randomUUID();
		return "user_" + uuid.toString().replace("-", "");
	}

	public String generateSevenDigitRandomNumber() {
		int randomNumber = (int) (Math.random() * 10000000);
		return String.format("%07d", randomNumber);
	}

}