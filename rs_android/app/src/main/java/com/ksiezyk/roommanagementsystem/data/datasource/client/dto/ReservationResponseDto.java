package com.ksiezyk.roommanagementsystem.data.datasource.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDto {
    private Integer reservationId;
    private String userNick;
    private String date;
    private String beginTime;
    private String endTime;
}
