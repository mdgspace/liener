package com.apps.my.liener;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by rahul on 3/12/16.
 */

public class BubbleHead implements View.OnTouchListener{
    private String TAG ="BubbleHead";
    public View view;
    public int height,widthMid;
    WindowManager.LayoutParams layoutParams;

    @Retention(SOURCE)
    @IntDef({HEAD_TYPE_DELETE, HEAD_TYPE_MAIN, HEAD_TYPE_TAB})
    public @interface HEAD_TYPE {}
    public static final int HEAD_TYPE_MAIN = 0;
    public static final int HEAD_TYPE_DELETE = 1;
    public static final int HEAD_TYPE_TAB = 2;

    int defaultType;

    public BubbleHead(Context context,int height, int widthMid,@HEAD_TYPE int head_type){
        defaultType=head_type;
        this.widthMid=widthMid;
        this.height=height;
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=li.inflate(R.layout.browser_bubblehead,null);
        if(defaultType!=HEAD_TYPE_DELETE){
            setListener();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch() called with: v = [" + v + "], event = [" + event + "]");
        return false;
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
        layoutParams.width = Constant.BubbleSizeDelete;
        layoutParams.height = Constant.BubbleSizeDelete;
        doInBackground();
    }


    BubbleListener fetchListener = null;

    private void doInBackground() {
        Log.d(TAG, "doInBackground() called");
        if (this.fetchListener != null)
            this.fetchListener.onComplete("result");
    }

    public void setListener(BubbleListener listener) {
        this.fetchListener = listener;
    }

    public void addDeleteView(){
        Log.d(TAG, "addDeleteView() called");
    }

    public void removeDeleteView(){
        Log.d(TAG, "removeDeleteView() called");
    }

    public void moveToDelete(){
        Log.d(TAG, "moveToDelete() called");
    }

    public void updateView(){
        Log.d(TAG, "updateView() called");
    }

    public void deleteFunction(){
        Log.d(TAG, "deleteFunction() called");
    }

    boolean onRightSide =true;

    public void setListener(){
        view.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = layoutParams.x;           initialY = layoutParams.y;
                            initialTouchX = event.getRawX();    initialTouchY = event.getRawY();
                            addDeleteView();
                            return false;
                        case MotionEvent.ACTION_MOVE:
                            if(!onRightSide&&defaultType==HEAD_TYPE_MAIN){
                                layoutParams.x = initialX + (int)(event.getRawX() - initialTouchX);
                            }
                            else{
                                layoutParams.x = initialX - (int)(event.getRawX() - initialTouchX);
                            }
                            layoutParams.y = initialY - (int)(event.getRawY() - initialTouchY);
                            if (onDeleteCheck()){
                                removeDeleteView();
                                moveToDelete();
                                addDeleteView();
                            } else {
                                updateView();
                            }
                            return false;
                        case MotionEvent.ACTION_UP:
                            removeDeleteView();
                            if (onDeleteCheck()) {
                                deleteFunction();
                                return true;
                            }
                            else  {
                                switch (defaultType){
                                    case HEAD_TYPE_DELETE:
                                        break;
                                    case HEAD_TYPE_MAIN:
                                        moveBubbleToSide();
                                        break;
                                    case HEAD_TYPE_TAB:
                                        moveBubbleToOldPosition(initialX,initialY);
                                        break;
                                }
                                updateView();
                                Log.d(TAG, "else in action_up");
                            }
                            return false;
                    }
                return false;
            }
        });
    }

    public boolean onDeleteCheck(){
        int y = (layoutParams.y - height/4);
        int x = (layoutParams.x - widthMid + Constant.BubbleSizeDelete/2);
        Log.d(TAG, "onDeleteCheck() called "+" height: "+height+" widthmid: "+widthMid+" x: "+x+" y: "+y);
       if ((x > -100 && x < 100) && (y > -100 && y < 100)) {
            Log.d("TESTING", "deleted");
            return true;
        }
        else return false;
    }

    public void moveBubbleToSide(){
        if(layoutParams.x>widthMid){
            if(onRightSide){
                layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                onRightSide=false;
            }
            else {
                layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                onRightSide=true;
            }
        }
        layoutParams.x=0;
    }

    public void moveBubbleToOldPosition(int initialX,int initialY){
        layoutParams.x=initialX;
        layoutParams.y=initialY;
    }
}
