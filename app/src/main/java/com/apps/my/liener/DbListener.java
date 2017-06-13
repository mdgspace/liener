package com.apps.my.liener;

/**
 * Created by rahul on 28/12/16.
 */

public interface DbListener {

    void onDataChanged();

    void onError(Throwable error);
}
