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
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

public class BubbleService extends Service implements OnKeyListener,View.OnTouchListener,View.OnFocusChangeListener {
    WindowManager bubbleWindow;
    Context context =this ;
    boolean onRightSide =true;
    String TAG = "BubbleService";
    WindowManager.LayoutParams paramBubble , paramBrowser;
    boolean is_open = false,is_running;
    int paramx = 0 ,paramy =0,count=1, current=0,bubbleWidth , heightNew, widthMid ;

    BrowserPage browserPageArray[] = new BrowserPage[20];
    int arrIndex[] = new int[20];
    WindowManager.LayoutParams paramDelete;
    ImageView delete;
    BubbleHead bh;
    HomeWatcher mHomeWatcher;

    public boolean onKey(View v, int keyCode, KeyEvent event){
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initVariables();
        initDelete();
        initParam();
        initHomeListener();
    }

    public void initVariables(){
        current=0; count=0;
        context = this;
        bh = new BubbleHead(context);
        for(int i=0; i<20 ; i++){
            arrIndex[i]= i;
        }

        bubbleWindow = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = bubbleWindow.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        heightNew = (int)((size.y*100)/128);
        widthMid = (int)(size.x/2);
    }

    public void initParam(){
        paramBubble = new WindowManager.LayoutParams(   WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        paramBubble.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        paramBubble.x = 0;
        paramBubble.y = heightNew;

        paramBrowser = new WindowManager.LayoutParams(  WindowManager.LayoutParams.FILL_PARENT,
                heightNew,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |  WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        paramBrowser.gravity = Gravity.BOTTOM | Gravity.RIGHT;
    }


    public void initDelete(){
        paramDelete = new WindowManager.LayoutParams( WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT );
        paramDelete.gravity = Gravity.BOTTOM | Gravity.CENTER;

        paramDelete.y = (int) (heightNew / 4);
        paramDelete.width = 200;        Log.d("testing", "addview1");
        delete = new ImageView(context);
        delete.setImageResource(R.mipmap.delete);
    }


    public void onDestroy() {
        Log.d(TAG, "onDestroy() called with: " + "");
        super.onDestroy();
        if(is_open){
            if(current>=0) {
                browserPageArray[arrIndex[current]].bubbleHead.performClick();
            }
            //bubbleWindow.removeView(bh.bubbleHead);
        }
        else{
            bubbleWindow.removeView(bh.bubbleHead);
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
            String url = intent.getStringExtra("url");
            addNewPage(url);
        }
        setNotification();
        return Service.START_STICKY;
    }

    public void addNewPage(String url){
        is_running = true;
        browserPageArray[arrIndex[count]]=new BrowserPage(context);
        browserPageArray[arrIndex[count]].loadUrl(url);
        if(count==0){
            setBubbleHead();
            Log.d("testing", "addview9");
            bubbleWindow.addView(bh.bubbleHead, paramBubble);
        }
        else{
            if(is_open){
                browserPageArray[current].bubbleHead.performClick();
            }
        }
        count++;
    }

    public void setNotification(){
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


    public void setBubbleHead() {
        bh.bubbleHead.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY, y, x;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!is_open) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = paramBubble.x;           initialY = paramBubble.y;
                            initialTouchX = event.getRawX();    initialTouchY = event.getRawY();
                            bubbleWindow.addView(delete, paramDelete);
                            return false;
                        case MotionEvent.ACTION_MOVE:
                            if(onRightSide){
                                paramBubble.x = initialX - (int)(event.getRawX() - initialTouchX);
                            }
                            else{
                                paramBubble.x = initialX + (int)(event.getRawX() - initialTouchX);
                            }
                            paramBubble.y = initialY - (int)(event.getRawY() - initialTouchY);
                            if (ondelete(delete.getWidth())){
                                Log.d(TAG, "action_move inside () called with: " + "v = [" + v + "], event = [" + event + "], height = [" + heightNew + "], width = [" + widthMid + "]");
                                Log.d("testing", "removeView3");
                                bubbleWindow.removeView(delete);            Log.d("testing", "update layout4");
                                bubbleWindow.updateViewLayout(bh.bubbleHead, paramDelete);       Log.d("testing", "addview2");
                                bubbleWindow.addView(delete, paramDelete);
                            } else {
                                bubbleWindow.updateViewLayout(bh.bubbleHead, paramBubble);
                            }
                            return false;
                        case MotionEvent.ACTION_UP:
                            Log.d("testing", "actionupfor0");
                            bubbleWindow.removeView(delete);
                            if (ondelete(delete.getWidth())) {
                                is_running = false;
                                Log.d(TAG, "stopself");
                                stopSelf();
                                return true;
                            }
                            else  {
                                if(paramBubble.x>widthMid){
                                    if(onRightSide){
                                        paramBubble.gravity = Gravity.BOTTOM | Gravity.LEFT;
                                        onRightSide=false;
                                    }
                                    else {
                                        paramBubble.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                                        onRightSide=true;
                                    }
                                }
                                paramBubble.x=0;
                                bubbleWindow.updateViewLayout(bh.bubbleHead, paramBubble);
                                Log.d(TAG, "else in action_up");
                            }
                            return false;
                    }
                }
                return false;
            }
        });
        bh.bubbleHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_running) {
                    Log.d("TESTING", "in 0 click");
                    if (!is_open) {
                        mHomeWatcher.startWatch();
                        paramBubble.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                        paramx = paramBubble.x;
                        paramy = paramBubble.y;
                        paramBubble.x = 0;
                        paramBubble.y = heightNew;
                        if (current == 0) {
                            browserPageArray[arrIndex[0]].bubbleHead.setImageResource(R.mipmap.bubble);
                        }
                        Log.d("testing", "update layout2");
                        bubbleWindow.removeView(bh.bubbleHead);
                        bubbleWidth = bh.bubbleHead.getWidth();
                        Log.d(TAG, " " + "bubblewidth = [" + bubbleWidth + "]");
                        addBrowser(current);
                        for (int i = 0; i < count; i++) {
                            //final int j = i;
                            addBubble(i);
                            Log.d(TAG, "addbubble(1) called with: v = [" + v + "]");
                            if (i != 0) {
                                paramBubble.x = paramBubble.x + bubbleWidth;
                                Log.d("testing", "addview3");
                            }
                            bubbleWindow.addView(browserPageArray[arrIndex[i]].bubbleHead, paramBubble);
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

    public void initHomeListener(){
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                Log.d("home", "onHomePressed() called with: " + "");
                minimizeBrowser(current);
                // do something here...
            }
            @Override
            public void onHomeLongPressed() {
            }
        });
    }


    public void addBrowser(final int index){
        browserPageArray[arrIndex[index]].browser.setOnTouchListener(this);
        browserPageArray[arrIndex[index]].browser.setOnFocusChangeListener(this);
        Log.d("testing", "addview4"); bubbleWindow.addView(browserPageArray[arrIndex[index]].browser, paramBrowser);
        browserPageArray[arrIndex[index]].browserwv.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey() called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if(keyCode == KeyEvent.KEYCODE_HOME){
                    Log.d(TAG, "onKey() called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");


                }
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (browserPageArray[arrIndex[index]].browserwv.canGoBack()) {
                        browserPageArray[arrIndex[index]].browserwv.goBack();
                    } else {
                        browserPageArray[arrIndex[index]].bubbleHead.performClick();
                    }
                }
                return false;
            }
        });
    }

    public void addBubble(final int index){
        Log.d(TAG, "addBubble() called with: index = [" + index + "]");

        browserPageArray[arrIndex[index]].bubbleHead.setOnTouchListener(new View.OnTouchListener() {
            int initialX;
            int initialY,y,x,z;
            float initialTouchX;
            float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //if(is_open) return false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "in Action_down");
                        z=paramBubble.x;
                        initialX = index*bubbleWidth;
                        Log.d(TAG, "ontouch action down: " + "index = [" + index + "], initialX = [" + initialX + "]");
                        initialY= heightNew;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        bubbleWindow.addView(delete, paramDelete);
                        bubbleWindow.removeView(browserPageArray[arrIndex[current]].browser);
                        return false;

                    case MotionEvent.ACTION_MOVE:
                        paramBubble.x = initialX - (int) (event.getRawX() - initialTouchX);
                        paramBubble.y = initialY - (int) (event.getRawY() - initialTouchY);
                        Log.d(TAG, "action_move () called with: " + "v = [" + v + "], height = [" + heightNew + "], width = [" + widthMid + "], x = [" + x + "], y = [" + y + "], event = [" + event + "]");
                        if (ondelete(delete.getWidth())) {
                            bubbleWindow.removeView(delete);
                            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[index]].bubbleHead, paramDelete);
                            bubbleWindow.addView(delete, paramDelete);
                        } else {
                            paramBubble.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[index]].bubbleHead, paramBubble);
                        }
                        return false;

                    case MotionEvent.ACTION_CANCEL:
                        return false;

                    case MotionEvent.ACTION_UP:
                        bubbleWindow.removeView(delete);
