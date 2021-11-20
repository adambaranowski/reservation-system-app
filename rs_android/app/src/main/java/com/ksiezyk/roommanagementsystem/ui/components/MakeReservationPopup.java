package com.ksiezyk.roommanagementsystem.ui.components;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.ksiezyk.roommanagementsystem.R;
import com.ksiezyk.roommanagementsystem.data.model.Room;

import java.util.List;

public class MakeReservationPopup {
    private List<Room> rooms;

    public MakeReservationPopup(List<Room> rooms) {
        this.rooms = rooms;
    }

    public void showPopupWindow(final View view) {
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_make_reservation, null);

        //Specify the length and width through constants
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // Set rooms
        AutoCompleteTextView chooseRoomView = popupView.findViewById(R.id.make_reservation_choose_room_view);
        ArrayAdapter aa = new ArrayAdapter(view.getContext(), R.layout.support_simple_spinner_dropdown_item, rooms);
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        chooseRoomView.setAdapter(aa);

        //Initialize the elements of our window, install the handler
        Button confirmButton = popupView.findViewById(R.id.make_reservation_confirm);
        confirmButton.setOnClickListener(v -> {
            //As an example, display the message
            Toast.makeText(view.getContext(), "Wow, popup action button", Toast.LENGTH_SHORT).show();
        });

        Button cancelButton = popupView.findViewById(R.id.make_reservation_cancel);
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        popupView.setOnTouchListener((v, event) -> {
            //Close the window when clicked
            v.performClick();
            popupWindow.dismiss();
            return true;
        });
    }
}
