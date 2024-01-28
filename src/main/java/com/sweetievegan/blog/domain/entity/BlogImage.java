package com.sweetievegan.blog.domain.entity;

import com.sweetievegan.recipe.domain.entity.BaseTime;
import com.sweetievegan.recipe.domain.entity.Recipe;
import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name="blog_images")
public class BlogImage extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blog_id")
	private Blog blog;

	private String imageName;

	private boolean isDeleted;

	public void setBlog(Blog blog) {
		this.blog = blog;
	}

	public BlogImage(String imageName) {
		this.imageName = imageName;
	}

	public void deleteBlogImage() {
		this.isDeleted = true;
	}
}
