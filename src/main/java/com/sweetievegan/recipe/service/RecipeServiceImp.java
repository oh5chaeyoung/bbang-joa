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
		if(recipe == null) {
			throw new GlobalException(GlobalErrorCode.NOT_FOUND_INFO);
		}
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
	public Long addRecipe(RecipeRegisterRequest request, List<MultipartFile> file, String memberId) {
		Member member = memberRepository.findMemberById(memberId);

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
	public RecipeDetailResponse updateRecipeDetail(String memberId, Long recipeId, RecipeRegisterRequest request, List<MultipartFile> file) {
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
}
