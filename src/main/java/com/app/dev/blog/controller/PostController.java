package com.app.dev.blog.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.app.dev.blog.dtos.PostDto;
import com.app.dev.blog.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	private PostService postService;
	
	public PostController(PostService postService) {
		super();
		this.postService = postService;
	}

	@PostMapping
	public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
		PostDto createdPost = postService.createPost(postDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
								   .path("/{id}")
								   .buildAndExpand(createdPost.getId())
								   .toUri();
		
		return ResponseEntity.created(location).body(createdPost);
	}
	
	@GetMapping
	public ResponseEntity<List<PostDto>> getAllPosts() {
		return ResponseEntity.ok(postService.getAllPosts());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<PostDto> getPostById(@PathVariable("id")long id) {
		return ResponseEntity.ok(postService.getPostById(id));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<PostDto> updatePost(@PathVariable("id") long id, @RequestBody PostDto postDto) {
		return ResponseEntity.ok(postService.updatePost(id, postDto));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePostById(@PathVariable("id") long id) {
		postService.deletePostById(id);
		return ResponseEntity.ok("PostEntity deleted successfully");
	}
}