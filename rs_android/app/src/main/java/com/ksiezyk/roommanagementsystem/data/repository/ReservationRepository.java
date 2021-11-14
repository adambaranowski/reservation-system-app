package com.ksiezyk.roommanagementsystem.data.repository;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ksiezyk.roommanagementsystem.data.Result;
import com.ksiezyk.roommanagementsystem.data.datasource.ReservationDataSource;
import com.ksiezyk.roommanagementsystem.data.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class ReservationRepository {

    private static volatile ReservationRepository instance;
    private ReservationDataSource dataSource;

    // private constructor : singleton access
    private ReservationRepository(ReservationDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static ReservationRepository getInstance(ReservationDataSource dataSource) {
        if (instance == null) {
            instance = new ReservationRepository(dataSource);
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Result<List<Reservation>> getReservations(int roomNumber, LocalDateTime beginDateTime,
                                                     LocalDateTime endDateTime) {
        return dataSource.getReservations(roomNumber, beginDateTime, endDateTime);
    }
}