package com.sweetievegan.blog.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogListResponse {
	private Long id;
	private String title;
	private String image;
	private String author;
}
