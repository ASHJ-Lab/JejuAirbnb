package com.example.jejuairbnb.controller.ReservationControllerDto.CreateReservationDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder    //added
@AllArgsConstructor
public class CreateReservationResponseDto {
    private Long productId;
    private Long userId;
    private String checkIn;
    private String checkOut;
    private int price;
}
