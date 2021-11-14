package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import androidx.annotation.Nullable;

public class ReservationFormState {
    @Nullable
    private Integer beginDateTimeError;
    @Nullable
    private Integer endDateTimeError;
    private boolean isDataValid;

    ReservationFormState(@Nullable Integer beginDateTimeError, @Nullable Integer endDateTimeError) {
        this.beginDateTimeError = beginDateTimeError;
        this.endDateTimeError = endDateTimeError;
        this.isDataValid = false;
    }

    ReservationFormState(boolean isDataValid) {
        this.beginDateTimeError = null;
        this.endDateTimeError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getBeginDateTimeError() {
        return beginDateTimeError;
    }

    @Nullable
    Integer getEndDateTimeError() {
        return endDateTimeError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
