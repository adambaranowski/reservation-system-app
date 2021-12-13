package com.ksiezyk.roommanagementsystem.data.datasource;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ksiezyk.roommanagementsystem.data.Result;
import com.ksiezyk.roommanagementsystem.data.datasource.client.RestClient;
import com.ksiezyk.roommanagementsystem.data.model.Room;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class RoomDataSource {
    private final RestClient client = RestClient.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Result<List<Room>> getRooms() {
        try {
            return new Result.Success<>(client.getAllRooms());
        } catch (Exception e) {
            return new Result.Error(new IOException("Error getting rooms", e));
        }
    }

}