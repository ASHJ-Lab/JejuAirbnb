package com.example.jejuairbnb.services;

import com.example.jejuairbnb.controller.UserControllerDto.CreateUserDto.CreateUserRequestDto;
import com.example.jejuairbnb.controller.UserControllerDto.CreateUserDto.CreateUserResponseDto;
import com.example.jejuairbnb.controller.UserControllerDto.FindUserDto.FindUserResponseDto;
import com.example.jejuairbnb.controller.UserControllerDto.MyInfoUserDto.MyInfoUserResponseDto;
import com.example.jejuairbnb.controller.UserControllerDto.UpdateUserDto.UpdateUserRequestDto;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.repository.IUserRepository;
import com.example.jejuairbnb.shared.Enum.ProviderEnum;
import com.example.jejuairbnb.shared.exception.HttpException;
import com.example.jejuairbnb.shared.services.SecurityService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.Map;

import static com.example.jejuairbnb.services.SocialLoginService.DOES_NOT_FOUND_KAKAO_TOKEN;

@Service
@AllArgsConstructor
public class UserService {

    static final String DUPLICATE_EMAIL = "중복된 이메일이 존재합니다.";
    static final String NOT_FOUND_USER = "존재하지 않는 유저입니다.";

    private final IUserRepository userRepository;
    private final SocialLoginService socialLoginService;
    private final SecurityService securityService;


    @Transactional
    public CreateUserResponseDto registerUser(
            CreateUserRequestDto requestDto,
            HttpServletResponse response
    ) {
        try {
            // Kakao Auth Token 으로 카카오 서버에 유저 정보를 요청한다.
            String kakaoToken = requestDto.getKakaoToken();
            // 카카오 서버에서 받아온 유저 정보를 변수에 담는다.
            Map<String, Object> responseKakaoData = socialLoginService.kakaoCallback(kakaoToken);
            // responseKakaoData 를 null 체크한다.
            if (responseKakaoData == null) {
                throw new HttpException(
                        false,
                        DOES_NOT_FOUND_KAKAO_TOKEN,
                        HttpStatus.NOT_FOUND
                );
            }
            // kakaoAuthId 를 통해서 email 과 username 을 가져온다.
            String kakaoAuthId = responseKakaoData.get("id").toString();

            Map<String,Object> kakaoAccount = (Map<String, Object>) responseKakaoData.get("kakao_account");
            MyInfoUserResponseDto myInfoUserResponseDto = socialLoginService.findKakaUserData(kakaoAccount);

            String email = myInfoUserResponseDto.getEmail();
            String username = myInfoUserResponseDto.getUsername();

            User findUserByKakaoAuthId = userRepository.findByKakaoAuthId(kakaoAuthId)
                    .orElse(null);

            if (findUserByKakaoAuthId == null) {
//           회원가입을 시킨다.
                User savedUser = userRepository.save(
                        User
                                .builder()
                                .username(username)
                                .email(email)
                                .kakaoAuthId(kakaoAuthId)
                                .provider(ProviderEnum.FALSE)
                                .build());

                return new CreateUserResponseDto(
                        savedUser.getUsername(),
                        savedUser.getEmail()
                );
            } else {
//            로그인을 시킨다.
                String getToken = securityService.createToken(email);
                Cookie cookie = new Cookie("access-token", String.valueOf(getToken));
                cookie.setMaxAge(60 * 60 * 24);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);

                return CreateUserResponseDto
                        .builder()
                        .email(email)
                        .username(username)
                        .build();
            }
        } catch (Exception e) {
            throw new HttpException(
                    false,
                    "INTERNAL_SERVER_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public MyInfoUserResponseDto getMyInfo(
            User user
    ) {
        return new MyInfoUserResponseDto(
                user.getEmail(),
                user.getUsername()
        );

    }

    @Transactional
    public FindUserResponseDto findUserById(
           Long userId
    ) {
      return userRepository.findById(userId)
              .map(user -> FindUserResponseDto.builder()
                      .userId(user.getId())
                      .email(user.getEmail())
                      .username(user.getUsername())
                      .build())
              .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER));
    }


    @Transactional
    public FindUserResponseDto updateUser(
            UpdateUserRequestDto requestDto
    ) {
        return userRepository.findByEmail(requestDto.getEmail())
                .map(user -> {
                    user.setUsername(requestDto.getUsername());
                    user.setEmail(requestDto.getEmail());
                    userRepository.save(user);
                    return FindUserResponseDto.builder()
                            .userId(user.getId())
                            .email(user.getEmail())
                            .username(user.getUsername())
                            .build();
                })
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER));
    }
}
