package com.sweetievegan.recipe.domain.repository;

import com.sweetievegan.recipe.domain.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
	@Query("SELECT r FROM Recipe r WHERE r.isBlocked = false")
	List<Recipe> findAll();
	@Query("SELECT r FROM Recipe r WHERE r.id = :recipeId and r.isBlocked = false")
	Recipe findRecipeById(Long recipeId);
	@Query("SELECT r FROM Recipe r WHERE r.member.id = :memberId and r.isBlocked = false")
	List<Recipe> findRecipesByMemberId(String memberId);
	List<Recipe> findRecipesByTitleContaining(String keyword);
}
