package com.apps.my.liener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by RAHUL on 5/12/2016.
 */
public class BrowserPage {

    private WebView browserwv;
    public BubbleHead bubbleHead;
    private static final String TAG = BrowserPage.class.getSimpleName();
    private LinearLayout browser;
    private String oldTitle = " ";
    private TextView tv;
    private DBHelper mydb;
    private long id, ts;
    private Canvas canvas;
    private RectF rectF;
    private Paint paint;
    private int BId;
    private Context context;
    private Page page;
    private ActionOverflowMenu action_overflow_view;
    private boolean isActionMenuOpen = false;

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
//    int lColor=Color.argb(alphaColor,50,50,50)

    BubbleService BubbleServiceActivity;

    RelativeLayout browserPane;

    public BrowserPage(final Context context, BubbleService bubbleService, int x, int height, int widthMid) {
        this.context = context;
        BubbleServiceActivity = bubbleService;
        mydb = DBHelper.init(context);

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        browser = (LinearLayout) li.inflate(R.layout.browser_page, null);
        bubbleHead = new BubbleHead(context, height, widthMid, BubbleHead.HEAD_TYPE_TAB);
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

        action_overflow_view = new ActionOverflowMenu(context);
        action_overflow_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick() called with: view = [" + view + "]");
                Log.d(TAG, "onClick() called with: view = [" + view.getId() + "]");
            }
        });

        browserPane = (RelativeLayout) browser.findViewById(R.id.browser_pane);


        action_overflow_view.setMenuOptionListener(new ActionOverflowMenu.MenuOptionListener() {
            @Override
            public void onOptionClicked(int resourceId) {
                switch (resourceId) {
                    case R.id.find_in_page:
                        Log.d(TAG, "onOptionClicked() called with: resourceId = [" + resourceId + "]");
                        break;
                    case R.id.open_in:
                        openInOtherBrowser();
                        break;
                    case R.id.desktop_site:
                        Log.d(TAG, "onOptionClicked() called with: resourceId = [" + resourceId + "]");
                        break;
                    default:
                        break;
                }
                closeActionOverflowMenu();
            }
        });
//
//        action_overflow_view.setOnFocusChangeListener(new View.OnFocusChangeListener(){
//
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                Log.d(TAG, "onFocusChange() called with: view = [" + view + "], b = [" + b + "]");
//            }
//        });


        browser.findViewById(R.id.overflow_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (action_overflow_view.isOpen()) {
                    closeActionOverflowMenu();
                } else {
                    openActionOverflowMenu();
                }
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
//        });iono

        browserwv = (WebView) browser.findViewById(R.id.webview);
        setBrowser();


    }

    private void closeActionOverflowMenu() {
        browserPane.removeView(action_overflow_view);
        action_overflow_view.setOpen(false);
    }

    private void openActionOverflowMenu() {
        browserPane.addView(action_overflow_view, action_overflow_view.getParams());
        action_overflow_view.setOpen(true);
    }

    private void openInOtherBrowser() {
//        sendEvent(BubbleListener.EVENT_TYPE_ACTION_OVERFLOW);

        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(browserwv.getUrl()));
        context.startActivity(openUrlIntent);
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

            public void onPageStarted(WebView view,
                                      String url,
                                      Bitmap favicon) {
                Log.d(TAG, "onPageStarted() called with: view = [" + view + "], url = [" + url + "], favicon = [" + favicon + "]");
                ts = System.currentTimeMillis() / 1000;
                oldTitle = view.getTitle();
                if (oldTitle.isEmpty() || oldTitle == null || oldTitle.equals("") || oldTitle.equals("null")) {
                    oldTitle = url;
                }
                if (page == null) {
                    page = new Page(oldTitle, url, String.valueOf(ts), "logo");
                } else {
                    page = new Page(oldTitle, url, String.valueOf(ts), "logo");
                }
                id = mydb.insertPage(true, page);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading() called with: " + "view = [" + view + "], url = [" + url + "]");

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
                    Log.d(TAG, "onProgressChanged() oldtitle with: view = [" + view + "], progress = [" + progress + "]");
                    oldTitle = browserwv.getTitle();
                    if (oldTitle != null && !oldTitle.isEmpty() && !oldTitle.equals("null")) {
                        page.setTitle(oldTitle);
                        page.setUrl(browserwv.getUrl());
                        page.setTs(String.valueOf(ts));
                    }
                    mydb.updateContact(true, (int) id, page);
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
        if (page == null) {
            page = new Page(name, browserwv.getUrl(), String.valueOf(System.currentTimeMillis() / 1000), "logo");
        } else {
            page.setTitle(name);
            page.setUrl(browserwv.getUrl());
            page.setTs(String.valueOf(System.currentTimeMillis() / 1000));
        }
        mydb.insertPage(false, page);
    }

    public void loadUrl(String url) {
        browserwv.loadUrl(url);
        ts = System.currentTimeMillis() / 1000;
        Log.d(TAG, "loadUrl() called with: " + "url = [" + url + "]");
        if (page == null) {
            page = new Page(url, url, String.valueOf(ts), "logo");
        } else {
            page.setTitle(url);
            page.setUrl(url);
            page.setTs(String.valueOf(ts));
            page.setLogo("logo");
        }
        //id = mydb.insertPage(true, page);
    }

    public void shareText(String title, String body) {
        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        Intent typechooser = Intent.createChooser(sendIntent, "Choose sharing method");
        typechooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(typechooser);
//        BubbleServiceActivity.minimizeBrowser(BubbleServiceActivity.current);
    }

    public void switchToSmall() {
        bubbleHead.switchToSmall();
    }

    public void switchToLarge() {
        bubbleHead.switchToLarge();
    }

//    BubbleListener fetchListener = null;
//
//    public void setListener(BubbleListener listener) {
//        this.fetchListener = listener;
//    }

//    public void sendEvent(@BubbleListener.EVENT_TYPE int event_type) {
//        Log.d(TAG, "sendEvent() called with: event_type = [" + event_type + "]");
//        if (this.fetchListener != null)
//            this.fetchListener.onEvent(event_type);
//    }

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

    public void performClick() {
        bubbleHead.getView().performClick();
    }

    public View getBubbleView() {
        return bubbleHead.getView();
    }

    public LayoutParams getBubbleLayout() {
        return bubbleHead.getLayoutParams();
    }

    public void setBubbleListener(BubbleListener bl) {
        Log.d(TAG, "setBubbleListener() called with: bl = [" + bl + "]");
        bubbleHead.setBubbleListener(bl);
        Log.d(TAG, "setBubbleListener() called with: bl = [" + bl + "]");
    }

    public View getBrowserView() {
        return browser;
    }

    public void setWebViewKeyListener() {
        browserwv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey() called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (keyCode == KeyEvent.KEYCODE_HOME) {
                    Log.d(TAG, "onKey() called with: " + "v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
                }
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (browserwv.canGoBack()) {
                        browserwv.goBack();
                    } else {
                        bubbleHead.getView().performClick();
                    }
                }
                return false;
            }
        });
    }

    public BubbleHead getBubble() {
        return bubbleHead;
    }

    public int getBubbleLayoutX() {
        return bubbleHead.getLayoutParamsX();
    }

    public void setBubbleLayoutX(int x) {
        bubbleHead.setLayoutParamsX(x);
    }


//    public void changeIconProgress(int progress){
//                            int x = (int) (progress * 12 / 100);
//                            int x = (int) (progress * 12 / 100);f
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
