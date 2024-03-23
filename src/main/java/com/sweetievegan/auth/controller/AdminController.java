package com.sweetievegan.auth.controller;

import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	private final AuthService authService;

	@GetMapping("/members")
	public ResponseEntity<List<MemberResponse>> getMembers(@AuthenticationPrincipal UserDetails user) {
		log.info("{}", user.getUsername());
		log.info("{}", user.getAuthorities());
		return ResponseEntity.ok(authService.getMembers());
	}
}
