package com.app.dev.blog.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.app.dev.blog.dtos.PostDto;
import com.app.dev.blog.model.PostEntity;

@Mapper(componentModel = "spring")
public interface PostMapper {
	
	public PostDto convertPostEntityToDto(PostEntity postEntity);
	
	public PostEntity convertPostDtoToEntity(PostDto postDto);
	
	public List<PostDto> convertPosEntitytListToPostDtoList(List<PostEntity> posts);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public void updatePostEntity(PostDto postDto, @MappingTarget PostEntity postEntity);
}
