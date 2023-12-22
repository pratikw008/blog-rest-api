package com.app.dev.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.app.dev.blog.model.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
	
	public boolean existsByPostEntity_Id(long id);
	
	public List<CommentEntity> findByPostEntity_Id(@Param("postId") long postId);
}
