package com.apps.my.liener;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;

public class ServiceCall extends Activity {
    TextView test;
    String TAG="tester";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with: " + "savedInstanceState = [" + savedInstanceState + "]");
        onNewIntent(getIntent());
    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        Log.d(TAG, "onNewIntent() called with: " + "intent = [" + intent + "]");
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            Intent urlIntent = new Intent(this, BubbleService.class);
            urlIntent.putExtra("url", data);
            startService(urlIntent);
            finish();
        }
    }
}