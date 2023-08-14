package com.example.jejuairbnb.services;

import com.example.jejuairbnb.controller.ProductControllerDto.FindProductOneResponseDto;
import com.example.jejuairbnb.controller.ProductControllerDto.FindProductResponseDto;
import com.example.jejuairbnb.controller.ProductControllerDto.ProductDto;
import com.example.jejuairbnb.domain.Comment;
import com.example.jejuairbnb.domain.Product;
import com.example.jejuairbnb.domain.Reservation;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.repository.ICommentRepository;
import com.example.jejuairbnb.repository.IProductRepository;
import com.example.jejuairbnb.repository.IReservationRepository;
import com.example.jejuairbnb.shared.Enum.ProviderEnum;
import com.example.jejuairbnb.shared.exception.HttpException;
import com.example.jejuairbnb.shared.response.CoreSuccessResponseWithData;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {
    private final IProductRepository productRepository;
    private final ICommentRepository commentRepository;
    private final IReservationRepository reservationRepository;

    public FindProductOneResponseDto findProductOneById(
            Long id
    ) {
        Product findProduct = productRepository.findById(id)
                .orElseThrow(() -> new HttpException(
                        false,
                        "존재하지 않는 상품입니다.",
                        HttpStatus.NOT_FOUND
                ));
        Long commentCount = commentRepository.countByProductId(id);
        Double commentAvg = commentRepository.avgByProductId(id);
        Long reservationCount = reservationRepository.countByProductId(id);
        Double commentMax = commentRepository.maxByProductId(id);

        findProduct.setCommentAvg(commentAvg);
        findProduct.setCommentCount(commentCount);
        findProduct.setReservationCount(reservationCount);
        findProduct.setCommentMax(commentMax);

        List<Comment> comments = commentRepository.findByProductId(id);
        List<Comment> commentDtos = toDtoList(comments);

		return FindProductOneResponseDto
                .builder()
                .name(findProduct.getName())
                .description(findProduct.getDescription())
                .price(findProduct.getPrice())
                .img(findProduct.getImg())
                .comments(commentDtos)
                .build();
    }

    public CoreSuccessResponseWithData findProduct(
            Pageable pageable
    ) {
        try {
            Page<Product> productPage = productRepository.findAll(pageable);
            List<Product> products = productPage.getContent();

            FindProductResponseDto findProductResponseDto = new FindProductResponseDto(
                    productDtos(products),
                    productPage.getContent().size(),
                    productPage.getTotalPages()
            );

            return new CoreSuccessResponseWithData(
                    true,
                    "상품 조회에 성공했습니다.",
                    200,
                    findProductResponseDto
            );
        } catch (Exception e) {
            throw new HttpException(
                    false,
                    "에러가 발생했습니다.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public FindProductResponseDto findMyProductByUser(
            User user,
            Pageable pageable
    ) {
        if (user.getProvider() == ProviderEnum.FALSE) {
            throw new HttpException(
                    false,
                    "PROVIDER 가 아닙니다.",
                    HttpStatus.NOT_FOUND
            );
        }

        Page<Product> productPage = productRepository.findByUserId(user.getId(), pageable);
        List<Product> products = productPage.getContent();
        // products 를 불러와서 각각의 product 의 reservation 을 불러온다.
        for (Product product : products) {
            List<Reservation> reservedDates = product.getReservations();
            product.setReservations(reservedDates);
        }

        List<ProductDto> productDtos = productDtos(products);

        return new FindProductResponseDto(
                productDtos,
                products.size(),
                productPage.getTotalPages()
        );
    }

    public List<Comment> toDtoList(List<Comment> comments) {
        return comments.stream()
                .map(comment -> {
                    Comment dto = new Comment();
                    dto.setId(comment.getId());
                    dto.setRating(comment.getRating());
                    dto.setDescription(comment.getDescription());
                    dto.setImg(comment.getImg());
                    dto.setCreatedAt(comment.getCreatedAt());
                    dto.setUpdatedAt(comment.getUpdatedAt());
                    dto.setDeletedAt(comment.getDeletedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ProductDto> productDtos(List<Product> products) {
        return products.stream()
                .map(this::convertToProductDto)
                .collect(Collectors.toList());
    }

//    Product 객체를 입력받아 ProductDto 객체로 변환
    private ProductDto convertToProductDto(Product product) {
        List<Map<String, String>> reservedDates = getReservedDatesForProduct(product);

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPosition(),
                product.getDescription(),
                product.getPrice(),
                product.getImg(),
                reservedDates
        );
    }

    // Product 객체의 모든 예약에 대한 시작일과 종료일을 가져온다.
    private List<Map<String, String>> getReservedDatesForProduct(Product product) {
        return product.getReservations().stream()
                .map(reservation -> getReservedDates(reservation.getCheckIn(), reservation.getCheckOut()))
                .collect(Collectors.toList());
    }

    public Map<String, String> getReservedDates(String checkIn, String checkOut) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(checkIn, formatter);
        LocalDate end = LocalDate.parse(checkOut, formatter);

        // LinkedHashMap 를 사용해서 순서를 보장한다.
        Map<String, String> reservedDates = new LinkedHashMap<>();
        reservedDates.put("start_date", start.toString());
        reservedDates.put("end_date", end.toString());

        return reservedDates;
    }
}
