package org.mettacenter.dailymettaapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

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

    public static void start(Context iContext) {
        Log.d(ConstsU.APP_TAG, "BackgroundDownloadServiceC.start");

        Intent tIntent = new Intent(iContext, BackgroundDownloadServiceC.class);
        AlarmManager tAlarmManager = (AlarmManager)iContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent tAlarmPendingIntent = PendingIntent.getService(iContext, REQ_CODE, tIntent, 0);
        tAlarmManager.cancel(tAlarmPendingIntent);
        tAlarmManager.setInexactRepeating(
                AlarmManager.RTC,
                Calendar.getInstance().getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                tAlarmPendingIntent);
        //-TODO: Change interval, 15 minutes is only for testing purposes
    }

    /*
     * Code is run here, since this is already a background thread we don't need (in fact we
     * cannot) run the code in FetchArticlesTask. See this link for more info:
     * http://stackoverflow.com/questions/31750269/intentservice-class-not-running-asynctask-on-main-ui-thread-method-execute-must
     */
    @Override
    protected void onHandleIntent(Intent iIntent) {
        Log.d(ConstsU.APP_TAG, "BackgroundDownloadServiceC.onHandleIntent");

        UtilitiesU.DownloadActionEnum tDownloadActionEnum = UtilitiesU.downloadLogic(this);
        if(tDownloadActionEnum == UtilitiesU.DownloadActionEnum.START_DOWNLOAD_OF_ARTICLES){
            UtilitiesU.downloadArticles(this);
        }
    }
}
