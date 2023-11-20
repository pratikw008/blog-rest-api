package com.app.dev.blog.controller.integrations;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.app.dev.blog.containers.TestContainerConfig;
import com.app.dev.blog.dtos.PostDto;
import com.app.dev.blog.mapper.PostMapper;
import com.app.dev.blog.model.PostEntity;
import com.app.dev.blog.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PostControllerIntegrationTest extends TestContainerConfig {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PostMapper postMapper;
	
	private PostDto postDto;
	
	@BeforeEach
	public void setup() {
		postDto = PostDto.builder()
				 .title("test title")
				 .description("test description")
				 .content("test content").build();
		
		postRepository.deleteAll();
	}
	
	@Test
	void givenPostDto_whenCreatePost_thenReturnCreatedPostDto() throws JsonProcessingException, Exception {
		ResultActions resultActions = mockMvc.perform(post("/api/posts")
						 .contentType(MediaType.APPLICATION_JSON)
						 .content(objectMapper.writeValueAsString(postDto)));
		
		resultActions.andExpect(status().isCreated())
					 	 .andExpect(jsonPath("$.title", CoreMatchers.is(postDto.getTitle())))
					 	 .andDo(print());
	}
	
	@Test
	void givenListPost_whenGetAllPosts_thenReturnPaginatedPostPageDto() throws Exception {
		int pageNo = 0;
		int pageSize = 10;
		String sortBy = "title";
		String sortDir = "desc";
		PostDto postDto2 = PostDto.builder()
				 				 .title("test title 2")
				 				 .description("test description 2")
				 				 .content("test content 2").build();
		
		List<PostEntity> posts = List.of(postMapper.convertPostDtoToEntity(postDto), 
										 postMapper.convertPostDtoToEntity(postDto2));
		postRepository.saveAll(posts);
		
		ResultActions resultActions = mockMvc.perform(get("/api/posts")
				.param("pageNo", String.valueOf(pageNo))
				.param("pageSize", String.valueOf(pageSize))
				.param("sortBy", sortBy)
				.param("sortDir", sortDir));
											
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.content.size()", CoreMatchers.is(posts.size())))
					 .andExpect(jsonPath("$.content[0].title", CoreMatchers.is("test title 2")))
					 .andDo(print());
	}
	
	@Test
	void givenValid_whenGetPostById_thenReturnPostDto() throws Exception {
		Long id = postRepository.save(postMapper.convertPostDtoToEntity(postDto)).getId();
		
		ResultActions resultActions = mockMvc.perform(get("/api/posts/{id}", id));
		
		resultActions.andExpect(status().isOk())
					 .andDo(print());		
	}
	
	@Test
	void givenValidIdPostDto_whenUpdatePost_thenReturnUpdatedPost() throws JsonProcessingException, Exception {
		PostDto updatedPostDto = PostDto.builder()
				 						 .title("test title updated")
				 						 .description("test description updated")
				 						 .content("test content updated")
				 						 .build();
		
		Long id = postRepository.save(postMapper.convertPostDtoToEntity(postDto)).getId();
		
		ResultActions resultActions = mockMvc.perform(put("/api/posts/{id}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedPostDto)));
		
		resultActions.andExpect(status().isOk())
					 .andExpect(jsonPath("$.title", CoreMatchers.is(updatedPostDto.getTitle())))
					 .andDo(print());
	}
	
	@Test
	void givenValidId_whenDeletePostById_thenReturnSuccessMsg() throws Exception {
		Long id = postRepository.save(postMapper.convertPostDtoToEntity(postDto)).getId();
		
		mockMvc.perform(delete("/api/posts/{id}", id))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$", CoreMatchers.is("PostEntity deleted successfully")))
			   .andDo(print());
	}
}
