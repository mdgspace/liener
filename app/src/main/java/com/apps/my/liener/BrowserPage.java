package com.apps.my.liener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import static java.security.AccessController.getContext;


/**
 * Created by RAHUL on 5/12/2016.
 */
public class BrowserPage {
    WebView browserwv;
    BubbleHead bubbleHead;
    private static final String TAG = BrowserPage.class.getSimpleName();
    View browser;
    String oldTitle = " ";
    TextView tv;
    DBHelper mydb;
    long id, ts;
    Canvas canvas;
    RectF rectF;
    Paint paint;
    int BId;
    Context context;

//    int alphaColor = 100;
//    int aColor=Color.argb(alphaColor,160,160,160);
//    int bColor=Color.argb(alphaColor,150,150,150);
//    int cColor=Color.argb(alphaColor,140,140,140);
//    int dColor=Color.argb(alphaColor,130,130,130);
//    int eColor=Color.argb(alphaColor,120,120,120);
//    int fColor=Color.argb(alphaColor,110,110,110);
//    int gColor=Color.argb(alphaColor,100,100,100);
//    int hColor=Color.argb(alphaColor,90,90,90);
//    int iColor=Color.argb(alphaColor,80,80,80);
//    int jColor=Color.argb(alphaColor,70,70,70);
//    int kColor=Color.argb(alphaColor,60,60,60);
//    int lColor=Color.argb(alphaColor,50,50,50);

    BubbleService BubbleServiceActivity;

    public BrowserPage(final Context context, BubbleService bubbleService, int x, int height, int widthMid, int BId) {
        this.BId = BId;
        this.context = context;
        BubbleServiceActivity = bubbleService;
        mydb = DBHelper.init(context);

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        browser = li.inflate(R.layout.browser_page, null);
        bubbleHead = new BubbleHead(context, height, widthMid, BubbleHead.HEAD_TYPE_TAB, BId);
        bubbleHead.initParams(x, height);

        tv = (TextView) browser.findViewById(R.id.txtview);
        tv.setText("Loading ...");

        browser.findViewById(R.id.add_bookmark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBookmark();
            }
        });

        //TODO Implement action_overflow touch listener
        browser.findViewById(R.id.share_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareText(browserwv.getTitle(), browserwv.getUrl());
            }
        });


