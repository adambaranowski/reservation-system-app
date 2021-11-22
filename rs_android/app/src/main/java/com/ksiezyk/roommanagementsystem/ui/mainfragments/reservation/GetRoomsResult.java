package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import androidx.annotation.Nullable;

import com.ksiezyk.roommanagementsystem.data.model.Room;

import java.util.List;

/**
 * Authentication result : success (user details) or error message.
 */
class GetRoomsResult {
    @Nullable
    private List<Room> success;
    @Nullable
    private Integer error;

    GetRoomsResult(@Nullable Integer error) {
        this.error = error;
    }

    GetRoomsResult(@Nullable List<Room> success) {
        this.success = success;
    }

    @Nullable
    List<Room> getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}