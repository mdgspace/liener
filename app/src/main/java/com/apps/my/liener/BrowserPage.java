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
    WebView browser;
    ImageView bubbleHead;

    public BrowserPage(Context context) {
        browser = new WebView(context);
        browser.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        browser.setBackgroundColor(Color.WHITE);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        browser.setWebViewClient(new WebViewClient() {
        });

        bubbleHead = new ImageView(context);
        bubbleHead.setImageResource(R.mipmap.bubblesmall);

        final ProgressBar Pbar;
        final TextView txtview = new TextView(context);
        Pbar = new ProgressBar(context);

        browser.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if(progress < 100 && Pbar.getVisibility() == ProgressBar.GONE){
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                    txtview.setVisibility(View.VISIBLE);
                }

                Pbar.setProgress(progress);
                if(progress == 100) {
                    Pbar.setVisibility(ProgressBar.GONE);
                    txtview.setVisibility(View.GONE);
                }
            }
        });
    }
}
