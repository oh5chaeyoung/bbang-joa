package com.sweetievegan.blog.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BlogDetailResponse {
	private Long id;
	private String title;
	private String author;
	private String content;
	private String tags;
	private List<String> imageNames;
}
