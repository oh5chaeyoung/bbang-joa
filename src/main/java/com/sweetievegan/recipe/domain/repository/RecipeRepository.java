package com.sweetievegan.recipe.domain.repository;

import com.sweetievegan.recipe.domain.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
	Recipe findRecipeById(Long id);
}
