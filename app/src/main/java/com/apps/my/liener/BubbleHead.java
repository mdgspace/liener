package com.apps.my.liener;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by rahul on 3/12/16.
 */

class BubbleHead{
    public View view;
    WindowManager.LayoutParams layoutParams;
    public BubbleHead(Context context){
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=li.inflate(R.layout.browser_bubblehead,null);
    }
    public void setProgressVisibility( int visibility){
        // visibility can be View.VISIBLE or View.INVISIBLE
        view.findViewById(R.id.progressBar).setVisibility(visibility);
    }
    public void initParams(int x,int y){
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.width = 100;
        layoutParams.height = 100;
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        layoutParams.x = x;
        layoutParams.y = y;
    }

    public void switchToSmall(){
        layoutParams.width = Constant.BubbleSizeSmall;
        layoutParams.height = Constant.BubbleSizeSmall;
    }

    public void switchToLarge(){
        layoutParams.width = Constant.BubbleSizeLarge;
        layoutParams.height = Constant.BubbleSizeLarge;
    }

    public void switchToDelete(){
       ImageView delete = (ImageView) view.findViewById(R.id.headimage);
        delete.setImageResource(R.mipmap.delete);
    }


//    BubbleListener fetchListener = null;
//
//    private void doInBackground(URL url) {
//        ...
//        if (this.fetchListener != null)
//            this.fetchListener.onComplete(result);
//    }
//
//    public void setListener(IAsyncFetchListener listener) {
//        this.fetchListener = listener
//    }
}
