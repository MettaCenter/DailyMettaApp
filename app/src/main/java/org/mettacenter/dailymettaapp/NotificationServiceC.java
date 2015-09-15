package org.mettacenter.dailymettaapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by sunyata on 2015-09-08.
 */
public class NotificationServiceC
    extends IntentService{

    private static final String NOTIFICATION_TAG = "notification_tag";
    private static final int NOTIFICATION_ID = 0;
    //-The same id is always used since we don't want to stack notifications
    private static final int REQ_CODE = 0;

    private static final String TAG = "NotificationServiceC";

    public NotificationServiceC() {
        super(TAG);
    }

    public static void start(Context iContext){
        AlarmManager tAlarmManager = (AlarmManager)iContext.getSystemService(Context.ALARM_SERVICE);

        Intent tIntent = new Intent(iContext, NotificationServiceC.class);
        PendingIntent tAlarmPendingIntent = PendingIntent.getService(iContext, REQ_CODE, tIntent, 0);

        SharedPreferences tSharedPrefs = iContext.getSharedPreferences(
                ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int tNotificationHour = tSharedPrefs.getInt(ConstsU.PREF_INT_NOTIFICATION_HOUR, ConstsU.NOTIFICATION_NOT_SET);
        int tNotificationMinute = tSharedPrefs.getInt(ConstsU.PREF_INT_NOTIFICATION_MINUTE, ConstsU.NOTIFICATION_NOT_SET);


        tAlarmManager.cancel(tAlarmPendingIntent);
        //TODO: Do we want to cancel the pending intent as well?


        if(tNotificationHour != ConstsU.NOTIFICATION_NOT_SET
                && tNotificationMinute != ConstsU.NOTIFICATION_NOT_SET){

            Calendar c = Calendar.getInstance();
            int tPresentHour = c.get(Calendar.HOUR_OF_DAY);
            int tPresentMinute = c.get(Calendar.MINUTE);
            c.set(Calendar.HOUR_OF_DAY, tNotificationHour);
            c.set(Calendar.MINUTE, tNotificationMinute);

            if(tPresentHour > tNotificationHour ||
                    (tPresentHour == tNotificationHour && tPresentMinute >= tNotificationMinute)){
                c.add(Calendar.DAY_OF_YEAR, 1); //-Please note "add"
            }

            tAlarmManager.setRepeating(AlarmManager.RTC, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, tAlarmPendingIntent);
            //-TODO: Exchange to setInexactRepeating (we are now using setRepeating for testing purposes)
        }
    }

    @Override
    protected void onHandleIntent(Intent iIntent) {
        Log.d(ConstsU.APP_TAG, "You have reached NotificationServiceC.onHandleIntent");

        showNotification(this);

    }

    public static void showNotification(Context iContext){



        //Check to see if notifications are enabled..
        SharedPreferences tSharedPreferences = iContext.getSharedPreferences(
                ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        long tHour = tSharedPreferences.getInt(ConstsU.PREF_INT_NOTIFICATION_HOUR, ConstsU.NOTIFICATION_NOT_SET);
        long tMinute = tSharedPreferences.getInt(ConstsU.PREF_INT_NOTIFICATION_MINUTE, ConstsU.NOTIFICATION_NOT_SET);
        if(tHour == ConstsU.NOTIFICATION_NOT_SET || tMinute == ConstsU.NOTIFICATION_NOT_SET){
            //..exit
            return;
        }else{
            //..continue
        }




        String tContentTextSg = "";


        Intent tArticleActivityIntent = new Intent(iContext, ArticleActivityC.class);
        tArticleActivityIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        /*
        From the documentation for Intent.FLAG_ACTIVITY_CLEAR_TOP:
        "
        This launch mode can also be used to good effect in conjunction with
        FLAG_ACTIVITY_NEW_TASK: if used to start the root activity of a task, it will bring any
        currently running instance of that task to the foreground, and then clear it to its root
        state. This is especially useful, for example, when launching an activity from the
        notification manager.
        "
         */
        //-Reference: http://developer.android.com/guide/components/tasks-and-back-stack.html



        String[] tTickerStringAr = iContext.getResources().getStringArray(R.array.ticker_strings);
        Random r = new Random();
        int i = r.nextInt(tTickerStringAr.length);

        //Building the notification
        Notification tNotification = new NotificationCompat.Builder(iContext)
                .setTicker(tTickerStringAr[i])
                .setSmallIcon(R.mipmap.metta_center_wheel)
                .setContentTitle("Daily Metta")
                .setContentText(tContentTextSg)
                .setContentIntent(PendingIntent.getActivity(iContext, 0, tArticleActivityIntent, 0))
                .setAutoCancel(true)
                .build();
        //-An additional feature that would be easy to add would be to show the title of the
        // newest daily metta in one of the text areas. This would be easy to implement since new
        // notifications automatically replace older notifications. The only problem would be that
        // a new article could have appeared in the meantime, which would lead the user to another
        // article than the one mentioned in the notification text
        ///.setContentText("Latest: ___________")

        //Displaying the notification
        NotificationManager tmpNotificationManager = (NotificationManager)iContext.getSystemService(NOTIFICATION_SERVICE);
        tmpNotificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, tNotification);

    }
}
