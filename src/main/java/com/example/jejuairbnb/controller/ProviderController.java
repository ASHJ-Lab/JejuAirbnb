package com.example.jejuairbnb.controller;

import com.example.jejuairbnb.controller.ProductControllerDto.FindProductResponseDto;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.services.ProductService;
import com.example.jejuairbnb.shared.services.SecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@Tag(name = "providers", description = "providers API")
@AllArgsConstructor
@RequestMapping("/api/providers")
public class ProviderController {
    private final ProductService productService;
    private final SecurityService securityService;

    @GetMapping("/my_provider_info")
    public FindProductResponseDto getMyInfo(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("쿠키가 없습니다.");
        }

        String accessToken = securityService.getTokenByCookie(cookies);
        User findUser = securityService.getSubject(accessToken);

        Pageable pageable = PageRequest.of(page, size);
        return productService.findMyProductByUser(
                findUser,
				pageable
        );
    }
}
