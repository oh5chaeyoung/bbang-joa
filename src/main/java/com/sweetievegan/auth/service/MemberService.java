package com.sweetievegan.auth.service;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import com.sweetievegan.recipe.dto.response.RecipeListResponse;

import java.util.List;

public interface MemberService {
	MemberResponse changeMemberNickname(String email, String nickname);
	MemberResponse changeMemberPassword(String exPassword, String newPassword);
	Member getMemberDetail(Long id);
	String checkEmail(String email);
	List<BlogListResponse> getMyBlogs(Long id);
	List<RecipeListResponse> getMyRecipes(Long id);
}
