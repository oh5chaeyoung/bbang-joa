package com.sweetievegan.blog.dto.request;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.blog.domain.entity.Blog;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class BlogRegisterRequest {
	private String title;
	private String content;
	private String tags;
	private String summary;

	public Blog toEntity(Member member) {
		return Blog.builder()
				.member(member)
				.title(title)
				.content(content)
				.tags(tags)
				.summary(summary)
				.blogImages(new ArrayList<>())
				.build();
	}
}
