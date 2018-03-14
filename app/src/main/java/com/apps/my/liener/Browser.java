package com.apps.my.liener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;
import static com.apps.my.liener.Constant.BubbleSizeDelete;
import static com.apps.my.liener.Constant.BubbleSizeLarge;

/**
 * Created by rahul on 5/6/17.
 */
public class Browser {
    private static final String TAG = Browser.class.getSimpleName();

    WindowManager bubbleWindow;
    WindowManager.LayoutParams paramBrowser, action_overflow_params;

    boolean is_open = false, is_running;

    int heightNew, widthMid;
    int defaultPage;
    int idCount;

    static List<BrowserPage> browserPages = new LinkedList<>();


    BubbleHead bh, deleteHead;

    HomeWatcher mHomeWatcher;

    LinearLayout browserLayout;

    Context context;
    int deleteHeight;
    int tempY;

    public Browser(Context context) {

        this.context = context;

        initVariables();
        initDeleteHead();
        initBubbleHead();
        initParamBrowser();
        initHomeListener();
        initActionOverflowParams();
    }


    public void initVariables() {
        defaultPage = -1;
        idCount = 0;


        bubbleWindow = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        loadDimensions();


    }

    private void initBubbleHead() {
        bh = new BubbleHead(context, heightNew, widthMid, BubbleHead.HEAD_TYPE_MAIN);
        bh.initParams(0, heightNew);
        bh.setBubbleListener(new BubbleListener() {
            @Override
            public void onClick() {
                expandBrowser(null);
            }

            @Override
            public void onDelete() {
                is_running = false;
                stopSelf();
            }

            @Override
            public void updateView() {
                bubbleWindow.updateViewLayout(bh.getView(), bh.getLayoutParams());
            }

            @Override
            public void onMove(boolean isMoving) {
                if (isMoving) {
                    deleteHead.setViewVisibility(View.VISIBLE);
                    deleteHeight = heightNew / 4 - BubbleSizeDelete / 2;
                    tempY = deleteHeight / 10;
                    Handler hand = new Handler();
                    recursionHandler(hand,1);
                } else {
                    Handler hand = new Handler();
                    recursionHandler(hand,2);
                }
            }

            @Override
            public void overDeleteArea(boolean isOver) {
                if (isOver) {
                    deleteHead.setLayoutParamsSize(Constant.BubbleSizeDeleteLarge);
                    bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
                    deleteHead.setLayoutParamsSize(Constant.BubbleSizeLarge);
                    bubbleWindow.updateViewLayout(bh.getView(), deleteHead.getLayoutParams());
                } else {
                    deleteHead.getLayoutParams().setSize(BubbleSizeDelete);
                    bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
                }
            }
        });
    }

