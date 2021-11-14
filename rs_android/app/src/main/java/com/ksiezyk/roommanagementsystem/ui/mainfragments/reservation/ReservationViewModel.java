package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ksiezyk.roommanagementsystem.R;
import com.ksiezyk.roommanagementsystem.data.Result;
import com.ksiezyk.roommanagementsystem.data.model.Reservation;
import com.ksiezyk.roommanagementsystem.data.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ReservationViewModel extends ViewModel {

    private ReservationRepository reservationRepository;
    private MutableLiveData<ReservationFormState> reservationFormState = new MutableLiveData<>();
    private MutableLiveData<GetReservationsResult> reservationsResult = new MutableLiveData<>();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    ReservationViewModel(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    LiveData<ReservationFormState> getReservationFormState() {
        return reservationFormState;
    }

    LiveData<GetReservationsResult> getReservationsResult() {
        return reservationsResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void reservationFormChanged(String beginDateTime, String endDateTime) {
        if (!isDateTimeValid(beginDateTime)) {
            reservationFormState.setValue(new ReservationFormState(R.string.invalid_begin_datetime, null));
        } else if (!isDateTimeValid(endDateTime)) {
            reservationFormState.setValue(new ReservationFormState(null, R.string.invalid_end_datetime));
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
            reservationsResult.setValue(new GetReservationsResult(data));
        } else {
            reservationsResult.setValue(new GetReservationsResult(R.string.get_reservations_failed));
        }
    }
}