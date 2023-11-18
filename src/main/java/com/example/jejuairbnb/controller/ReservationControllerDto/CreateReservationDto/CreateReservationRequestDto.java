package com.example.jejuairbnb.controller.ReservationControllerDto.CreateReservationDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationRequestDto {
    private String checkIn;
    private String checkOut;
    private Long productId;
}
