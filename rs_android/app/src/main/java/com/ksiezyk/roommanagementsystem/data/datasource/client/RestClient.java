package com.ksiezyk.roommanagementsystem.data.datasource.client;

import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksiezyk.roommanagementsystem.data.datasource.LoginDataSource;
import com.ksiezyk.roommanagementsystem.data.datasource.client.dto.LoginRequestDto;
import com.ksiezyk.roommanagementsystem.data.datasource.client.dto.LoginResponseDto;
import com.ksiezyk.roommanagementsystem.data.datasource.client.dto.ReservationGetRequestDto;
import com.ksiezyk.roommanagementsystem.data.datasource.client.dto.ReservationPostRequestDto;
import com.ksiezyk.roommanagementsystem.data.datasource.client.dto.ReservationResponseDto;
import com.ksiezyk.roommanagementsystem.data.model.Reservation;
import com.ksiezyk.roommanagementsystem.data.model.Room;
import com.ksiezyk.roommanagementsystem.data.repository.LoginRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RestClient {
    private static final String AUTH_SERVER_URL = "http://10.0.2.2:8081";
    private static final String BACKEND_URL = "http://10.0.2.2:8082";

    private static final String LOGIN_URL = "/login";
    private static final String ROOMS_API_URL = "/rooms";
    private static final String RESERVATIONS_URL = "/reservations";

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private static volatile RestClient instance = null;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final LoginRepository loginRepository = LoginRepository.getInstance();

    private RestClient() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static RestClient getInstance(){
        if (instance == null){
            instance = new RestClient();
        }
        return instance;
    }

    private String post(String url, String json, String token) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);

        Request.Builder builder = new Request.Builder();
        builder.url(url)
                .post(body)
                .build();

        if (token != null) {
            builder.addHeader("Authentication", "Bearer: " + token);
        }

        Request request = builder.build();

        Response response = client.newCall(request).execute();

        return response.body().string();

    }

    private String get(String url, String token) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(url)
                .get()
                .build();

        if (token != null) {
            builder.addHeader("Authentication", "Bearer: " + token);
        }

        Request request = builder.build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws IOException {

        String url = AUTH_SERVER_URL + LOGIN_URL;

        LoginResponseDto loginResponseDto = null;

            String s = mapper.writeValueAsString(loginRequestDto);

            loginResponseDto = mapper.readValue(post(url, s, null), LoginResponseDto.class);


        return loginResponseDto;
    }

    public List<Room> getAllRooms() throws IOException {
        String url = BACKEND_URL + ROOMS_API_URL + "/numbers";

        List<Room> rooms = new ArrayList<>();

            String responseJson = get(url, loginRepository.getUser().getToken());
            Integer[] roomsNumbers = mapper.readValue(responseJson, Integer[].class);

            for (int number: roomsNumbers
                 ) {
                rooms.add(new Room(number));
            }


        return rooms;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Reservation addReservation(int roomNumber, LocalDateTime beginDt,
                                      LocalDateTime endDt) throws IOException {
        ReservationPostRequestDto requestDto = new ReservationPostRequestDto();

        //Create start date in proper format
        String beginDay = beginDt.getDayOfMonth() < 10 ?
                "0" + beginDt.getDayOfMonth() :
                String.valueOf(beginDt.getDayOfMonth());

        String beginMonth = beginDt.getMonthValue() < 10 ?
                "0" + beginDt.getMonthValue() :
                String.valueOf(beginDt.getMonthValue());


        String beginDate = beginDay + "-" + beginMonth + "-" + beginDt.getYear();


        //Create end date in proper format
        String endDay = endDt.getDayOfMonth() < 10 ?
                "0" + endDt.getDayOfMonth() :
                String.valueOf(endDt.getDayOfMonth());

        String endMonth = endDt.getMonthValue() < 10 ?
                "0" + endDt.getMonthValue() :
                String.valueOf(endDt.getMonthValue());

        String endDate = endDay + "-" + endMonth + "-" + endDt.getYear();

        String beginHour = beginDt.getHour() < 10 ?
                "0" + beginDt.getHour() :
                String.valueOf(beginDt.getHour());

        String beginMinute = beginDt.getMinute() < 10 ?
                "0" + beginDt.getMinute() :
                String.valueOf(beginDt.getMinute());

        String beginTime = beginHour + ":" + beginMinute;

        String endHour = endDt.getHour() < 10 ?
                "0" + endDt.getHour() :
                String.valueOf(endDt.getHour());

        String endMinute = endDt.getMinute() < 10 ?
                "0" + endDt.getMinute() :
                String.valueOf(endDt.getMinute());

        String endTime = endHour + ":" + endMinute;


        requestDto.setBeginDate(beginDate);
        requestDto.setEndDate(endDate);
        requestDto.setBeginTime(beginTime);
        requestDto.setEndTime(endTime);
        requestDto.setRoomNumber(roomNumber);

        String requestJson = mapper.writeValueAsString(requestDto);

        post(BACKEND_URL+RESERVATIONS_URL, requestJson, loginRepository.getUser().getToken());

        return new Reservation(1,
                LocalDate.of(beginDt.getYear(), beginDt.getMonth(), beginDt.getDayOfMonth()),
                LocalTime.of(beginDt.getHour(), beginDt.getMinute()),
                LocalTime.of(endDt.getHour(), endDt.getMinute()),
                loginRepository.getUser().getDisplayName());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Reservation> getReservations(int roomNumber, LocalDateTime beginDt,
                                      LocalDateTime endDt) throws IOException {
        List<Reservation> result = new ArrayList<>();

        ReservationGetRequestDto requestDto = new ReservationGetRequestDto();

        String beginDay = beginDt.getDayOfMonth() < 10 ?
                "0" + beginDt.getDayOfMonth() :
                String.valueOf(beginDt.getDayOfMonth());

        String beginMonth = beginDt.getMonthValue() < 10 ?
                "0" + beginDt.getMonthValue() :
                String.valueOf(beginDt.getMonthValue());


        String beginDate = beginDay + "-" + beginMonth + "-" + beginDt.getYear();

        String endDay = endDt.getDayOfMonth() < 10 ?
                "0" + endDt.getDayOfMonth() :
                String.valueOf(endDt.getDayOfMonth());

        String endMonth = endDt.getMonthValue() < 10 ?
                "0" + endDt.getMonthValue() :
                String.valueOf(endDt.getMonthValue());

        String endDate = endDay + "-" + endMonth + "-" + endDt.getYear();

        requestDto.setBeginDate(beginDate);
        requestDto.setEndDate(endDate);
        requestDto.setRoomNumber(roomNumber);

        String requestJson = mapper.writeValueAsString(requestDto);

        String url = BACKEND_URL + RESERVATIONS_URL + "/forPeriod/getAll";

        String response = post(url, requestJson, loginRepository.getUser().getToken());

        ReservationResponseDto[] reservationResponseDtos = mapper.readValue(response, ReservationResponseDto[].class);

        for (ReservationResponseDto dto: reservationResponseDtos
             ) {

            LocalDate date = LocalDate.parse(dto.getDate());
            LocalTime beginTime = LocalTime.parse(dto.getBeginTime());
            LocalTime endTime = LocalTime.parse(dto.getEndTime());
            result.add(new Reservation(1, date, beginTime, endTime, dto.getUserNick()));
        }

        return result;

    }
}
