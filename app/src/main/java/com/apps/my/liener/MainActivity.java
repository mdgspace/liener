package com.apps.my.liener;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button startServ;
    Button stopServ;
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
                        startService(new Intent(getBaseContext(),BubbleService.class));

                    }
                }
        );
        stopServ.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopService(new Intent(getBaseContext(),BubbleService.class));

                    }
                }
        );
    }
}