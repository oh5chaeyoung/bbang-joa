package com.sweetievegan.recipe.domain.repository;

import com.sweetievegan.recipe.domain.entity.RecipeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeImageRepository extends JpaRepository<RecipeImage, Long> {
	List<RecipeImage> findRecipeImageByRecipeId(Long id);
}
