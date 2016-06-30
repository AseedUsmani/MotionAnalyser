package com.example.ghostriley.motionanalyser;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by GhostRiley on 29/06/2016.
 */
public class notiServices extends IntentService {

    public notiServices() {
        super("notiServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(notiServices.this, "onHandleIntent called", Toast.LENGTH_SHORT).show();
        clearNotification();
        Toast.makeText(notiServices.this, "onHandleIntent called", Toast.LENGTH_SHORT).show();
    }

    public void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        Intent intent = new Intent(notiServices.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
