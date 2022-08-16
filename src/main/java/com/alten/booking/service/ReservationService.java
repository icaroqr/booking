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
        validateToCreateReservation(dto); 
        return new ReservationDto(createReservation(dto));
    }

    private void validateToCreateReservation(ReservationCreateDto dto) {
        if(dto.getRoomId() != null){
            Room  room = roomService.findById(dto.getRoomId());
            // Check if the reservation dates are valid
            if(hasValidDatesEntries(new ReservationDto(dto))){
                LocalDate startDate = LocalDate.parse(dto.getStartDate());
                LocalDate endDate = LocalDate.parse(dto.getEndDate());
                // Check if dates are valid
                validateDateRules(room, startDate, endDate);
                // Check if room is available
                validateRoomAvailabilityToCreate(dto, startDate, endDate);
            }else{
                throw new InvalidReservationException("Invalid dates informed");
            }
        }else{
            throw new InvalidReservationException("The reservation room is required");
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

    private void validateRoomAvailabilityToCreate(ReservationCreateDto dto, LocalDate startDate, LocalDate endDate) {
        if(reservationRepo.findTotalReservationsByRoomAndDate(dto.getRoomId(), startDate, endDate) > 0){
            throw new InvalidReservationException("This room is already reserved for these dates, please try another dates");
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
        Reservation reservation = findById(reservationId);
        validateToUpdateReservation(reservation, dto); 
        return new ReservationDto(updateReservation(reservation, dto));
    }

    private void validateToUpdateReservation(Reservation reservation, ReservationUpdateDto dto) {
        Room room = reservation.getRoom();
        LocalDate startDate = reservation.getStartDate();
        LocalDate endDate = reservation.getEndDate();
        boolean isChangingRoom = dto.getRoomId() != null && !dto.getRoomId().equals(room.getId());
        // Check if the user updating the reservation is the owner of the reservation
        validateReservationGuestEmail(reservation, dto);
        // Check if it's changing room
        if(isChangingRoom){
            room = roomService.findById(dto.getRoomId());
        }
        // Check if it's changing dates
        if(dto.getStartDate() != null && !LocalDate.parse(dto.getStartDate()).equals(reservation.getStartDate())){
            startDate = LocalDate.parse(dto.getStartDate());
        }
        if(dto.getEndDate() != null && !LocalDate.parse(dto.getEndDate()).equals(reservation.getEndDate())){
            endDate = LocalDate.parse(dto.getEndDate());
        }
        // Check if dates are valid
        validateDateRules(room, startDate, endDate);
        // Check if room is available
        validateRoomAvailabilityToUpdate(room, reservation, startDate, endDate);
        // Check if status is valid
        validateReservationStatus(dto.getStatus());
    }

    private void validateReservationGuestEmail(Reservation reservation, ReservationUpdateDto dto) {
        if(!reservation.getGuestEmail().equals(dto.getGuestEmail())){
            throw new InvalidReservationException("You can't update a reservation that is not yours");
        }
    }

    private void validateRoomAvailabilityToUpdate(Room room, Reservation reservation, LocalDate startDate, LocalDate endDate) {
        if(reservationRepo.findTotalReservationsByRoomAndDateExceptCurrentReservation(room.getId(), startDate, endDate, reservation.getId()) > 0){
            throw new InvalidReservationException("This room is already reserved for these dates, please try another dates");
        }
    }

    private void validateReservationStatus(String status) {
        if(status != null){
            try{
                StatusEnum.valueOf(status);
            }catch(IllegalArgumentException e){
                throw new InvalidReservationException("Trying to update to an invalid reservation status");
            }
        }
    }

    private Reservation updateReservation(Reservation reservation, ReservationUpdateDto dto) {
        if(dto.getStartDate() != null){
            reservation.setStartDate(LocalDate.parse(dto.getStartDate()));
        }
        if(dto.getEndDate() != null){
            reservation.setEndDate(LocalDate.parse(dto.getEndDate()));
        }
        if(dto.getRoomId() != null){
            reservation.setRoom(roomService.findById(dto.getRoomId()));
        }
        if(dto.getStatus() != null){
            reservation.setStatus(dto.getStatus());
        }
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

    public void cancelReservation(Long reservationId, ReservationUpdateDto dto) {
        Reservation reservation = findById(reservationId);
        validateReservationGuestEmail(reservation, dto);
        reservation.setStatus(StatusEnum.CANCELED.toString());
        reservationRepo.save(reservation);
    }


}
