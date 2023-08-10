package com.example.jejuairbnb.adminController;


import com.example.jejuairbnb.adminController.AdminCommentDto.CreateCommentDto.CreateCommentRequestDto;
import com.example.jejuairbnb.adminController.AdminCommentDto.UpdateCommentDto.UpdateCommentRequestDto;
import com.example.jejuairbnb.adminServices.AdminCommentService;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.shared.response.CoreSuccessResponse;
import com.example.jejuairbnb.shared.services.SecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "admin comment", description = "ADMIN 코멘트 API")
@AllArgsConstructor
@RequestMapping("/api/admin/comments")
public class AdminCommentController {
    private final AdminCommentService adminCommentService;
    private final SecurityService securityService;

    @PostMapping("")
    public CoreSuccessResponse createComment(
            @RequestBody CreateCommentRequestDto requestDto,
            @CookieValue("access-token") String accessToken
    ) {
        User findUser = securityService.getSubject(accessToken);
        return adminCommentService.createComment(
                findUser,
                requestDto
        );
    }
    @PutMapping("/{id}")
    public CoreSuccessResponse updateComment(
            @CookieValue("access-token") String accessToken,
            @PathVariable Long id,
            @RequestBody UpdateCommentRequestDto requestDto
    ) {
        User findUser = securityService.getSubject(accessToken);
        return adminCommentService.updateComment(
                id,
                findUser,
                requestDto
        );
    }

    @DeleteMapping("/{id}")
    public CoreSuccessResponse deleteComment(
            @CookieValue("access-token") String accessToken,
            @PathVariable Long id
    ) {
        User findUser = securityService.getSubject(accessToken);
        return adminCommentService.deleteComment(
                id,
                findUser
        );
    }
}

