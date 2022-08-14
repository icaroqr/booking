package com.alten.booking.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alten.booking.domain.Reservation;
import com.alten.booking.domain.Room;
import com.alten.booking.dto.RoomCheckDto;
import com.alten.booking.exceptions.NotFoundException;
import com.alten.booking.repository.RoomRepository;

@Service
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private ReservationService reservationService;

    public Room findById(Long id) {
        Optional<Room> result = roomRepo.findById(id);
		return result.orElseThrow(() -> new NotFoundException("Room not found for id: " + id));
    }

    public List<String> getRoomAvailableDates(Long id) {
        Room room = findById(id);
        List<Reservation> roomReservations = reservationService.findAllReservedByRoom(id);
        List<String> availableDates = new ArrayList<>();
        for(int i = 0; i < room.getRoomDetails().getMaxReserveAdvanceDays(); i++){
            LocalDate date = LocalDate.now().plusDays(i);
            if(isReservationDateAvailable(roomReservations, date)){
                availableDates.add(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
        }
        return availableDates;
    }

    private boolean isReservationDateAvailable(List<Reservation> roomReservations, LocalDate date) {
        boolean available = true;
        for (Reservation reservation : roomReservations) {
            if(date.isAfter(reservation.getStartDate()) && date.isBefore(reservation.getEndDate()) || 
                (date.isEqual(reservation.getStartDate()) || date.isEqual(reservation.getEndDate()))){
                available = false;
                break;
            }
        }
        return available;
    }

    public Boolean isRoomAvailable(Long id, @Valid RoomCheckDto check) {
        boolean available = true;
        LocalDate startDate = LocalDate.parse(check.getStartDate());
        LocalDate endDate = LocalDate.parse(check.getEndDate());

        List<Reservation> roomReservations = reservationService.findAllReservedByRoom(id);
        for (Reservation reservation : roomReservations) {
            if(startDate.isAfter(reservation.getStartDate()) && startDate.isBefore(reservation.getEndDate()) || 
                endDate.isAfter(reservation.getStartDate()) && endDate.isBefore(reservation.getEndDate()) ||
                (startDate.isEqual(reservation.getStartDate()) || endDate.isEqual(reservation.getEndDate()))){
                available = false;
                break;
            }
        }
        return available;
    }
    
}
