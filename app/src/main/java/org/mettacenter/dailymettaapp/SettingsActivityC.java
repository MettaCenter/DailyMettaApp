package org.mettacenter.dailymettaapp;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sunyata on 2015-09-04.
 */
public class SettingsActivityC
        extends AppCompatActivity
        implements TimePickerFragmentC.OnDateSetListenerI {

    @Override
    public void onCreate(Bundle iSavedInstanceState){
        super.onCreate(iSavedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout tNotificationLl = (LinearLayout)findViewById(R.id.notificationOnOffLayout);
        tNotificationLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences tSharedPrefs = getSharedPreferences(
                        ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                int tNotificationHour = tSharedPrefs.getInt(ConstsU.PREF_NOTIFICATION_HOUR, ConstsU.NOTIFICATION_NOT_SET);
                int tNotificationMinute = tSharedPrefs.getInt(ConstsU.PREF_NOTIFICATION_MINUTE, ConstsU.NOTIFICATION_NOT_SET);


                if(tNotificationHour != ConstsU.NOTIFICATION_NOT_SET
                        && tNotificationMinute != ConstsU.NOTIFICATION_NOT_SET) {

                    SharedPreferences.Editor tPrefEditor = getSharedPreferences(
                            ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();
                    tPrefEditor.putInt(ConstsU.PREF_NOTIFICATION_HOUR, ConstsU.NOTIFICATION_NOT_SET);
                    tPrefEditor.putInt(ConstsU.PREF_NOTIFICATION_MINUTE, ConstsU.NOTIFICATION_NOT_SET);
                    tPrefEditor.commit();

                }else{

                    DialogFragment tTimePickerFragment = TimePickerFragmentC.newInstance(SettingsActivityC.this);
                    tTimePickerFragment.show(
                            SettingsActivityC.this.getFragmentManager(),
                            "TimePicker");

                }

                updateGui();
            }
        });

        TextView tNotificationTimeTextView = (TextView)findViewById(R.id.notificationTimeTextView);
        tNotificationTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment tTimePickerFragment = TimePickerFragmentC.newInstance(SettingsActivityC.this);
                tTimePickerFragment.show(
                        SettingsActivityC.this.getFragmentManager(),
                        "TimePicker");

                updateGui();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        updateGui();
    }

    public void updateGui(){
        ImageView tOnOffIv = (ImageView)findViewById(R.id.notificationOnOffImage);

        SharedPreferences tSharedPrefs = getSharedPreferences(
                ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int tNotificationHour = tSharedPrefs.getInt(ConstsU.PREF_NOTIFICATION_HOUR, ConstsU.NOTIFICATION_NOT_SET);
        int tNotificationMinute = tSharedPrefs.getInt(ConstsU.PREF_NOTIFICATION_MINUTE, ConstsU.NOTIFICATION_NOT_SET);

        TextView tTextView = (TextView)findViewById(R.id.notificationTimeTextView);
        LinearLayout tLayout = (LinearLayout)findViewById(R.id.notificationOnOffLayout);

        if(tNotificationHour != ConstsU.NOTIFICATION_NOT_SET
                && tNotificationMinute != ConstsU.NOTIFICATION_NOT_SET){
            tOnOffIv.setImageResource(R.mipmap.ic_check_box_black_24dp);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, tNotificationHour);
            c.set(Calendar.MINUTE, tNotificationMinute);

            SimpleDateFormat tSimpleDateFormat;
            if(android.text.format.DateFormat.is24HourFormat(this) == true){
                tSimpleDateFormat = new SimpleDateFormat("HH:mm");
            }else{
                tSimpleDateFormat = new SimpleDateFormat("hh:mm aa");
            }
            tTextView.setText(tSimpleDateFormat.format(c.getTime()));
        }else{
            tOnOffIv.setImageResource(R.mipmap.ic_check_box_outline_blank_black_24dp);
            tTextView.setText("--:--");
        }
    }
}
