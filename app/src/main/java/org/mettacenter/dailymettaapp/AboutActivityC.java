package org.mettacenter.dailymettaapp;

import android.app.DialogFragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by sunyata on 2015-09-04.
 */
public class AboutActivityC
        extends AppCompatActivity {
    @Override
    public void onCreate(Bundle iSavedInstanceState){
        super.onCreate(iSavedInstanceState);
        setContentView(R.layout.activity_about);

        PackageInfo tPackageInfo = null;
        try {
            tPackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(ConstsU.TAG, e.getMessage());
        }

        if(tPackageInfo != null){
            TextView tVersionTv = (TextView)findViewById(R.id.version);
            tVersionTv.setText("App version: " + tPackageInfo.versionName);
        }
    }
}
