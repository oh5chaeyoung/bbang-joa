package com.sweetievegan.recipe.domain.repository;

import com.sweetievegan.recipe.domain.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
	Recipe findRecipeById(Long recipeId);
	List<Recipe> findRecipesByMemberId(String memberId);
	List<Recipe> findRecipesByTitleContaining(String keyword);
}
