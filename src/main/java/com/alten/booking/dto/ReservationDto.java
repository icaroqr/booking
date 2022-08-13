package com.alten.booking.dto;

import java.time.format.DateTimeFormatter;

import com.alten.booking.domain.Reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    
   
    private Long reservationId;
    private Long roomId;
    private String guestEmail;
    private String startDate;
    private String endDate;
    private String status;


    public ReservationDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.guestEmail = reservation.getGuestEmail();
        this.startDate = reservation.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.endDate = reservation.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.status = reservation.getStatus();
        if(reservation.getRoom() != null) {
            this.roomId = reservation.getRoom().getId();
        }
    }

}
