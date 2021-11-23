package com.ksiezyk.roommanagementsystem.ui.login;

import android.content.SharedPreferences;
import android.util.Log;

public class CredentialsManager {
    public static final String PREF_NAME = "USER_CREDENTIALS";
    public final String USERNAME_KEY = "USERNAME";
    public final String PASSWORD_KEY = "PASSWORD";

    SharedPreferences sharedPreferences;

    public CredentialsManager(SharedPreferences sharedPref) {
        sharedPreferences = sharedPref;
    }

    public String getUsername() {
        String username = sharedPreferences.getString(USERNAME_KEY, "");
        return username;
    }

    public String getPassword() {
        String password = sharedPreferences.getString(PASSWORD_KEY, "");
        return password;
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();
    }

    public void setPassword(String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PASSWORD_KEY, password);
        editor.apply();
    }

    public boolean areCredsSet() {
        String username = getUsername();
        String password = getPassword();

        return (!username.equals("") && !password.equals(""));
    }

    public void clearCreds() {
        sharedPreferences.edit().clear().apply();
    }
}
