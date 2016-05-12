package com.apps.my.liener;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
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

public class BubbleService extends Service{
    WindowManager bubbleWindow;
    ImageView bubbleHead;
    Context context =this ;
    WindowManager.LayoutParams paramBubble,paramBrowser;
    boolean is_open = false;
    int paramx = 0 ,paramy =0,count=1, current=0,bubbleWidth;
    BrowserPage browserPageArray[] = new BrowserPage[20];


    @Override
    public void onCreate() {
        current=0;
        count=0;
        super.onCreate();

        bubbleWindow = (WindowManager) getSystemService(WINDOW_SERVICE);
        context = this;

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
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        paramBrowser.gravity = Gravity.BOTTOM | Gravity.RIGHT;

        bubbleWindow.addView(browserPageArray[0].bubbleHead, paramBubble);

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

                    bubbleWindow.addView(browserPageArray[current].browser,paramBrowser);

                    for (int i = 1; i < count; i++) {
                        final int j = i;
                        bubbleWindow.addView(browserPageArray[i].bubbleHead, paramBubble);
                        browserPageArray[i].bubbleHead.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(j!=current){
                                    browserPageArray[current].bubbleHead.setImageResource(R.mipmap.bubblesmall);
                                    browserPageArray[j].bubbleHead.setImageResource(R.mipmap.bubble);
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
                        bubbleWindow.addView(browserPageArray[0].browser,paramBrowser);
                        bubbleWindow.removeView(browserPageArray[current].browser);
                        browserPageArray[0].bubbleHead.setImageResource(R.mipmap.bubble);
                        browserPageArray[current].bubbleHead.setImageResource(R.mipmap.bubblesmall);
                        current=0;
                    }
                }
            }
        });
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