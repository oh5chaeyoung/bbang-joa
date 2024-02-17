package com.sweetievegan.auth.controller;

import com.sweetievegan.auth.dto.request.NicknameModifyRequest;
import com.sweetievegan.auth.dto.request.PasswordModifyRequest;
import com.sweetievegan.auth.dto.request.EmailCheckRequest;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.service.MemberServiceImp;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
	private final MemberServiceImp memberServiceImp;

	@GetMapping("/me")
	public ResponseEntity<MemberResponse> getMyMemberInfo(@AuthenticationPrincipal User user){
		Long memberId = Long.parseLong(user.getUsername());
		MemberResponse myInfo = MemberResponse.of(memberServiceImp.getMemberDetail(memberId));
		return ResponseEntity.ok(myInfo);
	}

	@GetMapping("/me/blogs")
	public ResponseEntity<List<BlogListResponse>> getMyBlogs(@AuthenticationPrincipal User user){
		Long memberId = Long.parseLong(user.getUsername());
		return ResponseEntity.ok(memberServiceImp.getMyBlogs(memberId));
	}

	@PostMapping("/email")
	public ResponseEntity<String> checkEmail(@RequestBody EmailCheckRequest request) {
		return ResponseEntity.ok(memberServiceImp.checkEmail(request.getEmail()));
	}

	@PostMapping("/nickname")
	public ResponseEntity<MemberResponse> setMemberNickname(@RequestBody NicknameModifyRequest request){
		return ResponseEntity.ok(memberServiceImp.changeMemberNickname(request.getEmail(), request.getNickname()));
	}

	@PostMapping("/password")
	public ResponseEntity<MemberResponse> setMemberPassword(@RequestBody PasswordModifyRequest request){
		return ResponseEntity.ok(memberServiceImp.changeMemberPassword(request.getExPassword(), request.getNewPassword()));
	}
}