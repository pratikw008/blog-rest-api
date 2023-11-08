package com.app.dev.blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.app.dev.blog.dtos.PostDto;
import com.app.dev.blog.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = PostController.class)
class PostControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PostService postService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private PostDto postDto;
	
	@BeforeEach
	public void setup() {
		postDto = PostDto.builder()
						 .id(1)
						 .title("test title")
						 .description("test description")
						 .content("test content").build();
	}
	
	@Test
	void givenPostDto_whenCreatePost_thenReturnCreatedPostDto() throws JsonProcessingException, Exception {
		given(postService.createPost(any(PostDto.class))).willReturn(postDto);
		
		ResultActions resultActions = mockMvc.perform(post("/api/posts").contentType(MediaType.APPLICATION_JSON)
						 .content(objectMapper.writeValueAsString(postDto)));
		
		resultActions.andExpect(status().isCreated())
					 	 .andExpect(jsonPath("$.title", CoreMatchers.is(postDto.getTitle())))
					 	 .andDo(print());
	}
	
	@Test
	void givenListPost_whenGetAllPosts_thenReturnListPostDto() throws Exception {
		PostDto postDto2 = PostDto.builder()
				 				 .id(2)
				 				 .title("test title 2")
				 				 .description("test description 2")
				 				 .content("test content 2").build();
		List<PostDto> posts = List.of(postDto, postDto2);
		given(postService.getAllPosts()).willReturn(posts);
		
		ResultActions resultActions = mockMvc.perform(get("/api/posts"));
											
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.size()", CoreMatchers.is(posts.size())))
					 .andDo(print());
	}
	
	@Test
	void givenValid_whenGetPostById_thenReturnPostDto() throws Exception {
		given(postService.getPostById(anyLong())).willReturn(postDto);
		
		ResultActions resultActions = mockMvc.perform(get("/api/posts/{id}", postDto.getId()));
		
		resultActions.andExpect(status().isOk())
					 .andDo(print());		
	}
	
	@Test
	void givenValidIdPostDto_whenUpdatePost_thenReturnUpdatedPost() throws JsonProcessingException, Exception {
		PostDto updatedPostDto = PostDto.builder()
				 						 .id(1)
				 						 .title("test title updated")
				 						 .description("test description updated")
				 						 .content("test content updated")
				 						 .build();
		given(postService.updatePost(anyLong(), any(PostDto.class))).willReturn(updatedPostDto);
		
		ResultActions resultActions = mockMvc.perform(put("/api/posts/{id}", postDto.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedPostDto)));
		
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.title", CoreMatchers.is(updatedPostDto.getTitle())))
					 .andDo(print());
	}
	
	@Test
	void givenValidId_whenDeletePostById_thenReturnSuccessMsg() throws Exception {
		willDoNothing().given(postService).deletePostById(anyLong());
		
		mockMvc.perform(delete("/api/posts/{id}", postDto.getId()))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", CoreMatchers.is("PostEntity deleted successfully")))
			   .andDo(print());
	}
}
