package com.sweetievegan.auth.service.oauth;

import com.sweetievegan.auth.domain.entity.CustomUserDetails;
import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
	private final MemberRepository memberRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findById(username).orElseThrow(() -> new IllegalArgumentException(("가입 안 된 사용자 ::: " + username)));
		return new CustomUserDetails(member);
	}
}
