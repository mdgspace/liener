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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button startServ;
    Button stopServ;
    private static final String TAG = MainActivity.class.getSimpleName();
    int c = 0;

    public final static String EXTRA_MESSAGE = "MESSAGE";
    private ListView obj;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startServ = (Button) findViewById(R.id.startService);
        stopServ = (Button) findViewById(R.id.stopService);

        startServ.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startService();
                        //getPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
                        Log.d(TAG, "onClick() permission called with: " + "v = [" + v + "]");
                        //
                        c = 1;

                    }
                }
        );
        stopServ.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopService(new Intent(getBaseContext(), BubbleService.class));
                        c = 0;
                    }
                }
        );


        mydb = new DBHelper(this);
        ArrayList array_list = mydb.getAllData(true);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);

        obj = (ListView) findViewById(R.id.listView1);
        obj.setAdapter(arrayAdapter);
//        obj.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                // TODO Auto-generated method stub
//                int id_To_Search = arg2 + 1;
//
//                Bundle dataBundle = new Bundle();
//                dataBundle.putInt("id", id_To_Search);
//
//                Intent intent = new Intent(getApplicationContext(),DisplayContact.class);
//
//                intent.putExtras(dataBundle);
//                startActivity(intent);
//            }
//        });
    }

//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        super.onOptionsItemSelected(item);
//
//        switch(item.getItemId())
//        {
//            case R.id.item1:Bundle dataBundle = new Bundle();
//                dataBundle.putInt("id", 0);
//
//                Intent intent = new Intent(getApplicationContext(),DisplayContact.class);
//                intent.putExtras(dataBundle);
//
//                startActivity(intent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    public void startService() {
        Intent urlIntent = new Intent(getBaseContext(), BubbleService.class);
        urlIntent.putExtra("url", "http://google.com/");
        startService(urlIntent);
    }

    private void getPermission(String permission) {
        if (isPermissionAllowed(permission)) {
            //If permission is already having then showing the toast
            startService();
            //Toast.makeText(MainActivity.this,"You already have the permission",Toast.LENGTH_LONG).show();
            Log.d(TAG, "getPermission() called with: " + "permission = [" + permission + "]");
            //Existing the method with retur
        } else {
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
    private void requestPermission(String permission) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Log.d(TAG, "requestPermission() denied previouslycalled with: " + "permission = [" + permission + "]");
        }
        Log.d(TAG, "requestPermission() called with: " + "permission = [" + permission + "]");
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{permission}, 123);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        Log.d(TAG, "onRequestPermissionsResult() called with: " + "requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults[0] + "]");
        if (requestCode == 123) {
            Log.d(TAG, "onRequestPermissionsResult() called with: " + "requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults.length + "]");
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