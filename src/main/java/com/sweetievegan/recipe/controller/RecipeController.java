package com.sweetievegan.recipe.controller;

import com.sweetievegan.recipe.dto.response.RecipeDetailResponse;
import com.sweetievegan.recipe.dto.response.RecipeListResponse;
import com.sweetievegan.recipe.dto.request.RecipeRegisterRequest;
import com.sweetievegan.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {
	private final RecipeService recipeService;

	@GetMapping("")
	public ResponseEntity<List<RecipeListResponse>> recipeList() {
		return ResponseEntity.status(HttpStatus.OK).body(recipeService.getAllRecipes());
	}
	@GetMapping("/{recipeId}")
	public ResponseEntity<RecipeDetailResponse> recipeDetails(@PathVariable("recipeId") Long recipeId) {
		return ResponseEntity.status(HttpStatus.OK).body(recipeService.findRecipeByRecipeId(recipeId));
	}
	@GetMapping("/search")
	public ResponseEntity<List<RecipeListResponse>> recipeListByTitleKeyword(@RequestParam(value = "keyword") String keyword) {
		return ResponseEntity.status(HttpStatus.OK).body(recipeService.findRecipesByKeyword(keyword));
	}
	@GetMapping("/all-count")
	public ResponseEntity<Long> recipeCounts() {
		return ResponseEntity.status(HttpStatus.OK).body(recipeService.getAllRecipesCount());
	}

	@GetMapping("/all-ids")
	public ResponseEntity<List<Long>> recipeIdList() {
		return ResponseEntity.status(HttpStatus.OK).body(recipeService.getAllRecipeIds());
	}
	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Long> recipeAdd(
			@RequestPart(value = "file", required = false) List<MultipartFile> file,
			@RequestPart RecipeRegisterRequest request,
			@AuthenticationPrincipal User user) {
		return ResponseEntity.status(HttpStatus.OK).body(recipeService.addRecipe(request, file, user.getUsername()));
	}
	@PutMapping("/{recipeId}")
	public ResponseEntity<RecipeDetailResponse> recipeModify(
			@PathVariable("recipeId") Long recipeId,
			@RequestPart(value = "file", required = false) List<MultipartFile> file,
			@RequestPart RecipeRegisterRequest request,
			@AuthenticationPrincipal User user) {
		return ResponseEntity.status(HttpStatus.OK).body(recipeService.updateRecipe(user.getUsername(), recipeId, request, file));
	}
	@DeleteMapping("/{recipeId}")
	public ResponseEntity<Long> recipeRemove(
			@PathVariable("recipeId") Long recipeId,
			@AuthenticationPrincipal User user) {
		return ResponseEntity.status(HttpStatus.OK).body(recipeService.removeRecipe(user.getUsername(), recipeId));
	}
}
