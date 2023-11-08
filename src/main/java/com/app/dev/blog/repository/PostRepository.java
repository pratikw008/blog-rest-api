package com.app.dev.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.dev.blog.model.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

}
