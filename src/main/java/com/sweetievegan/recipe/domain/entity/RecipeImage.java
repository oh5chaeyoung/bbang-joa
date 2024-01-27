package com.sweetievegan.recipe.domain.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name="recipe_images")
public class RecipeImage extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipe_id")
	private Recipe recipe;

	private String imageName;

	private boolean isDeleted;

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public RecipeImage(String imageName) {
		this.imageName = imageName;
	}

	public void deleteRecipeImage() {
		this.isDeleted = true;
	}
}
