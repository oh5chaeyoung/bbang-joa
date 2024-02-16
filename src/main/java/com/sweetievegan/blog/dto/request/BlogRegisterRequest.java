package com.sweetievegan.blog.dto.request;

import com.sweetievegan.auth.domain.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogRegisterRequest {
	private String title;
	private String content;
	private String tags;
	private String author;
}
