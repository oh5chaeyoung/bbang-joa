package com.sweetievegan.recipe.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecipeRegisterRequest {
	private String title;
	private String author;
	private String duration;
	private Long level;
	private String description;
	private String ingredients;
	private String notes;
	private String steps;
}
