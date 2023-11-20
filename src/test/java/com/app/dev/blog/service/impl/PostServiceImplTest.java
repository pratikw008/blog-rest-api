package com.app.dev.blog.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.app.dev.blog.dtos.PostDto;
import com.app.dev.blog.dtos.PostPageDto;
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
	void givenEmptyPosts_whenGetAllPosts_thenReturnEmptyPostPageDto() {
		int pageNo = 0;
		int pageSize = 10;
		String sortBy = "title";
		String sortDir = Sort.Direction.ASC.name();
		
		given(postRepository.count()).willReturn(0L);
		
		PostPageDto postPageDto = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
		
		assertThat(postPageDto.getContent()).isEmpty();
		
		verify(postRepository, never()).findAll(any(Pageable.class));
	}
	
	@Test
	void givenListOfPosts_whenGetAllPosts_thenReturnPaginatedPostPageDtoSortByASC() {
		int pageNo = 0;
		int pageSize = 10;
		String sortBy = "title";
		String sortDir = Sort.Direction.ASC.name();

		PostEntity postEntity2 = PostEntity.builder()
				   .id(2l)
				   .title("ztest title")
				   .description("ztest description")
				   .content("ztest content").build();
		
		PostDto postDto2 = PostDto.builder()
				   				  .id(2l)
				   				  .title("ztest title")
				   				  .description("ztest description")
				   				  .content("ztest content").build();
		
		List<PostEntity> content = List.of(postEntity, postEntity2);
		
		int totalElements = content.size();
		
		List<PostDto> postDtos = List.of(postDto, postDto2);
		
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
		
		Page<PostEntity> page = new PageImpl<>(content, pageable, totalElements);
		
		given(postRepository.count()).willReturn(Long.valueOf(totalElements));
		given(postRepository.findAll(pageable)).willReturn(page);
		given(postMapper.convertPosEntitytListToPostDtoList(anyList())).willReturn(postDtos);
		
		PostPageDto postPageDto = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
		
		assertThat(postPageDto.getContent()).hasSize(totalElements);
		assertIterableEquals(postDtos, postPageDto.getContent());
		assertThat(postPageDto.getContent()).isSortedAccordingTo(Comparator.comparing(PostDto::getTitle));
		assertThat(postPageDto.getContent().get(0).getTitle()).isEqualTo("test title");
	}
	
	@Test
	void givenListOfPosts_whenGetAllPosts_thenReturnPaginatedPostPageDtoSortByDESC() {
		int pageNo = 0;
		int pageSize = 10;
		String sortBy = "title";
		String sortDir = Sort.Direction.DESC.name();

		PostEntity postEntity2 = PostEntity.builder()
				   						   .id(2l)
				   						   .title("ztest title")
				   						   .description("ztest description")
				   						   .content("ztest content").build();
		
		PostDto postDto2 = PostDto.builder()
				   				  .id(2l)
				   				  .title("ztest title")
				   				  .description("ztest description")
				   				  .content("ztest content").build();
		
		List<PostEntity> content = List.of(postEntity, postEntity2);
		
		int totalElements = content.size();
		
		List<PostDto> postDtos = List.of(postDto2, postDto);
		
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
		
		Page<PostEntity> page = new PageImpl<>(content, pageable, totalElements);
		
		given(postRepository.count()).willReturn(Long.valueOf(totalElements));
		given(postRepository.findAll(pageable)).willReturn(page);
		given(postMapper.convertPosEntitytListToPostDtoList(anyList())).willReturn(postDtos);
		
		PostPageDto postPageDto = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
		
		assertThat(postPageDto.getContent()).hasSize(totalElements);
		assertIterableEquals(postDtos, postPageDto.getContent());
		assertThat(postPageDto.getContent()).isSortedAccordingTo(Comparator.comparing(PostDto::getTitle).reversed());
		assertThat(postPageDto.getContent().get(0).getTitle()).isEqualTo("ztest title");
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
		int id = 0;
		given(postRepository.findById(anyLong())).willReturn(Optional.empty());
		
		assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(id));
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
