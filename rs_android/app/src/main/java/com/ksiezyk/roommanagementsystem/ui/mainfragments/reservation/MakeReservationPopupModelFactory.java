package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ksiezyk.roommanagementsystem.data.datasource.LoginDataSource;
import com.ksiezyk.roommanagementsystem.data.datasource.ReservationDataSource;
import com.ksiezyk.roommanagementsystem.data.repository.LoginRepository;
import com.ksiezyk.roommanagementsystem.data.repository.ReservationRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class MakeReservationPopupModelFactory implements ViewModelProvider.Factory {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MakeReservationPopupModel.class)) {
            return (T) new MakeReservationPopupModel(
                    ReservationRepository.getInstance(new ReservationDataSource()),
                    LoginRepository.getInstance(new LoginDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}