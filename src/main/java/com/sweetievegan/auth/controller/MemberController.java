package com.sweetievegan.auth.controller;

import com.sweetievegan.auth.dto.request.MemberSummayModifyRequest;
import com.sweetievegan.auth.dto.request.MemberNicknameModifyRequest;
import com.sweetievegan.auth.dto.request.MemberPasswordModifyRequest;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.service.member.MemberService;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import com.sweetievegan.recipe.dto.response.RecipeListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/members/me")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;

	@GetMapping("")
	public ResponseEntity<MemberResponse> memberDetails(@AuthenticationPrincipal User user){
		return ResponseEntity.ok(memberService.findMemberByMemberId(user.getUsername()));
	}

	@GetMapping("blogs")
	public ResponseEntity<List<BlogListResponse>> myBlogList(@AuthenticationPrincipal User user){
		return ResponseEntity.ok(memberService.findBlogsByMemberId(user.getUsername()));
	}

	@GetMapping("recipes")
	public ResponseEntity<List<RecipeListResponse>> myRecipeList(@AuthenticationPrincipal User user){
		return ResponseEntity.ok(memberService.findRecipesByMemberId(user.getUsername()));
	}

	@PostMapping("/nickname")
	public ResponseEntity<MemberResponse> myNicknameModify(@AuthenticationPrincipal User user,
	                                                       @RequestBody MemberNicknameModifyRequest request){
		return ResponseEntity.ok(memberService.changeMemberNickname(user.getUsername(), request.getNickname()));
	}

	@PostMapping("/password")
	public ResponseEntity<MemberResponse> myPasswordModify(@AuthenticationPrincipal User user,
	                                                       @RequestBody MemberPasswordModifyRequest request){
		return ResponseEntity.ok(memberService.changeMemberPassword(user.getUsername(), request.getExPassword(), request.getNewPassword()));
	}

	@PostMapping("/summary")
	public ResponseEntity<MemberResponse> mySummaryModify(@AuthenticationPrincipal User user,
	                                                      @RequestBody MemberSummayModifyRequest request){
		return ResponseEntity.ok(memberService.changeMemberSummary(user.getUsername(), request.getSummary()));
	}

	@PostMapping(value = "/profile", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<MemberResponse> myProfileModify(@AuthenticationPrincipal User user,
	                                                      @RequestPart(value = "file", required = true) MultipartFile file){
		return ResponseEntity.ok(memberService.changeMemberProfile(user.getUsername(), file));
	}
}