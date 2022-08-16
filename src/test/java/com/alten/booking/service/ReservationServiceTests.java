package com.alten.booking.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.alten.booking.domain.Room;
import com.alten.booking.domain.RoomDetails;
import com.alten.booking.domain.StatusEnum;
import com.alten.booking.domain.Hotel;
import com.alten.booking.domain.Reservation;
import com.alten.booking.dto.ReservationCreateDto;
import com.alten.booking.dto.ReservationDto;
import com.alten.booking.exceptions.InvalidReservationException;
import com.alten.booking.repository.ReservationRepository;
import com.alten.booking.repository.RoomRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@SpringBootTest
public class ReservationServiceTests {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationService reservationService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomService roomService;

    private static Hotel mockHotel;
    private static Room mockRoom;
    private static RoomDetails mockRoomDetails;

    private static Reservation existingReservation;
    private static ReservationCreateDto newReservationDto;
    private static ReservationCreateDto invalidReservationDto;
    private static ReservationDto existingReservationDto;

    @BeforeAll
    public static void setUp(){
        //Mock a new room
        mockHotel = new Hotel(1L,"Cancun Last Hotel", new ArrayList<>());
        mockRoom = new Room(1L, mockHotel, null);
        mockRoomDetails = new RoomDetails(1L, 3, 30);
        mockRoom.setRoomDetails(mockRoomDetails);
        mockHotel.getRooms().add(mockRoom);

        //Mock an existing reservation
        existingReservation = new Reservation(1L,"guestEmail@gmail.com", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(3), StatusEnum.RESERVED.toString(), mockRoom);
        //Mock a new reservation dto
        newReservationDto = new ReservationCreateDto("guestEmail@gmail.com", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
        LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE), mockRoom.getId());
        //Mock an invalid new reservation dto
        invalidReservationDto =  new ReservationCreateDto("guestEmail@gmail.com", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
        LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE), null);
        //Mock an existing reservation dto
        existingReservationDto = new ReservationDto(existingReservation);
        
    }

    @Test
    @DisplayName("Test creating reservation - Success")
	public void whenValidData_thenReservationShouldBeCreated() {
        given(reservationService.validateAndCreateReservation(newReservationDto)).willReturn(existingReservationDto);
        
        ReservationDto createdReservation =  reservationService.validateAndCreateReservation(newReservationDto);
		
        assertThat(createdReservation).isNotNull();
        assertThat(createdReservation.getReservationId()).isEqualTo(1L);
	}

    @Test
    @DisplayName("Test creating reservation - Invalid data")
	public void whenCreateReservation_AndHasNoRoom_thenInvalidReservationExceptionIsThrown() {
        given(reservationService.validateAndCreateReservation(invalidReservationDto)).willThrow(new InvalidReservationException("The reservation room is required"));

		assertThrows(InvalidReservationException.class, () -> reservationService.validateAndCreateReservation(invalidReservationDto));
	}

    @Test
    @DisplayName("Test finding reservation - Success")
	public void whenValidId_thenReservationShouldBeFound() {
        given(reservationService.findById(1L)).willReturn(existingReservation);

        Reservation searchedReservation = reservationService.findById(1L);
		
		assertThat(searchedReservation.getId()).isEqualTo(existingReservation.getId());
	}
    
}
