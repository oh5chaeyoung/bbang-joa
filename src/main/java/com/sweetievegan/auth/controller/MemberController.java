package com.sweetievegan.auth.controller;

import com.sweetievegan.auth.dto.request.NicknameModifyRequest;
import com.sweetievegan.auth.dto.request.PasswordModifyRequest;
import com.sweetievegan.auth.dto.request.EmailCheckRequest;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.service.member.MemberServiceImp;
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
	private final MemberServiceImp memberServiceImp;

	@GetMapping("/me")
	public ResponseEntity<MemberResponse> getMyMemberInfo(@AuthenticationPrincipal User user){
		MemberResponse myInfo = MemberResponse.of(memberServiceImp.getMemberDetail(user.getUsername()));
		return ResponseEntity.ok(myInfo);
	}

	@GetMapping("/me/blogs")
	public ResponseEntity<List<BlogListResponse>> getMyBlogs(@AuthenticationPrincipal User user){
		return ResponseEntity.ok(memberServiceImp.getMyBlogs(user.getUsername()));
	}

	@GetMapping("/me/recipes")
	public ResponseEntity<List<RecipeListResponse>> getMyRecipes(@AuthenticationPrincipal User user){
		return ResponseEntity.ok(memberServiceImp.getMyRecipes(user.getUsername()));
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