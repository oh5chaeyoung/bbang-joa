package com.sweetievegan.recipe.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
}
