package com.sweetievegan.recipe.domain.entity;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.recipe.dto.request.RecipeRegisterRequest;
import com.sweetievegan.util.domain.entity.BaseTime;
import lombok.*;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="recipes")
public class Recipe extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recipe_id")
	private Long id;

	private String title;
	private String duration;
	private Long level;
	private String description;
	private String ingredients;
	private String notes;
	private String steps;

	private boolean isBlocked;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToMany(mappedBy = "recipe")
	private List<RecipeImage> recipeImages;

	public void editRecipe(RecipeRegisterRequest request) {
		this.title = request.getTitle();
		this.duration = request.getDuration();
		this.level = request.getLevel();
		this.description = request.getDescription();
		this.ingredients = request.getIngredients();
		this.notes = request.getNotes();
		this.steps = request.getSteps();
	}

	public void blockRecipe() {
		this.isBlocked = true;
	}
}
