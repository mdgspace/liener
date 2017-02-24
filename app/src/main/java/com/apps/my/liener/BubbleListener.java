package com.apps.my.liener;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.util.EventListener;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by rahul on 3/12/16.
 */

public interface BubbleListener extends EventListener {

    @Retention(SOURCE)
    @IntDef({TOUCH_EVENT_TYPE_ADD_DELETE, TOUCH_EVENT_TYPE_DELETE, TOUCH_EVENT_TYPE_ON_DELETE, TOUCH_EVENT_TYPE_OFF_DELETE, TOUCH_EVENT_TYPE_REMOVE_DELETE, TOUCH_EVENT_TYPE_UPDATE, TOUCH_EVENT_TYPE_REMOVE_BROWSER, TOUCH_EVENT_TYPE_ADD_BROWSER})
    @interface TOUCH_EVENT_TYPE {
    }

    int TOUCH_EVENT_TYPE_ADD_DELETE = 0;
    int TOUCH_EVENT_TYPE_REMOVE_DELETE = 1;
    int TOUCH_EVENT_TYPE_ON_DELETE = 2;
    int TOUCH_EVENT_TYPE_DELETE = 3;
    int TOUCH_EVENT_TYPE_UPDATE = 4;
    int TOUCH_EVENT_TYPE_REMOVE_BROWSER = 5;
    int TOUCH_EVENT_TYPE_ADD_BROWSER = 6;
    int TOUCH_EVENT_TYPE_OFF_DELETE = 7;

    @Retention(SOURCE)
    @IntDef({CLICK_EVENT_TYPE_MINIMIZE, CLICK_EVENT_TYPE_EXPAND})
    @interface CLICK_EVENT_TYPE {
    }

    int CLICK_EVENT_TYPE_MINIMIZE = 0;
    int CLICK_EVENT_TYPE_EXPAND = 1;

    @Retention(SOURCE)
    @IntDef({EVENT_TYPE_ACTION_OVERFLOW})
    @interface EVENT_TYPE {
    }

    int EVENT_TYPE_ACTION_OVERFLOW = 0;


    void onTouchEvent(@TOUCH_EVENT_TYPE int event_type, @BubbleHead.HEAD_TYPE int head_type, int BId);

    void onClickEvent(@CLICK_EVENT_TYPE int event_type, @BubbleHead.HEAD_TYPE int head_type, int BId);

    void onEvent(@EVENT_TYPE int event_type);

    void onError(Throwable error);
}
