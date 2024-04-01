package com.sweetievegan.auth.controller;

import com.sweetievegan.auth.dto.request.EmailCheckRequest;
import com.sweetievegan.auth.dto.request.EmailValidCodeRequest;
import com.sweetievegan.auth.service.oauth.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
	private final EmailService emailService;
	@PostMapping("")
	public ResponseEntity<Boolean> emailSend(@RequestBody EmailCheckRequest request) {
		return ResponseEntity.ok(emailService.sendCode(request));
	}

	@PostMapping("/valid-code")
	public ResponseEntity<Boolean> emailValidCodeCheck(@RequestBody EmailValidCodeRequest request) {
		return ResponseEntity.ok(emailService.checkValidCode(request));
	}
}
