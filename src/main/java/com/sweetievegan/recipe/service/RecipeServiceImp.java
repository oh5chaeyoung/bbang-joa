package com.sweetievegan.recipe.service;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.service.MemberServiceImp;
import com.sweetievegan.recipe.domain.entity.Recipe;
import com.sweetievegan.recipe.domain.entity.RecipeImage;
import com.sweetievegan.recipe.domain.repository.RecipeImageRepository;
import com.sweetievegan.recipe.domain.repository.RecipeRepository;
import com.sweetievegan.recipe.dto.response.RecipeDetailResponse;
import com.sweetievegan.recipe.dto.response.RecipeListResponse;
import com.sweetievegan.recipe.dto.request.RecipeRegisterRequest;
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
	private final MemberServiceImp memberServiceImp;
	private final RecipeRepository recipeRepository;
	private final ImageService ImageService;
	private final RecipeImageRepository recipeImageRepository;

	@Override
	public List<RecipeListResponse> getAllRecipes() {
		List<Recipe> recipes = recipeRepository.findAll();

		List<RecipeListResponse> responses = new ArrayList<>();
		for(Recipe recipe : recipes) {
			RecipeListResponse response = RecipeListResponse.builder()
					.id(recipe.getId())
					.title(recipe.getTitle())
					.level(recipe.getLevel())
					.createDate(recipe.getCreateDate())
					.build();

			/* Image files ****************************/
			if(!recipe.getRecipeImages().isEmpty()) {
				List<String> imageNames = recipe.getRecipeImages().stream()
						.map(RecipeImage::getImageName)
						.collect(Collectors.toList());
				response.setImageNames(imageNames);
			}
			/* Image files */

			responses.add(response);
		}
		return responses;
	}
	@Override
	public RecipeDetailResponse findRecipeByRecipeId(Long recipeId) {
		Recipe recipe = recipeRepository.findRecipeById(recipeId);
		RecipeDetailResponse response = RecipeDetailResponse.builder()
				.title(recipe.getTitle())
				.author(recipe.getMember().getNickname())
				.duration(recipe.getDuration())
				.level(recipe.getLevel())
				.description(recipe.getDescription())
				.ingredients(recipe.getIngredients())
				.notes(recipe.getNotes())
				.steps(recipe.getSteps())
				.createDate(recipe.getCreateDate())
				.build();

		/* Image files ****************************/
		if(!recipe.getRecipeImages().isEmpty()) {
			List<String> imageNames = recipe.getRecipeImages().stream()
					.map(RecipeImage::getImageName)
					.collect(Collectors.toList());
			response.setImageNames(imageNames);
		}
		/* Image files */

		return response;
	}
	@Override
	public Long addRecipe(RecipeRegisterRequest request, List<MultipartFile> file, Long memberId) {
		Member member = memberServiceImp.getMemberDetail(memberId);

		Recipe recipe = Recipe.builder()
				.title(request.getTitle())
				.member(member)
				.duration(request.getDuration())
				.level(request.getLevel())
				.description(request.getDescription())
				.ingredients(request.getIngredients())
				.notes(request.getNotes())
				.steps(request.getSteps())
				.recipeImages(new ArrayList<>())
				.build();

		/* Image files ****************************/
		List<String> recipeImageList = ImageService.addFile(file, "recipe");
		for(String fn : recipeImageList) {
			recipeImageRepository.save(RecipeImage.builder()
					.imageName(fn)
					.recipe(recipe)
					.isDeleted(false)
					.build());
		}
		for (String recipeImagePath : recipeImageList) {
			recipe.addRecipeImage(new RecipeImage(recipeImagePath));
		}
		/* Image files */

		return recipeRepository.save(recipe).getId();
	}
	@Override
	public RecipeRegisterRequest updateRecipeDetail(Long recipeId, RecipeRegisterRequest request, List<MultipartFile> file) {
		Recipe recipe = recipeRepository.findRecipeById(recipeId);
		recipe.editRecipe(request);

		/* Image files ****************************/
		List<RecipeImage> deleteRecipeImageList = recipeImageRepository.findRecipeImageByRecipeId(recipeId);
		for(RecipeImage d : deleteRecipeImageList) {
			recipeImageRepository.delete(d);
		}

		List<String> recipeImageList = ImageService.addFile(file, "recipe");
		for(String fn : recipeImageList) {
			recipeImageRepository.save(RecipeImage.builder()
					.imageName(fn)
					.recipe(recipe)
					.isDeleted(false)
					.build());
		}
		for (String recipeImagePath : recipeImageList) {
			recipe.addRecipeImage(new RecipeImage(recipeImagePath));
		}
		/* Image files */

		return request;
	}
	@Override
	public Long removeRecipe(Long recipeId) {
		recipeRepository.deleteById(recipeId);
		return recipeId;
	}
}
