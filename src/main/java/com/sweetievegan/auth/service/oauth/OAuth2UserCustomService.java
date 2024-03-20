package com.sweetievegan.auth.service.oauth;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import com.sweetievegan.auth.util.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User user = super.loadUser(userRequest);
		saveOrUpdate(user);
		return user;
	}

	private Member saveOrUpdate(OAuth2User oAuth2User) {
		Map<String, Object> attributes = oAuth2User.getAttributes();

		log.info("att = {}", attributes);

		String sub = (String) attributes.get("sub");
		String email = (String) attributes.get("email");
		String nickname = (String) attributes.get("name");
		String profile = (String) attributes.get("picture");
		Member member = memberRepository.findMemberByEmail("email")
				.map(entity -> entity.update(nickname))
				.orElse(Member.builder()
						.id(sub)
						.email(email)
						.nickname(nickname)
						.profile(profile)
						.authority(Authority.ROLE_USER)
						.provider("google")
						.isDeleted(false)
						.build());
		return memberRepository.save(member);
	}

	public String createMemberId() {
		UUID uuid = UUID.randomUUID();
		return "user_" + uuid.toString().replace("-", "");
	}
}
