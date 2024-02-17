package com.sweetievegan.blog.dto.response;

import com.sweetievegan.blog.domain.entity.Blog;
import com.sweetievegan.blog.domain.entity.BlogImage;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class BlogDetailResponse {
	private Long id;
	private String title;
	private String author;
	private String content;
	private String tags;
	private List<String> imageNames;

	public static BlogDetailResponse of(Blog blog) {
		BlogDetailResponse response = BlogDetailResponse.builder()
										.id(blog.getId())
										.title(blog.getTitle())
										.author(blog.getMember().getNickname())
										.content(blog.getContent())
										.tags(blog.getTags())
										.build();

		if(!blog.getBlogImages().isEmpty()) {
			List<String> imageNames = blog.getBlogImages().stream()
					.map(BlogImage::getImageName)
					.collect(Collectors.toList());
			response.setImageNames(imageNames);
		}
		return response;
	}
}
