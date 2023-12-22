package com.app.dev.blog.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.dev.blog.dtos.CommentDto;
import com.app.dev.blog.exception.BlogApiException;
import com.app.dev.blog.exception.ResourceNotFoundException;
import com.app.dev.blog.mapper.CommentMapper;
import com.app.dev.blog.model.CommentEntity;
import com.app.dev.blog.model.PostEntity;
import com.app.dev.blog.repository.CommentRepository;
import com.app.dev.blog.repository.PostRepository;
import com.app.dev.blog.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService {
	
	private CommentRepository commentRepository;
	
	private CommentMapper commentMapper;
	
	private PostRepository postRepository;

	public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper,
			PostRepository postRepository) {
		super();
		this.commentRepository = commentRepository;
		this.commentMapper = commentMapper;
		this.postRepository = postRepository;
	}

	@Override
	public CommentDto createComment(long postId, CommentDto commentDto) {
		return postRepository.findById(postId)
			.map(postEntity -> this.create(postEntity, commentMapper.convertCommentDtoToEntity(commentDto)))
			.orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
	}

	private CommentDto create(PostEntity postEntity, CommentEntity commentEntity) {
		commentEntity.setPostEntity(postEntity);
		CommentEntity savedComment = commentRepository.save(commentEntity);
		return commentMapper.convertCommentEntityToDto(savedComment);
	}
	
	@Override
	public List<CommentDto> getCommentsByPostId(long postId) {
		if(!commentRepository.existsByPostEntity_Id(postId))
			throw new ResourceNotFoundException("Post", "id", postId);
		
		List<CommentEntity> comments = commentRepository.findByPostEntity_Id(postId);
		return commentMapper.convertCommentEntityListToDtoList(comments);
	}
	
	@Override
	public CommentDto getCommentById(long postId, long commentId) {
		if(!postRepository.existsById(postId))
			throw new ResourceNotFoundException("Post", "id", postId);
		
		CommentEntity commentEntity =  commentRepository.findById(commentId)
			.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
		
		if(commentEntity.getPostEntity().getId() != postId)
			throw new BlogApiException("Comment does not belongs to post");
		
		return commentMapper.convertCommentEntityToDto(commentEntity);
	}
	
	@Override
	public CommentDto updateComment(long postId, long commentId, CommentDto commentDto) {
		if(!postRepository.existsById(postId))
			throw new ResourceNotFoundException("Post", "id", postId);
		
		CommentEntity commentEntity = commentRepository.findById(commentId)
			.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
		
		if(commentEntity.getPostEntity().getId() != postId)
			throw new BlogApiException("Comment does not belongs to post");
		
		commentMapper.updateCommentEntity(commentDto, commentEntity);
		
		CommentEntity updatedComment = commentRepository.save(commentEntity);
		
		return commentMapper.convertCommentEntityToDto(updatedComment);
	}
	
	@Override
	public void deleteCommetById(long postId, long commentId) {
		if(!postRepository.existsById(postId))
			throw new ResourceNotFoundException("Post", "id", postId);
		
		CommentEntity commentEntity = commentRepository.findById(commentId)
			.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
		
		if(commentEntity.getPostEntity().getId() != postId)
			throw new BlogApiException("Comment does not belongs to post");
		
		commentRepository.delete(commentEntity);
	}
}
