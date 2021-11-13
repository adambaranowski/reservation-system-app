package com.ksiezyk.roommanagementsystem.data.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Getter
@AllArgsConstructor
public class Reservation {
    private int id;
    private LocalDate date;
    private LocalTime beginTime;
    private LocalTime endTime;
    private String userNick;
}