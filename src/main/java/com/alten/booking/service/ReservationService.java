package com.alten.booking.service;

import java.time.LocalDate;
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

    public ReservationDto findDtoById(Long id) {
		return new ReservationDto(findById(id));
    }

    public List<ReservationDto> findAll(){
        return toDtoList(reservationRepo.findAll());
    }

    public ReservationDto validateAndCreateReservation(ReservationCreateDto dto){
        try{
            validateReservation(new ReservationDto(dto)); 
            return new ReservationDto(createReservation(dto));
        }catch(InvalidReservationException e){
            e.printStackTrace();
            throw e;
        }
    }

    private void validateReservation(ReservationDto dto) throws InvalidReservationException{
        Room room = null;
        // Check if it's validating an existing reservation
        if(dto.getReservationId() != null){
            Reservation reservation = findById(dto.getReservationId());
            room = reservation.getRoom();
        }else{
            room = roomService.findById(dto.getRoomId());
        }
        if(hasValidDates(dto) && room != null){
            LocalDate startDate = LocalDate.parse(dto.getStartDate());
            LocalDate endDate = LocalDate.parse(dto.getEndDate());
            
            if(startDate.isAfter(endDate) && startDate.isBefore(LocalDate.now())){
                throw new InvalidReservationException("Start date must be after today and before end date");
            }
            if(startDate.isEqual(endDate)){
                throw new InvalidReservationException("Start date must be different than end date");
            }
            if(startDate.plusDays(room.getRoomDetails().getMaxReserveDays()).isAfter(endDate)){
                throw new MaxReserveDaysException("Your reservation can't be longer than " + room.getRoomDetails().getMaxReserveDays() + " days");
            }
            if(LocalDate.now().plusDays(room.getRoomDetails().getMaxReserveAdvanceDays()).isAfter(startDate) ||
            LocalDate.now().plusDays(room.getRoomDetails().getMaxReserveAdvanceDays()).isAfter(endDate)){
                throw new MaxReserveAdvanceDaysException("Your reservation can't start or end more than " + room.getRoomDetails().getMaxReserveAdvanceDays() + " days from today");
            }
            if(dto.getReservationId() != null){
                if(reservationRepo.findTotalReservationsByRoomAndDateExceptCurrentReservation(dto.getRoomId(), startDate, endDate, dto.getReservationId()) > 0){
                    throw new InvalidReservationException("This room is already reserved for these dates");
                }
            }else{
                if(reservationRepo.findTotalReservationsByRoomAndDate(dto.getRoomId(), startDate, endDate) > 0){
                    throw new InvalidReservationException("This room is already reserved for these dates");
                }
            }
        }
    }

    private boolean hasValidDates(ReservationDto dto) {
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
            .and(ReservationSpecifications.btwStartDateAndEndDate(dto.getStartDate(), dto.getEndDate())
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
        try{
            validateReservation(new ReservationDto(reservationId, dto)); 
            return new ReservationDto(updateReservation(reservationId, dto));
        }catch(InvalidReservationException e){
            e.printStackTrace();
            throw e;
        }
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


}
