package com.example.jejuairbnb.controller.CommentControllerDto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentRequestDto {
    private Float rating;
    private String description;
    private String img;
}