    public void recursionHandler(final Handler hand, final int mode) {


        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mode == 1 && deleteHead.getLayoutParamsY() < deleteHeight) {
                    recursionHandler(hand, mode);
                    deleteHead.setLayoutParamsY(deleteHead.getLayoutParamsY() + tempY);
                    bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());

                } else if (mode == 2 && deleteHead.getLayoutParamsY() > 0) {
                    recursionHandler(hand, mode);
                    deleteHead.setLayoutParamsY(deleteHead.getLayoutParamsY() - tempY);
                    bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
                }
                else {
                    if (mode == 1) {
                        deleteHead.setLayoutParamsY(deleteHeight);
                    } else {
                        deleteHead.getView().setVisibility(View.INVISIBLE);
                        deleteHead.setLayoutParamsY(0);
                    }
                }
            }
        }, 5);
    }

    public void initParamBrowser() {
        paramBrowser = new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT,
                heightNew,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        paramBrowser.gravity = Gravity.BOTTOM | Gravity.RIGHT;

        browserLayout = new LinearLayout(context);
        browserLayout.setBackgroundColor(Color.WHITE);
    }

    public void initDeleteHead() {
        deleteHead = new BubbleHead(context, heightNew, widthMid, BubbleHead.HEAD_TYPE_DELETE);
        deleteHead.initParams(0, 0, Gravity.BOTTOM | Gravity.CENTER);
        bubbleWindow.addView(deleteHead.getView(), deleteHead.getLayoutParams());
    }


    public void initActionOverflowParams() {
        action_overflow_params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        action_overflow_params.gravity = Gravity.TOP | Gravity.RIGHT;
        action_overflow_params.y = (int) (heightNew * 28 / 100);
        Log.d(TAG, "initActionOverflowParams() called" + action_overflow_params.y);
    }

    public void initHomeListener() {
        mHomeWatcher = new HomeWatcher(context);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                minimizeBrowser();
                // do something here...
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
    }

    public void loadDimensions() {
        Display display = bubbleWindow.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        heightNew = (int) ((size.y * 100) / 128);
        widthMid = (int) (size.x / 2);
    }

    public void finish() {
        if (is_open) {
            if (!browserPages.isEmpty()) {
                getCurrentBrowserPage().performClick();
                // Minimizes before destroying
            }
        } else {
            bubbleWindow.removeView(bh.getView());
        }
        browserPages.clear();
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

//    @Override
//    public void onTouchEvent(@BubbleListener.TOUCH_EVENT_TYPE int event_type, @BubbleHead.HEAD_TYPE int head_type, int BId) {
//        Log.d(TAG, "Listener onEvent() called with: type = [" + event_type + "]");
//        if (isMenuOpen) {
//            //[ACTION_FLOW]bubbleWindow.removeView(getBrowserTab(current).action_overflow_view);
//            isMenuOpen = false;
//        }
//        switch (event_type) {
//            case TOUCH_EVENT_TYPE_ADD_DELETE:
//                deleteHead.setViewVisibility(View.VISIBLE);
//                break;
//            case TOUCH_EVENT_TYPE_DELETE:
//                switch (head_type) {
//                    case BubbleHead.HEAD_TYPE_MAIN:
//                        is_running = false;
//                        stopSelf();
//                        break;
//                    case BubbleHead.HEAD_TYPE_TAB:
//                        deletePage(getIndex(BId));
//                        break;
//                }
//                break;
//            case TOUCH_EVENT_TYPE_ON_DELETE:
//                switch (head_type) {
//                    case BubbleHead.HEAD_TYPE_MAIN:
//                        deleteHead.setLayoutParamsSize(Constant.BubbleSizeDeleteLarge);
//                        bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
//                        deleteHead.setLayoutParamsSize(Constant.BubbleSizeLarge);
//                        bubbleWindow.updateViewLayout(bh.getView(), deleteHead.getLayoutParams());
//                        break;
//                    case BubbleHead.HEAD_TYPE_TAB:
//                        deleteHead.setLayoutParamsSize(Constant.BubbleSizeDeleteLarge);
//                        bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
//                        deleteHead.setLayoutParamsSize(Constant.BubbleSizeLarge);
//                        bubbleWindow.updateViewLayout(browserPageArray[BId].getBubbleView(), deleteHead.getLayoutParams());
//                        break;
//                }
//                deleteHead.getLayoutParams().setSize(BubbleSizeDelete);
//                break;
//
//            case TOUCH_EVENT_TYPE_OFF_DELETE:
//                deleteHead.getLayoutParams().setSize(BubbleSizeDelete);
//                bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
//                break;
//            case TOUCH_EVENT_TYPE_REMOVE_DELETE:
//                deleteHead.getView().setVisibility(View.INVISIBLE);
//                break;
//            case TOUCH_EVENT_TYPE_UPDATE:
//                switch (head_type) {
//                    case BubbleHead.HEAD_TYPE_MAIN:
//                        bubbleWindow.updateViewLayout(bh.getView(), bh.getLayoutParams());
//                        break;
//                    case BubbleHead.HEAD_TYPE_TAB:
//                        bubbleWindow.updateViewLayout(browserPageArray[BId].getBubbleView(), browserPageArray[BId].getBubbleLayout());
//                        break;
//                }
//                break;
//            case TOUCH_EVENT_TYPE_REMOVE_BROWSER:
//                bubbleWindow.removeView(browserLayout);
//                browserLayout.removeView(getBrowserTab(current).getBrowserView());
//                Log.d(TAG, "removeview8");
//                break;
//            case TOUCH_EVENT_TYPE_ADD_BROWSER:
//                //Add view layout
//                //Add view to layout
//
//                browserLayout.addView(getBrowserTab(current).getBrowserView(), paramBrowser);
//                bubbleWindow.addView(browserLayout, paramBrowser);
//
//                //bubbleWindow.addView(getBrowserTab(current).browser, paramBrowser);
//                break;
//        }
//    }

//    @Override
//    public void onClickEvent(@BubbleListener.CLICK_EVENT_TYPE int event_type, @BubbleHead.HEAD_TYPE int head_type, int BId) {
//        Log.d(TAG, "onClickEvent() called with: event_type = [" + event_type + "], head_type = [" + head_type + "], BId = [" + BId + "]");
//        if (isMenuOpen) {
//            //[ACTION_FLOW]bubbleWindow.removeView(getBrowserTab(current).action_overflow_view);
//            isMenuOpen = false;
//        }
//        switch (event_type) {
//            case CLICK_EVENT_TYPE_EXPAND:
//                expandBrowser();
//                break;
//            case CLICK_EVENT_TYPE_MINIMIZE:
//                Log.d(TAG, "onClickEvent() called with: event_type = [" + event_type + "], head_type = [" + head_type + "], BId = [" + BId + "], GetIndex = [" + getIndex(BId) + "]");
//                minimizeBrowser(getIndex(BId));
//                break;
//        }
//    }


//    public int getIndex(int BId) {
//        for (int i = 0; i < count; i++) {
//            if (arrIndex[i] == BId) {
//                return i;
//            }
//        }
//        Log.d(TAG, "getIndex() Error called with: BId = [" + BId + "]");
//        return -1;
//    }


//    public void shiftBubble(int index) {
//        getBrowserTab(index).setBubbleLayoutX(getBrowserTab(index).getBubbleLayoutX() - Constant.BubbleSizeLarge);
//    }

//    private BrowserPage getBrowserTab(int index) {
//        return browserPageArray[arrIndex[index]];
//    }

//    private void addBrowserTab(int index, BrowserPage browserPage) {
//        browserPageArray[arrIndex[index]] = browserPage;
//    }


    public void addTab(BubbleService bubbleService, String url) {
        is_running = true;

        final BrowserPage browserPage = new BrowserPage(context, 0, heightNew, widthMid, idCount++);
//        final int BId = arrIndex[count];
        browserPage.setPageListener(new PageListener() {
            @Override
            public void onMinimize() {
                minimizeBrowser();
            }
        });
//        addBrowserTab(count, browserPage);

        browserPage.setBubbleListener(new BubbleListener() {
            @Override
            public void onClick() {
                onHeadPress(browserPage);
            }

            @Override
            public void onDelete() {
                deleteHead.getView().setVisibility(View.INVISIBLE);
                deletePage(browserPage);
            }

            @Override
            public void updateView() {
                updateViewLayout(browserPage);
            }

            @Override
            public void onMove(boolean isMoving) {
                if (isMoving) {
                    enableDeleteView();
                } else {
                    disableDeleteView();
                }
            }

            @Override
            public void overDeleteArea(boolean isOver) {
                if (isOver) {
                    enableOverDelete(browserPage);
                } else {
                    disableOverDelete();
                }
            }
        });


//        getBrowserTab(count).setListener(this);

        browserPage.loadUrl(url);

        browserPages.add(browserPage);
        if (browserPages.size() == 1) {
            bubbleWindow.addView(bh.getView(), bh.getLayoutParams());    // Adds main bubble head to the display
        } else {
            if (is_open) {
                getCurrentBrowserPage().performClick();
            }
        }

    }

    public void enableOverDelete(BrowserPage browserPage){
        deleteHead.setLayoutParamsSize(Constant.BubbleSizeDeleteLarge);
        bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
        deleteHead.setLayoutParamsSize(Constant.BubbleSizeLarge);
        bubbleWindow.updateViewLayout(browserPage.getBubbleView(), deleteHead.getLayoutParams());
    }

    public void disableOverDelete(){
        deleteHead.getLayoutParams().setSize(BubbleSizeDelete);
        bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
    }

    public void enableDeleteView(){
        deleteHead.setViewVisibility(View.VISIBLE);
        bubbleWindow.removeView(browserLayout);
        removeView(getCurrentBrowserPage());
    }

    public void disableDeleteView(){
        deleteHead.getView().setVisibility(View.INVISIBLE);
        bubbleWindow.updateViewLayout(getCurrentBrowserPage().getBubbleView(), getCurrentBrowserPage().getBubbleLayout());
        browserLayout.addView(getCurrentBrowserPage().getBrowserView(), paramBrowser);
        bubbleWindow.addView(browserLayout, paramBrowser);
    }

    public BrowserPage getCurrentBrowserPage(){
        BrowserPage browserPage;
        for (Iterator<BrowserPage> iter = browserPages.iterator(); iter.hasNext(); ) {
            browserPage = iter.next();
            if(browserPage.getId()==defaultPage)
                return browserPage;
        }
        return null;
    }

    public void removeView(BrowserPage browserPage){
        browserLayout.removeView(browserPage.getBrowserView());
    }

    public void updateViewLayout(BrowserPage browserPage){
        bubbleWindow.updateViewLayout(browserPage.getBubbleView(), browserPage.getBubbleLayout());
    }

    public void expandBrowser(BrowserPage browserPage) {
        if (is_running) {
            if (!is_open) {
                mHomeWatcher.startWatch();
                bubbleWindow.removeView(bh.getView());

                if(browserPage!=null)
                    defaultPage = browserPage.getId();
                else
                    defaultPage = browserPages.get(browserPages.size()-1).getId();

                int x = -BubbleSizeLarge;
                for (Iterator<BrowserPage> iter = browserPages.iterator(); iter.hasNext(); ) {
                    browserPage = iter.next();
                    browserPage.bubbleHead.initParams(x+BubbleSizeLarge, heightNew);
                    bubbleWindow.addView(browserPage.getBubbleView(),browserPage.getBubbleLayout());
                }

                getCurrentBrowserPage().switchToLarge();
                addBrowser(getCurrentBrowserPage());
                is_open = true;

            } else {
                swapDefaultPage(browserPage);
            }
        }
    }

    public void swapDefaultPage(BrowserPage browserPage){
        browserLayout.removeView(getCurrentBrowserPage().getBrowserView());
        getCurrentBrowserPage().switchToSmall();
        defaultPage = browserPage.getId();
        getCurrentBrowserPage().switchToLarge();
        getCurrentBrowserPage().setWebViewKeyListener();
        browserLayout.addView(getCurrentBrowserPage().getBrowserView());
    }

    public void addBrowser(BrowserPage browserPage) {

        //Add view to layout
        browserLayout.addView(browserPage.getBrowserView(), paramBrowser);

//        if (index == current) {
//            bubbleWindow.addView(browserLayout, paramBrowser);
//        }

        browserPage.setWebViewKeyListener();
        bubbleWindow.addView(browserLayout, paramBrowser);
    }

    public void onHeadPress(BrowserPage browserPage){
        if (getCurrentBrowserPage().getId() == defaultPage) { //minimize everything
            minimizeBrowser();
        } else { //swap default tab
            swapTab(browserPage);
        }
    }

    public void swapTab(BrowserPage browserPage){
        browserPage.setWebViewKeyListener();
        browserLayout.addView(browserPage.getBrowserView(), paramBrowser);
        browserLayout.removeView(getCurrentBrowserPage().getBrowserView());
        getCurrentBrowserPage().switchToSmall();
        browserPage.switchToLarge();
        defaultPage = browserPage.getId();
    }

    public void minimizeBrowser() {

            BrowserPage browserPage;
            for (Iterator<BrowserPage> iter = browserPages.iterator(); iter.hasNext(); ) {
                browserPage = iter.next();
                bubbleWindow.removeView(browserPage.getBubbleView());
            }

            bubbleWindow.removeView(browserLayout);
            browserLayout.removeView(getCurrentBrowserPage().getBrowserView());
            getCurrentBrowserPage().switchToSmall();

            bubbleWindow.addView(bh.getView(), bh.getLayoutParams());
            is_open = false;
            mHomeWatcher.stopWatch();
    }

    public void deletePage(BrowserPage browserPage) {

        if(browserPages.size()==1){
            browserPages.clear();
            stopSelf();
        }

        bubbleWindow.removeView(browserPage.getBubbleView());

        Iterator<BrowserPage> iter = browserPages.iterator();
        BrowserPage temp;
        while (iter.hasNext()){
            temp = iter.next();
            if(temp.getId()==browserPage.getId())
                break;
        }
        while (iter.hasNext()){
            temp = iter.next();
            temp.bubbleHead.initParams(temp.bubbleHead.getLayoutParamsX()-BubbleSizeLarge, heightNew);
            bubbleWindow.updateViewLayout(temp.getBubbleView(),temp.getBubbleLayout());
        }

        if(browserPage.getId()==defaultPage){
            int tempId = browserPages.indexOf(getCurrentBrowserPage());
            if(tempId==0)
                defaultPage = 1;
            else
                defaultPage = tempId - 1;
            browserLayout.removeView(browserPage.getBrowserView());
            browserLayout.addView(getCurrentBrowserPage().getBrowserView(), paramBrowser);
            getCurrentBrowserPage().switchToLarge();
        }
        browserPages.remove(browserPage);
    }


    private void stopSelf() {
        Intent bubbleService = new Intent(context, BubbleService.class);
        context.stopService(bubbleService);
    }
}


