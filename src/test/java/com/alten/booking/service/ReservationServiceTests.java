package com.alten.booking.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import com.alten.booking.domain.Room;
import com.alten.booking.domain.RoomDetails;
import com.alten.booking.domain.Hotel;
import com.alten.booking.domain.Reservation;
import com.alten.booking.dto.ReservationCreateDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

@SpringBootTest
public class ReservationServiceTests {

    @Autowired
    private ReservationService reservationService;

    private static Hotel mockHotel;
    private static Room mockRoom;
    private static RoomDetails mockRoomDetails;

    @BeforeAll
    public static void setUp(){
        mockHotel = new Hotel(1L,"Cancun Last Hotel", new ArrayList<>());
        mockRoom = new Room(1L, mockHotel, null);
        mockRoomDetails = new RoomDetails(1L, 3, 30);
        mockRoom.setRoomDetails(mockRoomDetails);
        mockHotel.getRooms().add(mockRoom);
    }

    private static String generateUniqueEmail(){
        Random random = new Random(); 
        int value = random.nextInt(999999);
        return "guest" + value + "@gmail.com";
    }

    @Test
	public void whenValidData_thenReservationShouldBeCreated() {
        String email = generateUniqueEmail();
        ReservationCreateDto reservation = new ReservationCreateDto(email, 
                                            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
                                            LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE), 
                                            mockRoom.getId());
		Reservation createdReservation = reservationService.createReservation(reservation);
		
        assertThat(createdReservation.getId()).isNotNull();
		assertThat(createdReservation.getGuestEmail()).isEqualTo(email);
	}

    @Test
	public void whenCreateReservation_AndHasNoRoom_thenInvalidDataExceptionIsThrown() {
        ReservationCreateDto reservation = new ReservationCreateDto(generateUniqueEmail(), 
                                            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
                                            LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE),
                                            null);

		assertThrows(InvalidDataAccessApiUsageException.class, () -> reservationService.createReservation(reservation));
	}

    @Test
	public void whenValidId_thenReservationShouldBeFound() {
        ReservationCreateDto reservation = new ReservationCreateDto(generateUniqueEmail(),
                                            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
                                            LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE),
                                            mockRoom.getId());
		Reservation createdReservation = reservationService.createReservation(reservation);
        Reservation searchedReservation = reservationService.findById(createdReservation.getId());
		
		assertThat(searchedReservation.getId()).isEqualTo(createdReservation.getId());
	}
    
}
