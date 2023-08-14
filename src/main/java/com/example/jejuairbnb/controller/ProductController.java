package com.example.jejuairbnb.controller;

import com.example.jejuairbnb.controller.ProductControllerDto.FindProductOneResponseDto;
import com.example.jejuairbnb.services.ProductService;
import com.example.jejuairbnb.shared.response.CoreSuccessResponseWithData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "product", description = "상품 API")
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping()
    public CoreSuccessResponseWithData getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        return productService.findProduct(pageable);
    }

    @GetMapping("/{id}")
    public FindProductOneResponseDto findProductOne(
            @PathVariable(name = "id") Long productId
    ) {
        return productService.findProductOneById(productId);
    }
}
