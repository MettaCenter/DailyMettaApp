package org.mettacenter.dailymettaapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * 1. Shows a calendar to the user for picking a date
 * 2. Calculates the ViewPager position corresponding to this date (Please be aware of leap day!)
 * 3. Sends that ViewPager position back to the article activity
 */
public class DatePickerFragmentC
        extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{

    @Override
    public Dialog onCreateDialog(Bundle iSavedInstanceState) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog rDatePickerDialog = new DatePickerDialog(getActivity(), this,
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        rDatePickerDialog.getDatePicker().setCalendarViewShown(true);
        rDatePickerDialog.getDatePicker().setSpinnersShown(false);
        return rDatePickerDialog;
    }

    /**
     * This link can be useful when converting between milliseconds and dates:
     * http://www.fileformat.info/tip/java/date2millis.htm
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();

        c.set(year, monthOfYear, dayOfMonth, 0, 0);
        c.setTimeZone(TimeZone.getTimeZone(ConstsU.SERVER_TIMEZONE));
        long tChosenDateInMilliSecondsStartLg = c.getTime().getTime();
        //-please note that the two methods called "getTime" return different types
        Log.d(ConstsU.TAG, "tChosenDateInMilliSecondsStartLg = " + tChosenDateInMilliSecondsStartLg);

        c.set(year, monthOfYear, dayOfMonth, 23, 59);
        c.setTimeZone(TimeZone.getTimeZone(ConstsU.SERVER_TIMEZONE));
        long tChosenDateInMilliSecondsEndLg = c.getTime().getTime();
        Log.d(ConstsU.TAG, "tChosenDateInMilliSecondsEndLg = " + tChosenDateInMilliSecondsEndLg);

        String[] tProj = {BaseColumns._ID, ArticleTableM.COLUMN_TIME_MONTH, ArticleTableM.COLUMN_TIME_DAYOFMONTH};
        String tSel =
                ArticleTableM.COLUMN_TIME_MONTH + " = " + monthOfYear
                + " AND "
                + ArticleTableM.COLUMN_TIME_DAYOFMONTH + " = " + dayOfMonth;
        //-Please note that "BETWEEN" surprisingly includes results that are "on the edge" in the comparison
        String tSortOrderSg = "DESC";

        Cursor tCursor = getActivity().getContentResolver().query(
                ContentProviderM.ARTICLE_CONTENT_URI, tProj, tSel, null, tSortOrderSg);
        if(tCursor != null && tCursor.getCount() > 0){
            tCursor.moveToFirst();
            long tDataBaseIdLg = tCursor.getLong(tCursor.getColumnIndexOrThrow(BaseColumns._ID));

            //Starting a new article activity with the fragment for the chosen article
            Intent tIntent = new Intent(getActivity(), ArticleActivityC.class);
            tIntent.putExtra(ConstsU.EXTRA_ARTICLE_POS_ID, tDataBaseIdLg - 1);
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
