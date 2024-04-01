package com.sweetievegan.config;

import com.sweetievegan.auth.jwt.*;
import com.sweetievegan.auth.service.oauth.OAuth2UserCustomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Component
public class WebSecurityConfig {
	private final TokenProvider tokenProvider;
	private final OAuth2UserCustomService oAuth2UserCustomService;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and()
				.httpBasic().disable()
				.csrf().disable()
				.formLogin().disable()
				.logout().disable();

		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		http.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler);

		http.authorizeRequests()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/auth/**").permitAll()
				.antMatchers("/email/**").permitAll()
				.antMatchers(HttpMethod.GET, "/recipes").permitAll()
				.antMatchers(HttpMethod.GET, "/blogs").permitAll()
				.antMatchers(HttpMethod.GET, "/recipes/**").permitAll()
				.antMatchers(HttpMethod.GET, "/blogs/**").permitAll()
				.anyRequest().authenticated();

		http.oauth2Login()
				.userInfoEndpoint()
				.userService(oAuth2UserCustomService)
				.and()
				.successHandler(oAuth2SuccessHandler);


		return http.build();
	}

	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter(tokenProvider);
	}
}
