package com.apps.my.liener;

import java.util.EventListener;

/**
 * Created by rahul on 3/12/16.
 */

public interface BubbleListener extends EventListener{
    void onComplete(String item);
    void onError(Throwable error);
}
