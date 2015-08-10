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
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sunyata on 2015-08-10.
 */
public class DatePickerFragmentC
        extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{

    //public static final String EXTRA_ARTICLE_TIME = "ARTICLE_TIME";

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

        /////Log.i(UtilitiesU.TAG, "onListItemClick, iId = " + iId);

        Calendar c1 = Calendar.getInstance();
        c1.set(year, monthOfYear, dayOfMonth, 0, 0);
        c1.setTimeZone(TimeZone.getTimeZone(UtilitiesU.TIMEZONE));
        long tChosenDateInMilliSecondsStartLg = c1.getTime().getTime();
        //-please note that the methods called "getTime" return different types
        Log.i(UtilitiesU.TAG, "tChosenDateInMilliSecondsStartLg = " + tChosenDateInMilliSecondsStartLg);

        Calendar c2 = Calendar.getInstance();
        c2.set(year, monthOfYear, dayOfMonth, 23, 59);
        c2.setTimeZone(TimeZone.getTimeZone(UtilitiesU.TIMEZONE));
        long tChosenDateInMilliSecondsEndLg = c2.getTime().getTime();
        Log.i(UtilitiesU.TAG, "tChosenDateInMilliSecondsEndLg = " + tChosenDateInMilliSecondsEndLg);

        String[] tProj = {BaseColumns._ID, ArticleTableM.COLUMN_TIME};
        String tSel = ArticleTableM.COLUMN_TIME + " BETWEEN " + tChosenDateInMilliSecondsStartLg + " AND " + tChosenDateInMilliSecondsEndLg;
        //-Please note that "BETWEEN" suprisingly includes results that are "on the edge" in the comparison
        String tSortOrderSg = "DESC";

        Cursor tCursor = getActivity().getContentResolver().query(
                ContentProviderM.ARTICLE_CONTENT_URI, tProj, tSel, null, tSortOrderSg);
        long tDataBaseIdLg = 0; //TODO: Handle the 0 case
        if(tCursor != null && tCursor.getCount() > 0){
            tCursor.moveToFirst();
            tDataBaseIdLg = tCursor.getLong(tCursor.getColumnIndexOrThrow(BaseColumns._ID));

            //Starting a new article activity with the fragment for the chosen article
            Intent tIntent = new Intent(getActivity(), ArticleActivityC.class);
            tIntent.putExtra(UtilitiesU.EXTRA_ARTICLE_POS_ID, tDataBaseIdLg - 1);
            //-One is subtracted here because the position in the ViewPager starts differently
            startActivityForResult(tIntent, 0); ///getActivity().

        }else{
            //TODO: 1. Dialog: No article found for this date. 2. Go to today
            AlertDialog tAlertDialog = new AlertDialog.Builder(getActivity()).create();
            //tAlertDialog.setTitle("");
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
