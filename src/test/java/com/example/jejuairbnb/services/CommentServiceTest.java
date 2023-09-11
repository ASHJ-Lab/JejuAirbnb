package com.example.jejuairbnb.services;

import com.example.jejuairbnb.controller.CommentControllerDto.FindCommentOneResponseDto;
import com.example.jejuairbnb.domain.Comment;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.repository.ICommentRepository;
import com.example.jejuairbnb.shared.Enum.ProviderEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@SpringBootTest
public class CommentServiceTest {

    @MockBean
    private ICommentRepository commentRepository;
    private CommentService commentService;


    @BeforeEach
    public void setup() {
        commentService = new CommentService(commentRepository);
    }

    @Test
    public void testFindOneCommentById() {
        //given
        long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("Test user");
        mockUser.setEmail("Test email");
        mockUser.setProvider(ProviderEnum.FALSE);

        Comment mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setRating(3.5f);
        mockComment.setDescription("Test Descript");
        mockComment.setImg("Test image");
        mockComment.setUser(mockUser);

        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockComment));

        // 실행
        FindCommentOneResponseDto response = commentService.findCommentOneById(userId);

        // 검증
        Assertions.assertEquals(response.getRating(), 3.5f);
        Assertions.assertEquals(response.getDescription(), "Test Descript");
        Assertions.assertEquals(response.getImg(),"Test image");
    }

    @Test
    public void testFindCommentbyDescription() {
        //given
        Long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("Test user");
        mockUser.setEmail("Test email");
        mockUser.setProvider(ProviderEnum.FALSE);

        Comment mockComment1 = new Comment();
        mockComment1.setId(1L);
        mockComment1.setRating(3.51f);
        mockComment1.setDescription("Test Descript1");
        mockComment1.setImg("Test image1");
        mockComment1.setUser(mockUser);

        Comment mockComment2 = new Comment();
        mockComment2.setId(2L);
        mockComment2.setRating(3.52f);
        mockComment2.setDescription("Test Descript2");
        mockComment2.setImg("Test image2");
        mockComment2.setUser(mockUser);

        Comment mockComment3 = new Comment();
        mockComment3.setId(3L);
        mockComment3.setRating(3.53f);
        mockComment3.setDescription("Test Descript3");
        mockComment3.setImg("Test image3");
        mockComment3.setUser(mockUser);

        List<Comment> mockComments = List.of(mockComment1);

        String testDescription = "Descript1";
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(commentRepository.findByDescriptionContaining(testDescription, pageable)).thenReturn((Page<Comment>) mockComments);

        //when
        List<Comment> comments = (commentRepository.findByDescriptionContaining(testDescription, pageable)).getContent();

        //then
        Assertions.assertEquals(1, comments.size());
        Assertions.assertEquals(mockComment1, comments.get(0));
    }

    @Test
    public void testReadCommentbyDescriptionAndPagenation() {
        //given
        long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("Test user");
        mockUser.setEmail("Test email");
        mockUser.setProvider(ProviderEnum.FALSE);

        Comment mockComment1 = new Comment();
        mockComment1.setId(1L);
        mockComment1.setRating(3.51f);
        mockComment1.setDescription("Test Descript1");
        mockComment1.setImg("Test image1");
        mockComment1.setUser(mockUser);

        Comment mockComment2 = new Comment();
        mockComment2.setId(2L);
        mockComment2.setRating(3.52f);
        mockComment2.setDescription("Test Descript2");
        mockComment2.setImg("Test image2");
        mockComment2.setUser(mockUser);

        Comment mockComment3 = new Comment();
        mockComment3.setId(3L);
        mockComment3.setRating(3.52f);
        mockComment3.setDescription("Descript3");
        mockComment3.setImg("Test image3");
        mockComment3.setUser(mockUser);

        Comment mockComment4 = new Comment();
        mockComment4.setId(4L);
        mockComment4.setRating(3.54f);
        mockComment4.setDescription("Descript4");
        mockComment4.setImg("image4");
        mockComment4.setUser(mockUser);

        String testDescription = "Test";
        int page = 1;       //page 가변
        int size = 2;
        Pageable pageable = PageRequest.of(page - 1, size);

        List<Comment> mockComments = Arrays.asList(
                mockComment1,
                mockComment2
        );
        Page<Comment> commentPage = new PageImpl<>(mockComments);
        Mockito.when(commentRepository.findByDescriptionContaining(testDescription, pageable))
                .thenReturn(commentPage);

        //when
        Page<Comment> resultPage = commentRepository.findByDescriptionContaining(testDescription, pageable);

        System.out.println("resultPage.getNumber():"+resultPage.getNumber()+"\n");
        System.out.println("resultPage.getNumberOfElements():"+resultPage.getNumberOfElements()+"\n");
        System.out.println("resultPage.getSize():"+resultPage.getSize()+"\n");

        List<Comment> comments = resultPage.getContent();

        System.out.println("comments:"+comments);

        // Then
        // Total :4, Find : 3, Display : 2
        Assertions.assertEquals(2, comments.size());
        Assertions.assertEquals(mockComment1, comments.get(0));
        Assertions.assertEquals(mockComment2, comments.get(1));
    }
}
