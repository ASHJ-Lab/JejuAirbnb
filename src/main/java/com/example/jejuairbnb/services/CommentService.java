package com.example.jejuairbnb.services;

import com.example.jejuairbnb.adminController.AdminCommentDto.CreateCommentDto.CreateCommentRequestDto;
import com.example.jejuairbnb.controller.CommentControllerDto.FindCommentOneResponseDto;
import com.example.jejuairbnb.controller.CommentControllerDto.FindCommentResponseDto;
import com.example.jejuairbnb.domain.Comment;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.repository.ICommentRepository;
import com.example.jejuairbnb.shared.exception.HttpException;
import com.example.jejuairbnb.shared.response.CoreSuccessResponseWithData;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    private final ICommentRepository commentRepository;

    public FindCommentOneResponseDto findCommentOneById(
            Long id
    ) {
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(() -> new HttpException(
                        false,
                        "존재하지 않는 댓글입니다.",
                        HttpStatus.NOT_FOUND
                ));
        return FindCommentOneResponseDto
                .builder()
                .rating(findComment.getRating())
                .description(findComment.getDescription())
                .img(findComment.getImg())
                .build();
    }

    public FindCommentResponseDto findComment(
            Pageable pageable
    ) {
        try {
            Page<Comment> commentPage = commentRepository.findAll(pageable);

            return FindCommentResponseDto
                    .builder()
                    .comments(commentPage.getContent())
                    .size(commentPage.getSize())
                    .totalPages(commentPage.getTotalPages())
                    .build();
        } catch (Exception e) {
            throw new HttpException(
                    false,
                    "에러가 발생했습니다.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public FindCommentResponseDto findMyCommentByUser(
            User user,
            Pageable pageable
    ) {
        Page<Comment> commentPage = commentRepository.findByUserId(user.getId(), pageable);
        List<Comment> comments = commentPage.getContent();

        return new FindCommentResponseDto(
                comments,
                comments.size(),
                commentPage.getTotalPages()
        );
    }
}
