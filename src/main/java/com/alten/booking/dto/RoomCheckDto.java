package com.alten.booking.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomCheckDto {

    @NotNull(message = "The reservation start date is required")
    private String startDate;

    @NotNull(message = "The reservation end date is required")
    private String endDate;
}
