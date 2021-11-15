package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ksiezyk.roommanagementsystem.data.datasource.ReservationDataSource;
import com.ksiezyk.roommanagementsystem.data.datasource.RoomDataSource;
import com.ksiezyk.roommanagementsystem.data.repository.ReservationRepository;
import com.ksiezyk.roommanagementsystem.data.repository.RoomRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class ReservationViewModelFactory implements ViewModelProvider.Factory {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReservationViewModel.class)) {
            return (T) new ReservationViewModel(
                    ReservationRepository.getInstance(new ReservationDataSource()),
                    RoomRepository.getInstance(new RoomDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}