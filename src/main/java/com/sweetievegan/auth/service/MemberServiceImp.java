package com.sweetievegan.auth.service;

import com.sweetievegan.auth.domain.entity.Member;
import com.sweetievegan.auth.domain.repository.MemberRepository;
import com.sweetievegan.auth.dto.response.MemberResponse;
import com.sweetievegan.blog.domain.entity.Blog;
import com.sweetievegan.blog.domain.entity.BlogImage;
import com.sweetievegan.blog.domain.repository.BlogRepository;
import com.sweetievegan.blog.dto.response.BlogListResponse;
import com.sweetievegan.config.SecurityUtil;
import com.sweetievegan.recipe.domain.entity.Recipe;
import com.sweetievegan.recipe.domain.entity.RecipeImage;
import com.sweetievegan.recipe.domain.repository.RecipeRepository;
import com.sweetievegan.recipe.dto.response.RecipeListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public MemberResponse changeMemberNickname(String email, String nickname){
		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("로그인 유정 정보가 없습니다."));
		member.setNickname(nickname);
		return MemberResponse.of(memberRepository.save(member));
	}

	public MemberResponse changeMemberPassword(String exPassword, String newPassword){
		Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
				.orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));
		if(!passwordEncoder.matches(exPassword, member.getPassword())){
			throw new RuntimeException("비밀번호가 맞지 않습니다.");
		}
		member.setPassword(passwordEncoder.encode((newPassword)));
		return MemberResponse.of(memberRepository.save(member));
	}

	public Member getMemberDetail(String id) {
		Member member = memberRepository.findMemberById(id);
		if (member == null) {
			throw new RuntimeException("로그인 유저 정보가 없습니다.");
		}
		return member;
	}

	public String checkEmail(String email) {
		boolean exist = memberRepository.existsByEmail(email);
		if(exist) {
			throw new RuntimeException("이미 가입된 이메일 입니다.");
		}
		return "가입할 수 있는 이메일 입니다.";
	}

	@Override
	public List<BlogListResponse> getMyBlogs(String id) {
		List<Blog> blogs = blogRepository.findBlogsByMemberId(id);
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
	public List<RecipeListResponse> getMyRecipes(String id) {
		List<Recipe> recipes = recipeRepository.findRecipesByMemberId(id);
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
