package com.sweetievegan.blog.service;

import com.sweetievegan.blog.domain.entity.Blog;
import com.sweetievegan.blog.domain.entity.BlogImage;
import com.sweetievegan.blog.domain.repository.BlogImageRepository;
import com.sweetievegan.blog.domain.repository.BlogRepository;
import com.sweetievegan.blog.dto.request.BlogRegisterRequest;
import com.sweetievegan.blog.dto.response.BlogDetailResponse;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import com.sweetievegan.recipe.domain.entity.RecipeImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogServiceImp implements BlogService {
	private final BlogRepository blogRepository;
	private final BlogImageService blogImageService;
	private final BlogImageRepository blogImageRepository;
	@Override
	public List<BlogListResponse> getAllBlogs() {
		List<Blog> blogs = blogRepository.findAll();

		List<BlogListResponse> responses = new ArrayList<>();
		for(Blog blog : blogs) {
			BlogListResponse response = BlogListResponse.builder()
					.id(blog.getId())
					.title(blog.getTitle())
					.author(blog.getAuthor())
					.tag(blog.getTags())
					.createDate(blog.getCreateDate())
					.build();

			/* Image files ****************************/
			if(!blog.getBlogImages().isEmpty()) {
				List<String> imageNames = blog.getBlogImages().stream()
						.map(BlogImage::getImageName)
						.collect(Collectors.toList());
				response.setImageNames(imageNames);
			}
			/* Image files */

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
				.content(blog.getContent())
				.tags(blog.getTags())
				.build();

		/* Image files ****************************/
		if(!blog.getBlogImages().isEmpty()) {
			List<String> imageNames = blog.getBlogImages().stream()
					.map(BlogImage::getImageName)
					.collect(Collectors.toList());
			response.setImageNames(imageNames);
		}
		/* Image files */

		return response;
	}

	@Override
	public Long addBlog(BlogRegisterRequest request, List<MultipartFile> file) {
		Blog blog = Blog.builder()
				.title(request.getTitle())
				.author(request.getAuthor())
				.content(request.getContent())
				.tags(request.getTags())
				.blogImages(new ArrayList<>())
				.build();

		/* Image files ****************************/
		List<String> blogImageList = blogImageService.addFile(file, "blog");
		for(String fn : blogImageList) {
			blogImageRepository.save(BlogImage.builder()
					.imageName(fn)
					.blog(blog)
					.isDeleted(false)
					.build());
		}
		for (String blogImagePath : blogImageList) {
			blog.addBlogImage(new BlogImage(blogImagePath));
		}
		/* Image files */

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
