package com.app.dev.blog.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
	
	private Long id;
	
	@NotEmpty(message = "Name should not be empty or null")
	private String name;
	
	@NotEmpty(message = "Email should not be empty or null")
	@Email
	private String email;

	@NotEmpty
	@Size(min = 10, message = "Comment body must have 10 characters")
	private String body;
}
