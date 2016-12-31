package com.apps.my.liener;

/**
 * Created by rahul on 28/12/16.
 */

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.util.EventListener;

import static java.lang.annotation.RetentionPolicy.SOURCE;
/**
 * Created by rahul on 3/12/16.
 */

public interface DbListener{

//    @Retention(SOURCE)
//    @IntDef({EVENT_TYPE_HISTORY_CHANGED, EVENT_TYPE_BOOKMARKS_CHANGED})
//    @interface EVENT_TYPE {
//    }
//
//    int EVENT_TYPE_HISTORY_CHANGED = 0;
//    int EVENT_TYPE_BOOKMARKS_CHANGED = 1;

    void onDataChanged();

    void onError(Throwable error);
}
