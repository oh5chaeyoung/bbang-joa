package com.sweetievegan.blog.domain.repository;

import com.sweetievegan.blog.domain.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
	Blog findBlogById(Long blogId);
	List<Blog> findBlogsByMemberId(String memberId);
	List<Blog> findBlogsByTitleContaining(String keyword);
}