package com.alten.booking.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
import com.alten.booking.exceptions.MaxReserveAdvanceDaysException;
import com.alten.booking.exceptions.MaxReserveDaysException;
import com.alten.booking.repository.ReservationRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

@SpringBootTest
public class ReservationServiceTests {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private RoomService roomService;

    private static Hotel mockHotel;
    private static Room mockRoom;
    private static RoomDetails mockRoomDetails;

    private static Reservation existingReservation;
    private static ReservationCreateDto newReservationDto;
    private static ReservationCreateDto invalidReservationDto;

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
        LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE), mockRoom.getId());
    }

    @Test
    @DisplayName("Test reservation validation - Success")
	public void whenValidData_thenReservationShouldValidatedAndCreated() {
        given(roomService.findById(1L)).willReturn(mockRoom);
        given(reservationService.createReservation(newReservationDto)).willReturn(existingReservation);

        ReservationDto createdReservation = reservationService.validateAndCreateReservation(newReservationDto);

        assertThat(createdReservation).isNotNull();
        assertThat(createdReservation.getReservationId()).isEqualTo(1L);
	}

    @Test
    @DisplayName("Test reservation validation - Invalid Reservation")
	public void whenCreateReservation_AndHasNoRoom_thenInvalidReservationExceptionIsThrown() {
        given(roomService.findById(1L)).willReturn(mockRoom);

        invalidReservationDto.setRoomId(null);

		assertThrows(InvalidReservationException.class, () -> reservationService.validateAndCreateReservation(invalidReservationDto));

        verify(reservationRepository, never()).save(any(Reservation.class));
	}

    @Test
    @DisplayName("Test reservation validation - More than 3 days")
	public void whenCreateReservation_AndHasMoreThan3Days_thenMaxReserveDaysExceptionIsThrown() {
        given(roomService.findById(1L)).willReturn(mockRoom);

        invalidReservationDto.setStartDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        invalidReservationDto.setEndDate(LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE));
        
		assertThrows(MaxReserveDaysException.class, () -> reservationService.validateAndCreateReservation(invalidReservationDto));

        verify(reservationRepository, never()).save(any(Reservation.class));
	}

    @Test
    @DisplayName("Test reservation validation - More than 30 days in advance")
	public void whenCreateReservation_AndIsMoreThan30DaysInAdvance_thenMaxReserveDaysExceptionIsThrown() {
        given(roomService.findById(1L)).willReturn(mockRoom);

        invalidReservationDto.setStartDate(LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE));
        invalidReservationDto.setEndDate(LocalDate.now().plusDays(33).format(DateTimeFormatter.ISO_LOCAL_DATE));

		assertThrows(MaxReserveAdvanceDaysException.class, () -> reservationService.validateAndCreateReservation(invalidReservationDto));

        verify(reservationRepository, never()).save(any(Reservation.class));
	}

    @Test
    @DisplayName("Test finding reservation - Success")
	public void whenValidId_thenReservationShouldBeFound() {
        given(reservationRepository.findById(1L)).willReturn(Optional.of(existingReservation));

        Reservation searchedReservation = reservationService.findById(1L);
		
        assertThat(searchedReservation).isNotNull();
		assertThat(searchedReservation.getId()).isEqualTo(existingReservation.getId());
	}
    
}
