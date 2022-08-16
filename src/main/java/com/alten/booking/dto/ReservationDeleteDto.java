package com.alten.booking.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDeleteDto {

    @NotBlank(message = "The guest email is required")
    private String guestEmail;
    
}
