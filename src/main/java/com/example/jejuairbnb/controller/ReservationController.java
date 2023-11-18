package com.example.jejuairbnb.controller;

import com.example.jejuairbnb.controller.ReservationControllerDto.CreateReservationDto.CreateReservationRequestDto;
import com.example.jejuairbnb.controller.ReservationControllerDto.CreateReservationDto.CreateReservationResponseDto;
import com.example.jejuairbnb.domain.User;
import com.example.jejuairbnb.services.ReservationService;
import com.example.jejuairbnb.shared.response.CoreSuccessResponse;
import com.example.jejuairbnb.shared.services.SecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "reservation", description = "예약 API")
@AllArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final SecurityService securityService;

    @PostMapping()
    public CreateReservationResponseDto createReservation(
            @CookieValue("access-token") String accessToken,
        @RequestBody CreateReservationRequestDto requestDto
    ) {
        User foundUser = securityService.getSubject(accessToken);
        return reservationService.createReservation(
                foundUser,
                requestDto
        );
    }

    @DeleteMapping("/{id}")
    public CoreSuccessResponse deleteReservation(
            @CookieValue("access-token") String accessToken,
            @PathVariable(name = "id")  Long id
    ) {
        User foundUser = securityService.getSubject(accessToken);
        return reservationService.deleteReservation(
                foundUser,
                id
        );
    }
}
