package com.app.dev.blog.controller.integrations;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.app.dev.blog.containers.TestContainerConfig;
import com.app.dev.blog.dtos.CommentDto;
import com.app.dev.blog.model.CommentEntity;
import com.app.dev.blog.model.PostEntity;
import com.app.dev.blog.repository.CommentRepository;
import com.app.dev.blog.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Rollback(true)
class CommentControllerIntegrationTest extends TestContainerConfig {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private CommentDto commentDto;
	
	private PostEntity postEntity ;
	
	@BeforeEach
	void setUp() {
		commentRepository.deleteAll();
		postRepository.deleteAll();
		
		postEntity = postRepository.save(PostEntity.builder()
   												   .title("test title integration test")
   												   .description("test description")
   												   .content("test content")
   												   .build());
		
		commentDto = CommentDto.builder()				
							   .name("test name 1")
							   .email("test email 1")
							   .body("test body 1")
							   .build();
	}

	@Test
	void givenPostIdComment_whenCreateComment_thenReturnComment() throws JsonProcessingException, Exception {
		ResultActions resultActions = mockMvc.perform(post("/api/posts/{postId}/comments", postEntity.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commentDto)));
		
		resultActions.andExpect(MockMvcResultMatchers.status().isCreated())
					 .andExpect(jsonPath("$.id", CoreMatchers.notNullValue()))
					 .andDo(print());
	}
	
	@Test
	void givenPostId_whenGetCommentsByPostId_thenReturnListComment() throws Exception {
		CommentEntity commentEntity = this.createCommentEntity();
		
		CommentEntity commentEntity2 = CommentEntity.builder()
										  .name("test name 2")
										  .email("test email 2")
										  .body("test body 2")
										  .postEntity(postEntity)
										  .build();
		
		List<CommentEntity> comments = commentRepository.saveAll(Arrays.asList(commentEntity, commentEntity2));
		
		ResultActions resultActions = mockMvc.perform(get("/api/posts/{postId}/comments", postEntity.getId())
				.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.size()", CoreMatchers.is(comments.size())))
					 .andDo(print());
	}
	
	@Test
	void givenPostIdCommentId_whenGetCommentById_thenReturnComment() throws Exception {
		Long commentId = commentRepository.save(this.createCommentEntity()).getId();
		
		ResultActions resultActions = mockMvc.perform(get("/api/posts/{postId}/comments/{commentId}"
														, postEntity.getId(), commentId)
														.accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.name", CoreMatchers.is(commentDto.getName())))
					 .andDo(print());
	}
	
	@Test
	void givenPostIdCommentIdComment_whenUpdateComment_thenReturnUpdatedComment() throws JsonProcessingException, Exception {
		CommentDto commentDtoRequest = CommentDto.builder()
				.name("test name")
				.email("test email")
				.body("Updated Test body").build();
		
		Long commentId = commentRepository.save(this.createCommentEntity()).getId();
		
		ResultActions resultActions = mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}"
														, postEntity.getId(), commentId)
														.contentType(MediaType.APPLICATION_JSON)
														.content(objectMapper.writeValueAsString(commentDtoRequest)));
		
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.body", CoreMatchers.is("Updated Test body")))
					 .andDo(print());
	}
	
	@Test
	void givenPostIdCommentId_whenDeleteComment_thenDeleteComment() throws Exception {
		Long commentId = commentRepository.save(this.createCommentEntity()).getId();
		
		ResultActions resultActions = mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}"
															, postEntity.getId(), commentId));
		
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$", CoreMatchers.is("Comment deleted successfully")))
					 .andDo(print());
	}
	
	private CommentEntity createCommentEntity() {
		return CommentEntity.builder()
				  			.name("test name 1")
				  			.email("test email 1")
				  			.body("test body 1")
				  			.postEntity(postEntity)
				  			.build();
	}
}
