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


public class BubbleService extends Service implements OnKeyListener, View.OnTouchListener, View.OnFocusChangeListener, BubbleListener {
    WindowManager bubbleWindow;
    Context context = this;
    boolean onRightSide = true;
    private static final String TAG = BubbleService.class.getSimpleName();
    WindowManager.LayoutParams paramBrowser, action_overflow_params;
    boolean is_open = false, is_running;
    int paramx = 0, paramy = 0, count = 0, current = 0, bubbleWidth, heightNew, widthMid;

    BrowserPage browserPageArray[] = new BrowserPage[20];
    int arrIndex[] = new int[20];
    BubbleHead bh, deleteHead;
    HomeWatcher mHomeWatcher;

    LinearLayout browserLayout;

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
        initActionOverflowParams();
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

        browserLayout = new LinearLayout(this);
        browserLayout.setBackgroundColor(Color.WHITE);
    }


    public void initDeleteHead() {
        Log.d("testing", "addview1");
        deleteHead = new BubbleHead(context, heightNew, widthMid, BubbleHead.HEAD_TYPE_DELETE, -1);
        deleteHead.initParams(0, (int) (heightNew / 4 - BubbleSizeDelete / 2), Gravity.BOTTOM | Gravity.CENTER);
        bubbleWindow.addView(deleteHead.getView(), deleteHead.getLayoutParams());
        Log.d(TAG, "DeleteHead added");
    }

    public void initActionOverflowParams() {
        action_overflow_params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        action_overflow_params.gravity = Gravity.TOP | Gravity.RIGHT;
        action_overflow_params.y = (int) (heightNew * 28 / 100);
        Log.d(TAG, "initActionOverflowParams() called" + action_overflow_params.y);
    }

    private boolean isMenuOpen = false;

    public void showActionOverflow() {
        if (isMenuOpen) {
            //[ACTION_OVERFLOW]bubbleWindow.removeView(getBrowserTab(current).action_overflow_view);
            isMenuOpen = false;
        } else {
            //[ACTION_OVERFLOW]bubbleWindow.addView(getBrowserTab(current).action_overflow_view, action_overflow_params);
            isMenuOpen = true;
        }
    }


    public void onDestroy() {
        Log.d(TAG, "onDestroy() called with: " + "");
        super.onDestroy();
        if (is_open) {
            if (current >= 0) {
                getBrowserTab(current).performClick();  // Minimizes before destroying
            }
        } else {
            bubbleWindow.removeView(bh.getView());
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
        Log.d(TAG, "addNewPage() called with: url = [" + url + "]");
        addBrowserTab(count,new BrowserPage(context, BubbleService.this, count * Constant.BubbleSizeLarge, heightNew, widthMid, arrIndex[count]));
        Log.d(TAG, "addNewPage() called with: url = [" + url + "]");
        getBrowserTab(count).setBubbleListener(this);
        //getBrowserTab(count).bubbleHead.setListener(this);
        Log.d(TAG, "addNewPage() called with: url = [" + url + "]");
        getBrowserTab(count).setListener(this);
        Log.d(TAG, "addNewPage() called with: url = [" + url + "]");
        getBrowserTab(count).loadUrl(url);
        if (count == 0) {
            Log.d("testing", "addview9");
            bubbleWindow.addView(bh.getView(), bh.getLayoutParams());    // Adds main bubble head to the display
        } else {
            if (is_open) {
                browserPageArray[current].performClick();
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
                    getBrowserTab(0).switchToLarge();
                }
                Log.d("testing", "update layout2");
                bubbleWindow.removeView(bh.getView());
                for (int i = 0; i < count; i++) {
                    bubbleWindow.addView(getBrowserTab(i).getBubbleView(), getBrowserTab(i).getBubbleLayout());
                }
                addBrowser(current);
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
        getBrowserTab(index).setBrowserListeners(this);
        Log.d("testing", "addview4");


        //Add view to layout
        browserLayout.addView(getBrowserTab(index).getBrowserView(), paramBrowser);

        if (index == current) {
            bubbleWindow.addView(browserLayout, paramBrowser);
        }

        //bubbleWindow.addView(getBrowserTab(index).browser, paramBrowser);
        Log.d("testing", "addview4b");
        getBrowserTab(index).setWebViewKeyListener();
    }


    public void minimizeBrowser(int index) {
        if (current == index) {
            for (int i = 0; i < count; i++) {
                Log.d("TESTING", "for loop removeView in minimize");
                Log.d("testing", "removeView4");
                bubbleWindow.removeView(getBrowserTab(i).getBubbleView());
            }

            Log.d("testing", "removeView5" + " current = [" + current + "]");

            //Remove layout
            //Remove view from layout

            bubbleWindow.removeView(browserLayout);
            browserLayout.removeView(getBrowserTab(current).getBrowserView());

            //bubbleWindow.removeView(getBrowserTab(current).browser);
            Log.d("testing", "update layout6");
            bubbleWindow.addView(bh.getView(), bh.getLayoutParams());
            is_open = false;
            mHomeWatcher.stopWatch();
        } else {
            addBrowser(index);
            Log.d("testing", "removeView6");

            //Remove view from layout
            browserLayout.removeView(getBrowserTab(current).getBrowserView());

            //bubbleWindow.removeView(getBrowserTab(current).browser);
            Log.d("testing", "removeView9");
            getBrowserTab(index).switchToLarge();
            bubbleWindow.updateViewLayout(getBrowserTab(index).getBubbleView(), getBrowserTab(index).getBubbleLayout());

            getBrowserTab(current).switchToSmall();
            bubbleWindow.updateViewLayout(getBrowserTab(current).getBubbleView(), getBrowserTab(current).getBubbleLayout());
            current = index;
        }
    }

    public void deletePage(int index) {
        int current_BId = arrIndex[current];
        bubbleWindow.removeView(getBrowserTab(index).getBubbleView());
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
            current=0;
            getBrowserTab(0).switchToLarge();
            bubbleWindow.updateViewLayout(getBrowserTab(0).getBubbleView(), getBrowserTab(0).getBubbleLayout());
        } else if (arrIndex[count] == current_BId) {
            getBrowserTab(current).switchToLarge();
        } else {
            current = getIndex(current_BId);
        }
        for (int i = index; i < count; i++) {
            Log.d("testing", "update layout7");
            bubbleWindow.updateViewLayout(getBrowserTab(i).getBubbleView(), getBrowserTab(i).getBubbleLayout());
        }

        if (current >= 0) {
            Log.d("testing", "addview8");

            //Add view Layout
            //Add view in layout

            browserLayout.addView(getBrowserTab(current).getBrowserView(), paramBrowser);
            bubbleWindow.addView(browserLayout, paramBrowser);

            //bubbleWindow.addView(getBrowserTab(current).browser, paramBrowser);
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
        if (isMenuOpen) {
            //[ACTION_FLOW]bubbleWindow.removeView(getBrowserTab(current).action_overflow_view);
            isMenuOpen = false;
        }
        switch (event_type) {
            case TOUCH_EVENT_TYPE_ADD_DELETE:
                deleteHead.setViewVisibility(View.VISIBLE);
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
                        deleteHead.setLayoutParamsSize(Constant.BubbleSizeDeleteLarge);
                        bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
                        deleteHead.setLayoutParamsSize(Constant.BubbleInDelete);
                        bubbleWindow.updateViewLayout(bh.getView(), deleteHead.getLayoutParams());
                        break;
                    case BubbleHead.HEAD_TYPE_TAB:
                        deleteHead.setLayoutParamsSize(Constant.BubbleSizeDeleteLarge);
                        bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
                        deleteHead.setLayoutParamsSize(Constant.BubbleInDelete);
                        bubbleWindow.updateViewLayout(browserPageArray[BId].getBubbleView(), deleteHead.getLayoutParams());
                        break;
                }
                deleteHead.getLayoutParams().setSize(BubbleSizeDelete);
                break;

            case TOUCH_EVENT_TYPE_OFF_DELETE:
                deleteHead.getLayoutParams().setSize(BubbleSizeDelete);
                bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
                break;
            case TOUCH_EVENT_TYPE_REMOVE_DELETE:
                deleteHead.getView().setVisibility(View.INVISIBLE);
                break;
            case TOUCH_EVENT_TYPE_UPDATE:
                switch (head_type) {
                    case BubbleHead.HEAD_TYPE_MAIN:
                        bubbleWindow.updateViewLayout(bh.getView(), bh.getLayoutParams());
                        break;
                    case BubbleHead.HEAD_TYPE_TAB:
                        bubbleWindow.updateViewLayout(browserPageArray[BId].getBubbleView(), browserPageArray[BId].getBubbleLayout());
                        break;
                }
                break;
            case TOUCH_EVENT_TYPE_REMOVE_BROWSER:
                bubbleWindow.removeView(browserLayout);
                browserLayout.removeView(getBrowserTab(current).getBrowserView());
                Log.d(TAG, "removeview8");
                break;
            case TOUCH_EVENT_TYPE_ADD_BROWSER:
                //Add view layout
                //Add view to layout

                browserLayout.addView(getBrowserTab(current).getBrowserView(), paramBrowser);
                bubbleWindow.addView(browserLayout, paramBrowser);

                //bubbleWindow.addView(getBrowserTab(current).browser, paramBrowser);
                break;
        }
    }

    @Override
    public void onClickEvent(@CLICK_EVENT_TYPE int event_type, @BubbleHead.HEAD_TYPE int head_type, int BId) {
        Log.d(TAG, "onClickEvent() called with: event_type = [" + event_type + "], head_type = [" + head_type + "], BId = [" + BId + "]");
        if (isMenuOpen) {
            //[ACTION_FLOW]bubbleWindow.removeView(getBrowserTab(current).action_overflow_view);
            isMenuOpen = false;
        }
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
    public void onEvent(@EVENT_TYPE int event_type) {
        switch (event_type) {
            case EVENT_TYPE_ACTION_OVERFLOW:
                showActionOverflow();
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
        getBrowserTab(index).setBubbleLayoutX(getBrowserTab(index).getBubbleLayoutX() - Constant.BubbleSizeLarge);
    }

    private BrowserPage getBrowserTab(int index){
        return browserPageArray[arrIndex[index]];
    }

    private void addBrowserTab(int index, BrowserPage browserPage){
        browserPageArray[arrIndex[index]] = browserPage;
    }
}
