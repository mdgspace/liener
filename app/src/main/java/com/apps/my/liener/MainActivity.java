package com.apps.my.liener;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button startServ;
    Button stopServ;
    String TAG="MainActivity";
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
                        getPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
                        Log.d(TAG, "onClick() permission called with: " + "v = [" + v + "]");
                            //
                            c=1;

                    }
                }
        );
        stopServ.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            stopService(new Intent(getBaseContext(), BubbleService.class));
                            c=0;
                        }
                    }
        );
    }

    public void startService(){
        Intent urlIntent = new Intent(getBaseContext(), BubbleService.class);
        urlIntent.putExtra("url", "http://google.com/");
        startService(urlIntent);
    }

    private void getPermission(String permission){
        if(isPermissionAllowed(permission)){
            //If permission is already having then showing the toast
            startService();
            //Toast.makeText(MainActivity.this,"You already have the permission",Toast.LENGTH_LONG).show();
            Log.d(TAG, "getPermission() called with: " + "permission = [" + permission + "]");
            //Existing the method with retur
        }
        else {
            Log.d(TAG, "else getPermission() called with: " + "permission = [" + permission + "]");
            //If the app has not the permission then asking for the permission
            requestPermission(permission);
        }
    }

    private boolean isPermissionAllowed(String permission) {
        //Getting the permission status
//        permission = Manifest.permission.READ_EXTERNAL_STORAGE
        int result = ContextCompat.checkSelfPermission(this, permission);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestPermission(String permission){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Log.d(TAG, "requestPermission() denied previouslycalled with: " + "permission = [" + permission + "]");
        }
        Log.d(TAG, "requestPermission() called with: " + "permission = [" + permission + "]");
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{permission},123);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        Log.d(TAG, "onRequestPermissionsResult() called with: " + "requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults[0] + "]");
        if(requestCode == 123){
            Log.d(TAG, "onRequestPermissionsResult() called with: " + "requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults.length + "]");
            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                startService();
                //Toast.makeText(this,"Permission granted",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                //Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }
}