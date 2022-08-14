package com.alten.booking.exceptions;

public class MaxReserveDaysException extends InvalidReservationException{

	public MaxReserveDaysException(String msg) {
		super(msg);
	}

}
