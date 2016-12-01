package com.apps.my.liener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by rahul on 13/10/16.
 */
public class MyCanvas extends View {
    int curr_progress=2;

    public MyCanvas(Context context,int x){
        super(context);
        curr_progress=x;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint pbg = new Paint();
        pbg.setColor(Color.argb(255,245,245,245));
        pbg.setStrokeWidth(20);
        RectF rectF = new RectF(0,0,100,100);
        canvas.drawOval(rectF,pbg);
        for(int i=0;i<curr_progress;i++){
            Paint paint=new Paint();
//        paint.setStyle(Paint.Style.FILL);
            rectF = new RectF(0,0,90,90);
            paint.setColor(Color.YELLOW);
            canvas.drawArc(rectF,270+70*i,60,true,paint);

        }
//        RectF rectF = new RectF(20,20,40,70);
//        canvas.drawArc(rectF,0,30,true,paint);
    }

}
