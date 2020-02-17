package dev.michaeljung.whereismymoney;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private Calendar calendar;

    public DatePickerFragment() {
        calendar = Calendar.getInstance();
    }

    DatePickerFragment(Calendar calendar) {
        this.calendar = calendar;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) requireParentFragment();
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }
}
