package com.sweetievegan.auth.controller;

import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.service.oauth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	private final AuthService authService;

	@GetMapping("/members")
	public ResponseEntity<List<MemberResponse>> memberList() {
		return ResponseEntity.ok(authService.getAllMembers());
	}

	@PutMapping("/blogs/{blogId}")
	public ResponseEntity<Boolean> blogBlock(@PathVariable("blogId") Long blogId) {
		return ResponseEntity.ok(authService.blockBlog(blogId));
	}

	@PutMapping("/recipes/{recipeId}")
	public ResponseEntity<Boolean> recipeBlock(@PathVariable("recipeId") Long recipeId) {
		return ResponseEntity.ok(authService.blockRecipe(recipeId));
	}
}
