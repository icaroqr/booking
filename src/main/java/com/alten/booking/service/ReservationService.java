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
import com.alten.booking.domain.StatusEnum;
import com.alten.booking.dto.ReservationCreateDto;
import com.alten.booking.dto.ReservationDto;
import com.alten.booking.dto.ReservationPageRequestDto;
import com.alten.booking.dto.ReservationPageResponseDto;
import com.alten.booking.exceptions.NotFoundException;
import com.alten.booking.exceptions.InvalidReservationException;
import com.alten.booking.repository.ReservationRepository;
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
        if(isValidReservation(dto) && isRoomAvailable(dto)){
            return new ReservationDto(createReservation(dto));
        } else {
            throw new InvalidReservationException("Reservation not valid");
        }
    }

    private boolean isValidReservation(ReservationCreateDto dto) {
        return true;
    }

    private boolean isRoomAvailable(ReservationCreateDto dto) {
        return true;
    }

    public Reservation createReservation(ReservationCreateDto dto) {
        Reservation reservation = toNewReservation(dto);
        return reservationRepo.save(reservation);
    }
   
    private Reservation toNewReservation(ReservationCreateDto dto) {
        Reservation reservation = new Reservation();
        reservation.setGuestEmail(dto.getGuestEmail());
        reservation.setStartDate(LocalDate.parse(dto.getStartDate()));
        reservation.setEndDate(LocalDate.parse(dto.getEndDate()));
        reservation.setRoom(roomService.findById(dto.getRoomId()));
        reservation.setCreateDate(LocalDate.now());
        reservation.setStatus(StatusEnum.RESERVED.toString());
        return reservation;
    }

    public ReservationPageResponseDto getReservationsPageList(ReservationPageRequestDto dto) {
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

}
