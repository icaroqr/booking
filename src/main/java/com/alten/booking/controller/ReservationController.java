package com.alten.booking.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.booking.dto.ReservationCreateDto;
import com.alten.booking.dto.ReservationPageRequestDto;
import com.alten.booking.dto.ReservationPageResponseDto;
import com.alten.booking.dto.ReservationUpdateDto;
import com.alten.booking.dto.ReservationDto;
import com.alten.booking.service.ReservationService;

@RestController
@RequestMapping(value = "reservation")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationDto> getReservation(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.findDtoById(id));
    }

    @GetMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationPageResponseDto> getUserRerservationList(@RequestBody @Valid ReservationPageRequestDto reservationPageRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getUserReservationsPageList(reservationPageRequest));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationDto> createReservation(@RequestBody @Valid ReservationCreateDto reservation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.validateAndCreateReservation(reservation));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable Long id, @RequestBody @Valid ReservationUpdateDto reservation) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.validateAndUpdateReservation(id, reservation));
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> cancelReservation(@PathVariable Long id, @RequestBody @Valid ReservationUpdateDto reservation) {
        reservationService.cancelReservation(id, reservation);
        return ResponseEntity.status(HttpStatus.OK).body("Reservation cancelled");
    }

   
}