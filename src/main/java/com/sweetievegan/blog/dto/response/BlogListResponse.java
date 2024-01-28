package com.sweetievegan.blog.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BlogListResponse {
	private Long id;
	private String title;
	private String author;
	private List<String> imageNames;
}
