package com.sweetievegan.auth.service.member;

import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import com.sweetievegan.recipe.dto.response.RecipeListResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberService {
	MemberResponse findMemberByMemberId(String memberId);
	MemberResponse changeMemberNickname(String memberId, String nickname);
	MemberResponse changeMemberPassword(String memberId, String exPassword, String newPassword);
	MemberResponse changeMemberSummary(String memberId, String summary);
	MemberResponse changeMemberProfile(String memberId, MultipartFile file);
	List<BlogListResponse> findBlogsByMemberId(String memberId);
	List<RecipeListResponse> findRecipesByMemberId(String memberId);
	Boolean removeMember(String memberId);
}
