package com.alten.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationUpdateDto {
    
    private Long roomId;
    
    private String startDate;

    private String endDate;

    private String status;

}
