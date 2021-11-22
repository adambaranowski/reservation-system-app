package com.ksiezyk.roommanagementsystem.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Getter
@AllArgsConstructor
public class Room {
    private int id;

    @Override
    public String toString() {
        return "Room " + id;
    }
}