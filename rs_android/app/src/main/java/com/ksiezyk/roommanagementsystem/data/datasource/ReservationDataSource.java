package com.ksiezyk.roommanagementsystem.data.datasource;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ksiezyk.roommanagementsystem.data.Result;
import com.ksiezyk.roommanagementsystem.data.model.Reservation;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
                    .mapToObj(this::dummyReservation)
                    .collect(Collectors.toList());
            return new Result.Success<>(reservations);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Reservation dummyReservation(int i) {
        LocalTime beginTime = LocalTime.of(13 + (i / 2), 30 * (i % 2));
        return new Reservation(
                i,
                LocalDate.now(),
                beginTime,
                beginTime.plusMinutes(30),
                "User" + i);
    }
}