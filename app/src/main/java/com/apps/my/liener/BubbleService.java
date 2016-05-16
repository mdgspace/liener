package com.apps.my.liener;

import android.app.ActionBar;
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
    String TAG = "testing";
    WindowManager.LayoutParams paramBubble , paramBrowser;
    boolean is_open = false,is_running;
    int paramx = 0 ,paramy =0,count=1, current=0,bubbleWidth , heightNew, widthMid ;
    BrowserPage browserPageArray[] = new BrowserPage[20];
    int arrIndex[] = new int[20];

    public boolean onKey(View v, int keyCode, KeyEvent event){
        return true;
    }

    @Override
    public void onCreate() {
        current=0; count=0;
        super.onCreate();
        context = this;


        for(int i=0; i<20 ; i++){
            arrIndex[i]= i;
        }

        bubbleWindow = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = bubbleWindow.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        heightNew = (int)((size.y*100)/128);
        widthMid = (int)(size.x/2);

        browserPageArray[arrIndex[0]] = new BrowserPage();
        browserPageArray[arrIndex[0]].setBrowserPage(context);
        browserPageArray[arrIndex[0]].browser.loadUrl("http://google.co.in");

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

        addListenerBubble();


    }

    public void addListenerBubble() {
        browserPageArray[arrIndex[0]].bubbleHead.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY, y, x;
            ImageView delete;
            WindowManager.LayoutParams paramDelete;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!is_open) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = paramBubble.x;
                            initialY = paramBubble.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();

                            delete = new ImageView(context);
                            delete.setImageResource(R.mipmap.delete);
                            paramDelete = new WindowManager.LayoutParams(
                                    WindowManager.LayoutParams.WRAP_CONTENT,
                                    WindowManager.LayoutParams.WRAP_CONTENT,
                                    WindowManager.LayoutParams.TYPE_TOAST,
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                    PixelFormat.TRANSLUCENT
                            );
                            paramDelete.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                            paramDelete.x = widthMid;
                            paramDelete.y = (int) (heightNew / 4);
                            Log.d("testing", "addview1");
                            bubbleWindow.addView(delete, paramDelete);

                            return false;
                        case MotionEvent.ACTION_UP:
                            Log.d("testing", "removeView2");
                            bubbleWindow.removeView(delete);
                            y = (paramBubble.y - (int) (heightNew / 4));
                            x = (paramBubble.x - widthMid);
                            if ((x > -100 && x < 100) && (y > -100 && y < 100)) {
                                is_running = false;
                                Log.d(TAG, "stopself");
                                stopSelf();
                                return true;
                            }
                            return false;
                        case MotionEvent.ACTION_MOVE:
                            paramBubble.x = initialX
                                    - (int) (event.getRawX() - initialTouchX);
                            paramBubble.y = initialY
                                    - (int) (event.getRawY() - initialTouchY);
                            //Log.d("testing","update layout1"); bubbleWindow.updateViewLayout(browserPageArray[arrIndex[0]].bubbleHead, paramBubble);

                            y = (paramBubble.y - (int) (heightNew / 4));
                            x = (paramBubble.x - widthMid);
                            if ((x > -100 && x < 100) && (y > -100 && y < 100)) {
                                Log.d(TAG, "action_move inside () called with: " + "v = [" + v + "], event = [" + event + "], height = [" + heightNew + "], width = [" + widthMid + "]");
                                paramBubble.x = widthMid;
                                paramBubble.y = (int) (heightNew / 4);

                                Log.d("testing", "removeView3");
                                bubbleWindow.removeView(delete);
                                Log.d("testing", "update layout4");
                                bubbleWindow.updateViewLayout(browserPageArray[arrIndex[0]].bubbleHead, paramBubble);
                                Log.d("testing", "addview2");
                                bubbleWindow.addView(delete, paramDelete);
                            } else {
                                //paramBubble.y=heightNew + 100;
                                Log.d("testing", "update layout5");
                                bubbleWindow.updateViewLayout(browserPageArray[arrIndex[0]].bubbleHead, paramBubble);
                                //paramBubble.y=heightNew;
                            }

                            return false;
                    }
                }
                return false;
            }
        });
        browserPageArray[0].bubbleHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_running) {
                    Log.d("TESTING", "in 0 click");
                    if (!is_open) {
                        paramBubble.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                        paramx = paramBubble.x;
                        paramy = paramBubble.y;
                        paramBubble.x = 0;
                        paramBubble.y = heightNew;
                        if (current == 0) {
                            browserPageArray[arrIndex[0]].bubbleHead.setImageResource(R.mipmap.bubble);
                        }
                        Log.d("testing", "update layout2");
                        bubbleWindow.updateViewLayout(browserPageArray[arrIndex[0]].bubbleHead, paramBubble);
                        bubbleWidth = browserPageArray[arrIndex[0]].bubbleHead.getWidth();
                        Log.d(TAG, " " + "bubblewidth = [" + bubbleWidth + "]");
                        //paramBubble.x = paramBubble.x + bubbleWidth;
                        addBrowser(current);

                        for (int i = 0; i < count; i++) {
                            //final int j = i;
                            addBubble(i);
                            if (i != 0) {
                                paramBubble.x = paramBubble.x + bubbleWidth;
                                Log.d("testing", "addview3");
                                bubbleWindow.addView(browserPageArray[arrIndex[i]].bubbleHead, paramBubble);
                            }
                        }
                        is_open = true;
                    } else {
                        Log.d(TAG, "min 0");
                        minimizeBrowser(0);
                    }
                }
            }
        });
    }


    public void addBrowser(final int index){
        browserPageArray[arrIndex[index]].browser.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (browserPageArray[arrIndex[index]].browser.canGoBack()) {
                        browserPageArray[arrIndex[index]].browser.goBack();
                    } else {
                        browserPageArray[arrIndex[index]].bubbleHead.performClick();
                    }
                }
                return false;
            }
        });
        Log.d("testing", "addview4"); bubbleWindow.addView(browserPageArray[arrIndex[index]].browser, paramBrowser);
    }

    public void addBubble(final int index){


        browserPageArray[arrIndex[index]].bubbleHead.setOnTouchListener(new View.OnTouchListener() {
            int initialX;
            int initialY,y,x,z;
            float initialTouchX;
            float initialTouchY;
            ImageView delete;
            WindowManager.LayoutParams paramDelete;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "in Action_down");
                        z=paramBubble.x;
                        initialX = index*bubbleWidth;
                        Log.d(TAG, "ontouch action down: " + "index = [" + index + "], initialX = [" + initialX + "]");
                        initialY= heightNew;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        
                        Log.d("testing", "removeView1"); bubbleWindow.removeView(browserPageArray[arrIndex[current]].browser);
                        delete = new ImageView(context);
                        delete.setImageResource(R.mipmap.delete);
                        paramDelete = new WindowManager.LayoutParams(
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.TYPE_TOAST,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                PixelFormat.TRANSLUCENT
                        );
                        paramDelete.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                        paramDelete.x = widthMid;
                        paramDelete.y = (int) (heightNew / 4);
                        Log.d("testing","addview5"); bubbleWindow.addView(delete, paramDelete);
                        return false;
                    case MotionEvent.ACTION_UP:
                        Log.d("testing", "removeView2"); bubbleWindow.removeView(delete);
                        y = (paramBubble.y - (int) (heightNew / 4));
                        x = (paramBubble.x - widthMid);
                        if ((x > -100 && x < 100) && (y > -100 && y < 100)) {
                            Log.d("TESTING", "deleted");
                            deletePage(index);
                        }
                        else {
                            paramBubble.x = initialX;
                            paramBubble.y = heightNew;
                            Log.d("testing","update layout3"); bubbleWindow.updateViewLayout(browserPageArray[arrIndex[index]].bubbleHead, paramBubble);
                            Log.d(TAG, "onTouch() action up called with: " + "v = [" + v + "], event = [" + event + "]");
                            Log.d("testing","addview6"); bubbleWindow.addView(browserPageArray[arrIndex[current]].browser, paramBrowser);
                        }
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        paramBubble.x = initialX
                                - (int) (event.getRawX() - initialTouchX);
                        paramBubble.y = initialY
                                - (int) (event.getRawY() - initialTouchY);
                        y = (paramBubble.y - (int) (heightNew / 4));
                        x = (paramBubble.x - widthMid);
                        Log.d(TAG, "action_move () called with: " + "v = [" + v + "], height = [" + heightNew + "], width = [" + widthMid + "], x = [" + x + "], y = [" + y + "], event = [" + event + "]");
                        if ((x > -100 && x < 100) && (y > -100 && y < 100)) {
                            Log.d(TAG, "action_move inside () called with: " + "v = [" + v + "], event = [" + event + "], height = [" + heightNew + "], width = [" + widthMid + "]");
                            paramBubble.x = widthMid;
                            paramBubble.y = (int) (heightNew / 4);

                            Log.d("testing", "removeView3"); bubbleWindow.removeView(delete);
                            Log.d("testing", "update layout4"); bubbleWindow.updateViewLayout(browserPageArray[arrIndex[index]].bubbleHead, paramBubble);
                            Log.d("testing","addview7"); bubbleWindow.addView(delete, paramDelete);
                        } else {
                            //paramBubble.y=heightNew + 100;
                            Log.d("testing", "update layout5"); bubbleWindow.updateViewLayout(browserPageArray[arrIndex[index]].bubbleHead, paramBubble);
                            //paramBubble.y=heightNew;
                        }



                        return false;
                    case MotionEvent.ACTION_CANCEL:
                        Log.d(TAG, "onTouch() cancel called with: " + "v = [" + v + "], event = [" + event + "]");
                        return false;
                }

                return false;
            }
        });

        browserPageArray[arrIndex[index]].bubbleHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "min " + "index = [" + index + "]");
                minimizeBrowser(index);
            }
        });
    }

    public void minimizeBrowser(int index){
        if(current==index) {
            for (int i = 1; i < count; i++) {
                Log.d("TESTING", "for loop removeView in minimize");
                Log.d("testing", "removeView4"); bubbleWindow.removeView(browserPageArray[arrIndex[i]].bubbleHead);
            }
            paramBubble.x = paramx;
            paramBubble.y = paramy;
            browserPageArray[arrIndex[0]].bubbleHead.setImageResource(R.mipmap.bubblesmall);
            Log.d("testing", "removeView5" + " current = [" + current + "]"); bubbleWindow.removeView(browserPageArray[arrIndex[current]].browser);
            Log.d("testing", "update layout6"); bubbleWindow.updateViewLayout(browserPageArray[arrIndex[0]].bubbleHead, paramBubble);
            addListenerBubble();
            is_open = false;
        }
        else{
            paramBubble.x = index*bubbleWidth;
            paramBubble.y = heightNew;
            addBrowser(index);
            Log.d("testing", "removeView6"); bubbleWindow.removeView(browserPageArray[arrIndex[current]].browser);
            browserPageArray[arrIndex[index]].bubbleHead.setImageResource(R.mipmap.bubble);
            browserPageArray[arrIndex[current]].bubbleHead.setImageResource(R.mipmap.bubblesmall);
            current=index;
        }
    }

    public void deletePage(int index) {
        Log.d("TESTING", "deletepage called");

        int temp = arrIndex[index];
        for (int i = index; i < count - 1; i++) {
            arrIndex[i] = arrIndex[i + 1];
        }
        arrIndex[count - 1] = temp;
        count--;

        Log.d("testing", "removeView7" + "arrindex =");
        bubbleWindow.removeView(browserPageArray[arrIndex[count]].bubbleHead);
        browserPageArray[arrIndex[current]].bubbleHead.setImageResource(R.mipmap.bubble);
        if (current >= count) {
            current--;
            browserPageArray[arrIndex[0]].bubbleHead.setImageResource(R.mipmap.bubble);
        }
        paramBubble.x = index * bubbleWidth;
        paramBubble.y = heightNew;
        for (int i = index; i < count; i++) {
            addBubble(i);
            Log.d("testing", "update layout7");
            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[i]].bubbleHead, paramBubble);
            paramBubble.x = paramBubble.x + bubbleWidth;
        }

        if (current >= 0) {
            Log.d("testing", "addview8");
            bubbleWindow.addView(browserPageArray[arrIndex[current]].browser, paramBrowser);
        }
        else{
            stopSelf();
        }
    }



    public void onDestroy() {
        Log.d(TAG, "onDestroy() called with: " + "");
        super.onDestroy();
        if(is_open){
            if(current>=0) {
                browserPageArray[arrIndex[current]].bubbleHead.performClick();
            }
            bubbleWindow.removeView( browserPageArray[arrIndex[0]].bubbleHead);
        }
        else{
            bubbleWindow.removeView( browserPageArray[arrIndex[0]].bubbleHead);
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
            is_running = true;
            if(count==0){
                String url = intent.getStringExtra("url");
                browserPageArray[arrIndex[0]].browser.loadUrl(url);
                Log.d("testing", "addview9"); bubbleWindow.addView(browserPageArray[arrIndex[0]].bubbleHead, paramBubble);
            }
            else{
                String url = intent.getStringExtra("url");
                if(is_open){
                    browserPageArray[current].bubbleHead.performClick();
                }
                browserPageArray[arrIndex[count]]=new BrowserPage();
                browserPageArray[arrIndex[count]].setBrowserPage(context);
                browserPageArray[arrIndex[count]].browser.loadUrl(url);
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
