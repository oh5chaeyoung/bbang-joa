package com.sweetievegan.blog.controller;

import com.sweetievegan.blog.dto.request.BlogRegisterRequest;
import com.sweetievegan.blog.dto.response.BlogDetailResponse;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import com.sweetievegan.blog.service.BlogService;
import com.sweetievegan.util.exception.GlobalErrorCode;
import com.sweetievegan.util.exception.GlobalException;
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
	private final BlogService blogService;

	@GetMapping("")
	public ResponseEntity<List<BlogListResponse>> blogList() {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.getAllBlogs());
	}

	@GetMapping("/{blogId}")
	public ResponseEntity<BlogDetailResponse> blogDetails(@PathVariable("blogId") Long blogId) {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.findBlogByBlogId(blogId));
	}

	@GetMapping("/search")
	public ResponseEntity<List<BlogListResponse>> blogListByTitleKeyword(@RequestParam(value = "keyword") String keyword) {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.findBlogsByKeyword(keyword));
	}

	@GetMapping("/all-count")
	public ResponseEntity<Long> blogCounts() {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.getAllBlogsCount());
	}

	@GetMapping("/all-ids")
	public ResponseEntity<List<Long>> blogIdList() {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.getAllBlogIds());
	}

	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Long> blogAdd(
			@RequestPart(value = "file", required = false) List<MultipartFile> file,
			@RequestPart BlogRegisterRequest request,
			@AuthenticationPrincipal User user) {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.addBlog(request, file, user.getUsername()));
	}

	@PutMapping(value = "/{blogId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<BlogDetailResponse> blogModify(
			@PathVariable("blogId") Long blogId,
			@RequestPart(value = "file", required = false) List<MultipartFile> file,
			@RequestPart BlogRegisterRequest request,
			@AuthenticationPrincipal User user) {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.updateBlog(user.getUsername(), blogId, request, file));
	}

	@DeleteMapping("/{blogId}")
	public ResponseEntity<Long> blogRemove(
			@PathVariable("blogId") Long blogId,
			@AuthenticationPrincipal User user) {
		if(user == null)
			throw new GlobalException(GlobalErrorCode.NOT_AUTHORIZED_USER);
		return ResponseEntity.status(HttpStatus.OK).body(blogService.removeBlog(user.getUsername(), blogId));
	}
}
