package com.apps.my.liener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.UUID;


/**
 * Created by RAHUL on 5/12/2016.
 */
public class BrowserPage {

    private WebView browserwv;
    public BubbleHead bubbleHead;
    private static final String TAG = BrowserPage.class.getSimpleName();
    private RelativeLayout browser;
    private String oldTitle = " ";
    private TextView tv, result_text;
    private DBHelper mydb;
    private long id, ts;
    private Canvas canvas;
    private RectF rectF;
    private Paint paint;
    private Context context;
    private Page page;
    private ActionOverflowMenu action_overflow_view;

    private EditText queryText;
    private Button findClose, preFind, nextFind;
    private int totalResult;
    private int curResult;

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

    FrameLayout browserPane;
    LinearLayout permiDialog;
    Button permiAc, permiDc;
    int permiToAsk;
    String faviconId;
    public BrowserPage(final Context context, int x, int height, int widthMid) {
        this.context = context;
        mydb = DBHelper.init(context);
        faviconId = UUID.randomUUID().toString();
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        browser = (RelativeLayout) li.inflate(R.layout.browser_page, null);
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


        browser.findViewById(R.id.share_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareText(browserwv.getTitle(), browserwv.getUrl());
            }
        });

        action_overflow_view = new ActionOverflowMenu(context);

        browserPane = (FrameLayout) browser.findViewById(R.id.browser_pane);
        permiDialog = (LinearLayout) browser.findViewById(R.id.permiDialog);
        permiDialog.setVisibility(View.INVISIBLE);
        permiAc = (Button) browser.findViewById(R.id.permiAc);
        permiDc = (Button) browser.findViewById(R.id.permiDc);

        action_overflow_view.setMenuOptionListener(new ActionOverflowMenu.MenuOptionListener() {
            @Override
            public void onOptionClicked(int resourceId) {
                switch (resourceId) {
                    case R.id.find_in_page:
                        Log.d(TAG, "onOptionClicked() called with: resourceId = [find in page]");
                        browser.findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
                        result_text.setText("0 of 0");
                        queryText.setText("");
                        break;
                    case R.id.open_in:
                        Log.d(TAG, "onOptionClicked() called with: resourceId = [open in other browser]");
                        openInOtherBrowser();
                        break;
                    case R.id.desktop_site:
                        Log.d(TAG, "onOptionClicked() called with: resourceId = [ desktop site]");
                        switchUA();
                        break;
                    default:
                        break;
                }
                closeActionOverflowMenu();
            }
        });

        action_overflow_view.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                    closeActionOverflowMenu();
            }
        });

        browser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });

        browser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                browser.requestFocus();
                return false;
            }

        });


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


        browserwv = (WebView) browser.findViewById(R.id.webview);
        setBrowser();
        setFindBar();

        action_overflow_view.setFocusableInTouchMode(true);
    }

    private void closeActionOverflowMenu() {
        if (action_overflow_view.isOpen()) {
            action_overflow_view.setOpen(false);
            browser.removeView(action_overflow_view);
        }
    }

    private void openActionOverflowMenu() {
        if (!action_overflow_view.isOpen()) {
            browser.addView(action_overflow_view, action_overflow_view.getParams());
            action_overflow_view.setOpen(true);
            action_overflow_view.requestFocus();
        }
    }

    private void openInOtherBrowser() {
        pageListener.onMinimize();
        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(browserwv.getUrl()));
        openUrlIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

            @Override
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
                    page = new Page(oldTitle, url, String.valueOf(ts), faviconId);
                } else {
                    page = new Page(oldTitle, url, String.valueOf(ts), faviconId);
                }
                id = mydb.insertPage(true, page);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading() called with: " + "view = [" + view + "], url = [" + url + "]");

                return false;
            }
        });
        browserwv.setWebChromeClient(new WebChromeClient() {
            @Override
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
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                if (icon != null) {
                    FileManager bitmapManager = new FileManager(context);
                    bitmapManager.saveBitmap(icon, faviconId);
                } else {
                    faviconId = "noFavicon";
                }
            }
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                   final String RESOURCE_AUDIO_CAPTURE = "android.webkit.resource.AUDIO_CAPTURE";
                   final String RESOURCE_MIDI_SYSEX = "android.webkit.resource.MIDI_SYSEX";
                   final String RESOURCE_PROTECTED_MEDIA_ID = "android.webkit.resource.PROTECTED_MEDIA_ID";
                   final String RESOURCE_VIDEO_CAPTURE = "android.webkit.resource.VIDEO_CAPTURE";
                    switch (request.getResources()[0]) {
                        case RESOURCE_VIDEO_CAPTURE: permiToAsk = 1;
                        case RESOURCE_AUDIO_CAPTURE: permiToAsk = 2;
                        case RESOURCE_MIDI_SYSEX: permiToAsk = 3;
                        case RESOURCE_PROTECTED_MEDIA_ID: permiToAsk = 1;
                    }
                    buttonListener(request,permiToAsk);
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
        bubbleHead.setBubbleListener(bl);
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

    boolean desktopView = false;
    String mobileUA;

    private void switchUA() {
        if (desktopView) {
            browserwv.getSettings().setUserAgentString(mobileUA);
            browserwv.reload();
            desktopView = false;
        } else {
            String desktopUA = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
            mobileUA = browserwv.getSettings().getUserAgentString();
            browserwv.getSettings().setUserAgentString(desktopUA);
            browserwv.reload();
            desktopView = true;
        }
    }

    private PageListener pageListener;

    public void setPageListener(PageListener pageListener) {
        this.pageListener = pageListener;
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
    private void setFindBar() {
        browserwv.setFindListener(new WebView.FindListener() {
            @Override
            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                curResult = activeMatchOrdinal + 1;
                totalResult = numberOfMatches;
                if (numberOfMatches == 0)
                    curResult = 0;
                result_text.setText(curResult + " of " + totalResult);
            }

        });
        browser.findViewById(R.id.search_layout).setVisibility(View.GONE);
        queryText = (EditText) browser.findViewById(R.id.query_text);
        result_text = (TextView) browser.findViewById(R.id.result_text);
        findClose = (Button) browser.findViewById(R.id.close_btn);
        preFind = (Button) browser.findViewById(R.id.pre_btn);
        nextFind = (Button) browser.findViewById(R.id.next_btn);

        queryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                browserwv.findAllAsync(queryText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browser.findViewById(R.id.search_layout).setVisibility(View.GONE);
                browserwv.clearMatches();
            }
        });
        preFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browserwv.findNext(false);
                //result_text.setText(curResult + " of " +totalResult);
            }
        });
        nextFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browserwv.findNext(true);
                //result_text.setText(curResult + " of " +totalResult);
            }
        });
    }
    public void buttonListener (final PermissionRequest req,final int tempPermiCount) {
        permiDialog.setVisibility(View.VISIBLE);
        permiAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences perValue = context.getSharedPreferences("Permissions", Context.MODE_PRIVATE);
                Boolean perStatus = perValue.getBoolean(Integer.toString(tempPermiCount), false);
                if (!perStatus) {
                    Intent i = new Intent(context, PermissionManager.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("perNum", tempPermiCount);
                    context.startActivity(i);
                } else {
                    req.grant(req.getResources());
                    permiDialog.setVisibility(View.INVISIBLE);
                }
            }
        });
        permiDc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                req.deny();
                permiDialog.setVisibility(View.INVISIBLE);
            }
        });




        /* PermissionManager pM = new PermissionManager();
        int tempResult;
        tempResult = pM.systemPermiManager( 1, new PermissionManager.TempInter(){
            @Override
            public void tempMeth(int a) {
                if (a == 200) {
                    req.grant(req.getResources());
                } else {
                    req.deny();
                }
            }
        });
        if (tempResult == 200) {
            req.grant(req.getResources());
        }
        return;*/
        /*permiDialog.setVisibility(View.VISIBLE);
        permiAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] temp = new String[1];
             //   temp[0] = req.getResources()[permiToAsk];
                req.grant(req.getResources());
                permiDialog.setVisibility(View.INVISIBLE);
            }
        });
        permiDc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] temp = new String[1];
//                temp[0] = req.getResources()[permiToAsk];
                req.deny();
                permiDialog.setVisibility(View.INVISIBLE);
            }
        });
   /*     permiToAsk = tempPermiCount;
        if (permiToAsk >= 0) {
            permiDialog.setVisibility(View.VISIBLE);
            permiAc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] temp = new String[1];
                    temp[0] = req.getResources()[permiToAsk];
                    req.grant(temp);
                    permiDialog.setVisibility(View.INVISIBLE);
                    buttonListener(req,permiToAsk--);
                }
            });
            permiDc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] temp = new String[1];
                    temp[0] = req.getResources()[permiToAsk];
                    req.grant(temp);
                    permiDialog.setVisibility(View.INVISIBLE);
                    buttonListener(req,permiToAsk--);
                }
            });
        }*/
    }
}
