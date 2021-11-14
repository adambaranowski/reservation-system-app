package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.ksiezyk.roommanagementsystem.ui.components.DateTimePickerWidget;
import com.ksiezyk.roommanagementsystem.ui.components.ReservationWidget;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservationFragment extends Fragment {
    private ReservationViewModel reservationViewModel;
    private int roomNumber;
    private ProgressBar loadingProgressBar;
    private DateTimePickerWidget beginDateTimeWidget;
    private DateTimePickerWidget endDateTimeWidget;
    private final TextWatcher afterFormChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // ignore
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // ignore
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void afterTextChanged(Editable s) {
            reservationViewModel.reservationFormChanged(
                    beginDateTimeWidget.getText().toString(),
                    endDateTimeWidget.getText().toString());
        }
    };
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
        beginDateTimeWidget = rootView.findViewById(R.id.reservation_begin_date_time_form);
        endDateTimeWidget = rootView.findViewById(R.id.reservation_end_date_time_form);
        searchButton = rootView.findViewById(R.id.reservations_search);
        reservationsContainer = rootView.findViewById(R.id.reservations_container);

        beginDateTimeWidget.addTextChangedListener(afterFormChangedListener);
        endDateTimeWidget.addTextChangedListener(afterFormChangedListener);

        roomNumber = 25;
        searchButton.setOnClickListener(e -> reservationViewModel.getReservations(roomNumber,
                beginDateTimeWidget.getText().toString(), endDateTimeWidget.getText().toString()));

        reservationViewModel.getReservationFormState().observe(getViewLifecycleOwner(), new Observer<ReservationFormState>() {
            @Override
            public void onChanged(@Nullable ReservationFormState reservationFormState) {
                if (reservationFormState == null) {
                    return;
                }
                searchButton.setEnabled(reservationFormState.isDataValid());
                if (reservationFormState.getBeginDateTimeError() != null) {
                    beginDateTimeWidget.setError(getString(reservationFormState.getBeginDateTimeError()));
                }
                if (reservationFormState.getEndDateTimeError() != null) {
                    endDateTimeWidget.setError(getString(reservationFormState.getEndDateTimeError()));
                }
            }
        });

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
        success.forEach(r -> reservationsContainer.addView(
                new ReservationWidget(getContext(), r, roomNumber)));
    }
}

