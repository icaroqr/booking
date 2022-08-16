package com.alten.booking.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationUpdateDto {

    @NotBlank(message = "The guest email is required")
    private String guestEmail;
    
    private Long roomId;
    
    private String startDate;

    private String endDate;

    private String status;

}
