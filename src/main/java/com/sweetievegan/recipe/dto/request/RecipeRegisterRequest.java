package com.sweetievegan.recipe.dto.request;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.recipe.domain.entity.Recipe;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class RecipeRegisterRequest {
	private String title;
	private String duration;
	private Long level;
	private String description;
	private String ingredients;
	private String notes;
	private String steps;

	public Recipe toEntity(Member member) {
		return Recipe.builder()
				.member(member)
				.title(title)
				.duration(duration)
				.level(level)
				.description(description)
				.ingredients(ingredients)
				.notes(notes)
				.steps(steps)
				.recipeImages(new ArrayList<>())
				.build();
	}
}
