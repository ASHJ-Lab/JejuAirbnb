package com.example.jejuairbnb.services;

import com.example.jejuairbnb.controller.ProviderControllerDto.CreateProviderDto.CreateProviderRequestDto;
import com.example.jejuairbnb.controller.ProviderControllerDto.CreateProviderDto.CreateProviderResponseDto;
import com.example.jejuairbnb.controller.ProviderControllerDto.FindProviderDto.FindProviderResponseDto;
import com.example.jejuairbnb.controller.ProviderControllerDto.LoginProviderDto.LoginProviderRequestDto;
import com.example.jejuairbnb.controller.ProviderControllerDto.LoginProviderDto.LoginProviderResponseDto;
import com.example.jejuairbnb.controller.ProviderControllerDto.UpdateProviderDto.UpdateProviderRequestDto;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.repository.IUserRepository;
import com.example.jejuairbnb.shared.exception.HttpException;
import com.example.jejuairbnb.shared.services.SecurityService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@AllArgsConstructor
public class ProviderService {

    static final String DUPLICATE_EMAIL = "중복된 이메일이 존재합니다.";
    static final String INVALID_PASSWORD = "비밀번호가 일치하지 않습니다.";

    static final String NOT_FOUND_PROVIDER = "존재하지 않는 제공자입니다.";

    private final IUserRepository userRepository;
    private final SecurityService securityService;

    @Transactional
    public CreateProviderResponseDto registerProvider(
            CreateProviderRequestDto requestDto
    ) throws NoSuchAlgorithmException {

        System.out.println("회원가입 요청: " + requestDto);
        userRepository
                .findByEmail(requestDto.getEmail())
                .orElseThrow(
                        () -> new IllegalArgumentException(DUPLICATE_EMAIL)
                );

        String password = requestDto.getPassword();

        if (!password.equals(requestDto.getRePassword())) {
            throw new IllegalArgumentException(INVALID_PASSWORD);
        }

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        String hasingPassword = Base64.getEncoder().encodeToString(hash);

        User findUser = User.builder()
                .username(requestDto.getProvidername())
                .email(requestDto.getEmail())
                .build();

        User savedProvider = userRepository.save(findUser);

        return CreateProviderResponseDto.builder()
                .providername(savedProvider.getUsername())
                .email(savedProvider.getEmail())
                .build();
    }

    @Transactional
    public FindProviderResponseDto findProviderById(
            Long providerId
    ) {
        User findUser = userRepository.findById(providerId)
                .orElseThrow(
                        () -> new IllegalArgumentException(NOT_FOUND_PROVIDER)
                );
        return FindProviderResponseDto.builder()
                .providerId(findUser.getId())
                .email(findUser.getEmail())
                .username(findUser.getUsername())
                .build();
    }

    @Transactional
    public FindProviderResponseDto updateProvider(
            UpdateProviderRequestDto requestDto
    ) {
        User findUser = userRepository
                .findByEmail(requestDto.getEmail())
                .orElseThrow(
                        () -> new IllegalArgumentException(NOT_FOUND_PROVIDER)
                );

        findUser.setUsername(requestDto.getProvidername());
        findUser.setEmail(requestDto.getEmail());

        User savedProvider = userRepository.save(findUser);
        return FindProviderResponseDto.builder()
                .providerId(savedProvider.getId())
                .email(savedProvider.getEmail())
                .username(savedProvider.getUsername())
                .build();
    }

    @Transactional
    public LoginProviderResponseDto loginProvider(
            LoginProviderRequestDto requestDto,
            HttpServletResponse response
    ){
        try {
            //SHA-256 으로 PW 검증
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(requestDto.getPassword().getBytes(StandardCharsets.UTF_8));

            User findUser = userRepository.findByEmail(requestDto.getEmail())
                    .orElseThrow(() -> new HttpException("가입되어있지 않은 유저 입니다.", HttpStatus.BAD_REQUEST));

            String getToken = securityService.createToken(findUser.getEmail());

            LoginProviderResponseDto responseDto = new LoginProviderResponseDto();
            responseDto.setEmail(findUser.getEmail());
            responseDto.setToken(getToken);

            Cookie cookie = new Cookie("access-token", String.valueOf(getToken));
            cookie.setMaxAge(60 * 60 * 24);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return responseDto;

        } catch (Exception e) {
            throw new RuntimeException("bad request");
        }
    }
}
