package org.mettacenter.dailymettaapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * 1. Shows a calendar to the user for picking a date
 * 2. Calculates the ViewPager position corresponding to this date (Please be aware of leap day!)
 * 3. Sends that ViewPager position back to the article activity
 */
public class DatePickerFragmentC
        extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{

    public static final String TAG = "DatePickerFragmentC";

    @Override
    public Dialog onCreateDialog(Bundle iSavedInstanceState) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog rDatePickerDialog = new DatePickerDialog(getActivity(), this,
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        rDatePickerDialog.getDatePicker().setCalendarViewShown(true);
        rDatePickerDialog.getDatePicker().setSpinnersShown(false);

        /*
        There is a bug in the CalendarView which slows it down very much if we have set the max date and scrolling to the end of the year:
        http://stackoverflow.com/questions/18437702/date-picker-dialogue-app-crashes-when-i-set-max-and-min-date
        Therefore we don't set the start date and end date

        c.clear();
        c.set(Calendar.YEAR, 2015);
        c.set(Calendar.DAY_OF_YEAR, c.getMinimum(Calendar.DAY_OF_YEAR));
        c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
        c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
        c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
        rDatePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

        c.clear();
        c.set(Calendar.YEAR, 2015);
        c.set(Calendar.DAY_OF_YEAR, c.getMaximum(Calendar.DAY_OF_YEAR));
        c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
        c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
        c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
        rDatePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis() - TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
        */

        return rDatePickerDialog;
    }

    /**
     * This link can be useful when converting between milliseconds and dates:
     * http://www.fileformat.info/tip/java/date2millis.htm
     *
     * //Please note that we don't change the time zone here since we want to use the local time zone
     */
    @Override
    public void onDateSet(DatePicker iView, int iYear, int iMonthOfYear, int iDayOfMonth) {
        long tPosLg = UtilitiesU.getArticleFragmentPositionFromDate(getActivity(), iMonthOfYear, iDayOfMonth);
        if(tPosLg != ConstsU.NO_ARTICLE_POS){
            //Starting a new article activity with the fragment for the chosen article
            Intent tIntent = new Intent(getActivity(), ArticleActivityC.class);
            tIntent.putExtra(ConstsU.EXTRA_ARTICLE_POS_ID, tPosLg);
            /*
            -One is subtracted here because the position in the ViewPager starts at zero
            while the SQLite db starts at 1. From the SQLite documentation:
            "Rows are assigned contiguously ascending rowid values, starting with 1[...]"
            https://www.sqlite.org/lang_createtable.html
            Another reason this simple conversion works is that we drop the database and then
            write new values to it
            */
            startActivityForResult(tIntent, 0);
        }else{
            AlertDialog tAlertDialog = new AlertDialog.Builder(getActivity()).create();
            tAlertDialog.setMessage("No article found for this date");
            tAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );
            tAlertDialog.show();
        }
    }
}