//        final PopupMenu popup = new PopupMenu(context,browser.findViewById(R.id.overflow_menu));
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.browser_menu, popup.getMenu());
//
//        browser.findViewById(R.id.overflow_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popup.show();
//            }
//        });

        browserwv = (WebView) browser.findViewById(R.id.webview);
        setBrowser();


    }

    public void setBrowser() {
        browserwv.setBackgroundColor(Color.WHITE);
        browserwv.getSettings().setJavaScriptEnabled(true);
        browserwv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        browserwv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                tv.setText(view.getTitle());
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading() called with: " + "view = [" + view + "], url = [" + url + "]");
                ts = System.currentTimeMillis() / 1000;
                id = mydb.insertContact(true, url, url, String.valueOf(ts));
                return false;
            }
        });
        browserwv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                if (progress < 100) {

                    bubbleHead.setProgressVisibility(View.VISIBLE);
                    Log.d(TAG, "onProgressChanged() called with: " + "view = [" + view + "], progress = [" + progress + "]");

                }
                if (progress == 100) {
                    Log.d(TAG, "onProgressChanged() called with: " + "view = [" + view + "], progress = [" + progress + "]");
                    bubbleHead.setProgressVisibility(View.INVISIBLE);
                }
                if (oldTitle != browserwv.getTitle()) {
                    oldTitle = browserwv.getTitle();
                    if (oldTitle != null && !oldTitle.isEmpty() && !oldTitle.equals("null"))
                        mydb.updateContact(true, (int) id, oldTitle, browserwv.getUrl(), String.valueOf(ts));
                    tv.setText(oldTitle);
                }
            }
        });
    }

    public void addBookmark() {
        String name = browserwv.getTitle();
        Log.d(TAG, "addBookmark() called with: " + name + "");
        if (name == "") {
            name = browserwv.getUrl();
        }
        Log.d(TAG, "onClick() called with: " + "name = [" + name + "]" + browserwv.getUrl() + "");
        mydb.insertContact(false, name, browserwv.getUrl(), String.valueOf(System.currentTimeMillis() / 1000));

    }

    public void loadUrl(String url) {
        browserwv.loadUrl(url);
        ts = System.currentTimeMillis() / 1000;
        Log.d(TAG, "loadUrl() called with: " + "url = [" + url + "]");
        id = mydb.insertContact(true, url, url, String.valueOf(ts));
    }

    public void shareText(String title, String body) {
        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        Intent typechooser = Intent.createChooser(sendIntent, "Choose sharing method");
        typechooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(typechooser);
        BubbleServiceActivity.minimizeBrowser(BubbleServiceActivity.current);
    }

    public void switchToSmall() {
        bubbleHead.switchToSmall();
    }

    public void switchToLarge() {
        bubbleHead.switchToLarge();
    }

    public void createIcon() {
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
//        bubbleHead = new ImageView(context);
//
//
//        Bitmap bitmap= Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
//        canvas = new Canvas(bitmap);
//        Paint pbg = new Paint();
//        pbg.setColor(Color.argb(255,255,255,255));
//        pbg.setStrokeWidth(0);
//        rectF = new RectF(0,0,120,120);
//        canvas.drawOval(rectF,pbg);
//        rectF = new RectF(5,5,115,115);
//        bubbleHead.setImageBitmap(bitmap);
//        paint=new Paint();
//        paint.setStrokeWidth(10);
//        paint.setStrokeWidth(10);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setStrokeJoin(Paint.Join.ROUND);
//        for(int i=0;i<12;i++){
//            paint=new Paint();
//            paint.setStrokeWidth(10);
//            paint.setStrokeWidth(10);
//            paint.setStyle(Paint.Style.FILL);
//            paint.setStrokeJoin(Paint.Join.ROUND);
//
////            switch (i){
////                case 0 :            paint.setColor(aColor); break;
////                case 1 :             paint.setColor(bColor);  break;
////                case 2 :            paint.setColor(cColor);     break;
////                case 3 :            paint.setColor(dColor); break;
////                case 4 :             paint.setColor(eColor);  break;
////                case 5 :            paint.setColor(fColor);     break;
////                case 6 :            paint.setColor(gColor); break;
////                case 7 :             paint.setColor(hColor);  break;
////                case 8 :            paint.setColor(iColor);     break;
////                case 9 :            paint.setColor(jColor); break;
////                case 10 :             paint.setColor(kColor);  break;
////                case 11 :            paint.setColor(lColor);     break;
////            }
////            canvas.drawArc(rectF,270+30*i,27,true,paint);
//
//        }
    }

//    public void changeIconProgress(int progress){
//                            int x = (int) (progress * 12 / 100);
//                    Log.d(TAG, "onProgressChanged() called with: " + "x = [" + x + "], progress = [" + progress + "]");
//                    for (int i = 0; i <= x; i++) {
//                        switch (i) {
//                            case 0:
//                                paint.setColor(aColor);
//                                break;
//                            case 1:
//                                paint.setColor(bColor);
//                                break;
//                            case 2:
//                                paint.setColor(cColor);
//                                break;
//                            case 3:
//                                paint.setColor(dColor);
//                                break;
//                            case 4:
//                                paint.setColor(eColor);
//                                break;
//                            case 5:
//                                paint.setColor(fColor);
//                                break;
//                            case 6:
//                                paint.setColor(gColor);
//                                break;
//                            case 7:
//                                paint.setColor(hColor);
//                                break;
//                            case 8:
//                                paint.setColor(iColor);
//                                break;
//                            case 9:
//                                paint.setColor(jColor);
//                                break;
//                            case 10:
//                                paint.setColor(kColor);
//                                break;
//                            case 11:
//                                paint.setColor(lColor);
//                                break;
//                        }
//                        canvas.drawArc(rectF, 270 + 30 * i, 27, true, paint);
//                    }
//    }
}
