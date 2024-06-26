package com.sweetievegan.recipe.service;

import com.sweetievegan.recipe.dto.response.RecipeDetailResponse;
import com.sweetievegan.recipe.dto.response.RecipeListResponse;
import com.sweetievegan.recipe.dto.request.RecipeRegisterRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecipeService {
	List<RecipeListResponse> getAllRecipes();
	RecipeDetailResponse findRecipeByRecipeId(Long recipeId);
	Long addRecipe(RecipeRegisterRequest request, List<MultipartFile> file, String memberId);
	RecipeDetailResponse updateRecipe(String memberId, Long recipeId, RecipeRegisterRequest request, List<MultipartFile> file);
	Long removeRecipe(String memberId, Long recipeId);
	Long getAllRecipesCount();
	List<Long> getAllRecipeIds();
	List<RecipeListResponse> findRecipesByKeyword(String keyword);
}
