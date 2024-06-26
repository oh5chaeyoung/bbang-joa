package com.sweetievegan.recipe.service;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import com.sweetievegan.recipe.domain.entity.Recipe;
import com.sweetievegan.recipe.domain.entity.RecipeImage;
import com.sweetievegan.recipe.domain.repository.RecipeImageRepository;
import com.sweetievegan.recipe.domain.repository.RecipeRepository;
import com.sweetievegan.recipe.dto.response.RecipeDetailResponse;
import com.sweetievegan.recipe.dto.response.RecipeListResponse;
import com.sweetievegan.recipe.dto.request.RecipeRegisterRequest;
import com.sweetievegan.util.exception.GlobalErrorCode;
import com.sweetievegan.util.exception.GlobalException;
import com.sweetievegan.util.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecipeServiceImp implements RecipeService {
	private final RecipeRepository recipeRepository;
	private final ImageService imageService;
	private final RecipeImageRepository recipeImageRepository;
	private final MemberRepository  memberRepository;

	@Override
	public List<RecipeListResponse> getAllRecipes() {
		List<Recipe> recipes = recipeRepository.findAll();

		List<RecipeListResponse> responses = new ArrayList<>();
		for(Recipe recipe : recipes) {
			RecipeListResponse response = RecipeListResponse.of(recipe);
			responses.add(response);
		}
		return responses;
	}
	@Override
	public RecipeDetailResponse findRecipeByRecipeId(Long recipeId) {
		Recipe recipe = recipeRepository.findRecipeById(recipeId);
		if(recipe == null) {
			throw new GlobalException(GlobalErrorCode.NOT_FOUND_INFO);
		}
		return RecipeDetailResponse.of(recipe);
	}
	@Override
	public Long addRecipe(RecipeRegisterRequest request, List<MultipartFile> file, String memberId) {
		Member member = memberRepository.findMemberById(memberId);
		Recipe recipe = request.toEntity(member);
		/* Image files ****************************/
		List<String> recipeImageList = imageService.addFile(file, "recipe");
		for(String fn : recipeImageList) {
			recipeImageRepository.save(RecipeImage.builder()
					.imageName(fn)
					.recipe(recipe)
					.isDeleted(false)
					.build());
		}
		/* Image files */

		return recipeRepository.save(recipe).getId();
	}
	@Override
	public RecipeDetailResponse updateRecipe(String memberId, Long recipeId, RecipeRegisterRequest request, List<MultipartFile> file) {
		Recipe recipe = recipeRepository.findRecipeById(recipeId);
		if(recipe == null) {
			throw new GlobalException(GlobalErrorCode.NOT_FOUND_INFO);
		}
		if(!memberId.equals(recipe.getMember().getId())) {
			throw new GlobalException(GlobalErrorCode.NOT_AUTHORIZED_USER);
		}
		recipe.editRecipe(request);

		/* Image files ****************************/
		/* remove */
		List<RecipeImage> removeRecipeImageList = recipeImageRepository.findRecipeImageByRecipeId(recipeId);
		for(RecipeImage recipeImage : removeRecipeImageList) {
			imageService.removeFile(recipeImage.getImageName());
			recipeImageRepository.delete(recipeImage);
		}

		/* add */
		List<String> recipeImageList = imageService.addFile(file, "recipe");
		for(String fn : recipeImageList) {
			recipeImageRepository.save(RecipeImage.builder()
					.imageName(fn)
					.recipe(recipe)
					.isDeleted(false)
					.build());
		}
		/* Image files */

		return findRecipeByRecipeId(recipeId);
	}
	@Override
	public Long removeRecipe(String memberId, Long recipeId) {
		Recipe recipe = recipeRepository.findRecipeById(recipeId);
		if(recipe == null) {
			throw new GlobalException(GlobalErrorCode.NOT_FOUND_INFO);
		}
		if(!memberId.equals(recipe.getMember().getId())) {
			throw new GlobalException(GlobalErrorCode.NOT_AUTHORIZED_USER);
		}
		List<RecipeImage> removeRecipeImageList = recipeImageRepository.findRecipeImageByRecipeId(recipeId);
		for(RecipeImage recipeImage : removeRecipeImageList) {
			imageService.removeFile(recipeImage.getImageName());
			recipeImageRepository.delete(recipeImage);
		}

		recipeRepository.deleteById(recipeId);
		return recipeId;
	}

	@Override
	public Long getAllRecipesCount() {
		return recipeRepository.count();
	}

	@Override
	public List<Long> getAllRecipeIds() {
		List<Recipe> recipes = recipeRepository.findAll();
		return recipes.stream()
				.map(Recipe::getId)
				.collect(Collectors.toList());
	}

	@Override
	public List<RecipeListResponse> findRecipesByKeyword(String keyword) {
		List<Recipe> recipes = recipeRepository.findRecipesByTitleContaining(keyword);

		List<RecipeListResponse> responses = new ArrayList<>();
		for(Recipe recipe : recipes) {
			RecipeListResponse response = RecipeListResponse.of(recipe);
			responses.add(response);
		}
		return responses;
	}
}
