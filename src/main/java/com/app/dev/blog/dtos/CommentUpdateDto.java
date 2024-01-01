package com.app.dev.blog.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentUpdateDto {
	
	private Long id;
	
	@Size(min = 2, message = "Comment name must have 2 characters")
	private String name;
	
	@Email
	private String email;

	@Size(min = 10, message = "Comment body must have 10 characters")
	private String body;
}
