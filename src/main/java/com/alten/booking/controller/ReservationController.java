package com.alten.booking.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.booking.dto.ReservationCreateDto;
import com.alten.booking.dto.ReservationPageRequestDto;
import com.alten.booking.dto.ReservationPageResponseDto;
import com.alten.booking.dto.ReservationDto;
import com.alten.booking.service.ReservationService;

@RestController
@RequestMapping(value = "reservation")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<ReservationDto> getReservation(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.findDtoById(id));
    }

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@RequestBody @Valid ReservationCreateDto reservation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.validateAndCreateReservation(reservation));
    }

    @GetMapping(value = "/list")
    public ResponseEntity<ReservationPageResponseDto> getRerservationList(@RequestBody @Valid ReservationPageRequestDto reservationlistRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getReservationsPageList(reservationlistRequest));
    }
}