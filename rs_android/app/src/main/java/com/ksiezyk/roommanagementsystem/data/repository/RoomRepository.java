package com.ksiezyk.roommanagementsystem.data.repository;

import com.ksiezyk.roommanagementsystem.data.Result;
import com.ksiezyk.roommanagementsystem.data.datasource.RoomDataSource;
import com.ksiezyk.roommanagementsystem.data.model.Room;

import java.util.List;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class RoomRepository {

    private static volatile RoomRepository instance;
    private RoomDataSource dataSource;

    // private constructor : singleton access
    private RoomRepository(RoomDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static RoomRepository getInstance(RoomDataSource dataSource) {
        if (instance == null) {
            instance = new RoomRepository(dataSource);
        }
        return instance;
    }

    public Result<List<Room>> getRooms() {
        return dataSource.getRooms();
    }
}