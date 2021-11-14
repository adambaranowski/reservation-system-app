package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import androidx.annotation.Nullable;

import com.ksiezyk.roommanagementsystem.data.model.Reservation;

import java.util.List;

/**
 * Authentication result : success (user details) or error message.
 */
class GetReservationsResult {
    @Nullable
    private List<Reservation> success;
    @Nullable
    private Integer error;

    GetReservationsResult(@Nullable Integer error) {
        this.error = error;
    }

    GetReservationsResult(@Nullable List<Reservation> success) {
        this.success = success;
    }

    @Nullable
    List<Reservation> getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}