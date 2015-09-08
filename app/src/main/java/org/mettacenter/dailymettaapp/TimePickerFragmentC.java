package org.mettacenter.dailymettaapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by sunyata on 2015-09-04.
 */
public class TimePickerFragmentC
        extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle iSavedInstanceState) {
        final Calendar c = Calendar.getInstance();
        TimePickerDialog rTimePickerDialog = new TimePickerDialog(getActivity(), this,
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
        return rTimePickerDialog;
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


        //Reading from the preferences


    }
}
