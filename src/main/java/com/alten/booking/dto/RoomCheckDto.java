package com.alten.booking.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomCheckDto {

    @NotBlank(message = "The reservation start date is required")
    private String startDate;

    @NotBlank(message = "The reservation end date is required")
    private String endDate;
}
