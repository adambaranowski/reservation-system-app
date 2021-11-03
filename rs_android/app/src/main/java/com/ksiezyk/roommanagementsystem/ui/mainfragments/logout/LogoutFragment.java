package com.ksiezyk.roommanagementsystem.ui.mainfragments.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.ksiezyk.roommanagementsystem.R;
import com.ksiezyk.roommanagementsystem.ui.login.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogoutFragment extends Fragment {
    private LogoutViewModel logoutViewModel;
    private NavController navController;
    private Button logoutConfirm;
    private Button logoutDecline;

    public LogoutFragment() {
        // Required empty public constructor
    }

    public static LogoutFragment newInstance() {
        LogoutFragment fragment = new LogoutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logoutViewModel = new ViewModelProvider(this, new LogoutViewModelFactory())
                .get(LogoutViewModel.class);
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        logoutConfirm = (Button) view.findViewById(R.id.logout_confirm);
        logoutDecline = (Button) view.findViewById(R.id.logout_decline);

        logoutConfirm.setOnClickListener(v -> handleLogout());
        logoutDecline.setOnClickListener(v -> navigateToHome());

        return view;
    }

    private void handleLogout() {
        logoutViewModel.logout();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void navigateToHome() {
        navController.navigate(R.id.navigate_home);
    }
}