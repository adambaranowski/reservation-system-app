package com.ksiezyk.roommanagementsystem.data.datasource;

import com.ksiezyk.roommanagementsystem.data.Result;
import com.ksiezyk.roommanagementsystem.data.datasource.client.RestClient;
import com.ksiezyk.roommanagementsystem.data.datasource.client.dto.LoginRequestDto;
import com.ksiezyk.roommanagementsystem.data.datasource.client.dto.LoginResponseDto;
import com.ksiezyk.roommanagementsystem.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final RestClient restClient = RestClient.getInstance();

    public Result<LoggedInUser> login(String username, String password) {

        try {
            LoginRequestDto requestDto = new LoginRequestDto();
            requestDto.setEmail(username);
            requestDto.setPassword(password);
            LoginResponseDto responseDto = restClient.login(requestDto);

            LoggedInUser user =
                    new LoggedInUser(
                            responseDto.getToken(),
                            responseDto.getEmail());
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}