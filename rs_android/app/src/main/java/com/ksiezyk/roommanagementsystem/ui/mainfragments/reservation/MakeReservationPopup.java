package com.ksiezyk.roommanagementsystem.ui.mainfragments.reservation;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.ksiezyk.roommanagementsystem.R;
import com.ksiezyk.roommanagementsystem.data.model.Reservation;
import com.ksiezyk.roommanagementsystem.data.model.Room;
import com.ksiezyk.roommanagementsystem.ui.components.DateTimePickerWidget;

import java.util.List;

public class MakeReservationPopup {
    private MakeReservationPopupModel makeReservationPopupModel;
    private List<Room> rooms;
    private Room chosenRoom;
    private DateTimePickerWidget beginDateTimeWidget;
    private DateTimePickerWidget endDateTimeWidget;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MakeReservationPopup(List<Room> rooms) {
        makeReservationPopupModel = new MakeReservationPopupModelFactory().create(MakeReservationPopupModel.class);
        this.rooms = rooms;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showPopupWindow(final View view) {
        //Create a View object yourself through inflater
        Context context = view.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_make_reservation, null);

        //Specify the length and width through constants
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler
        ArrayAdapter aa = new ArrayAdapter(view.getContext(), R.layout.support_simple_spinner_dropdown_item, rooms);
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        AutoCompleteTextView chooseRoomView = popupView.findViewById(R.id.make_reservation_choose_room_view);
        chooseRoomView.setAdapter(aa);
        chooseRoomView.setOnItemClickListener((parent, v, position, id) -> {
            chosenRoom = (Room) parent.getItemAtPosition(position);
            afterFormChangedListener();
        });

        beginDateTimeWidget = popupView.findViewById(R.id.reservation_begin_date_time_form);
        beginDateTimeWidget.addTextChangedListener(this::afterFormChangedListener);

        endDateTimeWidget = popupView.findViewById(R.id.reservation_end_date_time_form);
        endDateTimeWidget.addTextChangedListener(this::afterFormChangedListener);

        Button confirmButton = popupView.findViewById(R.id.make_reservation_confirm);
        confirmButton.setOnClickListener(v -> {
            makeReservationPopupModel.createReservation();
            Reservation reservation = makeReservationPopupModel.getCreateReservationResult().getValue().getSuccess();
            String msg = String.format("Reservation{\n\tid: %d, \n\tbegin: %s, \n\tend: %s, \n\tuser: %s\n}",
                    reservation.getId(),
                    reservation.getBeginTime().toString(),
                    reservation.getEndTime().toString(),
                    reservation.getUserNick());
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            popupWindow.dismiss();
        });

        Button cancelButton = popupView.findViewById(R.id.make_reservation_cancel);
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        popupView.setOnTouchListener((v, event) -> {
            v.performClick();
            popupWindow.dismiss();
            return true;
        });

        // Update UI when form changes
        makeReservationPopupModel.getMakeReservationForm().observe(getLifecycleOwner(context),
                form -> confirmButton.setEnabled(form.isValid()));
    }

    private LifecycleOwner getLifecycleOwner(Context context) {
        while (!(context instanceof LifecycleOwner)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return (LifecycleOwner) context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void afterFormChangedListener() {
        makeReservationPopupModel.makeReservationFormChanged(
                chosenRoom,
                beginDateTimeWidget.getDateTime(),
                endDateTimeWidget.getDateTime());
    }
}