//                        y = (paramBubble.y - paramDelete.y);
//                        x = (paramBubble.x - paramDelete.x);
                        if (ondelete(delete.getWidth())) {
                            Log.d(TAG, "delete page called");
                            deletePage(index);
                        }
                        else {
                            paramBubble.gravity =Gravity.BOTTOM | Gravity.RIGHT;
                            paramBubble.x = initialX;
                            paramBubble.y = heightNew;
                            Log.d(TAG, "arrayindex: "+ current);
                            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[index]].bubbleHead, paramBubble);
                            Log.d(TAG, "onTouch() action up called with: " + "v = [" + v + "], event = [" + event + "]");
                            Log.d("testing","addview6"); bubbleWindow.addView(browserPageArray[arrIndex[current]].browser, paramBrowser);
                        }
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
            for (int i = 0; i < count; i++) {
                Log.d("TESTING", "for loop removeView in minimize");
                Log.d("testing", "removeView4"); bubbleWindow.removeView(browserPageArray[arrIndex[i]].bubbleHead);
            }
            paramBubble.x = paramx;
            paramBubble.y = paramy;
            if(onRightSide){
                paramBubble.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            }
            else {
                paramBubble.gravity = Gravity.BOTTOM | Gravity.LEFT;
            }
            browserPageArray[arrIndex[0]].bubbleHead.setImageResource(R.mipmap.bubblesmall);
            Log.d("testing", "removeView5" + " current = [" + current + "]"); bubbleWindow.removeView(browserPageArray[arrIndex[current]].browser);
            Log.d("testing", "update layout6"); bubbleWindow.addView(bh.bubbleHead, paramBubble);
            //addListenerBubble();
            is_open = false;
            mHomeWatcher.stopWatch();
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





    public boolean ondelete(int deletewidth){
        int y = (paramBubble.y - heightNew/4);
        int x = (paramBubble.x - widthMid + deletewidth/2);
        Log.d(TAG, "ondelete() called with:" + "x : " + x + "y : " + y + " deletewidth = [" + deletewidth + "]" + " parambubblex "+paramBubble.x + " parambubbley "+paramBubble.y + " pheightnew "+heightNew +" widthmid "+widthMid);
        if ((x > -100 && x < 100) && (y > -100 && y < 100)) {
            Log.d("TESTING", "deleted");
            return true;
        }
        else return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "Touch event: " + event.toString());

        // log it

        return false;
    }


    @Override
    public void onFocusChange(View view, boolean b) {
        Log.d(TAG, "onFocusChange() called with: " + "view = [" + view + "], b = [" + b + "]");
    }



}
