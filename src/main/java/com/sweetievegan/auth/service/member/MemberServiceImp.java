package com.sweetievegan.auth.service.member;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.blog.domain.entity.Blog;
import com.sweetievegan.blog.domain.entity.BlogImage;
import com.sweetievegan.blog.domain.repository.BlogRepository;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import com.sweetievegan.recipe.domain.entity.Recipe;
import com.sweetievegan.recipe.domain.entity.RecipeImage;
import com.sweetievegan.recipe.domain.repository.RecipeRepository;
import com.sweetievegan.recipe.dto.response.RecipeListResponse;
import com.sweetievegan.util.exception.GlobalErrorCode;
import com.sweetievegan.util.exception.GlobalException;
import com.sweetievegan.util.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
public class MemberServiceImp implements MemberService {
	private final MemberRepository memberRepository;
	private final BlogRepository blogRepository;
	private final RecipeRepository recipeRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ImageService imageService;

	public MemberResponse changeMemberNickname(String memberId, String nickname){
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_USER));
		member.setNickname(nickname);
		return MemberResponse.of(memberRepository.save(member));
	}

	public MemberResponse changeMemberPassword(String memberId, String exPassword, String newPassword){
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_USER));
		if(!passwordEncoder.matches(exPassword, member.getPassword())){
			throw new GlobalException(GlobalErrorCode.NOT_MATCH_PASSWORD);
		}
		member.setPassword(passwordEncoder.encode((newPassword)));
		return MemberResponse.of(memberRepository.save(member));
	}

	@Override
	public MemberResponse changeMemberSummary(String memberId, String summary) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_USER));
		member.setSummary(summary);
		return MemberResponse.of(memberRepository.save(member));
	}

	@Override
	public MemberResponse changeMemberProfile(String memberId, MultipartFile file) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new GlobalException(GlobalErrorCode.NOT_FOUND_USER));
		/* remove */
		imageService.removeFile(member.getProfile());
		/* add */
		String profileName = imageService.addOneFile(file, "member");
		member.setProfile(profileName);
		return MemberResponse.of(memberRepository.save(member));
	}

	public MemberResponse getMemberDetail(String memberId) {
		Member member = memberRepository.findMemberById(memberId);
		if (member == null) {
			throw new GlobalException(GlobalErrorCode.NOT_FOUND_USER);
		}
		return MemberResponse.of(member);
	}

	public String checkEmail(String email) {
		boolean exist = memberRepository.existsByEmail(email);
		if(exist) {
			throw new GlobalException(GlobalErrorCode.EXIST_EMAIL);
		}
		return "가입할 수 있는 이메일 입니다.";
	}

	@Override
	public List<BlogListResponse> getMyBlogs(String memberId) {
		List<Blog> blogs = blogRepository.findBlogsByMemberId(memberId);
		List<BlogListResponse> responses = new ArrayList<>();
		for(Blog blog : blogs) {
			BlogListResponse response = BlogListResponse.builder()
					.id(blog.getId())
					.title(blog.getTitle())
					.author(blog.getMember().getNickname())
					.tag(blog.getTags())
					.createDate(blog.getCreateDate())
					.build();

			/* Image files ****************************/
			if(!blog.getBlogImages().isEmpty()) {
				List<String> imageNames = blog.getBlogImages().stream()
						.map(BlogImage::getImageName)
						.collect(Collectors.toList());
				response.setImageNames(imageNames);
			}
			/* Image files */

			responses.add(response);
		}
		return responses;
	}

	@Override
	public List<RecipeListResponse> getMyRecipes(String memberId) {
		List<Recipe> recipes = recipeRepository.findRecipesByMemberId(memberId);
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
}
