package com.alten.booking.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alten.booking.domain.Reservation;
import com.alten.booking.domain.Room;
import com.alten.booking.domain.StatusEnum;
import com.alten.booking.dto.ReservationCreateDto;
import com.alten.booking.dto.ReservationDto;
import com.alten.booking.dto.ReservationPageRequestDto;
import com.alten.booking.dto.ReservationPageResponseDto;
import com.alten.booking.dto.ReservationUpdateDto;
import com.alten.booking.dto.RoomCheckDto;
import com.alten.booking.exceptions.NotFoundException;
import com.alten.booking.repository.ReservationRepository;
import com.alten.booking.exceptions.InvalidReservationException;
import com.alten.booking.exceptions.MaxReserveAdvanceDaysException;
import com.alten.booking.exceptions.MaxReserveDaysException;
import com.alten.booking.specifications.ReservationSpecifications;

@Service
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepo;

    @Autowired
    private RoomService roomService;

    public Reservation findById(Long id) {
        Optional<Reservation> result = reservationRepo.findById(id);
		return result.orElseThrow(() -> new NotFoundException("Reservation not found for id: " + id));
    }

    public List<Reservation> findAllReservationsByRoom(Long roomId) {
        return reservationRepo.findAllByRoomIdAndStatus(roomId, StatusEnum.RESERVED.toString());
    }

    public ReservationDto findDtoById(Long id) {
		return new ReservationDto(findById(id));
    }

    public ReservationDto validateAndCreateReservation(ReservationCreateDto dto){
        validateReservation(new ReservationDto(dto)); 
        return new ReservationDto(createReservation(dto));
    }

    private void validateReservation(ReservationDto dto) {
        Room room = null;
        // Check if it's validating an existing reservation without changing room
        if(dto.getRoomId() == null && dto.getReservationId() != null){
            room = findById(dto.getReservationId()).getRoom();
        }else
        if(dto.getRoomId() != null){
            room = roomService.findById(dto.getRoomId());
        }else{
            throw new InvalidReservationException("The reservation room is required");
        }
        // Check if the reservation dates are valid
        if(hasValidDatesEntries(dto) && room != null){
            LocalDate startDate = LocalDate.parse(dto.getStartDate());
            LocalDate endDate = LocalDate.parse(dto.getEndDate());
            // Check if dates are valid
            validateDateRules(room, startDate, endDate);
            // Check if room is available
            validateRoomAvailability(dto, startDate, endDate);
        }
    }

    private void validateDateRules(Room room, LocalDate startDate, LocalDate endDate) {
        if(startDate.isAfter(endDate) || startDate.isBefore(LocalDate.now())){
            throw new InvalidReservationException("Start date must be after today and before end date");
        }
        if(startDate.isEqual(endDate)){
            throw new InvalidReservationException("Start date must be different than end date");
        }
        if(Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays() > room.getRoomDetails().getMaxReserveDays()){
            throw new MaxReserveDaysException("Your reservation can't be longer than " + room.getRoomDetails().getMaxReserveDays() + " days");
        }
        if(startDate.isAfter(LocalDate.now().plusDays(room.getRoomDetails().getMaxReserveAdvanceDays())) ||
            endDate.isAfter(LocalDate.now().plusDays(room.getRoomDetails().getMaxReserveAdvanceDays()))){
            throw new MaxReserveAdvanceDaysException("Your reservation can't be longer than "+ room.getRoomDetails().getMaxReserveAdvanceDays() +" days in advance");
        }
    }

    private void validateRoomAvailability(ReservationDto dto, LocalDate startDate, LocalDate endDate) {
        if(dto.getReservationId() != null){
            if(reservationRepo.findTotalReservationsByRoomAndDateExceptCurrentReservation(dto.getRoomId(), startDate, endDate, dto.getReservationId()) > 0){
                throw new InvalidReservationException("This room is already reserved for these dates, please try another dates");
            }
            if(dto.getStatus() != null){
                try{
                    StatusEnum.valueOf(dto.getStatus());
                }catch(IllegalArgumentException e){
                    throw new InvalidReservationException("Reservation status not valid");
                }
            }
        }else{
            if(reservationRepo.findTotalReservationsByRoomAndDate(dto.getRoomId(), startDate, endDate) > 0){
                throw new InvalidReservationException("This room is already reserved for these dates, please try another dates");
            }
        }
    }

    private boolean hasValidDatesEntries(ReservationDto dto) {
        return dto.getStartDate() != null && 
               dto.getEndDate() != null && 
               !dto.getStartDate().trim().isEmpty() && 
               !dto.getEndDate().trim().isEmpty();
    }

    public Reservation createReservation(ReservationCreateDto dto) {
        Reservation reservation = new Reservation();
        reservation.setGuestEmail(dto.getGuestEmail());
        reservation.setStartDate(LocalDate.parse(dto.getStartDate()));
        reservation.setEndDate(LocalDate.parse(dto.getEndDate()));
        reservation.setRoom(roomService.findById(dto.getRoomId()));
        reservation.setCreateDate(LocalDate.now());
        reservation.setStatus(StatusEnum.RESERVED.toString());
        return reservationRepo.save(reservation);
    }

    public ReservationPageResponseDto getUserReservationsPageList(ReservationPageRequestDto dto) {
        Specification<Reservation> filters = Specification.where(
            ReservationSpecifications.equalToGuestEmail(dto.getGuestEmail()))
            .and(ReservationSpecifications.startDateBtw(dto.getStartDate(), dto.getEndDate())
            .and(ReservationSpecifications.equalToRoom(dto.getRoomId()))
        );
        Page<Reservation> page = reservationRepo.findAll(filters,dto.toPageRequest());
        return new ReservationPageResponseDto(page.getTotalElements(), page.getSize(), page.getTotalPages(), toDtoList(page.getContent()));
    }

    private List<ReservationDto> toDtoList(List<Reservation> reservations) {
        List<ReservationDto> dtos = new ArrayList<>();
        for (Reservation reservation : reservations) {
            ReservationDto dto = new ReservationDto(reservation);
            dtos.add(dto);
        }
        return dtos;
    }

    public ReservationDto validateAndUpdateReservation(Long reservationId, ReservationUpdateDto dto) {
        validateReservation(new ReservationDto(reservationId, dto)); 
        return new ReservationDto(updateReservation(reservationId, dto));
    }

    private Reservation updateReservation(Long reservationId, ReservationUpdateDto dto) {
        Reservation reservation = findById(reservationId);
        if(dto.getStartDate() != null){
            reservation.setStartDate(LocalDate.parse(dto.getStartDate()));
        }
        if(dto.getEndDate() != null){
            reservation.setEndDate(LocalDate.parse(dto.getEndDate()));
        }
        if(dto.getRoomId() != null){
            reservation.setRoom(roomService.findById(dto.getRoomId()));
        }
        reservation.setStatus(dto.getStatus());
        return reservationRepo.save(reservation);
    }

    public List<String> getRoomAvailableDates(Long id) {
        Room room = roomService.findById(id);
        List<Reservation> roomReservations = findAllReservationsByRoom(id);
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

    public Boolean isRoomAvailable(Long id, RoomCheckDto check) {
        boolean available = true;
        LocalDate startDate = LocalDate.parse(check.getStartDate());
        LocalDate endDate = LocalDate.parse(check.getEndDate());

        List<Reservation> roomReservations = findAllReservationsByRoom(id);
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

    public void deleteReservation(Long id) {
        reservationRepo.deleteById(id);
    }


}
