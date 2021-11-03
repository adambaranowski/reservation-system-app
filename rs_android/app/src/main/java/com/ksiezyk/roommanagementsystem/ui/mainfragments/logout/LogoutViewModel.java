package com.ksiezyk.roommanagementsystem.ui.mainfragments.logout;

import androidx.lifecycle.ViewModel;

import com.ksiezyk.roommanagementsystem.data.LoginRepository;

public class LogoutViewModel extends ViewModel {

    private LoginRepository loginRepository;

    LogoutViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public void logout() {
        // can be launched in a separate asynchronous job
        loginRepository.logout();
    }
}
