package com.ksiezyk.roommanagementsystem.data.datasource.client.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPostRequestDto {

    private Integer roomNumber;
    private String beginDate;
    private String endDate;
    private String beginTime;
    private String endTime;
    private List<Integer> daysOfWeek = new ArrayList<>();

}
