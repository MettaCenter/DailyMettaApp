package org.mettacenter.dailymettaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sunyata on 2015-09-08.
 *
 * Please note that this will not handle the cases before the device has been restarted, therefore we use other classes (NotificationServiceC and _______)
 */
public class BootReceiverC
        extends BroadcastReceiver {


    @Override
    public void onReceive(Context iContext, Intent iIntent) {


        if(iIntent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")){



            //Setting the alarm for..

            //..notifications

            NotificationServiceC.setServiceNotification(iContext);



            //..checking for feed updates



        }

    }
}
