package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import androidx.annotation.Nullable;

import com.ksiezyk.roommanagementsystem.data.model.Reservation;

/**
 * Authentication result : success (user details) or error message.
 */
class CreateReservationResult {
    @Nullable
    private Reservation success;
    @Nullable
    private Integer error;

    CreateReservationResult(@Nullable Integer error) {
        this.error = error;
    }

    CreateReservationResult(@Nullable Reservation success) {
        this.success = success;
    }

    @Nullable
    Reservation getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}