package com.app.dev.blog.controller;

import static org.mockito.BDDMockito.given;
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
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.app.dev.blog.dtos.CommentDto;
import com.app.dev.blog.dtos.CommentUpdateDto;
import com.app.dev.blog.service.CommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CommentService commentService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private CommentDto commentDto;
	
	@BeforeEach
	void setUp() {		
		commentDto = CommentDto.builder()
				.id(1l)
				.name("test name")
				.email("test email")
				.body("test body")
				.build();
	}
	
	@Test
	void givenPostIdComment_whenCreateComment_thenReturnComment() throws JsonProcessingException, Exception {
		long postId = 1;
		given(commentService.createComment(postId, commentDto)).willReturn(commentDto);
		
		ResultActions resultActions = mockMvc.perform(post("/api/posts/{postId}/comments", postId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commentDto)));
		
		resultActions.andExpect(MockMvcResultMatchers.status().isCreated())
					 .andExpect(jsonPath("$.id", CoreMatchers.notNullValue()))
					 .andDo(print());
	}

	@Test
	void givenPostId_whenGetCommentsByPostId_thenReturnListComment() throws Exception {
		long postId = 1;
		CommentDto commentDto2 = CommentDto.builder()
										  .id(2l)
										  .name("test name 2")
										  .email("test email 2")
										  .body("test body 2")
										  .build();
		List<CommentDto> comments = List.of(commentDto, commentDto2);
		
		given(commentService.getCommentsByPostId(postId)).willReturn(comments);
		
		ResultActions resultActions = mockMvc.perform(get("/api/posts/{postId}/comments", postId)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.size()", CoreMatchers.is(comments.size())))
					 .andDo(print());
	}
	
	@Test
	void givenPostIdCommentId_whenGetCommentById_thenReturnComment() throws Exception {
		long postId = 1;
		long commentId = 1;
		given(commentService.getCommentById(postId, commentId)).willReturn(commentDto);
		
		ResultActions resultActions = mockMvc.perform(get("/api/posts/{postId}/comments/{commentId}", postId, commentId)
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.name", CoreMatchers.is(commentDto.getName())))
					 .andDo(print());
	}
	
	@Test
	void givenPostIdCommentIdComment_whenUpdateComment_thenReturnUpdatedComment() throws JsonProcessingException, Exception {
		long postId = 1;
		long commentId = 1;
		CommentUpdateDto commentUpdateRequest = CommentUpdateDto.builder()
				.id(1l)
				.name("test name")
				.email("testemail@gmail.com")
				.body("Updated Test body").build();
		
		CommentDto commentDtoResponse = CommentDto.builder()
				.id(1l)
				.name("test name")
				.email("testemail@gmail.com")
				.body("Updated Test body").build();
		given(commentService.updateComment(postId, commentId, commentUpdateRequest)).willReturn(commentDtoResponse);
		
		ResultActions resultActions = mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, commentId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commentUpdateRequest)));
		
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.body", CoreMatchers.is(commentDtoResponse.getBody())))
					 .andDo(print());
	}
	
	@Test
	void givenPostIdCommentId_whenDeleteComment_thenDeleteComment() throws Exception {
		long postId = 1;
		long commentId = 1;
		BDDMockito.willDoNothing().given(commentService).deleteCommetById(postId, commentId);
		
		ResultActions resultActions = mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId));
		
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$", CoreMatchers.is("Comment deleted successfully")))
					 .andDo(print());
	}
}
