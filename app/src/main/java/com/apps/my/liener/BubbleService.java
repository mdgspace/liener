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

import java.util.logging.LogManager;

public class BubbleService extends Service implements OnKeyListener, View.OnTouchListener, View.OnFocusChangeListener, BubbleListener {
    WindowManager bubbleWindow;
    Context context = this;
    boolean onRightSide = true;
    private static final String TAG = BubbleService.class.getSimpleName();
    WindowManager.LayoutParams paramBrowser;
    boolean is_open = false, is_running;
    int paramx = 0, paramy = 0, count = 0, current = 0, bubbleWidth, heightNew, widthMid;

    BrowserPage browserPageArray[] = new BrowserPage[20];
    int arrIndex[] = new int[20];
    BubbleHead bh, deleteHead;
    HomeWatcher mHomeWatcher;

    public boolean onKey(View v, int keyCode, KeyEvent event) {
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

    public void initVariables() {
        current = 0;
        count = 0;
        context = this;
        for (int i = 0; i < 20; i++) {
            arrIndex[i] = i;
        }

        bubbleWindow = (WindowManager) getSystemService(WINDOW_SERVICE);

        loadDimensions();
        bh = new BubbleHead(context, heightNew, widthMid, BubbleHead.HEAD_TYPE_MAIN, -2);
        bh.initParams(0, heightNew);
        bh.setListener(this);
    }

    public void initParamBrowser() {
        paramBrowser = new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT,
                heightNew,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        paramBrowser.gravity = Gravity.BOTTOM | Gravity.RIGHT;
    }


    public void initDeleteHead() {
        Log.d("testing", "addview1");
        deleteHead = new BubbleHead(context, heightNew, widthMid, BubbleHead.HEAD_TYPE_DELETE, -1);
        deleteHead.initParams(0, (int) (heightNew / 4));
        deleteHead.layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        bubbleWindow.addView(deleteHead.view, deleteHead.layoutParams);
        Log.d(TAG, "DeleteHead added");
    }


    public void onDestroy() {
        Log.d(TAG, "onDestroy() called with: " + "");
        super.onDestroy();
        if (is_open) {
            if (current >= 0) {
                browserPageArray[arrIndex[current]].bubbleHead.view.performClick();  // Minimizes before destroying
            }
        } else {
            bubbleWindow.removeView(bh.view);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!("" + intent + "").equals("null")) {
            String url = intent.getStringExtra("url");
            addNewPage(url);
        }
        setNotification();      // Notification in notification panel to keep the activity running in background
        return Service.START_STICKY;
    }

    public void addNewPage(String url) {
        is_running = true;
        browserPageArray[arrIndex[count]] = new BrowserPage(context, BubbleService.this, count * Constant.BubbleSizeLarge, heightNew, widthMid, arrIndex[count]);
        browserPageArray[arrIndex[count]].bubbleHead.setListener(this);
        browserPageArray[arrIndex[count]].loadUrl(url);
        if (count == 0) {
            Log.d("testing", "addview9");
            bubbleWindow.addView(bh.view, bh.layoutParams);    // Adds main bubble head to the display
        } else {
            if (is_open) {
                browserPageArray[current].bubbleHead.view.performClick();
            }
        }
        count++;
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


    public void expandBrowser() {
        if (is_running) {
            Log.d("TESTING", "in 0 click");
            if (!is_open) {
                mHomeWatcher.startWatch();
                if (current == 0) {
                    browserPageArray[arrIndex[0]].switchToLarge();
                }
                Log.d("testing", "update layout2");
                bubbleWindow.removeView(bh.view);
                bubbleWidth = bh.view.getWidth();
                Log.d(TAG, "" + "bubblewidth = [" + bubbleWidth + "]");
                addBrowser(current);
                for (int i = 0; i < count; i++) {
                    bubbleWindow.addView(browserPageArray[arrIndex[i]].bubbleHead.view, browserPageArray[arrIndex[i]].bubbleHead.layoutParams);
                }
                is_open = true;
            } else {
                Log.d(TAG, "min 0");
                minimizeBrowser(0);
            }
        }
    }

    public void initHomeListener() {
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


    public void addBrowser(final int index) {
        browserPageArray[arrIndex[index]].browser.setOnTouchListener(this);
        browserPageArray[arrIndex[index]].browser.setOnFocusChangeListener(this);
        Log.d("testing", "addview4");
        bubbleWindow.addView(browserPageArray[arrIndex[index]].browser, paramBrowser);
        Log.d("testing", "addview4b");
        browserPageArray[arrIndex[index]].browserwv.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey() called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (keyCode == KeyEvent.KEYCODE_HOME) {
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


    public void minimizeBrowser(int index) {
        if (current == index) {
            for (int i = 0; i < count; i++) {
                Log.d("TESTING", "for loop removeView in minimize");
                Log.d("testing", "removeView4");
                bubbleWindow.removeView(browserPageArray[arrIndex[i]].bubbleHead.view);
            }

            Log.d("testing", "removeView5" + " current = [" + current + "]");
            bubbleWindow.removeView(browserPageArray[arrIndex[current]].browser);
            Log.d("testing", "update layout6");
            bubbleWindow.addView(bh.view, bh.layoutParams);
            is_open = false;
            mHomeWatcher.stopWatch();
        } else {
            addBrowser(index);
            Log.d("testing", "removeView6");
            bubbleWindow.removeView(browserPageArray[arrIndex[current]].browser);
            Log.d("testing", "removeView9");
            browserPageArray[arrIndex[index]].switchToLarge();
            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[index]].bubbleHead.view, browserPageArray[arrIndex[index]].bubbleHead.layoutParams);

            browserPageArray[arrIndex[current]].switchToSmall();
            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[current]].bubbleHead.view, browserPageArray[arrIndex[current]].bubbleHead.layoutParams);
            current = index;
        }
    }

    public void deletePage(int index) {
        int current_BId = arrIndex[current];
        bubbleWindow.removeView(browserPageArray[arrIndex[index]].bubbleHead.view);
        Log.d("TESTING", "deletepage called");

        int temp = arrIndex[index];
        for (int i = index; i < count - 1; i++) {
            arrIndex[i] = arrIndex[i + 1];
        }
        arrIndex[count - 1] = temp;
        count--;

        for (int i = index; i < count; i++) {
            shiftBubble(i);
        }

        Log.d("testing", "removeView7" + "arrindex =");

        if (current >= count) {
            current--;
            browserPageArray[arrIndex[0]].switchToLarge();
            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[0]].bubbleHead.view, browserPageArray[arrIndex[0]].bubbleHead.layoutParams);
        } else if (arrIndex[count] == current_BId) {
            browserPageArray[arrIndex[current]].switchToLarge();
        } else {
            current = getIndex(current_BId);
        }
        for (int i = index; i < count; i++) {
            Log.d("testing", "update layout7");
            bubbleWindow.updateViewLayout(browserPageArray[arrIndex[i]].bubbleHead.view, browserPageArray[arrIndex[i]].bubbleHead.layoutParams);
        }

