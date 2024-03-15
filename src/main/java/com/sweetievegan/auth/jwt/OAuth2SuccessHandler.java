package com.sweetievegan.auth.jwt;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
	private static final String REDIRECT_PATH = "/blogs";

	private final TokenProvider tokenProvider;
	private final MemberRepository memberRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		Member member = memberRepository.findMemberById((String) oAuth2User.getAttributes().get("sub"));

		String accessToken = tokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
		String targetUrl = getTargetUrl(accessToken);

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	private String getTargetUrl(String token) {
		return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
				.queryParam("accessToken", token)
				.build()
				.toUriString();
	}
}
