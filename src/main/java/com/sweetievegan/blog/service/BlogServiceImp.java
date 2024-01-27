package com.sweetievegan.blog.service;

import com.sweetievegan.blog.domain.entity.Blog;
import com.sweetievegan.blog.domain.repository.BlogRepository;
import com.sweetievegan.blog.dto.request.BlogRegisterRequest;
import com.sweetievegan.blog.dto.response.BlogDetailResponse;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogServiceImp implements BlogService {
	private final BlogRepository blogRepository;
	@Override
	public List<BlogListResponse> getAllBlogs() {
		List<Blog> blogs = blogRepository.findAll();

		List<BlogListResponse> responses = new ArrayList<>();
		for(Blog blog : blogs) {
			BlogListResponse response = BlogListResponse.builder()
					.id(blog.getId())
					.title(blog.getTitle())
					.author(blog.getAuthor())
					.image(blog.getImage())
					.build();
			responses.add(response);
		}
		return responses;
	}

	@Override
	public BlogDetailResponse findBlogByBlogId(Long blogId) {
		Blog blog = blogRepository.findBlogById(blogId);
		BlogDetailResponse response = BlogDetailResponse.builder()
				.id(blog.getId())
				.title(blog.getTitle())
				.author(blog.getAuthor())
				.image(blog.getImage())
				.content(blog.getContent())
				.tags(blog.getTags())
				.build();
		return response;
	}

	@Override
	public Long addBlog(BlogRegisterRequest request, MultipartFile file) {
		Blog blog = Blog.builder()
				.title(request.getTitle())
				.author(request.getAuthor())
				.image(request.getImage())
				.content(request.getContent())
				.tags(request.getTags())
				.build();
		return blogRepository.save(blog).getId();
	}

	@Override
	public BlogRegisterRequest updateBlogDetail(Long blogId, BlogRegisterRequest request) {
		return null;
	}

	@Override
	public Long removeBlog(Long blogId) {
		blogRepository.deleteById(blogId);
		return blogId;
	}
}
