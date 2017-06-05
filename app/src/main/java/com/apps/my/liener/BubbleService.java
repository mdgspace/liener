package com.apps.my.liener;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;

import java.util.logging.LogManager;

import static com.apps.my.liener.Constant.BubbleSizeDelete;


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
            if(intent.getBooleanExtra("isRecent",false)){
                browser.expandBrowser();
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
