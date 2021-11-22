package com.ksiezyk.roommanagementsystem.data.datasource;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ksiezyk.roommanagementsystem.data.Result;
import com.ksiezyk.roommanagementsystem.data.model.Reservation;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class ReservationDataSource {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Result<List<Reservation>> getReservations(int roomNumber, LocalDateTime beginDt,
                                                     LocalDateTime endDt) {
        int amount = (int) Duration.between(beginDt, endDt).toHours() * 2;
        try {
            List<Reservation> reservations = IntStream.range(0, amount)
                    .mapToObj(i -> dummyReservation(beginDt, i))
                    .collect(Collectors.toList());
            return new Result.Success<>(reservations);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error getting reservations", e));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Reservation dummyReservation(LocalDateTime beginDateTime, int i) {
        LocalDateTime startDateTime = beginDateTime.plusHours(i / 2).plusMinutes(30 * (i % 2));
        return new Reservation(
                i,
                startDateTime.toLocalDate(),
                startDateTime.toLocalTime(),
                startDateTime.toLocalTime().plusMinutes(30),
                "User " + i);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Result<Reservation> createReservation(int roomNumber, LocalDateTime beginDt,
                                                 LocalDateTime endDt, String userName) {
        try {
            return new Result.Success<>(new Reservation(
                    0,
                    beginDt.toLocalDate(),
                    beginDt.toLocalTime(),
                    endDt.toLocalTime(),
                    userName
            ));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error creating reservation", e));
        }
    }
}