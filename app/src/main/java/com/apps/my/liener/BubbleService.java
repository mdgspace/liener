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

public class BubbleService extends Service implements OnKeyListener,View.OnTouchListener,View.OnFocusChangeListener {
    WindowManager bubbleWindow;
    Context context =this ;
    boolean onRightSide =true;
    String TAG = "BubbleService";
    WindowManager.LayoutParams paramBrowser;
    boolean is_open = false,is_running;
    int paramx = 0 ,paramy =0,count=0, current=0,bubbleWidth , heightNew, widthMid ;

    BrowserPage browserPageArray[] = new BrowserPage[20];
    int arrIndex[] = new int[20];
    BubbleHead bh,deleteHead;
    HomeWatcher mHomeWatcher;

    public boolean onKey(View v, int keyCode, KeyEvent event){
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initVariables();
        initDeleteHead();
        initParamBrowser();
        initHomeListener();
    }

    public void initVariables(){
        current=0;
        count=0;
        context = this;
        for(int i=0; i<20 ; i++){
            arrIndex[i]= i;
        }

        bubbleWindow = (WindowManager) getSystemService(WINDOW_SERVICE);

        loadDimensions();
        bh = new BubbleHead(context,heightNew,widthMid, BubbleHead.HEAD_TYPE_MAIN);
        bh.initParams(0,heightNew);
    }

