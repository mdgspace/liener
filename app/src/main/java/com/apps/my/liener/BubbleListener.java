package com.apps.my.liener;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.util.EventListener;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by rahul on 3/12/16.
 */

public interface BubbleListener extends EventListener{

    @Retention(SOURCE)
    @IntDef({EVENT_TYPE_ADD_DELETE,EVENT_TYPE_DELETE,EVENT_TYPE_MOVE_DELETE,EVENT_TYPE_REMOVE_DELETE,EVENT_TYPE_UPDATE,EVENT_TYPE_REMOVE_BROWSER,EVENT_TYPE_ADD_BROWSER})
    @interface EVENT_TYPE {}
    int EVENT_TYPE_ADD_DELETE = 0;
    int EVENT_TYPE_REMOVE_DELETE = 1;
    int EVENT_TYPE_MOVE_DELETE = 2;
    int EVENT_TYPE_DELETE = 3;
    int EVENT_TYPE_UPDATE = 4;
    int EVENT_TYPE_REMOVE_BROWSER = 5;
    int EVENT_TYPE_ADD_BROWSER = 6;


    void onEvent(@EVENT_TYPE int event_type, @BubbleHead.HEAD_TYPE int head_type, int BId);
    void onError(Throwable error);
}
