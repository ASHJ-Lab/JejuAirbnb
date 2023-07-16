package com.example.jejuairbnb.services;

import com.example.jejuairbnb.controller.ProviderControllerDto.CreateProviderDto.CreateProviderRequestDto;
import com.example.jejuairbnb.controller.ProviderControllerDto.FindProviderDto.FindProviderResponseDto;
import com.example.jejuairbnb.controller.UserControllerDto.CreateUserDto.CreateUserRequestDto;
import com.example.jejuairbnb.domain.Provider;
import com.example.jejuairbnb.repository.IProviderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.jejuairbnb.services.UserService.DUPLICATE_EMAIL;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class ProviderLoginTest {

    @MockBean // 스프링 부트 테스트에서 사용하는 목(mock) 객체를 생성하는 어노테이션입니다
    private IProviderRepository providerRepository;

    private ProviderService providerService;

    @Test
    public void testEmailForm() throws NoSuchAlgorithmException{

        // given
        CreateProviderRequestDto requestDto = CreateProviderRequestDto.builder()
                .providername("test")
                .password("test")
                .rePassword("test")
                .email("test@gmail.com")
                .build();

        // when
        boolean chekEmailFormResult = chekEmailForm(requestDto);

        //정규 표현식 확인
        // then
        Assertions.assertTrue(chekEmailFormResult);
    }

    //EmailFormTest -> 어디에 구현할까?
    private boolean chekEmailForm(CreateProviderRequestDto requestDto){

        String email = requestDto.getEmail();

        String regx = "^(.+)@(.+)$";

        //Compile regular expression to get the pattern
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    @Test
    public void testResignedProvider() throws NoSuchAlgorithmException{

        // Test Scenario
        // 1. 회원 탈퇴한 객체 생성
        // 2. 회원 탈퇴 이메일로 신규 가입 요청
        // 3. unregistedID 인지 확인 -> true 라면 exception 발생 시킬 것

        // given
        // 1. 회원 탈퇴한 객체 생성
        CreateProviderRequestDto requestDto = CreateProviderRequestDto.builder()
                .providername("test")
                .password("test")
                .rePassword("test")
                .email("test@gmail.com")
                .build();

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(requestDto.getPassword().getBytes(StandardCharsets.UTF_8));

        String hashingPassword = Base64.getEncoder().encodeToString(hash);

        Provider existingProvider = Provider.builder()
                .providername(requestDto.getProvidername())
                .password(hashingPassword)
                .email(requestDto.getEmail())
                //.email("test2@gmail.com")
                .build();

        // TODO : 회원 탈퇴 과정에서 진행 -> Service로 만들기
        existingProvider.setUnregistedID(1L);

        Mockito.when(providerRepository.findByEmail(requestDto.getEmail())).thenReturn(existingProvider);

        // 2. 회원 탈퇴 이메일로 신규 가입 요청
        // register 과정 에서 findProvider 찾기   //sqve되면 안되므로 우선 찾기만 -> register에 추후 등록
        // when
        Provider findProvider = providerRepository.findByEmail(requestDto.getEmail());

        // 3. unregistedID 인지 확인
        // then
        Long unregistedID = findProvider.getUnregistedID();
        Assertions.assertEquals(1L, unregistedID);
    }


    @Test
    public void testLogin() throws NoSuchAlgorithmException{

    }

    @Test
    public void testJwtTokenProvider() throws NoSuchAlgorithmException{
    }
}