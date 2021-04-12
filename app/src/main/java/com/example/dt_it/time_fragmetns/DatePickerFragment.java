package com.example.dt_it.time_fragmetns;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public final static String DATE_FRAGMENT_TAG = "Date dialog";
    public final static String TIME_FRAGMENT_TAG = "TimePicker";

    private final FragmentManager fragmentManager;
    private final CharSequence title;
    private final Calendar calendar;

    public DatePickerFragment(FragmentManager fragmentManager, CharSequence title) {
        this.fragmentManager = fragmentManager;
        this.title = title;
        calendar = Calendar.getInstance();
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,
                year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        new TimePickerFragment(calendar, title).show(fragmentManager, TIME_FRAGMENT_TAG);
    }

    public void show() {
        show(fragmentManager, DATE_FRAGMENT_TAG);
    }
}
