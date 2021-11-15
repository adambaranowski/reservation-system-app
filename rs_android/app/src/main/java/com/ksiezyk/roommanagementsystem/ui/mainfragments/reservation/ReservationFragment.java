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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ksiezyk.roommanagementsystem.R;
import com.ksiezyk.roommanagementsystem.data.model.Reservation;
import com.ksiezyk.roommanagementsystem.data.model.Room;
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
        roomsSpinner = rootView.findViewById(R.id.reservations_rooms);
        beginDateTimeWidget = rootView.findViewById(R.id.reservation_begin_date_time_form);
        endDateTimeWidget = rootView.findViewById(R.id.reservation_end_date_time_form);
        searchButton = rootView.findViewById(R.id.reservations_search);
        reservationsContainer = rootView.findViewById(R.id.reservations_container);

        // Update form state when data changes
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
        beginDateTimeWidget.addTextChangedListener(afterFormChangedListener);
        endDateTimeWidget.addTextChangedListener(afterFormChangedListener);

        // Update UI when form state changes
        reservationViewModel.getReservationFormState().observe(getViewLifecycleOwner(), new Observer<ReservationFormState>() {
            @Override
            public void onChanged(@Nullable ReservationFormState reservationFormState) {
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
            }
        });

        // Get reservations when button clicked
        searchButton.setOnClickListener(e -> reservationViewModel.getReservations(
                ((Room) roomsSpinner.getSelectedItem()).getId(),
                beginDateTimeWidget.getText().toString(), endDateTimeWidget.getText().toString()));

        // Update UI when rooms change
        reservationViewModel.getGetRoomsResult().observe(getViewLifecycleOwner(), new Observer<GetRoomsResult>() {
            @Override
            public void onChanged(GetRoomsResult getRoomsResult) {
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
            }
        });

        // Update UI when reservations change
        reservationViewModel.getGetReservationsResult().observe(getViewLifecycleOwner(), new Observer<GetReservationsResult>() {
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

