package com.app.dev.blog.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.app.dev.blog.dtos.CommentDto;
import com.app.dev.blog.model.CommentEntity;

@Mapper(componentModel = "spring")
public interface CommentMapper {
	
	public CommentDto convertCommentEntityToDto(CommentEntity commentEntity);
	
	@Mapping(target = "postEntity", ignore = true)
	public CommentEntity convertCommentDtoToEntity(CommentDto commentDto);
	
	public List<CommentDto> convertCommentEntityListToDtoList(List<CommentEntity> commentEntities);
	
	@Mapping(target = "postEntity", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public void updateCommentEntity(CommentDto commentDto, @MappingTarget CommentEntity commentEntity);
}
