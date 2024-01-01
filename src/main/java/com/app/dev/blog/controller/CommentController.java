package com.app.dev.blog.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.app.dev.blog.dtos.CommentDto;
import com.app.dev.blog.dtos.CommentUpdateDto;
import com.app.dev.blog.service.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class CommentController {
	
	private CommentService commentService;

	public CommentController(CommentService commentService) {
		super();
		this.commentService = commentService;
	}
	
	@PostMapping("/{postId}/comments")
	public ResponseEntity<CommentDto> createComment(@PathVariable("postId") long postId, 
													@Valid @RequestBody CommentDto commentDto) {
		CommentDto savedComment = commentService.createComment(postId, commentDto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
								   .path("/{postId}")
								   .buildAndExpand(savedComment.getId())
								   .toUri();
		return ResponseEntity.created(location).body(savedComment);
	}
	
	@GetMapping("/{postId}/comments")
	public List<CommentDto> getCommentsByPostId(@PathVariable("postId") long postId) {
		return commentService.getCommentsByPostId(postId);
	}
	
	@GetMapping("/{postId}/comments/{commentId}")
	public ResponseEntity<CommentDto> getCommentById(@PathVariable("postId") long postId, @PathVariable("commentId") long commentId) {
		CommentDto commentDto = commentService.getCommentById(postId, commentId);
		return ResponseEntity.ok(commentDto);
	}
	
	@PutMapping("/{postId}/comments/{commentId}")
	public ResponseEntity<CommentDto> updateComment(@PathVariable("postId") long postId, 
													@PathVariable("commentId") long commentId, 
													@Valid @RequestBody CommentUpdateDto commentDto) {
		CommentDto updatedComment = commentService.updateComment(postId, commentId, commentDto);
		return ResponseEntity.ok(updatedComment);
	}
	
	@DeleteMapping("/{postId}/comments/{commentId}")
	public ResponseEntity<String> deleteComment(@PathVariable("postId") long postId, @PathVariable("commentId") long commentId) {
		commentService.deleteCommetById(postId, commentId);
		return ResponseEntity.ok("Comment deleted successfully");
	}
}
