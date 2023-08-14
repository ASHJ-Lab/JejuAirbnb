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

    @PutMapping("/{id}")
    public CoreSuccessResponseWithData updateComment(
            @PathVariable Long id,
            @RequestBody UpdateCommentRequestDto requestDto
    ) {
        return commentService.updateComment(
                id,
                requestDto
        );
    }

    @DeleteMapping("/{id}")
    public CoreSuccessResponse deleteComment(
            @PathVariable Long id
    ) {
        return commentService.deleteComment(id);
    }
}
