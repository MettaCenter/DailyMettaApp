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

    public static void setServiceNotification(Context iContext){

        boolean tIsNotificationActive = false;

        AlarmManager tAlarmManager = (AlarmManager)iContext.getSystemService(Context.ALARM_SERVICE);

        Intent tIntent = new Intent(iContext, NotificationServiceC.class);
        PendingIntent tAlarmPendingIntent = PendingIntent.getService(iContext, REQ_CODE, tIntent, 0);


        SharedPreferences tSharedPrefs = iContext.getSharedPreferences(
                ConstsU.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int tNotificationHour = tSharedPrefs.getInt(ConstsU.PREF_NOTIFICATION_HOUR, ConstsU.NOTIFICATION_NOT_SET);
        int tNotificationMinute = tSharedPrefs.getInt(ConstsU.PREF_NOTIFICATION_MINUTE, ConstsU.NOTIFICATION_NOT_SET);

        if(tNotificationHour == ConstsU.NOTIFICATION_NOT_SET
                || tNotificationMinute == ConstsU.NOTIFICATION_NOT_SET){
            tAlarmManager.cancel(tAlarmPendingIntent);
        }else{
            Calendar c = Calendar.getInstance();
            int tPresentHour = c.get(Calendar.HOUR_OF_DAY);
            int tPresentMinute = c.get(Calendar.MINUTE);
            c.set(Calendar.HOUR_OF_DAY, tNotificationHour);
            c.set(Calendar.MINUTE, tNotificationMinute);

            if(tPresentHour > tNotificationHour ||
                    (tPresentHour == tNotificationHour && tPresentMinute >= tNotificationMinute)){
                c.add(Calendar.DAY_OF_YEAR, 1); //-Please note "add"
            }

            tAlarmManager.cancel(tAlarmPendingIntent);
            tAlarmManager.setRepeating(AlarmManager.RTC, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, tAlarmPendingIntent);
            //-TODO: Exchange to setInexactRepeating (using setRepeating for testing purposes)

            //TODO: Do we want to use this: tAlarmPendingIntent.cancel();
        }
    }

    @Override
    protected void onHandleIntent(Intent iIntent) {
        Log.d(ConstsU.TAG, "You have reached NotificationServiceC.onHandleIntent");



        //////////////////////////


        Intent tArticleActivityIntent = new Intent(this, ArticleActivityC.class);
        tArticleActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //-TODO: Change how the activity is started?
        // http://stackoverflow.com/questions/21833402/difference-between-intent-flag-activity-clear-task-and-intent-flag-activity-task


        //Building the notification
        Notification tNotification = new NotificationCompat.Builder(this)
                .setTicker("Read today's Daily Metta!")
                .setSmallIcon(R.mipmap.metta_center_wheel)
                .setContentTitle("Daily Metta")
                .setContentIntent(PendingIntent.getActivity(this, 0, tArticleActivityIntent, 0))
                .setAutoCancel(true)
                .build();
        //-An additional feature that would be easy to add would be to show the title of the
        // newest daily metta in one of the text areas. This would be easy to implement since new
        // notifications automatically replace older notifications. The only problem would be that
        // a new article could have appeared in the meantime, which would lead the user to another
        // article than the one mentioned in the notification text
        ///.setContentText("Latest: ___________")

        //Displaying the notification
        NotificationManager tmpNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        tmpNotificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, tNotification);
    }
}
