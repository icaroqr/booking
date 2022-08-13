package com.alten.booking.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPageResponseDto {
	
	private Long totalReservations;
	private int pageSize;
	private int totalPages;
	private List<ReservationDto> reservations;
}
