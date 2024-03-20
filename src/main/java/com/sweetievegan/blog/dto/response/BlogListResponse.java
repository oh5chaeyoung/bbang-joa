package com.sweetievegan.blog.dto.response;

import com.sweetievegan.blog.domain.entity.Blog;
import com.sweetievegan.blog.domain.entity.BlogImage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class BlogListResponse {
	private Long id;
	private String title;
	private String author;
	private String tags;
	private String summary;
	private LocalDateTime createDate;
	private List<String> imageNames;

	public static BlogListResponse of(Blog blog) {
		BlogListResponse response = BlogListResponse.builder()
				.id(blog.getId())
				.title(blog.getTitle())
				.author(blog.getMember().getNickname())
				.tags(blog.getTags())
				.summary(blog.getSummary())
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
		return response;
	}
}
