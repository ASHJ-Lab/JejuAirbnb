package com.example.jejuairbnb.controller;

import com.example.jejuairbnb.controller.UserControllerDto.CreateUserDto.CreateUserRequestDto;
import com.example.jejuairbnb.controller.UserControllerDto.CreateUserDto.CreateUserResponseDto;
import com.example.jejuairbnb.controller.UserControllerDto.MyInfoUserDto.MyInfoUserResponseDto;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.services.UserService;
import com.example.jejuairbnb.shared.services.SecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "user", description = "유저 API")
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final SecurityService securityService;

    @PostMapping("/kakao_login")
    public CreateUserResponseDto registerUser(
            @RequestBody CreateUserRequestDto requestDto,
            HttpServletResponse httpServletResponse
    ) {
        return userService.registerUser(
                requestDto,
                httpServletResponse
        );
    }

    @GetMapping("/my_info")
    public MyInfoUserResponseDto getMyInfo(
            HttpServletRequest request
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("쿠키가 없습니다.");
        }

        String accessToken = securityService.getTokenByCookie(cookies);

        User findUser = securityService.getSubject(accessToken);
        return userService.getMyInfo(
                findUser
        );
    }
}
