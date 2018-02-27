package com.apps.my.liener;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by kaishu on 25/2/18.
 */

public class FileManager {
    File fileLocation;
    String fileName;
    Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    public void saveBitmap(Bitmap image, String fileName) {
        new backgroundFileManager(context, image).execute("/favicons", fileName);
    }

    public Uri getBitmapUri(String fileName) {
        File bitmapLoc = new File(context.getFilesDir() + "/favicons", fileName);
        if (bitmapLoc.exists()) {
            return Uri.fromFile(bitmapLoc);
        }
        return null;
    }

    public void downloadFile(String url, String userAgent, String contentDisposition, String mimeType) {

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimeType);
            String cookies = CookieManager.getInstance().getCookie(url);
            request.addRequestHeader("cookie", cookies);
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription("Downloading file...");
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
            DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(context, "Downloading File", Toast.LENGTH_LONG).show();

    }

    public void uploadFile() {

    }


    private static class backgroundFileManager extends AsyncTask<String, String, String> {
        Context c;
        Bitmap bitmap;

        private backgroundFileManager(Context c, Bitmap bitmap) {
            this.c = c;
            this.bitmap = bitmap;
        }

        private backgroundFileManager(Context c, Object o) {
            //use this for downloading other things
        }


        @Override
        protected String doInBackground(String... value) {

            File baseLoc = new File(c.getFilesDir() + (String) value[0]);
            if (!baseLoc.exists()) {
                baseLoc.mkdir();
            }
            File toBestored = new File(baseLoc, (String) value[1]);//location and filename
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(toBestored);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
