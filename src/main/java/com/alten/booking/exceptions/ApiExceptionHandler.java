package com.alten.booking.exceptions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null, LocalDate.now());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(InvalidReservationException.class)
	public ResponseEntity<ApiError> handleInvalidReservationException(InvalidReservationException ex) {
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), "", LocalDate.now());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MaxReserveDaysException.class)
	public ResponseEntity<ApiError> handleMaxReserveDaysException(MaxReserveDaysException ex) {
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), "maxReserveDays", LocalDate.now());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MaxReserveAdvanceDaysException.class)
	public ResponseEntity<ApiError> handleMaxReserveAdvanceDaysException(MaxReserveAdvanceDaysException ex) {
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), "maxReserveAdvanceDays", LocalDate.now());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<ApiError> errors = new ArrayList<>();
		ex.getBindingResult().getAllErrors().forEach(error -> 
			errors.add(new ApiError(HttpStatus.BAD_REQUEST.value(), error.getDefaultMessage(), ((FieldError) error).getField(), LocalDate.now()))
		);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}


}
