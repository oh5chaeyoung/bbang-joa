package com.sweetievegan.config;

import com.sweetievegan.auth.jwt.JwtAccessDeniedHandler;
import com.sweetievegan.auth.jwt.JwtAuthenticationEntryPoint;
import com.sweetievegan.auth.updatedjwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Component
public class WebSecurityConfig {
	private final TokenProvider tokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.cors().and()
				.httpBasic().disable()
				.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and()
				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler)

				.and()
				.authorizeRequests()
				.antMatchers("/auth/**").permitAll()
				.antMatchers("/members/email").permitAll()
				.antMatchers(HttpMethod.GET, "/recipes").permitAll()
				.antMatchers(HttpMethod.GET, "/blogs").permitAll()
				.antMatchers(HttpMethod.GET, "/recipes/**").permitAll()
				.antMatchers(HttpMethod.GET, "/blogs/**").permitAll()
//				.antMatchers("/recipes/**").hasRole("USER")
				.anyRequest().authenticated()

				.and()
				.apply(new JwtSecurityConfig(tokenProvider));

		return http.build();
	}
}
