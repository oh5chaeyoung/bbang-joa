package com.sweetievegan.recipe.dto.response;

import com.sweetievegan.recipe.domain.entity.Recipe;
import com.sweetievegan.recipe.domain.entity.RecipeImage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class RecipeListResponse {
	private Long id;
	private String title;
	private Long level;
	private LocalDateTime createDate;
	private List<String> imageNames;

	public static RecipeListResponse of(Recipe recipe) {
		RecipeListResponse response = RecipeListResponse.builder()
				.id(recipe.getId())
				.title(recipe.getTitle())
				.level(recipe.getLevel())
				.createDate(recipe.getCreateDate())
				.build();

		/* Image files ****************************/
		if(!recipe.getRecipeImages().isEmpty()) {
			List<String> imageNames = recipe.getRecipeImages().stream()
					.map(RecipeImage::getImageName)
					.collect(Collectors.toList());
			response.setImageNames(imageNames);
		}
		/* Image files */
		return response;
	}
}
