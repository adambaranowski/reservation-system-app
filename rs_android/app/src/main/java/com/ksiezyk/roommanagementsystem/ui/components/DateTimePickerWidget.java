package com.ksiezyk.roommanagementsystem.ui.components;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ksiezyk.roommanagementsystem.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimePickerWidget extends TextInputLayout {
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
    private Calendar calendar;
    private TextInputEditText editText;

    public DateTimePickerWidget(Context context) {
        super(context);
        init();
    }

    public DateTimePickerWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
        init();
    }

    public DateTimePickerWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //Create a View object yourself through inflater
        View view = inflate(getContext(), R.layout.widget_date_time_picker, this);
        editText = view.findViewById(R.id.date_time_picker_edit_text);
        editText.setOnClickListener(v -> {
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
                                    editText.setText(getDateTime());
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
        return calendar == null ? "" : formatter.format(calendar.getTime());
    }

    public void addTextChangedListener(Runnable listener) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                listener.run();
            }
        });
    }
}
