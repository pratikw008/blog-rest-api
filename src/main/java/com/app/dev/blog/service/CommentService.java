package com.app.dev.blog.service;

import java.util.List;

import com.app.dev.blog.dtos.CommentDto;
import com.app.dev.blog.dtos.CommentUpdateDto;

public interface CommentService {
	
	public CommentDto createComment(long postId, CommentDto commentDto);
	
	public List<CommentDto> getCommentsByPostId(long postId);
	
	public CommentDto getCommentById(long postId, long commentId);
	
	public CommentDto updateComment(long postId, long commentId, CommentUpdateDto commentDto);
	
	public void deleteCommetById(long postId, long commentId);
}
