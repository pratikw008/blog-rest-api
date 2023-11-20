package com.app.dev.blog.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.dev.blog.dtos.PostDto;
import com.app.dev.blog.dtos.PostPageDto;
import com.app.dev.blog.exception.ResourceNotFoundException;
import com.app.dev.blog.mapper.PostMapper;
import com.app.dev.blog.model.PostEntity;
import com.app.dev.blog.repository.PostRepository;
import com.app.dev.blog.service.PostService;

@Service
public class PostServiceImpl implements PostService {
	
	private PostRepository postRepository;
	
	private PostMapper postMapper;

	public PostServiceImpl(PostRepository postRepository, PostMapper postMapper) {
		super();
		this.postRepository = postRepository;
		this.postMapper = postMapper;
	}

	@Override
	public PostDto createPost(PostDto postDto) {
		PostEntity postEntity = postMapper.convertPostDtoToEntity(postDto);
		PostEntity created = postRepository.save(postEntity);
		return postMapper.convertPostEntityToDto(created);
	}
	
	@Override
	public PostPageDto getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
		
		if(postRepository.count() == 0)
			return new PostPageDto();
		
		Sort sort;
		if(sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())) {
			sort = Sort.by(sortBy).ascending();
		}
		else {
			sort = Sort.by(sortBy).descending();
		}
		
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		Page<PostEntity> page = postRepository.findAll(pageable);
		
		PostPageDto postPageDto = new PostPageDto();
		postPageDto.setContent(postMapper.convertPosEntitytListToPostDtoList(page.getContent()));
		postPageDto.setPageNo(page.getNumber());
		postPageDto.setPageSize(page.getSize());
		postPageDto.setTotalElements(page.getTotalElements());
		postPageDto.setTotalPages(page.getTotalPages());
		postPageDto.setLast(page.isLast());
				
		return postPageDto;
	}
	
	@Override
	public PostDto getPostById(long id) {
		return postRepository.findById(id)
					  		 .map(postMapper::convertPostEntityToDto)
					  		 .orElseThrow(() -> new ResourceNotFoundException("PostEntity", "Id", id));
	}
	
	@Override
	public PostDto updatePost(long id, PostDto postDto) {
		postDto.setId(id);
		return postRepository.findById(id)
					  		 .map(entity -> this.update(entity, postDto))
					  		 .orElseThrow(() -> new ResourceNotFoundException("PostEntity", "Id", id));
	}

	private PostDto update(PostEntity entity, PostDto postDto) {
		postMapper.updatePostEntity(postDto, entity);
		PostEntity updatedPost = postRepository.save(entity);
		return postMapper.convertPostEntityToDto(updatedPost);
	}
	
	@Override
	public void deletePostById(long id) {
		PostEntity post = postRepository.findById(id)
					  .orElseThrow(() -> new ResourceNotFoundException("PostEntity", "Id", id));
		postRepository.delete(post);
	}
	
}
