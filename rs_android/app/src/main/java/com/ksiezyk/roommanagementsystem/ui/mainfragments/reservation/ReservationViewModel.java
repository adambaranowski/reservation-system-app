package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ksiezyk.roommanagementsystem.R;
import com.ksiezyk.roommanagementsystem.data.Result;
import com.ksiezyk.roommanagementsystem.data.model.Reservation;
import com.ksiezyk.roommanagementsystem.data.model.Room;
import com.ksiezyk.roommanagementsystem.data.repository.ReservationRepository;
import com.ksiezyk.roommanagementsystem.data.repository.RoomRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ReservationViewModel extends ViewModel {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private MutableLiveData<GetRoomsResult> getRoomsResult = new MutableLiveData<>();
    private MutableLiveData<ReservationFormState> reservationFormState = new MutableLiveData<>();
    private MutableLiveData<GetReservationsResult> getReservationsResult = new MutableLiveData<>();

    ReservationViewModel(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    LiveData<GetRoomsResult> getGetRoomsResult() {
        return getRoomsResult;
    }

    LiveData<ReservationFormState> getReservationFormState() {
        return reservationFormState;
    }

    LiveData<GetReservationsResult> getGetReservationsResult() {
        return getReservationsResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void reservationFormChanged(Room room, String beginDateTime, String endDateTime) {
        if (room == null) {
            reservationFormState.setValue(new ReservationFormState(R.string.reservations_invalid_room_number, null, null));
        } else if (!isDateTimeValid(beginDateTime)) {
            reservationFormState.setValue(new ReservationFormState(null, R.string.reservations_invalid_begin_datetime, null));
        } else if (!isDateTimeValid(endDateTime)) {
            reservationFormState.setValue(new ReservationFormState(null, null, R.string.reservations_invalid_end_datetime));
        } else {
            reservationFormState.setValue(new ReservationFormState(true));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateTimeValid(String dateTime) {
        if (dateTime == null) {
            return false;
        } else {
            try {
                LocalDateTime.parse(dateTime, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                return false;
            }
        }
        return true;
    }

    public void getRooms() {
        // can be launched in a separate asynchronous job
        Result<List<Room>> result = roomRepository.getRooms();
        if (result instanceof Result.Success) {
            List<Room> data = ((Result.Success<List<Room>>) result).getData();
            getRoomsResult.setValue(new GetRoomsResult(data));
        } else {
            getRoomsResult.setValue(new GetRoomsResult(R.string.reservations_get_rooms_failed));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getReservations(int roomNumber, String beginDateTimeString, String endDateTimeString) {
        // can be launched in a separate asynchronous job
        Result<List<Reservation>> result = null;
        if (reservationFormState.getValue() != null && reservationFormState.getValue().isDataValid()) {
            LocalDateTime beginDateTime = LocalDateTime.parse(beginDateTimeString, dateTimeFormatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeString, dateTimeFormatter);
            result = reservationRepository.getReservations(roomNumber,
                    beginDateTime, endDateTime);
        }

        if (result instanceof Result.Success) {
            List<Reservation> data = ((Result.Success<List<Reservation>>) result).getData();
            getReservationsResult.setValue(new GetReservationsResult(data));
        } else {
            getReservationsResult.setValue(new GetReservationsResult(R.string.reservations_get_reservations_failed));
        }
    }
}