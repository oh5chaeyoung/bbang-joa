package com.sweetievegan.blog.domain.repository;

import com.sweetievegan.blog.domain.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
	Blog findBlogById(Long id);
}
