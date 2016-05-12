package com.apps.my.liener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button startServ;
    Button stopServ;
    int c=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startServ= (Button) findViewById(R.id.startService);
        stopServ= (Button) findViewById(R.id.stopService);

        startServ.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                   public void onClick(View v) {
                        if(c==0){
                            startService(new Intent(getBaseContext(),BubbleService.class));
                            c=1;
                        }
                    }
                }
        );
        stopServ.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(c==1){
                            stopService(new Intent(getBaseContext(), BubbleService.class));
                            c=0;
                        }
                    }
                }
        );
    }
}