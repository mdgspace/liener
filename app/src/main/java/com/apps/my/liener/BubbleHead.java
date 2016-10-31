package com.apps.my.liener;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by rahul on 18/9/16.
 */

public class BubbleHead {
    ImageView imageView;
    WindowManager.LayoutParams layoutParams;
    public BubbleHead(Context context,int x,int y,int imageResource){
        imageView = new ImageView(context);
        imageView.setImageResource(imageResource);
        initParams(x,y);
    }
    
    public void initParams(int x,int y){
        layoutParams = new WindowManager.LayoutParams(
                                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                                        WindowManager.LayoutParams.TYPE_TOAST,
                                                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                                        PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        layoutParams.x = x;
        layoutParams.y = y;
    }
}
