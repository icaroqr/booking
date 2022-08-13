package com.alten.booking.dto;

import java.time.LocalDate;
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

    public ReservationDto(Long reservationId, Long roomId, String guestEmail, LocalDate startDate, LocalDate endDate, String status) {
        this.reservationId = reservationId;
        this.guestEmail = guestEmail;
        this.startDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.endDate = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.status = status;
        this.roomId = roomId;
    }

}
