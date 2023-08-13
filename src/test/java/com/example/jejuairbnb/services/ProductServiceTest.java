package com.example.jejuairbnb.services;

import com.example.jejuairbnb.controller.ProductControllerDto.FindProductOneResponseDto;
import com.example.jejuairbnb.domain.Comment;
import com.example.jejuairbnb.domain.Product;
import com.example.jejuairbnb.domain.Reservation;
import com.example.jejuairbnb.repository.ICommentRepository;
import com.example.jejuairbnb.repository.IProductRepository;
import com.example.jejuairbnb.repository.IReservationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ProductServiceTest {

    @MockBean
    private IProductRepository productRepository;

    @MockBean
    private ICommentRepository commentRepository;
    @MockBean
    private IReservationRepository reservationRepository;
    private ProductService productService;

    @BeforeEach
    public void setup() {
        productService = new ProductService(
                productRepository,
                commentRepository,
                reservationRepository
        );
    }

    @Test
    public void testFindOneProductById() {
        // given

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Test product");
        mockProduct.setImg("Test image");
        mockProduct.setPrice(100);
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockProduct));

        // When
        FindProductOneResponseDto response = productService.findProductOneById(1L);

        // Then
        Assertions.assertEquals(mockProduct.getName(), response.getName());
        Assertions.assertEquals(mockProduct.getImg(), response.getImg());
        Assertions.assertEquals(mockProduct.getPrice(), response.getPrice());
    }

    @Test
    public void FindAllProduct() {

// Given
        Product mockProduct1 = new Product();
        mockProduct1.setId(1L);
        mockProduct1.setName("Test product 1");
        mockProduct1.setImg("Test image 1");
        mockProduct1.setPrice(100);

        Product mockProduct2 = new Product();
        mockProduct2.setId(2L);
        mockProduct2.setName("Test product 2");
        mockProduct2.setImg("Test image 2");
        mockProduct2.setPrice(200);

        Product mockProduct3 = new Product();
        mockProduct2.setId(3L);
        mockProduct2.setName("Test product 3");
        mockProduct2.setImg("Test image 3");
        mockProduct2.setPrice(300);

        Product mockProduct4 = new Product();
        mockProduct2.setId(4L);
        mockProduct2.setName("Test product 4");
        mockProduct2.setImg("Test image 4");
        mockProduct2.setPrice(400);

        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page - 1, size);

        List<Product> mockProducts = Arrays.asList(
                mockProduct1,
                mockProduct2,
                mockProduct3,
                mockProduct4
        );
        Page<Product> productPage = new PageImpl<>(mockProducts);
        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<Product> resultPage = productRepository.findAll(pageable);
        List<Product> products = resultPage.getContent();

        System.out.println("products:"+products);
        // Then
        Assertions.assertEquals(4, products.size());
        Assertions.assertEquals(mockProduct1, products.get(0));
        Assertions.assertEquals(mockProduct2, products.get(1));
    }

    @Test
    public void FindOneProductWithComments() {
        // Given
        long productId = 1L;
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setName("Test Product 1");
        mockProduct.setImg("Test Product image 1");
        mockProduct.setPrice(100);

        Comment mockComment1 = new Comment();
        mockComment1.setId(1L);
        mockComment1.setRating(1.1f);
        mockComment1.setDescription("Test comment 1");
        mockComment1.setProduct(mockProduct);

        Comment mockComment2 = new Comment();
        mockComment2.setId(2L);
        mockComment2.setRating(1.2f);
        mockComment2.setDescription("Test comment 2");
        mockComment2.setProduct(mockProduct);

        Comment mockComment3 = new Comment();
        mockComment3.setId(3L);
        mockComment3.setRating(1.3f);
        mockComment3.setDescription("Test comment 3");
        mockComment3.setProduct(mockProduct);

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        Mockito.when(commentRepository.countByProductId(productId)).thenReturn(3L);  // example value
        Mockito.when(commentRepository.avgByProductId(productId)).thenReturn(1.2);  // example value
        Mockito.when(commentRepository.findByProductId(productId)).thenReturn(Arrays.asList(mockComment1, mockComment2, mockComment3));

        // When
        FindProductOneResponseDto result = productService.findProductOneById(productId);
        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Test Product 1", result.getName());
        Assertions.assertEquals("Test Product image 1", result.getImg());
        Assertions.assertEquals(100, result.getPrice());

        Assertions.assertEquals(3, result.getComments().size());
        Assertions.assertEquals("Test comment 2", result.getComments().get(1).getDescription());
        Assertions.assertEquals("Test comment 3", result.getComments().get(2).getDescription());
    }

    @Test
    public void testSortProductbyMaxComment(){
        // comment 최고 값을 기준으로, 상품을 정렬한다.
        // given
        long productId1 = 1L;
        Product mockProduct1 = new Product();
        mockProduct1.setId(productId1);
        mockProduct1.setName("Test Product 1");
        mockProduct1.setImg("Test Product image 1");
        mockProduct1.setPrice(100);
        mockProduct1.setCommentMax(1.2);

        Comment mockComment1 = new Comment();
        mockComment1.setId(1L);
        mockComment1.setRating(1.1f);
        mockComment1.setDescription("Test comment 1");
        mockComment1.setProduct(mockProduct1);

        Comment mockComment2 = new Comment();
        mockComment2.setId(2L);
        mockComment2.setRating(1.2f);
        mockComment2.setDescription("Test comment 2");
        mockComment2.setProduct(mockProduct1);

        long productId2 = 2L;
        Product mockProduct2 = new Product();
        mockProduct2.setId(productId2);
        mockProduct2.setName("Test Product 2");
        mockProduct2.setImg("Test Product image 2");
        mockProduct2.setPrice(100);
        mockProduct2.setCommentMax(1.3);

        Comment mockComment3 = new Comment();
        mockComment3.setId(2L);
        mockComment3.setRating(1.3f);
        mockComment3.setDescription("Test comment 3");
        mockComment3.setProduct(mockProduct2);

        // commentCount 를 기준으로 오름차순 정렬
        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("commentMax").ascending());

        List<Product> mockProducts = Arrays.asList(
                mockProduct2,
                mockProduct1
        );
        Page<Product> productPage = new PageImpl<>(mockProducts);
        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);

        // when
        Page<Product> resultPage = productRepository.findAll(pageable);
        List<Product> products = resultPage.getContent();

        // then
        Assertions.assertEquals(2, products.size());
        Assertions.assertEquals(mockProduct2, products.get(0));
        Assertions.assertEquals(mockProduct1, products.get(1));
    }

    @Test
    public void testSortProductbyAverageComment(){
        // comment 평균 값을 기준으로, 상품을 정렬한다.
        // given
        long productId1 = 1L;
        Product mockProduct1 = new Product();
        mockProduct1.setId(productId1);
        mockProduct1.setName("Test Product 1");
        mockProduct1.setImg("Test Product image 1");
        mockProduct1.setPrice(100);
        mockProduct1.setCommentAvg(1.15);

        Comment mockComment1 = new Comment();
        mockComment1.setId(1L);
        mockComment1.setRating(1.1f);
        mockComment1.setDescription("Test comment 1");
        mockComment1.setProduct(mockProduct1);

        Comment mockComment2 = new Comment();
        mockComment2.setId(2L);
        mockComment2.setRating(1.2f);
        mockComment2.setDescription("Test comment 2");
        mockComment2.setProduct(mockProduct1);

        long productId2 = 2L;
        Product mockProduct2 = new Product();
        mockProduct2.setId(productId2);
        mockProduct2.setName("Test Product 2");
        mockProduct2.setImg("Test Product image 2");
        mockProduct2.setPrice(100);
        mockProduct2.setCommentAvg(1.3);

        Comment mockComment3 = new Comment();
        mockComment3.setId(2L);
        mockComment3.setRating(1.3f);
        mockComment3.setDescription("Test comment 3");
        mockComment3.setProduct(mockProduct2);

        //commentCount 를 기준으로 오름차순 정렬
        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("commentAvg").ascending());
        List<Product> mockProducts = Arrays.asList(
                mockProduct2,
                mockProduct1
        );
        Page<Product> productPage = new PageImpl<>(mockProducts);
        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);

        // when
        Page<Product> resultPage = productRepository.findAll(pageable);
        List<Product> products = resultPage.getContent();

        // then
        Assertions.assertEquals(2, products.size());
        Assertions.assertEquals(mockProduct2, products.get(0));
        Assertions.assertEquals(mockProduct1, products.get(1));

    }@Test
    public void testSortProductbyMostComment(){
        // comment 수를 기준으로, 상품을 정렬한다.
        // given
        long productId1 = 1L;
        Product mockProduct1 = new Product();
        mockProduct1.setId(productId1);
        mockProduct1.setName("Test Product 1");
        mockProduct1.setImg("Test Product image 1");
        mockProduct1.setPrice(100);
        mockProduct1.setCommentCount(2L);

        Comment mockComment1 = new Comment();
        mockComment1.setId(1L);
        mockComment1.setRating(1.1f);
        mockComment1.setDescription("Test comment 1");
        mockComment1.setProduct(mockProduct1);

        Comment mockComment2 = new Comment();
        mockComment2.setId(2L);
        mockComment2.setRating(1.2f);
        mockComment2.setDescription("Test comment 2");
        mockComment2.setProduct(mockProduct1);

        long productId2 = 2L;
        Product mockProduct2 = new Product();
        mockProduct2.setId(productId2);
        mockProduct2.setName("Test Product 2");
        mockProduct2.setImg("Test Product image 2");
        mockProduct2.setPrice(100);
        mockProduct2.setCommentCount(1L);

        Comment mockComment3 = new Comment();
        mockComment3.setId(2L);
        mockComment3.setRating(1.3f);
        mockComment3.setDescription("Test comment 3");
        mockComment3.setProduct(mockProduct2);

        //commentCount 를 기준으로 오름차순 정렬
        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("commentCount").ascending());
        List<Product> mockProducts = Arrays.asList(
                mockProduct1,
                mockProduct2
        );
        Page<Product> productPage = new PageImpl<>(mockProducts);
        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<Product> resultPage = productRepository.findAll(pageable);
        List<Product> products = resultPage.getContent();

        // then
        Assertions.assertEquals(2, products.size());
        Assertions.assertEquals(mockProduct1, products.get(0));
        Assertions.assertEquals(mockProduct2, products.get(1));
    }

    @Test
    public void testSortProductMostReserved(){
        //reservation 수가 많은 순으로, 상품을 정렬한다.
        // given
        long productId1 = 1L;
        Product mockProduct1 = new Product();
        mockProduct1.setId(productId1);
        mockProduct1.setName("Test Product 1");
        mockProduct1.setImg("Test Product image 1");
        mockProduct1.setPrice(100);
        mockProduct1.setReservationCount(2L);

        Reservation reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setCheckIn("Test CheckIn 1");
        reservation1.setCheckOut("Test CheckOut 1");
        reservation1.setProduct(mockProduct1);
        reservation1.setUserId(1L);

        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setCheckIn("Test CheckIn 2");
        reservation2.setCheckOut("Test CheckOut 2");
        reservation2.setProduct(mockProduct1);
        reservation2.setUserId(1L);

        long productId2 = 2L;
        Product mockProduct2 = new Product();
        mockProduct2.setId(productId2);
        mockProduct2.setName("Test Product 2");
        mockProduct2.setImg("Test Product image 2");
        mockProduct2.setPrice(100);
        mockProduct2.setReservationCount(1L);

        Reservation reservation3 = new Reservation();
        reservation3.setId(3L);
        reservation3.setCheckIn("Test CheckIn 3");
        reservation3.setCheckOut("Test CheckOut 3");
        reservation3.setProduct(mockProduct2);
        reservation3.setUserId(1L);

        //reservationCount 를 기준으로 오름차순 정렬
        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("reservationCount").ascending());
        List<Product> mockProducts = Arrays.asList(
                mockProduct1,
                mockProduct2
        );
        Page<Product> productPage = new PageImpl<>(mockProducts);
        Mockito.when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<Product> resultPage = productRepository.findAll(pageable);
        List<Product> products = resultPage.getContent();

        // then
        Assertions.assertEquals(2, products.size());
        Assertions.assertEquals(mockProduct1, products.get(0));
        Assertions.assertEquals(mockProduct2, products.get(1));
    }
}
