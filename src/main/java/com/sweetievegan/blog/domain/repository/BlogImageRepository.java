package com.sweetievegan.blog.domain.repository;

import com.sweetievegan.blog.domain.entity.BlogImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogImageRepository extends JpaRepository<BlogImage, Long> {
	List<BlogImage> findBlogImageByBlogId(Long blogId);
}
