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
import java.util.List;

public class ReservationViewModel extends ViewModel {

    private ReservationRepository reservationRepository;
    private MutableLiveData<GetReservationsResult> reservationsResult = new MutableLiveData<>();

    ReservationViewModel(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    LiveData<GetReservationsResult> getReservationsResult() {
        return reservationsResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getReservations(int roomNumber, LocalDateTime beginDateTime, LocalDateTime endDateTime) {
        // can be launched in a separate asynchronous job
        Result<List<Reservation>> result = reservationRepository.getReservations(roomNumber,
                beginDateTime, endDateTime);

        if (result instanceof Result.Success) {
            List<Reservation> data = ((Result.Success<List<Reservation>>) result).getData();
            reservationsResult.setValue(new GetReservationsResult(data));
        } else {
            reservationsResult.setValue(new GetReservationsResult(R.string.get_reservations_failed));
        }
    }
}