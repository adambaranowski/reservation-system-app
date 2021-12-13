package com.ksiezyk.roommanagementsystem.data.datasource.client.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationGetRequestDto {
    private Integer roomNumber;
    private String beginDate;
    private String endDate;
}
