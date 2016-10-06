package com.apps.my.liener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
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


    public BrowserPage(Context context) {
        oldTitle="";
//        browserwv = new WebView(context);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        browser= li.inflate(R.layout.browser_page, null);
        browserwv = (WebView) browser.findViewById(R.id.webview);
        //browserwv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        browserwv.setBackgroundColor(Color.WHITE);
        browserwv.getSettings().setJavaScriptEnabled(true);
        browserwv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        //browser = new LinearLayout(context);
        //browser.setOrientation(LinearLayout.VERTICAL);

        //LinearLayout ll = new LinearLayout(context);
        //ll.setOrientation(LinearLayout.HORIZONTAL);


        //final TextView txtview = new TextView(context);
        //txtview.setMaxWidth(ll.getWidth()/2);

        //ll.addView(txtview);
        //browser.addView(ll);
        //browser.addView(browserwv);
        tv=(TextView) browser.findViewById(R.id.txtview);
        tv.setText("Loading ...");

        browserwv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                tv.setText(view.getTitle());
                //Log.d(TAG, "textview set" + txtview.getText() + "]");
            }
        });

        bubbleHead = new ImageView(context);
        bubbleHead.setImageResource(R.mipmap.bubblesmall);



//        final ProgressBar Pbar;
//        Pbar = new ProgressBar(context);

        browserwv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
//                if(progress < 100 && Pbar.getVisibility() == ProgressBar.GONE){
//                    //Pbar.setVisibility(ProgressBar.VISIBLE);
//                   // txtview.setVisibility(View.VISIBLE);
//                }
                if(progress <100){
                    Log.d(TAG, "onProgressChanged() called with: " + "view = [" + view + "], progress = [" + progress + "]");
                }
                //Pbar.setProgress(progress);
                if(progress == 100) {
                    Log.d(TAG, "onProgressChanged() called with: " + "view = [" + view + "], progress = [" + progress + "]");
                }
                if(oldTitle != browserwv.getTitle()){//getTitle has the newer Title
                    // get the Title
                    oldTitle = browserwv.getTitle();
                    tv.setText(oldTitle);
                }
            }
        });
    }
}
