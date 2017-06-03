package com.apps.my.liener;

import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

import static com.apps.my.liener.Constant.BubbleInDelete;
import static com.apps.my.liener.Constant.BubbleSizeDeleteLarge;
import static com.apps.my.liener.Constant.BubbleSizeLarge;
import static com.apps.my.liener.Constant.BubbleSizeSmall;

/**
 * Created by rahul on 23/12/16.
 */
public class LayoutParams extends WindowManager.LayoutParams {
    private static final String TAG = LayoutParams.class.getSimpleName();
    private boolean isRightAlign;
    LayoutParams(){
        super(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        this.width = BubbleSizeSmall;
        this.height = BubbleSizeSmall;
        this.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        this.isRightAlign = true;
    }

    public void setSize(int size){
        this.y = this.y + (width - size)/2;
        this.width = size;
        this.height = size;
    }

    public boolean isRightAlign() {
        return isRightAlign;
    }

    public void setRightAlign(boolean right) {
        isRightAlign = right;
    }
}
