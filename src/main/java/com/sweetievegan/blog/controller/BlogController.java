package com.sweetievegan.blog.controller;

import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.auth.service.MemberService;
import com.sweetievegan.blog.dto.request.BlogRegisterRequest;
import com.sweetievegan.blog.dto.response.BlogDetailResponse;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import com.sweetievegan.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogController {
	private final MemberService memberService;
	private final BlogService blogService;

	@GetMapping("")
	public ResponseEntity<List<BlogListResponse>> getBlogList() {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.getAllBlogs());
	}

	@GetMapping("/{blogId}")
	public ResponseEntity<BlogDetailResponse> getBlogById(@PathVariable("blogId") Long blogId) {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.findBlogByBlogId(blogId));
	}

	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Long> blogAdd(
			@RequestPart(value = "file", required = false) List<MultipartFile> file,
			@RequestPart BlogRegisterRequest request,
			@AuthenticationPrincipal User user) {
		Long memberId = Long.parseLong(user.getUsername());
		return ResponseEntity.status(HttpStatus.OK).body(blogService.addBlog(request, file, memberId));
	}

	@PutMapping("/{blogId}")
	public ResponseEntity<BlogRegisterRequest> blogModify(
			@PathVariable("blogId") Long blogId,
			@RequestBody BlogRegisterRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.updateBlogDetail(blogId, request));
	}

	@DeleteMapping("/{blogId}")
	public ResponseEntity<Long> blogRemove(@PathVariable("blogId") Long blogId) {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.removeBlog(blogId));
	}
}
