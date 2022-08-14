package com.alten.booking.exceptions;

public class MaxReserveAdvanceDaysException extends InvalidReservationException{

	public MaxReserveAdvanceDaysException(String msg) {
		super(msg);
	}

}
