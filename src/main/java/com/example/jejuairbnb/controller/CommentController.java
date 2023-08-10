package com.example.jejuairbnb.controller;

import com.example.jejuairbnb.adminController.AdminCommentDto.CreateCommentDto.CreateCommentRequestDto;
import com.example.jejuairbnb.controller.CommentControllerDto.UpdateCommentRequestDto;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.services.CommentService;
import com.example.jejuairbnb.shared.response.CoreSuccessResponse;
import com.example.jejuairbnb.shared.response.CoreSuccessResponseWithData;
import com.example.jejuairbnb.shared.services.SecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private final SecurityService securityService;

    @PostMapping("")
    public CoreSuccessResponseWithData createComment(
            HttpServletRequest request,
            @RequestBody CreateCommentRequestDto requestDto
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("쿠키가 없습니다.");
        }

        String accessToken = securityService.getTokenByCookie(cookies);
        User findUser = securityService.getSubject(accessToken);

        return commentService.createComment(
                findUser,
                requestDto
        );
    }

    @GetMapping()
    public FindCommentResponseDto getComments (
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
    public FindCommentOneResponseDto findCommentOne (
            @Parameter(description = "코멘트 id", required = true) Long id
    ){
        return commentService.findCommentOneById(id);
    }

    @PutMapping("/{id}")
    public CoreSuccessResponseWithData updateComment (
            @PathVariable Long id,
            @RequestBody UpdateCommentRequestDto requestDto
    ){
        return commentService.updateComment(
                id,
                requestDto
        );
    }

    @DeleteMapping("/{id}")
    public CoreSuccessResponse deleteComment (
            @PathVariable Long id
    ){
        return commentService.deleteComment(id);
    }
}
