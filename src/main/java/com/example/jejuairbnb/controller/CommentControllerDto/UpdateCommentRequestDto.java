package com.example.jejuairbnb.controller.CommentControllerDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class UpdateCommentRequestDto {
    private Float rating;
    private String description;
    private String img;
}
