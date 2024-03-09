package com.sweetievegan.auth.controller;

import com.sweetievegan.auth.dto.request.MemberSummayModifyRequest;
import com.sweetievegan.auth.dto.request.NicknameModifyRequest;
import com.sweetievegan.auth.dto.request.PasswordModifyRequest;
import com.sweetievegan.auth.dto.request.EmailCheckRequest;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.service.member.MemberService;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import com.sweetievegan.recipe.dto.response.RecipeListResponse;
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
	private final MemberService memberService;

	@GetMapping("/me")
	public ResponseEntity<MemberResponse> getMyMemberInfo(@AuthenticationPrincipal User user){
		return ResponseEntity.ok(memberService.getMemberDetail(user.getUsername()));
	}

	@GetMapping("/me/blogs")
	public ResponseEntity<List<BlogListResponse>> getMyBlogs(@AuthenticationPrincipal User user){
		return ResponseEntity.ok(memberService.getMyBlogs(user.getUsername()));
	}

	@GetMapping("/me/recipes")
	public ResponseEntity<List<RecipeListResponse>> getMyRecipes(@AuthenticationPrincipal User user){
		return ResponseEntity.ok(memberService.getMyRecipes(user.getUsername()));
	}

	@PostMapping("/email")
	public ResponseEntity<String> checkEmail(@RequestBody EmailCheckRequest request) {
		return ResponseEntity.ok(memberService.checkEmail(request.getEmail()));
	}

	@PostMapping("/nickname")
	public ResponseEntity<MemberResponse> setMemberNickname(@RequestBody NicknameModifyRequest request){
		return ResponseEntity.ok(memberService.changeMemberNickname(request.getEmail(), request.getNickname()));
	}

	@PostMapping("/password")
	public ResponseEntity<MemberResponse> setMemberPassword(@RequestBody PasswordModifyRequest request){
		return ResponseEntity.ok(memberService.changeMemberPassword(request.getExPassword(), request.getNewPassword()));
	}

	@PostMapping("/summary")
	public ResponseEntity<MemberResponse> setMemberSummary(@AuthenticationPrincipal User user,
	                                                       @RequestBody MemberSummayModifyRequest request){
		return ResponseEntity.ok(memberService.changeMemberSummary(user.getUsername(), request.getSummary()));
	}
}