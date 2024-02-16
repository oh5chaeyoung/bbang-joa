package com.sweetievegan.auth.service;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.config.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public MemberResponse getMyInfoBySecurity(){
		return memberRepository.findById(SecurityUtil.getCurrentMemberId())
				.map(MemberResponse::of)
				.orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));
	}

	@Transactional
	public MemberResponse changeMemberNickname(String email, String nickname){
		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("로그인 유정 정보가 없습니다."));
		member.setNickname(nickname);
		return MemberResponse.of(memberRepository.save(member));
	}

	@Transactional
	public MemberResponse changeMemberPassword(String email, String exPassword, String newPassword){
		Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
				.orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));

		if(!passwordEncoder.matches(exPassword, member.getPassword())){
			throw new RuntimeException("비밀번호가 맞지 않습니다.");
		}
		member.setPassword(passwordEncoder.encode((newPassword)));
		return MemberResponse.of(memberRepository.save(member));
	}

	public Member getMemberDetail(Long id) {
		Member member = memberRepository.findMemberById(id);
		if (member == null) {
			throw new RuntimeException("로그인 유저 정보가 없습니다.");
		}
		return member;
	}
}
