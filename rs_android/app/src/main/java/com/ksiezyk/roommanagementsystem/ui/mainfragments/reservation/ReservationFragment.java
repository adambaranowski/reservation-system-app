package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    private AutoCompleteTextView chooseRoomView;
    private Room chosenRoom;
    private DateTimePickerWidget beginDateTimeWidget;
    private DateTimePickerWidget endDateTimeWidget;
    private Button searchButton;
    private FloatingActionButton makeReservationButton;
    private LinearLayout reservationsContainer;
    private List<Room> rooms;

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
        chooseRoomView = rootView.findViewById(R.id.reservations_rooms);
        chooseRoomView.setOnItemClickListener((parent, view, position, id) -> {
            chosenRoom = (Room) parent.getItemAtPosition(position);
            afterFormChangedListener();
        });

        beginDateTimeWidget = rootView.findViewById(R.id.reservation_begin_date_time_form);
        beginDateTimeWidget.addTextChangedListener(this::afterFormChangedListener);

        endDateTimeWidget = rootView.findViewById(R.id.reservation_end_date_time_form);
        endDateTimeWidget.addTextChangedListener(this::afterFormChangedListener);

        searchButton = rootView.findViewById(R.id.reservations_search);
        searchButton.setOnClickListener(e -> reservationViewModel.getReservations(
                chosenRoom.getId(),
                beginDateTimeWidget.getDateTime(),
                endDateTimeWidget.getDateTime()));

        makeReservationButton = rootView.findViewById(R.id.make_reservation_button);
        makeReservationButton.setOnClickListener(v -> {
            MakeReservationPopup makeReservationPopup = new MakeReservationPopup(rooms);
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
                rooms = getRoomsResult.getSuccess();
                updateUiWithRooms();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void afterFormChangedListener() {
        reservationViewModel.reservationFormChanged(
                chosenRoom,
                beginDateTimeWidget.getDateTime(),
                endDateTimeWidget.getDateTime());
    }

    private void showGetRoomsFailed(@StringRes Integer errorString) {
        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void updateUiWithRooms() {
        ArrayAdapter aa = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, rooms);
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        chooseRoomView.setAdapter(aa);
    }

    private void showGetReservationsFailed(@StringRes Integer errorString) {
        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUiWithReservations(List<Reservation> success) {
        reservationsContainer.removeAllViews();
        success.forEach(r -> reservationsContainer.addView(
                new ReservationWidget(getContext(), r, chosenRoom.getId())));
    }
}

