package com.alten.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.alten.booking.controller.ReservationController;
import com.alten.booking.repository.ReservationRepository;
import com.alten.booking.service.ReservationService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BookingApplicationTests {

	@Autowired
    private ReservationController reservationController;

    @Autowired
    private ReservationService reservationService;

	@Autowired
    private ReservationRepository reservationRepository;

	@Test
	void contextLoads() {
		assertThat(reservationController).isNotNull();
        assertThat(reservationService).isNotNull();
		assertThat(reservationRepository).isNotNull();
	}

}
