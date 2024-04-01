package com.sweetievegan.blog.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetievegan.auth.jwt.TokenProvider;
import com.sweetievegan.blog.dto.request.BlogRegisterRequest;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import com.sweetievegan.blog.domain.entity.Blog;
import com.sweetievegan.blog.domain.repository.BlogImageRepository;
import com.sweetievegan.blog.domain.repository.BlogRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class BlogControllerTest {
	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	BlogRepository blogRepository;
	@Autowired
	BlogImageRepository blogImageRepository;

	Member member;

	@Autowired
	private TokenProvider tokenProvider;

	@BeforeEach
	void setSecurityContext() {
		member = memberRepository.findMemberById("test");

		String token = tokenProvider.generateToken(member, Duration.ofDays(14));

		Authentication authentication = tokenProvider.getAuthentication(token);

		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authentication);
	}

	@BeforeEach
	public void mockMvcSetUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@BeforeEach
	void deleteBlogs() {
		blogImageRepository.deleteAll();
		blogRepository.deleteAll();
	}
	@DisplayName("addBlog : 블로그 글 등록에 성공한다.")
	@Test
	void addBlog() throws Exception {
		final String url = "/blogs";
		int num = createRandomNumber();
		BlogRegisterRequest requestDto = BlogRegisterRequest.builder()
				.title("title_" + num)
				.content("content_" + num)
				.tags("tag_" + num)
				.summary("summary_" + num)
				.build();
		String requestJson = new ObjectMapper().writeValueAsString(requestDto);
		MockMultipartFile request = new MockMultipartFile("request", "request", "application/json", requestJson.getBytes(StandardCharsets.UTF_8));

		MockMultipartFile file1 = new MockMultipartFile("file", "testFile1.txt", "text/plain", "Test file content 1".getBytes());

		mockMvc.perform(multipart(url)
							.file(file1)
							.file(request)
				)
				.andExpect(status().isOk()) ;
	}

	@DisplayName("modifyBlog : 블로그 글 수정에 성공한다.")
	@Test
	void modifyBlog() throws Exception {
		final String url = "/blogs/{blogId}";
		int num = createRandomNumber();
		Blog savedBlog = createDefaultBlog();
		BlogRegisterRequest requestDto = BlogRegisterRequest.builder()
				.title("modified title_" + num)
				.content("modified content_" + num)
				.tags("modified tag_" + num)
				.summary("modified summary_" + num)
				.build();
		String requestJson = new ObjectMapper().writeValueAsString(requestDto);
		MockMultipartFile request = new MockMultipartFile("request", "request", "application/json", requestJson.getBytes(StandardCharsets.UTF_8));

		MockMultipartFile file1 = new MockMultipartFile("file", "testFile1.txt", "text/plain", "Test file content 1".getBytes());

		mockMvc.perform(
						MockMvcRequestBuilders
								.multipart(HttpMethod.PUT, url, savedBlog.getId())
							.file(file1)
							.file(request)
				)
				.andExpect(status().isOk()) ;
	}

	@DisplayName("findAllBlogs : 블로그 목록 조회에 성공한다.")
	@Test
	void findAllBlogs() throws Exception {
		final String url = "/blogs";
		Blog savedBlog = createDefaultBlog();

		final ResultActions resultActions = mockMvc.perform(get(url)
													.accept(MediaType.APPLICATION_JSON));
		log.info("ResultActions: {}", resultActions.andReturn().getResponse().getContentAsString());

		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value(savedBlog.getTitle()));
	}

	@DisplayName("findBlog : 블로그 글 조회에 성공한다.")
	@Test
	void findBlog() throws Exception {
		final String url = "/blogs/{blogId}";
		Blog savedBlog = createDefaultBlog();

		final ResultActions resultActions = mockMvc.perform(get(url, savedBlog.getId()));

		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").value(savedBlog.getContent()));
	}

	@DisplayName("deleteBlog : 블로그 글 삭제에 성공한다.")
	@Test
	void deleteBlog() throws Exception {
		final String url = "/blogs/{blogId}";
		Blog savedBlog = createDefaultBlog();

		mockMvc.perform(delete(url, savedBlog.getId()))
				.andExpect(status().isOk()) ;
	}
	private Blog createDefaultBlog() {
		int num = createRandomNumber();
		return blogRepository.save(Blog.builder()
				.title("title_" + num)
				.content("content_" + num)
				.member(member)
				.isBlocked(false).build());
	}

	private int createRandomNumber() {
		Random random = new Random();
		return random.nextInt(101);
	}
}