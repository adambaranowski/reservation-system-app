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
import com.ksiezyk.roommanagementsystem.data.repository.LoginRepository;
import com.ksiezyk.roommanagementsystem.data.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MakeReservationPopupModel extends ViewModel {
    private final ReservationRepository reservationRepository;
    private final LoginRepository loginRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final MutableLiveData<MakeReservationForm> makeReservationForm = new MutableLiveData<>();
    private final MutableLiveData<CreateReservationResult> createReservationResult = new MutableLiveData<>();

    public MakeReservationPopupModel(ReservationRepository reservationRepository, LoginRepository loginRepository) {
        this.reservationRepository = reservationRepository;
        this.loginRepository = loginRepository;
    }

    public LiveData<MakeReservationForm> getMakeReservationForm() {
        return makeReservationForm;
    }

    public LiveData<CreateReservationResult> getCreateReservationResult() {
        return createReservationResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void makeReservationFormChanged(Room room, String beginDateTimeString, String endDateTimeString) {
        LocalDateTime beginDateTime = parseDateTime(beginDateTimeString);
        LocalDateTime endDateTime = parseDateTime(endDateTimeString);
        makeReservationForm.setValue(new MakeReservationForm(room.getId(), beginDateTime, endDateTime));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDateTime parseDateTime(String dateTime) {
        if (dateTime == null) return null;
        try {
            return LocalDateTime.parse(dateTime, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createReservation() {
        if (makeReservationForm.getValue() == null || !makeReservationForm.getValue().isValid())
            return;

        MakeReservationForm form = makeReservationForm.getValue();
        Result<Reservation> result = reservationRepository.createReservation(
                form.getRoomNumber(),
                form.getBeginDateTime(),
                form.getEndDateTime(),
                loginRepository.getUser().getDisplayName());

        if (result instanceof Result.Success) {
            Reservation data = ((Result.Success<Reservation>) result).getData();
            createReservationResult.setValue(new CreateReservationResult(data));
        } else {
            createReservationResult.setValue(
                    new CreateReservationResult(R.string.reservations_create_reservation_failed));
        }
    }
}

class MakeReservationForm {
    private final boolean isValid;
    private final Integer roomNumber;
    private final LocalDateTime beginDateTime;
    private final LocalDateTime endDateTime;

    MakeReservationForm(Integer roomNumber, LocalDateTime beginDateTime, LocalDateTime endDateTime) {
        this.roomNumber = roomNumber;
        this.beginDateTime = beginDateTime;
        this.endDateTime = endDateTime;

        isValid = roomNumber != null && beginDateTime != null && endDateTime != null;
    }

    public boolean isValid() {
        return isValid;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public LocalDateTime getBeginDateTime() {
        return beginDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
}
