package com.app.dev.blog.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.dev.blog.dtos.CommentDto;
import com.app.dev.blog.dtos.CommentUpdateDto;
import com.app.dev.blog.exception.BlogApiException;
import com.app.dev.blog.exception.ResourceNotFoundException;
import com.app.dev.blog.mapper.CommentMapper;
import com.app.dev.blog.model.CommentEntity;
import com.app.dev.blog.model.PostEntity;
import com.app.dev.blog.repository.CommentRepository;
import com.app.dev.blog.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
	
	@Mock
	private CommentRepository commentRepository;
	
	@Mock
	private PostRepository postRepository;
	
	@Mock
	private CommentMapper commentMapper;
	
	@InjectMocks
	private CommentServiceImpl commentService;
	
	private PostEntity postEntity;
	
	private CommentEntity commentEntity;
	
	private CommentDto commentDto;
	
	@BeforeEach
	void setUp() {
		Set<CommentEntity> comments = new HashSet<>();
		comments.add(commentEntity);
		postEntity = PostEntity.builder()
				.id(1l)
				.title("test title")
				.description("test description")
				.content("test content")
				.comments(comments)
				.build();
		
		commentEntity = CommentEntity.builder()
				.id(1l)
				.name("test name")
				.email("test email")
				.body("test body")
				.postEntity(postEntity)
				.build();
		
		commentDto = CommentDto.builder()
				.id(1l)
				.name("test name")
				.email("test email")
				.body("test body")
				.build();
		
	}
	
	@Test
	void test() {
		assertThat(commentRepository).isNotNull();
		assertThat(postRepository).isNotNull();
		assertThat(commentService).isNotNull();
	}
	
	@Test
	void givenValidPostIdComment_whenCreateComment_thenReturnComment() {
		long postId = 1;
		given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(postEntity));
		given(commentMapper.convertCommentDtoToEntity(any(CommentDto.class))).willReturn(new CommentEntity());
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity);
		given(commentMapper.convertCommentEntityToDto(any(CommentEntity.class))).willReturn(commentDto);
		
		CommentDto savedComment = commentService.createComment(postId, commentDto);
		
		assertThat(savedComment).isNotNull();
	}
	
	@Test
	void givenInvalidPostIdComment_whenCreateComment_thenThrowException() {
		long postId = 0;
		given(postRepository.findById(anyLong())).willReturn(Optional.empty());
		
		assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(postId, commentDto));
		
		verify(commentRepository, never()).save(any(CommentEntity.class));
	}
	
	@Test
	void givenInvalidPostId_whenGetCommentsByPostId_thenThrowException() {
		long postId = 0;
		//given(commentRepository.existsByPostEntity_Id(anyLong())).willReturn(false);
		this.mockCommentExistsByPostId(postId, false);
		assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentsByPostId(postId));
		
		verify(commentRepository, times(1)).existsByPostEntity_Id(anyLong());
		verify(commentRepository, never()).findByPostEntity_Id(anyLong());
	}
	
	@Test
	void givenValidPostId_whenGetCommentsByPostId_thenReturnAllComments() {
		long postId = 1;
		//given(commentRepository.existsByPostEntity_Id(anyLong())).willReturn(true);
		this.mockCommentExistsByPostId(postId, true);
		given(commentRepository.findByPostEntity_Id(postId)).willReturn(List.of(commentEntity));
		given(commentMapper.convertCommentEntityListToDtoList(anyList())).willReturn(List.of(commentDto));
		
		List<CommentDto> comments = commentService.getCommentsByPostId(postId);
		
		assertThat(comments).isNotEmpty().hasSize(1);
	}
	
	@Test
	void givenInvalidPostId_whenGetCommentById_theThrowException() {
		long postId = 0;
		long commentId = 0;
		//given(postRepository.existsById(anyLong())).willReturn(false);
		this.mockPostExistsById(postId, false);
		
		assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(postId, commentId));
		
		verify(commentRepository, never()).findById(anyLong());
	}
	
	@Test
	void givenValidPostIdInvalidCommentId_whenGetCommentById_theThrowException() {
		long postId = 0;
		long commentId = 0;
		//given(postRepository.existsById(anyLong())).willReturn(true);
		this.mockPostExistsById(postId, true);
		//given(commentRepository.findById(anyLong())).willReturn(Optional.empty());
		this.mockFindByCommentId(commentId, Optional.empty());
		
		assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(postId, commentId));
	}
	
	@Test
	void givenValidPostIdValidCommentIdNotBelongsToPost_whenGetCommentById_theThrowException() {
		long postId = 2;
		long commentId = 1;
		//given(postRepository.existsById(anyLong())).willReturn(true);
		this.mockPostExistsById(postId, true);
		//given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(commentEntity));
		this.mockFindByCommentId(commentId, Optional.ofNullable(commentEntity));
		
		assertThrows(BlogApiException.class, () -> commentService.getCommentById(postId, commentId));
	}
	
	@Test
	void givenValidPostIdValidCommentIdBelongsToPost_whenGetCommentById_thenThrowException() {
		long postId = 1;
		long commentId = 1;
		//given(postRepository.existsById(anyLong())).willReturn(true);
		this.mockPostExistsById(postId, true);
		//given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(commentEntity));
		this.mockFindByCommentId(commentId, Optional.ofNullable(commentEntity));
		given(commentMapper.convertCommentEntityToDto(any(CommentEntity.class))).willReturn(commentDto);
		
		CommentDto commentById = commentService.getCommentById(postId, commentId);
		
		assertThat(commentById).isNotNull();
	}
	
	@Test
	void givenInvalidPostId_whenUpdateComment_thenThrowException() {
		long postId = 0;
		long commentId = 1;
		//given(postRepository.existsById(anyLong())).willReturn(false);
		this.mockPostExistsById(postId, false);
		
		Executable methodCall = () -> commentService.updateComment(postId, commentId, this.createCommentUpdateDto());
		assertThrows(ResourceNotFoundException.class, methodCall);
	}
	
	@Test
	void givenValidPostIdInvalidCommentId_whenUpdateComment_thenThrowException() {
		long postId = 1;
		long commentId = 0;
		//given(postRepository.existsById(anyLong())).willReturn(true);
		this.mockPostExistsById(postId, true);
		//given(commentRepository.findById(commentId)).willReturn(Optional.empty());
		this.mockFindByCommentId(commentId, Optional.empty());
		
		Executable methodCall = () -> commentService.updateComment(postId, commentId, this.createCommentUpdateDto());
		assertThrows(ResourceNotFoundException.class, methodCall);
	}
	
	@Test
	void givenCommentIdNotBelongsToPostId_whenUpdateComment_thenThrowException() {
		long postId = 2;
		long commentId = 1;
		//given(postRepository.existsById(anyLong())).willReturn(true);
		this.mockPostExistsById(postId, true);
		//given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(commentEntity));
		this.mockFindByCommentId(commentId, Optional.ofNullable(commentEntity));
		
		Executable methodCall = () -> commentService.updateComment(postId, commentId, this.createCommentUpdateDto());
		assertThrows(BlogApiException.class, methodCall);
	}
	
	@Test
	void givenCommentIdBelongsToPostId_whenUpdateComment_thenReturnUpdatedComment() {
		long postId = 1;
		long commentId = 1;
		
		CommentEntity updatedCommentEntity = CommentEntity.builder()
				.id(1l)
				.name("test name")
				.email("test@email.com")
				.body("Updated Test body").build();
		
		CommentDto commentDtoResponse = CommentDto.builder()
				.id(1l)
				.name("test name")
				.email("test@email.com")
				.body("Updated Test body").build();
		
		//given(postRepository.existsById(anyLong())).willReturn(true);
		this.mockPostExistsById(postId, true);
		//given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(commentEntity));
		this.mockFindByCommentId(commentId, Optional.ofNullable(commentEntity));
		given(commentRepository.save(any(CommentEntity.class))).willReturn(updatedCommentEntity);
		//given(commentMapper.convertCommentEntityToDto(any(CommentEntity.class))).willReturn(commentDtoRequest);
		this.mockCommentMapperToDto(updatedCommentEntity, commentDtoResponse);
		
		CommentDto updatedComment = commentService.updateComment(postId, commentId, createCommentUpdateDto());
		
		assertThat(updatedComment).isNotNull();
		assertThat(updatedComment.getBody()).isEqualTo(this.createCommentUpdateDto().getBody());
		verify(commentMapper, times(1)).updateCommentEntity(this.createCommentUpdateDto(), commentEntity);
	}
	
	@Test
	void givenInvalidPost_whenDeleteCommetById_thenThrowException() {
		long postId = 0;
		long commentId = 0;
		this.mockPostExistsById(postId, false);
		
		assertThrows(ResourceNotFoundException.class, () -> commentService.deleteCommetById(postId, commentId));
	}
	
	@Test
	void givenValidPostIdInvalidCommentId_whenDeleteCommetById_thenThrowException() {
		long postId = 1;
		long commentId = 0;
		
		this.mockPostExistsById(postId, true);
		this.mockFindByCommentId(commentId, Optional.empty());
		
		assertThrows(ResourceNotFoundException.class, () -> commentService.deleteCommetById(postId, commentId));
	}
	
	@Test
	void givenCommentIdNotBelongsToPost_whenDeleteCommetById_thenThrowException() {
		long postId = 2;
		long commentId = 1;
		
		this.mockPostExistsById(postId, true);
		this.mockFindByCommentId(commentId, Optional.ofNullable(commentEntity));
		
		assertThrows(BlogApiException.class, () -> commentService.deleteCommetById(postId, commentId));
	}
	
	@Test
	void givenCommentIdBelongsToPost_whenDeleteCommetById_thenDeleteComment() {
		long postId = 1;
		long commentId = 1;
		
		this.mockPostExistsById(postId, true);
		this.mockFindByCommentId(commentId, Optional.ofNullable(commentEntity));
		BDDMockito.willDoNothing().given(commentRepository).delete(commentEntity);
		
		commentService.deleteCommetById(postId, commentId);
		
		verify(commentRepository, times(1)).delete(commentEntity);
	}
	
	private void mockPostExistsById(long postId, boolean value) {
		given(postRepository.existsById(postId)).willReturn(value);
	}
	
	private void mockCommentExistsByPostId(long postId, boolean value) {
		given(commentRepository.existsByPostEntity_Id(postId)).willReturn(value);
	}
	
	private void mockFindByCommentId(long commentId, Optional<CommentEntity> optionalCommentEntity) {
		given(commentRepository.findById(commentId)).willReturn(optionalCommentEntity);
	}
	
	private void mockCommentMapperToDto(CommentEntity commentEntity, CommentDto commentDto) {
		given(commentMapper.convertCommentEntityToDto(commentEntity)).willReturn(commentDto);
	}
	
	private CommentUpdateDto createCommentUpdateDto() {
		return CommentUpdateDto.builder()
							  .id(1l)
							  .name("test name")
							  .email("test@email.com")
							  .body("Updated Test body").build();
	}
}
