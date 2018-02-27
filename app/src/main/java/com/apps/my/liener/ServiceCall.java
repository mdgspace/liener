package com.apps.my.liener;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class ServiceCall extends Activity {
    TextView test;
    private static final String TAG = ServiceCall.class.getSimpleName();
    int PERMISSION_CODE = 123;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with: " + "savedInstanceState = [" + savedInstanceState + "]");
        intent = getIntent();
        //getPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermissions();
        } else
            startService();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                askPermissions();
            } else {
                startService();
            }
        }
    }

    private void askPermissions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please give permission to use the app")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 111);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void startService() {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            Intent urlIntent = new Intent(this, BubbleService.class);
            urlIntent.putExtra("url", data);
            startService(urlIntent);
            finish();
        } else if (Intent.ACTION_PROCESS_TEXT.equals(action)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String query = getIntent()
                        .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString();
                String formattedQuery = query.replace(" ","+");
                String url = "https://www.google.co.in/search?q=" + formattedQuery;
                Intent urlIntent = new Intent(this, BubbleService.class);
                urlIntent.putExtra("url", url);
                startService(urlIntent);
                finish();
            }


        }
    }

    private void getPermission(String permission) {
        if (isPermissionAllowed(permission)) {
            //If permission is already having then showing the toast
            startService();
            //Toast.makeText(ServiceCall.this,"You already have the permission",Toast.LENGTH_LONG).show();
            //Existing the method with retur
        } else {

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
    private void requestPermission(String permission) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                startService();
                //Toast.makeText(this,"Permission granted",Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                //Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

}