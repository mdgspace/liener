package com.apps.my.liener;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by rahul on 18/9/16.
 */
public class BubbleHead {
    ImageView bubbleHead;
    public BubbleHead(Context context){
        bubbleHead = new ImageView(context);
        bubbleHead.setImageResource(R.mipmap.bubblesmall);
    }
}
