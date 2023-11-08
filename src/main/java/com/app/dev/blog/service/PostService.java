package com.app.dev.blog.service;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.app.dev.blog.dtos.PostDto;

public interface PostService {
	
	public PostDto createPost(PostDto postDto);
	
	public List<PostDto> getAllPosts();
	
	public PostDto getPostById(long id);
	
	public PostDto updatePost(@PathVariable("id") long id, @RequestBody PostDto postDto);
	
	public void deletePostById(long id);
}
