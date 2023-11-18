package com.example.jejuairbnb.adminController;

import com.example.jejuairbnb.adminController.AdminProductDto.CreateProductDto.CreateProductRequestDto;
import com.example.jejuairbnb.adminController.AdminProductDto.UpdateProductDto.UpdateProductRequestDto;
import com.example.jejuairbnb.adminServices.AdminProductService;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.shared.response.CoreSuccessResponse;
import com.example.jejuairbnb.shared.services.SecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "admin product", description = "ADMIN 상품 API")
@AllArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {
    private final AdminProductService adminProductService;
    private final SecurityService securityService;

    @PostMapping("")
    public CoreSuccessResponse createProduct(
            @CookieValue("access-token") String accessToken,
            @RequestBody CreateProductRequestDto requestDto
    ) {
        User findUser = securityService.getSubject(accessToken);
        return adminProductService.createProduct(
                findUser,
                requestDto
        );
    }

    @PutMapping("/{id}")
    public CoreSuccessResponse updateProduct(
            @CookieValue("access-token") String accessToken,
            @PathVariable(name = "id") Long id,
            @RequestBody UpdateProductRequestDto requestDto
    ) {
        User findUser = securityService.getSubject(accessToken);
        return adminProductService.updateProduct(
                id,
                findUser,
                requestDto
        );
    }

    @DeleteMapping("/{id}")
    public CoreSuccessResponse deleteProduct(
            @CookieValue("access-token") String accessToken,
            @PathVariable(name = "id")  Long id
    ) {
        User findUser = securityService.getSubject(accessToken);
        return adminProductService.deleteProduct(
                id,
                findUser
        );
    }
}
