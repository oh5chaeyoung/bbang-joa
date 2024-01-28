package com.sweetievegan.blog.domain.repository;

import com.sweetievegan.blog.domain.entity.BlogImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogImageRepository extends JpaRepository<BlogImage, Long> {
}
