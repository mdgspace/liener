package com.apps.my.liener;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
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

public class BubbleService extends Service implements OnKeyListener {
    WindowManager bubbleWindow;
    Context context =this ;
    String TAG = "BUBBLETEST";
    WindowManager.LayoutParams paramBubble , paramBrowser;
    boolean is_open = false;
    int paramx = 0 ,paramy =0,count=1, current=0,bubbleWidth;
    BrowserPage browserPageArray[] = new BrowserPage[20];

    public boolean onKey(View v, int keyCode, KeyEvent event){
        Log.d(TAG, "onKey() called with: " + "");
        return true;
    }

    @Override
    public void onCreate() {
        current=0;
        count=0;
        super.onCreate();
        context = this;

        bubbleWindow = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = bubbleWindow.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        final int heightNew = (int)((height*100)/128);

        browserPageArray[0] = new BrowserPage();
        browserPageArray[0].setBrowserPage(context);
        browserPageArray[0].browser.loadUrl("http://google.co.in");

        paramBubble = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        paramBubble.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        paramBubble.x = 0;
        paramBubble.y = heightNew;

        paramBrowser = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FILL_PARENT,
                heightNew,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        paramBrowser.gravity = Gravity.BOTTOM | Gravity.RIGHT;

        browserPageArray[0].bubbleHead.setOnTouchListener(new View.OnTouchListener() {
            int initialX;
            int initialY;
            float initialTouchX;
            float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!is_open) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = paramBubble.x;
                            initialY = paramBubble.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return false;
                        case MotionEvent.ACTION_UP:
                            return false;
                        case MotionEvent.ACTION_MOVE:
                            paramBubble.x = initialX
                                    - (int) (event.getRawX() - initialTouchX);
                            paramBubble.y = initialY
                                    - (int) (event.getRawY() - initialTouchY);
                            bubbleWindow.updateViewLayout(browserPageArray[0].bubbleHead, paramBubble);
                            return false;
                    }
                }
                return false;
            }
        });

        browserPageArray[0].bubbleHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TESTING","in click");
                if (!is_open) {
                    paramBubble.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                    paramx = paramBubble.x;
                    paramy = paramBubble.y;
                    paramBubble.x = 0;
                    paramBubble.y = heightNew;
                    if(current==0){
                        browserPageArray[0].bubbleHead.setImageResource(R.mipmap.bubble);
                    }
                    bubbleWindow.updateViewLayout(browserPageArray[0].bubbleHead, paramBubble);
                    bubbleWidth=browserPageArray[0].bubbleHead.getWidth();
                    paramBubble.x = paramBubble.x + bubbleWidth;

                    browserPageArray[current].browser.setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            Log.d(TAG, "onKey() called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
                            //This is the filter
                            if (event.getAction() != KeyEvent.ACTION_DOWN)
                                return false;

                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                Log.d(TAG, "onKey() inside called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
                                if(browserPageArray[current].browser.canGoBack()){
                                    browserPageArray[current].browser.goBack();
                                }
                                else {
                                    browserPageArray[current].bubbleHead.performClick();
                                }
                            }
                            return false;
                        }
                    });
                    bubbleWindow.addView(browserPageArray[current].browser, paramBrowser);

                    for (int i = 1; i < count; i++) {
                        final int j = i;
                        bubbleWindow.addView(browserPageArray[i].bubbleHead, paramBubble);
                        browserPageArray[i].bubbleHead.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(j!=current){
                                    browserPageArray[current].bubbleHead.setImageResource(R.mipmap.bubblesmall);
                                    browserPageArray[j].bubbleHead.setImageResource(R.mipmap.bubble);
                                    browserPageArray[j].browser.setOnKeyListener(new OnKeyListener() {
                                        @Override
                                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                                            Log.d(TAG, "onKey() called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
                                            //This is the filter
                                            if (event.getAction() != KeyEvent.ACTION_DOWN)
                                                return false;

                                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                Log.d(TAG, "onKey() inside called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
                                                if(browserPageArray[j].browser.canGoBack()){
                                                    browserPageArray[j].browser.goBack();
                                                }
                                                else {
                                                    browserPageArray[j].bubbleHead.performClick();
                                                }
                                            }
                                            return false;
                                        }
                                    });
                                    bubbleWindow.addView(browserPageArray[j].browser, paramBrowser);
                                    bubbleWindow.removeView(browserPageArray[current].browser);
                                    current=j;
                                }
                                else{
                                    for (int i = 1; i < count; i++) {
                                        bubbleWindow.removeView(browserPageArray[i].bubbleHead);
                                    }
                                    paramBubble.x = paramx;
                                    paramBubble.y = paramy;
                                    bubbleWindow.removeView(browserPageArray[current].browser);
                                    bubbleWindow.updateViewLayout(browserPageArray[0].bubbleHead, paramBubble);
                                    is_open = false;
                                }
                            }
                        });
                        paramBubble.x = paramBubble.x + bubbleWidth;
                    }
                    is_open = true;
                }
                else{
                   if(current==0) {
                        for (int i = 1; i < count; i++) {
                            bubbleWindow.removeView(browserPageArray[i].bubbleHead);
                        }
                        paramBubble.x = paramx;
                        paramBubble.y = paramy;
                        browserPageArray[0].bubbleHead.setImageResource(R.mipmap.bubblesmall);
                        bubbleWindow.removeView(browserPageArray[current].browser);
                        bubbleWindow.updateViewLayout(browserPageArray[0].bubbleHead, paramBubble);
                        is_open = false;
                    }
                    else{
                       browserPageArray[0].browser.setOnKeyListener(new OnKeyListener() {
                           @Override
                           public boolean onKey(View v, int keyCode, KeyEvent event) {
                               Log.d(TAG, "onKey() called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
                               if (event.getAction() != KeyEvent.ACTION_DOWN)
                                   return false;
                               if (keyCode == KeyEvent.KEYCODE_BACK) {
                                   Log.d(TAG, "onKey() inside called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
                                   if(browserPageArray[0].browser.canGoBack()){
                                       browserPageArray[0].browser.goBack();
                                   }
                                   else {
                                       browserPageArray[0].bubbleHead.performClick();
                                   }
                               }
                               return false;
                           }
                       });
                        bubbleWindow.addView(browserPageArray[0].browser,paramBrowser);
                        bubbleWindow.removeView(browserPageArray[current].browser);
                        browserPageArray[0].bubbleHead.setImageResource(R.mipmap.bubble);
                        browserPageArray[current].bubbleHead.setImageResource(R.mipmap.bubblesmall);
                        current=0;
                    }
                }
            }
        });

        bubbleWindow.addView(browserPageArray[0].bubbleHead, paramBubble);
    }

    public void onDestroy() {
        super.onDestroy();
        if(is_open){
            browserPageArray[0].bubbleHead.performClick();
        }
        if (browserPageArray[0].bubbleHead != null) {
            bubbleWindow.removeView(browserPageArray[0].bubbleHead);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        if(("[" + intent + "]").equals("[null]")){}
        else{
            if(count==0){}
            else{
                String url = intent.getStringExtra("url");
                browserPageArray[count]=new BrowserPage();
                browserPageArray[count].setBrowserPage(context);
                browserPageArray[count].browser.loadUrl(url);
            }
            count++;
        }

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
        return Service.START_STICKY;
    }
}
