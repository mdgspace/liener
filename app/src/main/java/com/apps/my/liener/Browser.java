package com.apps.my.liener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import static android.content.Context.WINDOW_SERVICE;
import static com.apps.my.liener.Constant.BubbleSizeDelete;

/**
 * Created by rahul on 5/6/17.
 */
public class Browser {
    private static final String TAG = Browser.class.getSimpleName();

    WindowManager bubbleWindow;
    WindowManager.LayoutParams paramBrowser, action_overflow_params;

    boolean is_open = false, is_running;
    int count = 0, current = 0, heightNew, widthMid;

    BrowserPage browserPageArray[] = new BrowserPage[20];
    int arrIndex[] = new int[20];

    BubbleHead bh, deleteHead;

    HomeWatcher mHomeWatcher;

    LinearLayout browserLayout;

    Context context;

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
        current = 0;
        count = 0;


        for (int i = 0; i < 20; i++) {
            arrIndex[i] = i;
        }

        bubbleWindow = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        loadDimensions();


    }

    private void initBubbleHead() {
        bh = new BubbleHead(context, heightNew, widthMid, BubbleHead.HEAD_TYPE_MAIN);
        bh.initParams(0, heightNew);
        bh.setBubbleListener(new BubbleListener() {
            @Override
            public void onClick() {
                expandBrowser();
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
                } else {
                    deleteHead.getView().setVisibility(View.INVISIBLE);
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
        deleteHead.initParams(0, (int) (heightNew / 4 - BubbleSizeDelete / 2), Gravity.BOTTOM | Gravity.CENTER);
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
                minimizeBrowser(current);
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
            if (current >= 0 && count > 0) {
                getBrowserTab(current).performClick();  // Minimizes before destroying
            }
        } else {
            bubbleWindow.removeView(bh.getView());
        }
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
        getBrowserTab(index).setBubbleLayoutX(getBrowserTab(index).getBubbleLayoutX() - Constant.BubbleSizeLarge);
    }

    private BrowserPage getBrowserTab(int index) {
        return browserPageArray[arrIndex[index]];
    }

    private void addBrowserTab(int index, BrowserPage browserPage) {
        browserPageArray[arrIndex[index]] = browserPage;
    }


    public void addTab(BubbleService bubbleService, String url) {
        is_running = true;

        BrowserPage browserPage = new BrowserPage(context, bubbleService, count * Constant.BubbleSizeLarge, heightNew, widthMid);
        final int BId = arrIndex[count];
        addBrowserTab(count, browserPage);
        getBrowserTab(count).setBubbleListener(new BubbleListener() {
            @Override
            public void onClick() {
                minimizeBrowser(getIndex(arrIndex[BId]));
            }

            @Override
            public void onDelete() {
                deleteHead.getView().setVisibility(View.INVISIBLE);
                deletePage(getIndex(BId));
            }

            @Override
            public void updateView() {
                bubbleWindow.updateViewLayout(browserPageArray[BId].getBubbleView(), browserPageArray[BId].getBubbleLayout());
            }

            @Override
            public void onMove(boolean isMoving) {
                if (isMoving) {
                    deleteHead.setViewVisibility(View.VISIBLE);
                    bubbleWindow.removeView(browserLayout);
                    browserLayout.removeView(getBrowserTab(current).getBrowserView());
                } else {
                    deleteHead.getView().setVisibility(View.INVISIBLE);
                    bubbleWindow.updateViewLayout(browserPageArray[BId].getBubbleView(), browserPageArray[BId].getBubbleLayout());
                    browserLayout.addView(getBrowserTab(current).getBrowserView(), paramBrowser);
                    bubbleWindow.addView(browserLayout, paramBrowser);
                }
            }

            @Override
            public void overDeleteArea(boolean isOver) {
                if (isOver) {
                    deleteHead.setLayoutParamsSize(Constant.BubbleSizeDeleteLarge);
                    bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
                    deleteHead.setLayoutParamsSize(Constant.BubbleSizeLarge);
                    bubbleWindow.updateViewLayout(browserPageArray[BId].getBubbleView(), deleteHead.getLayoutParams());
                } else {
                    deleteHead.getLayoutParams().setSize(BubbleSizeDelete);
                    bubbleWindow.updateViewLayout(deleteHead.getView(), deleteHead.getLayoutParams());
                }
            }
        });


//        getBrowserTab(count).setListener(this);

        getBrowserTab(count).loadUrl(url);
        if (count == 0) {
            bubbleWindow.addView(bh.getView(), bh.getLayoutParams());    // Adds main bubble head to the display
        } else {
            if (is_open) {
                browserPageArray[current].performClick();
            }
        }
        count++;
    }

    public void expandBrowser() {
        if (is_running) {
            if (!is_open) {
                mHomeWatcher.startWatch();
                if (current == 0) {
                    getBrowserTab(0).switchToLarge();
                }
                bubbleWindow.removeView(bh.getView());

                for (int i = 0; i < count; i++) {
                    bubbleWindow.addView(getBrowserTab(i).getBubbleView(), getBrowserTab(i).getBubbleLayout());
                }
                addBrowser(current);
                is_open = true;
            } else {
                minimizeBrowser(0);
            }
        }
    }

    public void addBrowser(final int index) {

        //Add view to layout
        browserLayout.addView(getBrowserTab(index).getBrowserView(), paramBrowser);

        if (index == current) {
            bubbleWindow.addView(browserLayout, paramBrowser);
        }

        getBrowserTab(index).setWebViewKeyListener();
    }

    public void minimizeBrowser(int index) {
        if (current == index) {
            for (int i = 0; i < count; i++) {
                bubbleWindow.removeView(getBrowserTab(i).getBubbleView());
            }

            bubbleWindow.removeView(browserLayout);
            browserLayout.removeView(getBrowserTab(current).getBrowserView());

            bubbleWindow.addView(bh.getView(), bh.getLayoutParams());
            is_open = false;
            mHomeWatcher.stopWatch();
        } else {
            addBrowser(index);

            //Remove view from layout
            browserLayout.removeView(getBrowserTab(current).getBrowserView());

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

        int temp = arrIndex[index];
        for (int i = index; i < count - 1; i++) {
            arrIndex[i] = arrIndex[i + 1];
        }
        arrIndex[count - 1] = temp;
        count--;

        for (int i = index; i < count; i++) {
            shiftBubble(i);
        }
        if (current >= count) {
            current = 0;
            getBrowserTab(0).switchToLarge();
            bubbleWindow.updateViewLayout(getBrowserTab(0).getBubbleView(), getBrowserTab(0).getBubbleLayout());
        } else if (arrIndex[count] == current_BId) {
            getBrowserTab(current).switchToLarge();
        } else {
            current = getIndex(current_BId);
        }
        for (int i = index; i < count; i++) {
            bubbleWindow.updateViewLayout(getBrowserTab(i).getBubbleView(), getBrowserTab(i).getBubbleLayout());
        }

        if (current >= 0 && count > 0) {
            browserLayout.addView(getBrowserTab(current).getBrowserView(), paramBrowser);
            bubbleWindow.addView(browserLayout, paramBrowser);

        } else {
            mHomeWatcher.stopWatch();
            stopSelf();
        }
    }

//    @Override
//    public void onEvent(@BubbleListener.EVENT_TYPE int event_type) {
//        switch (event_type) {
//            case EVENT_TYPE_ACTION_OVERFLOW:
//                minimizeBrowser(current);
//                break;
//        }
//    }
//
//    @Override
//    public void onError(Throwable error) {
//
//    }

    private void stopSelf() {
        Intent bubbleService = new Intent(context, BubbleService.class);
        context.stopService(bubbleService);
    }

}
