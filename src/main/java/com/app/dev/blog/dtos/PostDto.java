package com.app.dev.blog.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
	
	private Long id;
	
	@NotEmpty
	@Size(min = 2, message = "Post title should have atleast 2 characters")
	private String title; 
	
	@NotEmpty
	@Size(min = 10, message = "Post description should have atleast 10 characters")
	private String description;
	
	@NotEmpty
	private String content;
}
