package com.sweetievegan.blog.domain.entity;

import com.sweetievegan.recipe.domain.entity.BaseTime;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="blogs")
public class Blog extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String image;
	private String author;
	private String content;
	private String tags;
}
