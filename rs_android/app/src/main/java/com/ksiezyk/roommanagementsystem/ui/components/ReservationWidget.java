package com.ksiezyk.roommanagementsystem.ui.components;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;

import com.ksiezyk.roommanagementsystem.data.model.Reservation;

import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ReservationWidget extends AppCompatTextView {
    private String text = "User: %s\nBegin date: %s\nEnd date: %s";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Reservation reservation;

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
    public ReservationWidget(Context context, Reservation res) {
        super(context);
        reservation = res;
        setText(getContent());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getContent() {
        return String.format(text,
                reservation.getUserNick(),
                formatter.format(reservation.getDate().atTime(reservation.getBeginTime())),
                formatter.format(reservation.getDate().atTime(reservation.getEndTime())));
    }
}