    public void initParamBrowser(){
        paramBrowser = new WindowManager.LayoutParams(  WindowManager.LayoutParams.FILL_PARENT,
                heightNew,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |  WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        paramBrowser.gravity = Gravity.BOTTOM | Gravity.RIGHT;
    }


    public void initDeleteHead(){
        Log.d("testing", "addview1");
        deleteHead = new BubbleHead(context,heightNew,widthMid, BubbleHead.HEAD_TYPE_DELETE);
        initListener();
        deleteHead.initParams(0,(int)(heightNew/4));
        deleteHead.switchToDelete();
        deleteHead.layoutParams.gravity= Gravity.BOTTOM | Gravity.CENTER;
        deleteHead.layoutParams.width = 200;
    }


    public void onDestroy() {
        Log.d(TAG, "onDestroy() called with: " + "");
        super.onDestroy();
        if(is_open){
            if(current>=0) {
                browserPageArray[arrIndex[current]].bubbleHead.view.performClick();  // Minimizes before destroying
            }
        }
        else{
            bubbleWindow.removeView(bh.view);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        if(!("" + intent + "").equals("null")){
            String url = intent.getStringExtra("url");
            addNewPage(url);
        }
        setNotification();      // Notification in notification panel to keep the activity running in background
        return Service.START_STICKY;
    }

    public void addNewPage(String url){
        is_running = true;
        browserPageArray[arrIndex[count]]=new BrowserPage(context,BubbleService.this,count*Constant.BubbleSizeLarge,heightNew,widthMid);
        browserPageArray[arrIndex[count]].loadUrl(url);
        if(count==0){
            setBubbleHead();        // if first tab then sets main bubble head listeners
            Log.d("testing", "addview9");
            bubbleWindow.addView(bh.view, bh.layoutParams);    // Adds main bubble head to the display
        }
        else{
            if(is_open){
                browserPageArray[current].bubbleHead.view.performClick();
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
//        bh.view.setOnTouchListener(new View.OnTouchListener() {
//            int initialX, initialY;
//            float initialTouchX, initialTouchY;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (!is_open) {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            initialX = bh.layoutParams.x;           initialY = bh.layoutParams.y;
//                            initialTouchX = event.getRawX();    initialTouchY = event.getRawY();
//                            bubbleWindow.addView(deleteHead.view, deleteHead.layoutParams);
//                            return false;
//                        case MotionEvent.ACTION_MOVE:
//                            if(onRightSide){
//                                bh.layoutParams.x = initialX - (int)(event.getRawX() - initialTouchX);
//                            }
//                            else{
//                                bh.layoutParams.x = initialX + (int)(event.getRawX() - initialTouchX);
//                            }
//                            bh.layoutParams.y = initialY - (int)(event.getRawY() - initialTouchY);
//                            if (ondelete(deleteHead.view.getWidth(),bh.layoutParams)){
//                                Log.d(TAG, "action_move inside () called with: " + "v = [" + v + "], event = [" + event + "], height = [" + heightNew + "], width = [" + widthMid + "]");
//                                Log.d("testing", "removeView3");
//                                bubbleWindow.removeView(deleteHead.view);            Log.d("testing", "update layout4");
//                                bubbleWindow.updateViewLayout(bh.view, deleteHead.layoutParams);       Log.d("testing", "addview2");
//                                bubbleWindow.addView(deleteHead.view, deleteHead.layoutParams);
//                            } else {
//                                bubbleWindow.updateViewLayout(bh.view, bh.layoutParams);
//                            }
//                            return false;
//                        case MotionEvent.ACTION_UP:
//                            Log.d("testing", "actionupfor0");
//                            bubbleWindow.removeView(deleteHead.view);
//                            if (ondelete(deleteHead.view.getWidth(),bh.layoutParams)) {
//                                is_running = false;
//                                Log.d(TAG, "stopself");
//                                stopSelf();
//                                return true;
//                            }
//                            else  {
//                                if(bh.layoutParams.x>widthMid){
//                                    if(onRightSide){
//                                        bh.layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
//                                        onRightSide=false;
//                                    }
//                                    else {
//                                        bh.layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
//                                        onRightSide=true;
//                                    }
//                                }
//                                bh.layoutParams.x=0;
//                                bubbleWindow.updateViewLayout(bh.view, bh.layoutParams);
//                                Log.d(TAG, "else in action_up");
//                            }
//                            return false;
//                    }
//                }
//                return false;
//            }
//        });
        bh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_running) {
                    Log.d("TESTING", "in 0 click");
                    if (!is_open) {
                        mHomeWatcher.startWatch();
                        bh.layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                        paramx = bh.layoutParams.x;
                        paramy = bh.layoutParams.y;
                        bh.layoutParams.x = 0;
                        bh.layoutParams.y = heightNew;
                        if (current == 0) {
                            browserPageArray[arrIndex[0]].switchToLarge();
                            //bubbleWindow.updateViewLayout(browserPageArray[arrIndex[0]].bubbleHead.view,browserPageArray[arrIndex[0]].bubbleHead.layoutParams);

                            //browserPageArray[arrIndex[0]].bubbleHead.view.setImageResource(R.mipmap.bubble);
                        }
                        Log.d("testing", "update layout2");
                        bubbleWindow.removeView(bh.view);
                        bubbleWidth = bh.view.getWidth();
                        Log.d(TAG, " " + "bubblewidth = [" + bubbleWidth + "]");
                        addBrowser(current);
                        for (int i = 0; i < count; i++) {
                            //final int j = i;
                            addBubble(i);
                            Log.d(TAG, "addbubble(1) called with: v = [" + v + "]");
                            if (i != 0) {
                                bh.layoutParams.x = bh.layoutParams.x + bubbleWidth;
                                Log.d("testing", "addview3");
                            }
                            bubbleWindow.addView(browserPageArray[arrIndex[i]].bubbleHead.view, browserPageArray[arrIndex[i]].bubbleHead.layoutParams);
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
                        browserPageArray[arrIndex[index]].bubbleHead.view.performClick();
                    }
                }
                return false;
            }
        });
    }

    public void addBubble(final int index){
        Log.d(TAG, "addBubble() called with: index = [" + index + "]");

//        browserPageArray[arrIndex[index]].bubbleHead.view.setOnTouchListener(new View.OnTouchListener() {
//            int initialX;
//            int initialY,y,x;
//            float initialTouchX;
//            float initialTouchY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                //if(is_open) return false;
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Log.d(TAG, "in Action_down");
//                        initialX = index*bubbleWidth;
//                        Log.d(TAG, "ontouch action down: " + "index = [" + index + "], initialX = [" + initialX + "]");
//                        initialY= heightNew;
//                        initialTouchX = event.getRawX();
//                        initialTouchY = event.getRawY();
//                        bubbleWindow.addView(deleteHead.view, deleteHead.layoutParams);
//                        bubbleWindow.removeView(browserPageArray[arrIndex[current]].browser);
//                        return false;
//
//                    case MotionEvent.ACTION_MOVE:
//                        browserPageArray[arrIndex[index]].bubbleHead.layoutParams.x = initialX - (int) (event.getRawX() - initialTouchX);
//                        browserPageArray[arrIndex[index]].bubbleHead.layoutParams.y = initialY - (int) (event.getRawY() - initialTouchY);
//                        Log.d(TAG, "action_move () called with: " + "v = [" + v + "], height = [" + heightNew + "], width = [" + widthMid + "], x = [" + x + "], y = [" + y + "], event = [" + event + "]");
//                        if (ondelete(deleteHead.view.getWidth(), browserPageArray[arrIndex[index]].bubbleHead.layoutParams)) {
//                            bubbleWindow.removeView(deleteHead.view);
//                            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[index]].bubbleHead.view, deleteHead.layoutParams);
//                            bubbleWindow.addView(deleteHead.view, deleteHead.layoutParams);
//                        } else {
//                            browserPageArray[arrIndex[index]].bubbleHead.layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
//                            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[index]].bubbleHead.view, browserPageArray[arrIndex[index]].bubbleHead.layoutParams);
//                        }
//                        return false;
//
//                    case MotionEvent.ACTION_CANCEL:
//                        return false;
//
//                    case MotionEvent.ACTION_UP:
//                        bubbleWindow.removeView(deleteHead.view);
//                        if (ondelete(deleteHead.view.getWidth(), browserPageArray[arrIndex[index]].bubbleHead.layoutParams)) {
//                            Log.d(TAG, "delete page called");
//                            deletePage(index);
//                        }
//                        else {
//                            browserPageArray[arrIndex[index]].bubbleHead.layoutParams.gravity =Gravity.BOTTOM | Gravity.RIGHT;
//                            browserPageArray[arrIndex[index]].bubbleHead.layoutParams.x = initialX;
//                            browserPageArray[arrIndex[index]].bubbleHead.layoutParams.y = heightNew;
//                            Log.d(TAG, "arrayindex: "+ current);
//                            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[index]].bubbleHead.view, browserPageArray[arrIndex[index]].bubbleHead.layoutParams);
//                            Log.d(TAG, "onTouch() action up called with: " + "v = [" + v + "], event = [" + event + "]");
//                            Log.d("testing","addview6"); bubbleWindow.addView(browserPageArray[arrIndex[current]].browser, paramBrowser);
//                        }
//                        return false;
//
//                }
//
//                return false;
//            }
//        });

        browserPageArray[arrIndex[index]].bubbleHead.view.setOnClickListener(new View.OnClickListener() {
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
                Log.d("testing", "removeView4"); bubbleWindow.removeView(browserPageArray[arrIndex[i]].bubbleHead.view);
            }
//            bh.layoutParams.x = paramx;
//            bh.layoutParams.y = paramy;
//            if(onRightSide){
//                bh.layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
//            }
//            else {
//                bh.layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
//            }
            //browserPageArray[arrIndex[0]].bubbleHead.view.setImageResource(R.mipmap.bubblesmall);

            Log.d("testing", "removeView5" + " current = [" + current + "]"); bubbleWindow.removeView(browserPageArray[arrIndex[current]].browser);
            Log.d("testing", "update layout6"); bubbleWindow.addView(bh.view, bh.layoutParams);
            //addListenerBubble();
            is_open = false;
            mHomeWatcher.stopWatch();
        }
        else{
            bh.layoutParams.x = index*bubbleWidth;
            bh.layoutParams.y = heightNew;
            addBrowser(index);
            Log.d("testing", "removeView6"); bubbleWindow.removeView(browserPageArray[arrIndex[current]].browser);
            browserPageArray[arrIndex[index]].switchToLarge();
            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[index]].bubbleHead.view,browserPageArray[arrIndex[index]].bubbleHead.layoutParams);

            browserPageArray[arrIndex[current]].switchToSmall();
            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[current]].bubbleHead.view,browserPageArray[arrIndex[current]].bubbleHead.layoutParams);
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
        bubbleWindow.removeView(browserPageArray[arrIndex[count]].bubbleHead.view);
        browserPageArray[arrIndex[current]].switchToLarge();
        bubbleWindow.updateViewLayout(browserPageArray[arrIndex[current]].bubbleHead.view,browserPageArray[arrIndex[current]].bubbleHead.layoutParams);
        if (current >= count) {
            current--;
            browserPageArray[arrIndex[0]].switchToLarge();
            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[0]].bubbleHead.view,browserPageArray[arrIndex[0]].bubbleHead.layoutParams);
        }
        bh.layoutParams.x = index * bubbleWidth;
        bh.layoutParams.y = heightNew;
        for (int i = index; i < count; i++) {
            addBubble(i);
            Log.d("testing", "update layout7");
            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[i]].bubbleHead.view, browserPageArray[arrIndex[i]].bubbleHead.layoutParams);
            bh.layoutParams.x = bh.layoutParams.x + bubbleWidth;
        }

        if (current >= 0) {
            Log.d("testing", "addview8");
            bubbleWindow.addView(browserPageArray[arrIndex[current]].browser, paramBrowser);
        }
        else{
            mHomeWatcher.stopWatch();
            stopSelf();
        }
    }





    public boolean ondelete(int deletewidth, WindowManager.LayoutParams layoutParams){
        int y = (layoutParams.y - heightNew/4);
        int x = (layoutParams.x - widthMid + deletewidth/2);
        Log.d(TAG, "ondelete() called with:" + "x : " + x + "y : " + y + " deletewidth = [" + deletewidth + "]" + " layoutParamsx "+layoutParams.x + " layoutParamsy "+layoutParams.y + " pheightnew "+heightNew +" widthmid "+widthMid);
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

    public void loadDimensions(){
        Display display = bubbleWindow.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        heightNew = (int)((size.y*100)/128);
        widthMid = (int)(size.x/2);
    }

    public void initListener(){
        deleteHead.setListener(new BubbleListener() {
            @Override
            public void onComplete(String item) {
                Log.d(TAG, "Listener onComplete() called with: item = [" + item + "]");
            }

            @Override
            public void onError(Throwable error) {

            }
        });
    }
}
