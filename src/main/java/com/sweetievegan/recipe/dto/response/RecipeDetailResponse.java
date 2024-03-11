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
public class RecipeDetailResponse {
	private String title;
	private String author;
	private String authorSummary;
	private String duration;
	private Long level;
	private String description;
	private String ingredients;
	private String notes;
	private String steps;
	private LocalDateTime createDate;
	private List<String> imageNames;

	public static RecipeDetailResponse of(Recipe recipe) {
		RecipeDetailResponse response = RecipeDetailResponse.builder()
				.title(recipe.getTitle())
				.author(recipe.getMember().getNickname())
				.authorSummary(recipe.getMember().getSummary())
				.duration(recipe.getDuration())
				.level(recipe.getLevel())
				.description(recipe.getDescription())
				.ingredients(recipe.getIngredients())
				.notes(recipe.getNotes())
				.steps(recipe.getSteps())
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
