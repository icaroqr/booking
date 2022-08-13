package com.alten.booking.exceptions;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {
	
	private int status;
	private String msg;
	private String field;
	private LocalDate date;
	
}
