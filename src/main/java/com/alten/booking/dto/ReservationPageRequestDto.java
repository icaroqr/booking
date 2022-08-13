package com.alten.booking.dto;

import java.time.LocalDate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPageRequestDto{
	
	private int page;
	private int size;
	private String guestEmail;
	private LocalDate startDate;
	private LocalDate endDate;

	public ReservationPageRequestDto(int page, int size) {
		this.page = page > 0 ? page : 0;
		this.size = size > 0 ? size : 5;
	}

	public PageRequest toPageRequest() {
		return PageRequest.of(page, size, Sort.by("createDate").descending());
	}
}
