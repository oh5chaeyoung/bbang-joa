package com.sweetievegan.blog.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogDetailResponse {
	private Long id;
	private String title;
	private String image;
	private String author;
	private String content;
	private String tags;
}
