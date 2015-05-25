package com.ly.hi.lbs.http.params;

import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by yancey on 2015/4/22 0022.
 */
public class RequestParams extends BaseRequestParams {
    protected String recdata;      //请求数据主体

    public RequestParams(Object rquest) {
        try {
            this.recdata = new Gson().toJson(rquest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRecdata() {
        Log.d("RequestParams=", recdata);
        return recdata;
    }

    public void setRecdata(String recdata) {
        this.recdata = recdata;
    }
}
