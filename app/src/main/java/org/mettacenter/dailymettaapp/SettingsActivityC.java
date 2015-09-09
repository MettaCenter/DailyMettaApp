package org.mettacenter.dailymettaapp;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sunyata on 2015-09-04.
 */
public class SettingsActivityC
        extends AppCompatActivity {

    @Override
    public void onCreate(Bundle iSavedInstanceState){
        super.onCreate(iSavedInstanceState);
        setContentView(R.layout.activity_settings);


        SharedPreferences tSharedPrefs = getSharedPreferences(
                ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int tNotificationHour = tSharedPrefs.getInt(ConstsU.PREF_NOTIFICATION_HOUR, ConstsU.NOTIFICATION_NOT_SET);
        int tNotificationMinute = tSharedPrefs.getInt(ConstsU.PREF_NOTIFICATION_MINUTE, ConstsU.NOTIFICATION_NOT_SET);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, tNotificationHour);
        c.set(Calendar.MINUTE, tNotificationMinute);

        SimpleDateFormat tSimpleDateFormat;
        if(android.text.format.DateFormat.is24HourFormat(this) == true){
            tSimpleDateFormat = new SimpleDateFormat("HH:mm");
        }else{
            tSimpleDateFormat = new SimpleDateFormat("hh:mm aa");
        }

        //-TODO: add am/pm?
        //-TODO: update gui when we get back from the time picker dialog

        TextView tTextView = (TextView)findViewById(R.id.notificationTimeTextView);

        tTextView.setText(tSimpleDateFormat.format(c.getTime()));

        CheckBox tNotificationCb = (CheckBox)findViewById(R.id.notificationOnOffCheckBox);
        tNotificationCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() == true) {
                    DialogFragment tTimePickerFragment = new TimePickerFragmentC();
                    tTimePickerFragment.show(
                            SettingsActivityC.this.getFragmentManager(),
                            "TimePicker");
                }
            }
        });
    }
}
