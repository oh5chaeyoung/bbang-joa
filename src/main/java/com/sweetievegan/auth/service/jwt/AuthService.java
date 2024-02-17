package com.sweetievegan.auth.service.jwt;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import com.sweetievegan.auth.dto.request.MemberLoginRequest;
import com.sweetievegan.auth.dto.request.MemberRegisterRequest;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.jwt.TokenDto;
import com.sweetievegan.auth.jwt.TokenProvider;
import com.sweetievegan.blog.service.BlogImageService;
import com.sweetievegan.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
	private final BlogImageService blogImageService;
	private final AuthenticationManagerBuilder managerBuilder;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	public MemberResponse signup(MemberRegisterRequest request, MultipartFile file) {
		if (memberRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("이미 가입되어 있는 유저입니다");
		}

		Member member = request.toMember(passwordEncoder);
		member.setProfile(blogImageService.addOneFile(file, "member"));
		return MemberResponse.of(memberRepository.save(member));
	}

	public TokenDto login(MemberLoginRequest requestDto) {
		UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication();
		Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);
		return tokenProvider.generateTokenDto(authentication);
	}

}