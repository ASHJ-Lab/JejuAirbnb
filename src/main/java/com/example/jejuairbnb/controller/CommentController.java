package com.example.jejuairbnb.controller;

import com.example.jejuairbnb.controller.CommentControllerDto.FindCommentOneResponseDto;
import com.example.jejuairbnb.controller.CommentControllerDto.FindCommentResponseDto;
import com.example.jejuairbnb.services.CommentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "comments", description = "댓글 API")
@AllArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping()
    public FindCommentResponseDto getComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        return commentService.findComment(pageable);
    }

    @GetMapping("/{id}")
    public FindCommentOneResponseDto findCommentOne(
            @Parameter(description = "코멘트 id", required = true) Long id
    ) {
        return commentService.findCommentOneById(id);
    }
}
