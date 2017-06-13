package com.apps.my.liener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by rahul on 19/1/17.
 */
public class Page implements Serializable {
    private static final String TAG = Page.class.getSimpleName();

    private String title;
    private String url;
    private String ts;
    private String logo;

    public Page(String title, String url, String ts, String logo) {
        this.title = title;
        this.url = url;
        this.ts = ts;
        this.logo = logo;
    }

    public Page(String data) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            try {
                this.title = jsonObject.getString("title");
                this.url = jsonObject.getString("url");
                this.ts = jsonObject.getString("ts");
                this.logo = jsonObject.getString("logo");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("url", url);
            jsonObject.put("ts", ts);
            jsonObject.put("logo", logo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
