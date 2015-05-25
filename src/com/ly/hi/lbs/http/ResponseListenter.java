package com.ly.hi.lbs.http;

import com.android.volley.VolleyError;

public interface ResponseListenter<T> {
    void onSuccessResponse(T response);

    void onErrorResponse(VolleyError error);
}