package com.apps.my.liener;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;


public class BubbleService extends Service {

    private static final String TAG = BubbleService.class.getSimpleName();

    Context context = this;

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return true;
    }

    Browser browser;

    @Override
    public void onCreate() {
        super.onCreate();
        browser = new Browser(context);
    }


    public void onDestroy() {
        super.onDestroy();
        browser.finish();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!("" + intent + "").equals("null")) {
            String url = intent.getStringExtra("url");
            browser.addTab(this, url);
            setNotification();// Notification in notification panel to keep the activity running in background
            if (intent.getBooleanExtra("isRecent", false)) {
                browser.expandBrowser(null);
            }
        }
        return Service.START_STICKY;
    }

    public void setNotification() {
        String text = "By SDSMDG";
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.bubble)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("Liener is Running")  // the label
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when clicked
                .build();
        startForeground(1, notification);
    }

}
