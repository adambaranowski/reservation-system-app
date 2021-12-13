package com.ksiezyk.roommanagementsystem.data.datasource;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ksiezyk.roommanagementsystem.data.Result;
import com.ksiezyk.roommanagementsystem.data.datasource.client.RestClient;
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

    private final RestClient restClient = RestClient.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Result<List<Reservation>> getReservations(int roomNumber, LocalDateTime beginDt,
                                                     LocalDateTime endDt) {

        try {
            List<Reservation> reservations = restClient.getReservations(roomNumber, beginDt, endDt);
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
            return new Result.Success(restClient.addReservation(roomNumber, beginDt, endDt));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error creating reservation", e));
        }
    }
}