package com.app.dev.blog.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.app.dev.blog.dtos.PostDto;
import com.app.dev.blog.dtos.PostPageDto;

public interface PostService {
	
	public PostDto createPost(PostDto postDto);
	
	public PostPageDto getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
	
	public PostDto getPostById(long id);
	
	public PostDto updatePost(@PathVariable("id") long id, @RequestBody PostDto postDto);
	
	public void deletePostById(long id);
}