        if (current >= 0) {
            Log.d("testing", "addview8");
            bubbleWindow.addView(browserPageArray[arrIndex[current]].browser, paramBrowser);
        } else {
            mHomeWatcher.stopWatch();
            stopSelf();
        }
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

    public void loadDimensions() {
        Display display = bubbleWindow.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        heightNew = (int) ((size.y * 100) / 128);
        widthMid = (int) (size.x / 2);
    }

    @Override
    public void onTouchEvent(@TOUCH_EVENT_TYPE int event_type, @BubbleHead.HEAD_TYPE int head_type, int BId) {
        Log.d(TAG, "Listener onEvent() called with: type = [" + event_type + "]");
        switch (event_type) {
            case TOUCH_EVENT_TYPE_ADD_DELETE:
                deleteHead.view.setVisibility(View.VISIBLE);
                break;
            case TOUCH_EVENT_TYPE_DELETE:
                switch (head_type) {
                    case BubbleHead.HEAD_TYPE_MAIN:
                        is_running = false;
                        stopSelf();
                        break;
                    case BubbleHead.HEAD_TYPE_TAB:
                        deletePage(getIndex(BId));
                        break;
                }
                break;
            case TOUCH_EVENT_TYPE_ON_DELETE:
                switch (head_type) {
                    case BubbleHead.HEAD_TYPE_MAIN:
                        deleteHead.layoutParams.setSize(Constant.BubbleSizeDeleteLarge);
                        bubbleWindow.updateViewLayout(deleteHead.view,deleteHead.layoutParams);
                        deleteHead.layoutParams.setSize(Constant.BubbleInDelete);
                        bubbleWindow.updateViewLayout(bh.view, deleteHead.layoutParams);
                        break;
                    case BubbleHead.HEAD_TYPE_TAB:
                        deleteHead.layoutParams.setSize(Constant.BubbleSizeDeleteLarge);
                        bubbleWindow.updateViewLayout(deleteHead.view,deleteHead.layoutParams);
                        deleteHead.layoutParams.setSize(Constant.BubbleInDelete);
                        bubbleWindow.updateViewLayout(browserPageArray[BId].bubbleHead.view, deleteHead.layoutParams);
                        break;
                }
                deleteHead.layoutParams.setSize(Constant.BubbleSizeDelete);
                break;

            case TOUCH_EVENT_TYPE_OFF_DELETE:
                deleteHead.layoutParams.setSize(Constant.BubbleSizeDelete);
                bubbleWindow.updateViewLayout(deleteHead.view,deleteHead.layoutParams);
                break;
            case TOUCH_EVENT_TYPE_REMOVE_DELETE:
                deleteHead.view.setVisibility(View.INVISIBLE);
                break;
            case TOUCH_EVENT_TYPE_UPDATE:
                switch (head_type) {
                    case BubbleHead.HEAD_TYPE_MAIN:
                        bubbleWindow.updateViewLayout(bh.view, bh.layoutParams);
                        break;
                    case BubbleHead.HEAD_TYPE_TAB:
                        bubbleWindow.updateViewLayout(browserPageArray[BId].bubbleHead.view, browserPageArray[BId].bubbleHead.layoutParams);
                        break;
                }
                break;
            case TOUCH_EVENT_TYPE_REMOVE_BROWSER:
                bubbleWindow.removeView(browserPageArray[arrIndex[current]].browser);
                Log.d(TAG, "removeview8");
                break;
            case TOUCH_EVENT_TYPE_ADD_BROWSER:
                bubbleWindow.addView(browserPageArray[arrIndex[current]].browser, paramBrowser);
                break;
        }
    }

