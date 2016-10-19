package com.apps.my.liener;

import android.app.Service;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.Image;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telecom.PhoneAccount;
import android.text.LoginFilter;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by RAHUL on 5/12/2016.
 */
public class BrowserPage {
    WebView browserwv;
    ImageView bubbleHead;
    String TAG = "BrowserPage";
    View browser;
    String oldTitle;
    TextView tv;
    DBHelper mydb;
    long id,ts;


    public BrowserPage(final Context context) {
        mydb = new DBHelper(context);
        oldTitle="";
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        browser= li.inflate(R.layout.browser_page, null);
        browserwv = (WebView) browser.findViewById(R.id.webview);
        //browserwv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        browserwv.setBackgroundColor(Color.WHITE);
        browserwv.getSettings().setJavaScriptEnabled(true);
        browserwv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        tv=(TextView) browser.findViewById(R.id.txtview);
        tv.setText("Loading ...");
        browser.findViewById(R.id.add_bookmark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=browserwv.getTitle();
                Log.d(TAG, "addBookmark() called with: " + name + "");
                if(name==""){
                    name=browserwv.getUrl();
                }
                mydb.insertContact(false,name, browserwv.getUrl(), String.valueOf(System.currentTimeMillis()/1000));
            }
        });

        browserwv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                tv.setText(view.getTitle());
                //Log.d(TAG, "textview set" + txtview.getText() + "]");
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                Log.d(TAG, "shouldOverrideUrlLoading() called with: " + "view = [" + view + "], url = [" + url + "]");
                ts = System.currentTimeMillis()/1000;
                id=mydb.insertContact(true,url, url, String.valueOf(ts));
                return false;
            }
        });

        bubbleHead = new ImageView(context);
        bubbleHead.setImageResource(R.mipmap.bubblesmall);
//        Paint paint=new Paint();
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(0x000000);
//        paint.setStrokeWidth(10);
//        RectF rectF = new RectF(20,20,40,70);
//        Bitmap bitmap= B
//                Bitmap.createBitmap(200,200,Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawArc(rectF,0,30,true,paint);
//        bitmap.
//        //bubbleHead.draw(canvas);
//        bubbleHead.setImageBitmap(bitmap);

       // bubbleHead = new MyCanvas(context,3);
         /*bubbleHead = new ImageView(context);


       Bitmap bitmap= Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint pbg = new Paint();
        pbg.setColor(Color.argb(255,255,255,255));
        pbg.setStrokeWidth(0);
        RectF rectF = new RectF(0,0,100,100);
        canvas.drawOval(rectF,pbg);
        bubbleHead.setImageBitmap(bitmap);
        for(int i=0;i<12;i++){
            Paint paint=new Paint();
            paint.setStrokeWidth(10);
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeJoin(Paint.Join.ROUND);

            switch (i){
                case 0 :            paint.setColor(Color.YELLOW); break;
                case 1 :             paint.setColor(Color.RED);  break;
                case 2 :            paint.setColor(Color.BLUE);     break;
                case 3 :            paint.setColor(Color.GREEN); break;
                case 4 :             paint.setColor(Color.MAGENTA);  break;
                case 5 :            paint.setColor(Color.GRAY);     break;
                case 6 :            paint.setColor(Color.YELLOW); break;
                case 7 :             paint.setColor(Color.RED);  break;
                case 8 :            paint.setColor(Color.BLUE);     break;
                case 9 :            paint.setColor(Color.GREEN); break;
                case 10 :             paint.setColor(Color.MAGENTA);  break;
                case 11 :            paint.setColor(Color.GRAY);     break;
            }
            canvas.drawArc(rectF,270+30*i,30,true,paint);

        }
        for(int i=0;i<12;i++){
            Paint paint=new Paint();
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(Color.WHITE);
            canvas.drawArc(rectF,270+30*i,30,true,paint);

        }

         */


        browserwv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                if(progress <100){
                    Log.d(TAG, "onProgressChanged() called with: " + "view = [" + view + "], progress = [" + progress + "]");
                }
                //Pbar.setProgress(progress);
                if(progress == 100) {
                    Log.d(TAG, "onProgressChanged() called with: " + "view = [" + view + "], progress = [" + progress + "]");
                }
                if(oldTitle != browserwv.getTitle()){//getTitle has the newer Title
                    // get the Title


                    //Log.d(TAG, "onProgressChanged() called with: " + "x = [" + x + "], progress = [" + mydb.getData((int)x) + "]");

                    oldTitle = browserwv.getTitle();
                    mydb.updateContact(true,(int)id,oldTitle,browserwv.getUrl(), String.valueOf(ts));
                    tv.setText(oldTitle);
                }
            }
        });
    }

    public void loadUrl(String url){
        browserwv.loadUrl(url);
        ts = System.currentTimeMillis()/1000;
        id=mydb.insertContact(true,url, url, String.valueOf(ts));
    }
}
