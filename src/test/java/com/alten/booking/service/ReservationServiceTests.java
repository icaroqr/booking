package com.alten.booking.service;

import org.junit.jupiter.api.BeforeAll;
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
import java.util.Optional;

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

    private static Reservation newReservation;
    private static Reservation invalidReservation;
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

        //Mock a new reservation
        newReservation = new Reservation(null,"guestEmail@gmail.com", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(3), StatusEnum.RESERVED.toString(), mockRoom);
        //Mock an existing reservation
        existingReservation = new Reservation(1L,"guestEmail@gmail.com", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(3), StatusEnum.RESERVED.toString(), mockRoom);
        //Mock a new reservation dto
        newReservationDto = new ReservationCreateDto("guestEmail@gmail.com", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
        LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE), mockRoom.getId());
        //Mock an invalid new reservation
        invalidReservation =  new Reservation(0L,"guestEmail@gmail.com", LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(3), StatusEnum.RESERVED.toString(), null);
        //Mock an invalid new reservation dto
        invalidReservationDto =  new ReservationCreateDto("guestEmail@gmail.com", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 
        LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE), null);
        //Mock an existing reservation dto
        existingReservationDto = new ReservationDto(existingReservation);
        
    }

    @Test
	public void whenValidData_thenReservationShouldBeCreated() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(mockRoom));
		given(reservationRepository.save(newReservation)).willReturn(existingReservation);
        given(reservationService.validateAndCreateReservation(newReservationDto)).willReturn(existingReservationDto);
        
        ReservationDto createdReservation =  reservationService.validateAndCreateReservation(newReservationDto);
		
        assertThat(createdReservation).isNotNull();
        assertThat(createdReservation.getReservationId()).isEqualTo(1L);
	}

    @Test
	public void whenCreateReservation_AndHasNoRoom_thenInvalidDataExceptionIsThrown() {
        given(reservationRepository.save(invalidReservation)).willThrow(new InvalidReservationException("The reservation room is required"));

		assertThrows(InvalidReservationException.class, () -> reservationService.validateAndCreateReservation(invalidReservationDto));
	}

    @Test
	public void whenValidId_thenReservationShouldBeFound() {
        given(reservationRepository.findById(1L)).willReturn(Optional.of(existingReservation));

        Reservation searchedReservation = reservationService.findById(1L);
		
		assertThat(searchedReservation.getId()).isEqualTo(existingReservation.getId());
	}
    
}
