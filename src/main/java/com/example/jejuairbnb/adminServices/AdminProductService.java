package com.example.jejuairbnb.adminServices;

import com.example.jejuairbnb.adminController.AdminProductDto.CreateProductDto.CreateProductRequestDto;
import com.example.jejuairbnb.adminController.AdminProductDto.UpdateProductDto.UpdateProductRequestDto;
import com.example.jejuairbnb.domain.Product;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.repository.IProductRepository;
import com.example.jejuairbnb.shared.Enum.ProviderEnum;
import com.example.jejuairbnb.shared.exception.HttpException;
import com.example.jejuairbnb.shared.response.CoreSuccessResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AdminProductService {
    private final IProductRepository productRepository;

    @Transactional
    public CoreSuccessResponse createProduct(
            User user,
            CreateProductRequestDto requestDto
    ) {
        if (user.getProvider() == ProviderEnum.FALSE){
            throw new HttpException(
                    false,
                    "PROVIDER 가 아닙니다.",
                    HttpStatus.NOT_FOUND
            );
        }

        if (requestDto.getDescription().length() > 1000) {
			throw new HttpException(
                    false,
                    "description 길이가 너무 깁니다.",
                    HttpStatus.BAD_REQUEST
            );
        }

        Product newProduct = Product
                .builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .img(requestDto.getImg())
                .userId(user.getId())
                .build();

        productRepository.save(newProduct);
        return new CoreSuccessResponse(
                true,
                "상품이 등록되었습니다.",
                201
        );
    }

    @Transactional
    public CoreSuccessResponse updateProduct(
        Long id,
        User user,
        UpdateProductRequestDto updateProductRequestDto
    ) {
        Product findProduct = productRepository.findById(id).orElseThrow(
                () -> new HttpException(
                        false,
                        "해당 상품이 없습니다.",
                        HttpStatus.NOT_FOUND
                )
        );

        if (user.getProvider() == ProviderEnum.FALSE){
            throw new HttpException(
                    false,
                    "PROVIDER 가 아닙니다.",
                    HttpStatus.NOT_FOUND
            );
        }

        findProduct.setName(updateProductRequestDto.getName());
        findProduct.setPrice(updateProductRequestDto.getPrice());
        findProduct.setImg(updateProductRequestDto.getImg());
        findProduct.setUserId(user.getId());
        productRepository.save(findProduct);

        return new CoreSuccessResponse(
                true,
                "상품이 수정되었습니다.",
                200
        );
    }

    @Transactional
    public CoreSuccessResponse deleteProduct(
            Long id,
            User user
    ) {
        Product findProduct = productRepository.findById(id)
                .orElseThrow(
                        () -> new HttpException(
                                false,
                                "해당 상품이 없습니다.",
                                HttpStatus.NOT_FOUND
                        )
                );

        if (user.getProvider() == ProviderEnum.FALSE) {
            throw new HttpException(
                    false,
                    "PROVIDER 가 아닙니다.",
                    HttpStatus.NOT_FOUND
            );
        }

        if (!findProduct.getUserId().equals(user.getId()) || findProduct.getUserId() != user.getId()) {
            throw new HttpException(
                    false,
                    "해당 상품의 주인이 아닙니다.",
                    HttpStatus.NOT_FOUND
            );
        }

        productRepository.delete(findProduct);
        return new CoreSuccessResponse(
                true,
                "상품이 삭제되었습니다.",
                200
        );
    }
}
