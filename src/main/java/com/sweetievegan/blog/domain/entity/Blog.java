package com.sweetievegan.blog.domain.entity;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.blog.dto.request.BlogRegisterRequest;
import com.sweetievegan.util.domain.entity.BaseTime;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="blogs")
public class Blog extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "blog_id")
	private Long id;

	private String title;
	private String content;
	private String tags;
	private String summary;

	private boolean isBlocked;

	@OneToMany(mappedBy = "blog")
	private List<BlogImage> blogImages;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public void editBlog(BlogRegisterRequest request) {
		this.title = request.getTitle();
		this.content = request.getContent();
		this.tags = request.getTags();
		this.summary = request.getSummary();
	}

	public void blockBlog() {
		this.isBlocked = true;
	}
}
