package com.example.ghostriley.motionanalyser;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by GhostRiley on 29/06/2016.
 */
public class notiServices extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //When clicked "No" in notifications, clear Memory and clear notification.
        final SharedPreferences sharedPreferences = context.getSharedPreferences("Data", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(0);
    }
}
