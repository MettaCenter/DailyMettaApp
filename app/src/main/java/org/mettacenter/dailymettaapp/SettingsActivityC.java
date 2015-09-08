package org.mettacenter.dailymettaapp;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

/**
 * Created by sunyata on 2015-09-04.
 */
public class SettingsActivityC
        extends AppCompatActivity {

    @Override
    public void onCreate(Bundle iSavedInstanceState){
        super.onCreate(iSavedInstanceState);
        setContentView(R.layout.activity_settings);

        CheckBox tNotificationCb = (CheckBox)findViewById(R.id.notificationCheckBox);
        tNotificationCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(((CheckBox)v).isChecked() == true){
                    DialogFragment tTimePickerFragment = new TimePickerFragmentC();
                    tTimePickerFragment.show(
                            SettingsActivityC.this.getFragmentManager(),
                            "TimePicker");
                }

            }
        });

    }

}
