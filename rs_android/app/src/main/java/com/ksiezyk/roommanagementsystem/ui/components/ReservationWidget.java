package com.ksiezyk.roommanagementsystem.ui.components;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.ksiezyk.roommanagementsystem.R;
import com.ksiezyk.roommanagementsystem.data.model.Reservation;

import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ReservationWidget extends CardView {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private Reservation reservation;
    private int roomNumber;
    private TextView dateView;
    private TextView timeView;
    private TextView userNickView;
    private TextView roomNumberView;

    public ReservationWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ReservationWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReservationWidget(Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ReservationWidget(Context context, Reservation res, int roomNum) {
        super(context);
        reservation = res;
        roomNumber = roomNum;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.reservation_widget, this);

        dateView = findViewById(R.id.reservation_date);
        timeView = findViewById(R.id.reservation_time);
        userNickView = findViewById(R.id.reservation_user_nick);
        roomNumberView = findViewById(R.id.reservation_room_number);

        setContent();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setContent() {
        dateView.setText(reservation.getDate().format(dateFormatter));
        timeView.setText(String.format(
                getContext().getString(R.string.reservation_time),
                reservation.getBeginTime().format(timeFormatter),
                reservation.getEndTime().format(timeFormatter)));
        userNickView.setText(reservation.getUserNick());
        roomNumberView.setText(String.format(
                getContext().getString(R.string.reservation_room),
                roomNumber));
    }
}
