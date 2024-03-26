package com.sweetievegan.auth.service.oauth;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import com.sweetievegan.auth.dto.request.MemberLoginRequest;
import com.sweetievegan.auth.dto.request.MemberRegisterRequest;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.dto.response.AccessTokenResponse;
import com.sweetievegan.auth.jwt.TokenProvider;
import com.sweetievegan.util.exception.GlobalErrorCode;
import com.sweetievegan.util.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImp implements AuthService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	@Override
	public boolean checkEmail(String email) {
		return memberRepository.existsByEmail(email);
	}

	@Override
	public List<MemberResponse> getAllMembers() {
		List<Member> members = memberRepository.findAll();
		List<MemberResponse> responses = new ArrayList<>();
		for(Member member : members) {
			responses.add(MemberResponse.of(member));
		}
		return responses;
	}

	@Override
	public MemberResponse signup(MemberRegisterRequest request) {
		if (memberRepository.existsByEmail(request.getEmail())) {
			throw new GlobalException(GlobalErrorCode.EXIST_EMAIL);
		}

		/* nickname ****************************/
		if(request.getNickname() == null) {
			request.setNickname("HelloUser_" + createSevenDigitRandomNumber());
		}
		/* nickname */

		Member member = request.toMember(passwordEncoder);
		member.setId(createMemberId());
		return MemberResponse.of(memberRepository.save(member));
	}

	@Override
	public AccessTokenResponse login(MemberLoginRequest request) {
		Member member = memberRepository.findMemberByEmail(request.getEmail())
				.orElseThrow(() -> new BadCredentialsException("잘못된 계정정보입니다."));
		if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new BadCredentialsException("잘못된 계정정보입니다.");
		}

		long tokenExpiresIn = Duration.ofHours(2).toMillis();
		String token = tokenProvider.generateToken(member, Duration.ofHours(2));
		return AccessTokenResponse.builder()
				.accessToken(token)
				.tokenExpiresIn(tokenExpiresIn)
				.build();
	}

	private String createMemberId() {
		UUID uuid = UUID.randomUUID();
		return "user_" + uuid.toString().replace("-", "");
	}

	private String createSevenDigitRandomNumber() {
		int randomNumber = (int) (Math.random() * 10000000);
		return String.format("%07d", randomNumber);
	}

}