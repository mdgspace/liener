package com.apps.my.liener;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
/**
 * Created by RAHUL on 5/9/2016.
 */
public class BubbleService extends Service{
    WindowManager bubbleWindow;
    ImageView bubbleHead;
    WindowManager.LayoutParams par;

    @Override
    public void onCreate() {
        super.onCreate();

        bubbleWindow = (WindowManager) getSystemService(WINDOW_SERVICE);
        bubbleHead = new ImageView(this);
        bubbleHead.setImageResource(R.mipmap.bubble); //set bubble icon

        par = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );  // Parameters for layout

        par.x = 0;
        par.y = 120;

        bubbleHead.setOnTouchListener(new View.OnTouchListener() {
            int initialX;
            int initialY;
            float initialTouchX;
            float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = par.x;
                        initialY = par.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        par.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        par.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        bubbleWindow.updateViewLayout(bubbleHead, par);
                        return true;
                }
                return false;
            }
        });
        bubbleWindow.addView(bubbleHead, par);

    }


    public void onDestroy() {
        super.onDestroy();
        if (bubbleHead != null) {
            bubbleWindow.removeView(bubbleHead);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

}