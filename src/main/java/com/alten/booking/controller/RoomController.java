package com.alten.booking.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alten.booking.dto.RoomCheckDto;
import com.alten.booking.service.ReservationService;

@RestController
@RequestMapping(value = "room")
public class RoomController {
    
    @Autowired
    private ReservationService reservationService;

    @GetMapping(value = "/{id}/availableDates")
    public ResponseEntity<List<String>> getRoomAvailableDates(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getRoomAvailableDates(id));
    }

    @GetMapping(value = "/{id}/available")
    public ResponseEntity<Boolean> isRoomAvailable(@PathVariable Long id, @RequestBody @Valid RoomCheckDto check) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.isRoomAvailable(id, check));
    }
   
}