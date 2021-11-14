package com.ksiezyk.roommanagementsystem.ui.components;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimePickerWidget extends AppCompatEditText {
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
    private Calendar calendar;

    public DateTimePickerWidget(Context context) {
        super(context);
        init();
    }

    public DateTimePickerWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateTimePickerWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(
                    getContext(),
                    (dateView, year, month, day) -> {
                        setDate(year, month, day);
                        TimePickerDialog timePicker = new TimePickerDialog(
                                getContext(),
                                (timeView, hour, minute) -> {
                                    setTime(hour, minute);
                                    setText(getDateTime());
                                },
                                cldr.get(Calendar.HOUR_OF_DAY),
                                cldr.get(Calendar.MINUTE),
                                true
                        );
                        timePicker.show();
                    },
                    cldr.get(Calendar.YEAR),
                    cldr.get(Calendar.MONTH),
                    cldr.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });
    }

    public void setDate(int year, int month, int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
    }

    public void setTime(int hour, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
    }

    public String getDateTime() {
        return formatter.format(calendar.getTime());
    }
}
