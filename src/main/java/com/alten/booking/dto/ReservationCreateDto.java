package com.alten.booking.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCreateDto {
    
    @NotBlank(message = "The guest email is required")
    private String guestEmail;

    @NotBlank(message = "The reservation start date is required")
    private String startDate;

    @NotBlank(message = "The reservation end date is required")
    private String endDate;

    @NotNull(message = "The reservation room is required")
    private Long roomId;

}
