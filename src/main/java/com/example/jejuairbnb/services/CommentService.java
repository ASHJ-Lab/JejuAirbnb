package com.example.jejuairbnb.services;

import com.example.jejuairbnb.adminController.AdminCommentDto.CreateCommentDto.CreateCommentRequestDto;
import com.example.jejuairbnb.controller.CommentControllerDto.FindCommentOneResponseDto;
import com.example.jejuairbnb.controller.CommentControllerDto.FindCommentResponseDto;
import com.example.jejuairbnb.controller.CommentControllerDto.UpdateCommentRequestDto;
import com.example.jejuairbnb.domain.Comment;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.repository.ICommentRepository;
import com.example.jejuairbnb.repository.IProductRepository;
import com.example.jejuairbnb.shared.exception.HttpException;
import com.example.jejuairbnb.shared.response.CoreSuccessResponse;
import com.example.jejuairbnb.shared.response.CoreSuccessResponseWithData;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    private final ICommentRepository commentRepository;
    private final IProductRepository productRepository;

    public CoreSuccessResponseWithData createComment(
            User user,
            CreateCommentRequestDto requestDto
    ) {
        try {
            Comment comment = Comment
                    .builder()
                    .rating(requestDto.getRating())
                    .description(requestDto.getDescription())
                    .img(requestDto.getImg())
                    .user(user)
                    .build();

            commentRepository.save(comment);

            return new CoreSuccessResponseWithData(
                    true,
                    "댓글이 성공적으로 등록되었습니다.",
                    201,
                    comment
            );
        } catch (Exception e) {
            throw new HttpException(
                    false,
                    "에러가 발생했습니다.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public CoreSuccessResponseWithData updateComment(
            Long id,
            UpdateCommentRequestDto requestDto
    ) {
        try {
            Comment foundComment = commentRepository.findById(id)
                    .orElseThrow(() -> new HttpException(
                            false,
                            "존재하지 않는 댓글입니다.",
                            HttpStatus.NOT_FOUND
                    ));
            foundComment.setRating(requestDto.getRating());
            foundComment.setDescription(requestDto.getDescription());
            foundComment.setImg(requestDto.getImg());
            foundComment.setUpdatedAt(LocalDateTime.now());

            commentRepository.save(foundComment);

            return new CoreSuccessResponseWithData(
                    true,
                    "댓글이 성공적으로 수정되었습니다.",
                    200,
                    foundComment
            );
        } catch (Exception e) {
            throw new HttpException(
                    false,
                    "에러가 발생했습니다.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

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
            Long productId,
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

    public CoreSuccessResponse deleteComment(
            Long id
    ) {
        try {
            Comment findComment = commentRepository.findById(id)
                    .orElseThrow(() -> new HttpException(
                            false,
                            "존재하지 않는 댓글입니다.",
                            HttpStatus.NOT_FOUND
                    ));

            findComment.setDeletedAt(LocalDateTime.now());
            commentRepository.save(findComment);

            return new CoreSuccessResponse(
                    true,
                    "댓글이 성공적으로 삭제되었습니다.",
                    200
            );
        } catch (Exception e) {
            throw new HttpException(
                    false,
                    "에러가 발생했습니다.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
