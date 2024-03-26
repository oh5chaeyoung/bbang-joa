package com.sweetievegan.auth.controller;

import com.sweetievegan.auth.dto.request.EmailCheckRequest;
import com.sweetievegan.auth.dto.request.MemberLoginRequest;
import com.sweetievegan.auth.dto.request.MemberRegisterRequest;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.dto.response.AccessTokenResponse;
import com.sweetievegan.auth.service.oauth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/email")
	public ResponseEntity<Boolean> emailCheck(@RequestBody EmailCheckRequest request) {
		return ResponseEntity.ok(authService.checkEmail(request.getEmail()));
	}

	@PostMapping("/signup")
	public ResponseEntity<MemberResponse> signup(@RequestBody MemberRegisterRequest request) {
		return ResponseEntity.ok(authService.signup(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AccessTokenResponse> login(@RequestBody MemberLoginRequest requestDto) {
		return ResponseEntity.ok(authService.login(requestDto));
	}
}
