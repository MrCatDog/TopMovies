package com.example.dt_it.time_fragmetns;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.example.dt_it.NotificationPublisher;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private final Calendar notifyTime;
    private final CharSequence title;

    public TimePickerFragment(Calendar calendar, CharSequence title) {
        notifyTime = calendar;
        this.title = title;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(
                getActivity(),
                AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                this,
                notifyTime.get(Calendar.HOUR_OF_DAY),
                notifyTime.get(Calendar.MINUTE),
                true
        );
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        notifyTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        notifyTime.set(Calendar.MINUTE, minute);
        notifyTime.set(Calendar.SECOND, 0);
        new NotificationPublisher().scheduleNotification(getActivity(), title, notifyTime.getTimeInMillis());
    }
}
