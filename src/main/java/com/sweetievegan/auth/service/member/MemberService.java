package com.sweetievegan.auth.service.member;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import com.sweetievegan.recipe.dto.response.RecipeListResponse;

import java.util.List;

public interface MemberService {
	MemberResponse changeMemberNickname(String email, String nickname);
	MemberResponse changeMemberPassword(String exPassword, String newPassword);
	Member getMemberDetail(String id);
	String checkEmail(String email);
	List<BlogListResponse> getMyBlogs(String id);
	List<RecipeListResponse> getMyRecipes(String id);
}
