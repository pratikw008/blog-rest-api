package com.app.dev.blog.exception;

import lombok.Getter;

@Getter
public class BlogApiException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4459042732871918404L;
	
	private final String message;

	public BlogApiException(String message) {
		super(message);
		this.message = message;
	}	
}
