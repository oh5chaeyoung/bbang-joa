package com.sweetievegan.blog.domain.repository;

import com.sweetievegan.blog.domain.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
	@Query("SELECT b FROM Blog b WHERE b.isBlocked = false")
	List<Blog> findAll();
	@Query("SELECT b FROM Blog b WHERE b.id = :blogId and b.isBlocked = false")
	Blog findBlogById(Long blogId);
	@Query("SELECT b FROM Blog b WHERE b.member.id = :memberId and b.isBlocked = false")
	List<Blog> findBlogsByMemberId(String memberId);
	List<Blog> findBlogsByTitleContaining(String keyword);
}