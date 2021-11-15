package com.ksiezyk.roommanagementsystem.data.datasource;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ksiezyk.roommanagementsystem.data.Result;
import com.ksiezyk.roommanagementsystem.data.model.Room;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class RoomDataSource {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Result<List<Room>> getRooms() {
        int amount = 5;
        try {
            List<Room> reservations = IntStream.range(0, amount)
                    .mapToObj(this::dummyRooms)
                    .collect(Collectors.toList());
            return new Result.Success<>(reservations);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error getting rooms", e));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Room dummyRooms(int i) {
        return new Room(i);
    }
}