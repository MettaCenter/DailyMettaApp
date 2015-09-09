package org.mettacenter.dailymettaapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by sunyata on 2015-09-08.
 */
public class BackgroundDownloadServiceC
        extends IntentService{

    private static final String TAG = "BackgroundDownloadServiceC";
    private static final int REQ_CODE = 1;

    public BackgroundDownloadServiceC() {
        super(TAG);
    }

    public static void setService(Context iContext) {

        Log.i(ConstsU.TAG, "BackgroundDownloadServiceC.setService");

        Intent tIntent = new Intent(iContext, BackgroundDownloadServiceC.class);
        AlarmManager tAlarmManager = (AlarmManager)iContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent tAlarmPendingIntent = PendingIntent.getService(iContext, REQ_CODE, tIntent, 0);
        tAlarmManager.cancel(tAlarmPendingIntent);
        tAlarmManager.setRepeating(
                AlarmManager.RTC,
                Calendar.getInstance().getTimeInMillis() + 20 * 60,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                tAlarmPendingIntent);

    }


    /*
     * Code is run here, since this is already a background thread we don't need (in fact we
     * cannot) run the code in FetchArticlesTask. See this link for more info:
     * http://stackoverflow.com/questions/31750269/intentservice-class-not-running-asynctask-on-main-ui-thread-method-execute-must
     */
    @Override
    protected void onHandleIntent(Intent iIntent) {

        Log.i(ConstsU.TAG, "BackgroundDownloadServiceC.onHandleIntent");

        UtilitiesU.DownloadActionEnum tDownloadActionEnum = UtilitiesU.downloadLogic(this);
        if(tDownloadActionEnum == UtilitiesU.DownloadActionEnum.DOWNLOAD_ARTICLES){
            UtilitiesU.downloadArticles(this);
        }

    }

}
