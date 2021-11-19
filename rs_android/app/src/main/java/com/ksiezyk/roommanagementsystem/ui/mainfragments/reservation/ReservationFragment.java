package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ksiezyk.roommanagementsystem.R;
import com.ksiezyk.roommanagementsystem.data.model.Reservation;
import com.ksiezyk.roommanagementsystem.data.model.Room;
import com.ksiezyk.roommanagementsystem.ui.components.DateTimePickerWidget;
import com.ksiezyk.roommanagementsystem.ui.components.MakeReservationPopup;
import com.ksiezyk.roommanagementsystem.ui.components.ReservationWidget;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservationFragment extends Fragment {
    private ReservationViewModel reservationViewModel;
    private ProgressBar loadingProgressBar;
    private Spinner roomsSpinner;
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
                    (Room) roomsSpinner.getSelectedItem(),
                    beginDateTimeWidget.getText().toString(),
                    endDateTimeWidget.getText().toString());
        }
    };
    private Button searchButton;
    private FloatingActionButton makeReservationButton;
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

        // Find UI elements
        loadingProgressBar = rootView.findViewById(R.id.reservations_loading_bar);
        reservationsContainer = rootView.findViewById(R.id.reservations_container);

        // Add handlers
        roomsSpinner = rootView.findViewById(R.id.reservations_rooms);
        roomsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reservationViewModel.reservationFormChanged(
                        (Room) roomsSpinner.getSelectedItem(),
                        beginDateTimeWidget.getText().toString(),
                        endDateTimeWidget.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        beginDateTimeWidget = rootView.findViewById(R.id.reservation_begin_date_time_form);
        beginDateTimeWidget.addTextChangedListener(afterFormChangedListener);

        endDateTimeWidget = rootView.findViewById(R.id.reservation_end_date_time_form);
        endDateTimeWidget.addTextChangedListener(afterFormChangedListener);

        searchButton = rootView.findViewById(R.id.reservations_search);
        searchButton.setOnClickListener(e -> reservationViewModel.getReservations(
                ((Room) roomsSpinner.getSelectedItem()).getId(),
                beginDateTimeWidget.getText().toString(), endDateTimeWidget.getText().toString()));

        makeReservationButton = rootView.findViewById(R.id.make_reservation_button);
        makeReservationButton.setOnClickListener(v -> {
            MakeReservationPopup makeReservationPopup = new MakeReservationPopup();
            makeReservationPopup.showPopupWindow(v);
        });

        // Update UI when form state changes
        reservationViewModel.getReservationFormState().observe(getViewLifecycleOwner(), reservationFormState -> {
            if (reservationFormState == null) {
                return;
            }
            searchButton.setEnabled(reservationFormState.isDataValid());
            if (reservationFormState.getRoomNumberError() != null) {
                beginDateTimeWidget.setError(getString(reservationFormState.getRoomNumberError()));
            } else {
                beginDateTimeWidget.setError(null);
            }
            if (reservationFormState.getBeginDateTimeError() != null) {
                beginDateTimeWidget.setError(getString(reservationFormState.getBeginDateTimeError()));
            } else {
                beginDateTimeWidget.setError(null);
            }
            if (reservationFormState.getEndDateTimeError() != null) {
                endDateTimeWidget.setError(getString(reservationFormState.getEndDateTimeError()));
            } else {
                endDateTimeWidget.setError(null);
            }
        });

        // Update UI when rooms change
        reservationViewModel.getGetRoomsResult().observe(getViewLifecycleOwner(), getRoomsResult -> {
            if (getRoomsResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (getRoomsResult.getError() != null) {
                showGetRoomsFailed(getRoomsResult.getError());
            }
            if (getRoomsResult.getSuccess() != null) {
                updateUiWithRooms(getRoomsResult.getSuccess());
            }
        });

        // Update UI when reservations change
        reservationViewModel.getGetReservationsResult().observe(getViewLifecycleOwner(), getReservationsResult -> {
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
        });

        reservationViewModel.getRooms();

        return rootView;
    }

    private void showGetRoomsFailed(@StringRes Integer errorString) {
        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void updateUiWithRooms(List<Room> success) {
        ArrayAdapter aa = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, success);
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        roomsSpinner.setAdapter(aa);
    }

    private void showGetReservationsFailed(@StringRes Integer errorString) {
        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUiWithReservations(List<Reservation> success) {
        reservationsContainer.removeAllViews();
        int roomNumber = ((Room) roomsSpinner.getSelectedItem()).getId();
        success.forEach(r -> reservationsContainer.addView(
                new ReservationWidget(getContext(), r, roomNumber)));
    }
}

