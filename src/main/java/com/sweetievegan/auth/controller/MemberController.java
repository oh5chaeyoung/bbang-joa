package com.sweetievegan.auth.controller;

import com.sweetievegan.auth.dto.request.ChangePasswordRequest;
import com.sweetievegan.auth.dto.request.EmailCheckRequest;
import com.sweetievegan.auth.dto.request.MemberRegisterRequest;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;
/*
	@GetMapping("/me")
	public ResponseEntity<MemberResponseDto> getMyMemberInfo(){
		MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
		return ResponseEntity.ok(myInfoBySecurity);
	}

 */
	@PostMapping("/email")
	public ResponseEntity<String> checkEmail(@RequestBody EmailCheckRequest request) {
		return ResponseEntity.ok(memberService.checkEmail(request.getEmail()));
	}

	@PostMapping("/nickname")
	public ResponseEntity<MemberResponse> setMemberNickname(@RequestBody MemberRegisterRequest requestDto){
		return ResponseEntity.ok(memberService.changeMemberNickname(requestDto.getEmail(),requestDto.getNickname() ));
	}

	@PostMapping("/password")
	public ResponseEntity<MemberResponse> setMemberPassword(@RequestBody ChangePasswordRequest requestDto){
		return ResponseEntity.ok(memberService.changeMemberPassword(requestDto.getEmail(),requestDto.getExPassword(), requestDto.getNewPassword()));
	}
}