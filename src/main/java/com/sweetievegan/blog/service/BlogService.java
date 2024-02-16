package com.sweetievegan.blog.service;

import com.sweetievegan.blog.dto.request.BlogRegisterRequest;
import com.sweetievegan.blog.dto.response.BlogDetailResponse;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogService {
	List<BlogListResponse> getAllBlogs();
	BlogDetailResponse findBlogByBlogId(Long blogId);
	Long addBlog(BlogRegisterRequest request, List<MultipartFile> file);
	BlogRegisterRequest updateBlogDetail(Long blogId, BlogRegisterRequest request);
	Long removeBlog(Long blogId);
}
