package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ksiezyk.roommanagementsystem.R;
import com.ksiezyk.roommanagementsystem.data.model.Reservation;
import com.ksiezyk.roommanagementsystem.ui.components.ReservationWidget;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservationFragment extends Fragment {
    private ReservationViewModel reservationViewModel;
    private ProgressBar loadingProgressBar;
    private Button searchButton;
    private LinearLayout reservationsContainer;

    public ReservationFragment() {
        // Required empty public constructor
    }

    public static ReservationFragment newInstance() {
        ReservationFragment fragment = new ReservationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reservationViewModel = new ViewModelProvider(this, new ReservationViewModelFactory())
                .get(ReservationViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_reservation, container, false);

        loadingProgressBar = rootView.findViewById(R.id.reservations_loading_bar);
        reservationsContainer = rootView.findViewById(R.id.reservations_container);
        searchButton = rootView.findViewById(R.id.reservations_search);

        searchButton.setOnClickListener(e -> reservationViewModel.getReservations(
                25, LocalDateTime.now(), LocalDateTime.now().plusHours(1)));

        reservationViewModel.getReservationsResult().observe(getViewLifecycleOwner(), new Observer<GetReservationsResult>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(@Nullable GetReservationsResult getReservationsResult) {
                if (getReservationsResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (getReservationsResult.getError() != null) {
                    showGetReservationsFailed(getReservationsResult.getError());
                }
                if (getReservationsResult.getSuccess() != null) {
                    updateUiWithReservations(getReservationsResult.getSuccess());
                }
            }
        });

        return rootView;
    }

    private void showGetReservationsFailed(@StringRes Integer errorString) {
        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUiWithReservations(List<Reservation> success) {
        reservationsContainer.removeAllViews();
        success.forEach(r -> reservationsContainer.addView(new ReservationWidget(getContext(), r)));
    }
}

