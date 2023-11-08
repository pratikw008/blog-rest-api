package com.app.dev.blog.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.dev.blog.dtos.PostDto;
import com.app.dev.blog.exception.ResourceNotFoundException;
import com.app.dev.blog.mapper.PostMapper;
import com.app.dev.blog.model.PostEntity;
import com.app.dev.blog.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {
	
	@Mock
	private PostRepository postRepository;
	
	@Mock
	private PostMapper postMapper;
	
	@InjectMocks
	private PostServiceImpl postService;
	
	private PostEntity postEntity;
	
	private PostDto postDto;
	
	@BeforeEach
	public void setup() {
		postEntity = PostEntity.builder()
							   .id(1l)
							   .title("test title")
							   .description("test description")
							   .content("test content").build();
		postDto = PostDto.builder()
						 .id(1l)
						 .title("test title")
						 .description("test description")
						 .content("test content").build();
	}
	
	@Test
	void givenPostDto_whenCreatePost_thenReturnCreatedPostDto() {
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity);
		
		given(postMapper.convertPostDtoToEntity(any(PostDto.class))).willReturn(postEntity);
		
		given(postMapper.convertPostEntityToDto(any(PostEntity.class))).willReturn(postDto);
		
		PostDto createdPost = postService.createPost(postDto);
		
		assertThat(createdPost).isNotNull();
		
		assertEquals(postDto, createdPost);
	}
	
	@Test
	void givenEmptyPosts_whenGetAllPosts_thenReturnEmptyList() {
		given(postRepository.findAll()).willReturn(Collections.emptyList());
		
		List<PostDto> allPosts = postService.getAllPosts();
		
		assertThat(allPosts).isEmpty();
	}
	
	@Test
	void givenPostsList_whenGetAllPosts_thenReturnPostDtoList() {
		given(postRepository.findAll()).willReturn(List.of(postEntity));
		
		given(postMapper.convertPosEntitytListToPostDtoList(anyList())).willReturn(List.of(postDto));
		
		List<PostDto> allPosts = postService.getAllPosts();
		
		assertThat(allPosts).hasSize(1)
							.contains(postDto);
	}
	
	@Test
	void givenValidId_whenGetPostById_thenReturnPostDto() {
		given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(postEntity));
		
		given(postMapper.convertPostEntityToDto(any(PostEntity.class))).willReturn(postDto);
		
		PostDto postById = postService.getPostById(postEntity.getId());
		
		assertThat(postById).isNotNull().isEqualTo(postDto);
	}
	
	@Test
	void givenInvalidId_whenGetPostById_thenThrowException() {
		given(postRepository.findById(anyLong())).willReturn(Optional.empty());
		
		assertThrows(RuntimeException.class, () -> postService.getPostById(postEntity.getId()));
		/**try {
			postService.getPostById(0);
		} 
		catch (Exception e) {
			assertEquals(RuntimeException.class, e.getClass());
		}**/
	}
	
	@Test
	void givenValidIdPostDto_whenUpdatePost_thenReturnUpdatedPostDto() {
		given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(postEntity));
		
		PostDto postDtoToUpdate = PostDto.builder()
										 .id(1l)
										 .title("test title updated")
										 .description("test description updated")
										 .content("test content updated")
										 .build();
		
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity);
		
		given(postMapper.convertPostEntityToDto(any(PostEntity.class))).willReturn(postDtoToUpdate);
		
		PostDto updatedPost = postService.updatePost(postDtoToUpdate.getId(), postDtoToUpdate);
		
		assertThat(updatedPost).isNotNull();
	}
	
	@Test
	void givenInvalidId_whenUpdatePost_thenThrowException() {
		given(postRepository.findById(anyLong())).willReturn(Optional.empty());
		
		//ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(0, postDto));
		assertThatThrownBy(() -> postService.updatePost(0, postDto))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasFieldOrPropertyWithValue("resourceName", "PostEntity")
					.hasMessage("PostEntity not found with Id : '0'");
		
		//assertEquals(ResourceNotFoundException.class, exception.getClass());
		//assertEquals("PostEntity", exception.getResourceName());
		verify(postRepository, never()).save(any(PostEntity.class));	
	}
	
	@Test
	void givenValidId_whenDeletePostById_thenDeletePost() {
		int id = 1;
		given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(postEntity));
		
		willDoNothing().given(postRepository).delete(any(PostEntity.class));
		
		postService.deletePostById(id);
		
		verify(postRepository, times(1)).delete(any(PostEntity.class));
	}
	
	@Test
	void givenInvalidId_whenDeletePostById_thenThrowsExceptionNotFound() {
		int id = 0;
		given(postRepository.findById(anyLong())).willReturn(Optional.empty());
		
		assertThrows(ResourceNotFoundException.class, () -> postService.deletePostById(id));
		
		verify(postRepository, never()).delete(any(PostEntity.class));
	}
}
