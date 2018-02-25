package com.apps.my.liener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * Created by kaishu on 1/2/18.
 */

public class PermissionManager extends Activity {
    final String CAMERA = "CAMERA";
    final String AUDIO = "RECORD_AUDIO";
    final String STORAGE = "WRITE_EXTERNAL_STORAGE";
    SharedPreferences.Editor setPermiStatus;
    int perNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setPermiStatus = getSharedPreferences("Permissions", MODE_PRIVATE).edit();
        perNumber = this.getIntent().getIntExtra("perNum", 0);
        if (perNumber != 0) {
             if(checkAndDecide(perNumber) == 200) {
                 finish();
             }
        }
        setPermiStatus.putBoolean(Integer.toString(perNumber), false);
        setPermiStatus.apply();
    }

    private int checkAndDecide(int i) {
        String tempPer;
        switch (i) {
        case 1: tempPer = CAMERA; break;
        case 2: tempPer = AUDIO; break;
        case 3: tempPer = STORAGE; break;
        default: tempPer = "";
        }

        if (checkPermission(tempPer)) {
            setPermiStatus.putBoolean(Integer.toString(perNumber), true);
            setPermiStatus.apply();
            return 200;
        }
        return 102;
    }

    private boolean checkPermission(String s) {

        int result = ContextCompat.checkSelfPermission(this, "android.permission."+s);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermission(s);
            return false;
        }
    }
    private void requestPermission(String s) {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission."+s}, 555);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 555:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setPermiStatus.putBoolean(Integer.toString(perNumber),true);
                    setPermiStatus.apply();
                    finish();
                } else {
                    setPermiStatus.putBoolean(Integer.toString(perNumber),false);
                    setPermiStatus.apply();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
