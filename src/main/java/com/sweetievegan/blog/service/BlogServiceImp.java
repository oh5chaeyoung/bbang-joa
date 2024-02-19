package com.sweetievegan.blog.service;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.service.MemberServiceImp;
import com.sweetievegan.blog.domain.entity.Blog;
import com.sweetievegan.blog.domain.entity.BlogImage;
import com.sweetievegan.blog.domain.repository.BlogImageRepository;
import com.sweetievegan.blog.domain.repository.BlogRepository;
import com.sweetievegan.blog.dto.request.BlogRegisterRequest;
import com.sweetievegan.blog.dto.response.BlogDetailResponse;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import com.sweetievegan.util.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BlogServiceImp implements BlogService {
	private final MemberServiceImp memberServiceImp;
	private final BlogRepository blogRepository;
	private final ImageService imageService;
	private final BlogImageRepository blogImageRepository;
	@Override
	public List<BlogListResponse> getAllBlogs() {
		List<Blog> blogs = blogRepository.findAll();

		List<BlogListResponse> responses = new ArrayList<>();
		for(Blog blog : blogs) {
			BlogListResponse response = BlogListResponse.builder()
					.id(blog.getId())
					.title(blog.getTitle())
					.author(blog.getMember().getNickname())
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
				.author(blog.getMember().getNickname())
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
	public Long addBlog(BlogRegisterRequest request, List<MultipartFile> file, Long memberId) {
		Member member = memberServiceImp.getMemberDetail(memberId);

		Blog blog = Blog.builder()
				.title(request.getTitle())
				.member(member)
				.content(request.getContent())
				.tags(request.getTags())
				.blogImages(new ArrayList<>())
				.build();

		/* Image files ****************************/
		List<String> blogImageList = imageService.addFile(file, "blog");
		for(String fn : blogImageList) {
			blogImageRepository.save(BlogImage.builder()
					.imageName(fn)
					.blog(blog)
					.isDeleted(false)
					.build());
		}
		/* Image files */

		return blogRepository.save(blog).getId();
	}

	@Override
	public BlogDetailResponse updateBlogDetail(Long memberId, Long blogId, BlogRegisterRequest request, List<MultipartFile> file) {
		Blog blog = blogRepository.findBlogById(blogId);
		if(memberId != blog.getMember().getId()) {
			throw new RuntimeException("게시글 수정 권한이 없습니다.");
		}
		blog.editBlog(request);

		/* Image files ****************************/
		List<BlogImage> removeBlogImagesList = blog.getBlogImages();
		for(BlogImage blogImage : removeBlogImagesList) {
			imageService.removeFile(blogImage.getImageName());
			blogImageRepository.delete(blogImage);
		}

		List<String> blogImageList = imageService.addFile(file, "blog");
		for(String fn : blogImageList) {
			blogImageRepository.save(BlogImage.builder()
					.imageName(fn)
					.blog(blog)
					.isDeleted(false)
					.build());
		}
		/* Image files */

		return findBlogByBlogId(blogId);
	}

	@Override
	public Long removeBlog(Long blogId) {
		/* implement to remove image */

		blogRepository.deleteById(blogId);
		return blogId;
	}
}
