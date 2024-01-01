package com.app.dev.blog.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorDetails {

	private LocalDateTime timestamp;
	
	private String message;
	
	private String status;
	
	private String path;
}