    @Override
    public void onClickEvent(@CLICK_EVENT_TYPE int event_type, @BubbleHead.HEAD_TYPE int head_type, int BId) {
        Log.d(TAG, "onClickEvent() called with: event_type = [" + event_type + "], head_type = [" + head_type + "], BId = [" + BId + "]");
        switch (event_type) {
            case CLICK_EVENT_TYPE_EXPAND:
                expandBrowser();
                break;
            case CLICK_EVENT_TYPE_MINIMIZE:
                Log.d(TAG, "onClickEvent() called with: event_type = [" + event_type + "], head_type = [" + head_type + "], BId = [" + BId + "], GetIndex = [" + getIndex(BId) + "]");
                minimizeBrowser(getIndex(BId));
                break;
        }
    }

    @Override
    public void onError(Throwable error) {

    }

    public int getIndex(int BId) {
        for (int i = 0; i < count; i++) {
            if (arrIndex[i] == BId) {
                return i;
            }
        }
        Log.d(TAG, "getIndex() Error called with: BId = [" + BId + "]");
        return -1;

    }


    public void shiftBubble(int index) {
        Log.d(TAG, "shiftBubble() called with: index = [" + index + "]");
        browserPageArray[arrIndex[index]].bubbleHead.layoutParams.x = browserPageArray[arrIndex[index]].bubbleHead.layoutParams.x - Constant.BubbleSizeLarge;
    }
}
