package com.ly.hi.lbs.http;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ly.hi.lbs.http.params.Params;
import com.ly.hi.lbs.http.params.RequestParams;
import com.ly.hi.lbs.http.params.ResponseParams;


/**
 * 文本数据请求
 * Created by yancey on 2015/4/22 0022.
 */
public class GsonRequest<T extends ResponseParams> extends Request<T> {
    private static String TAG = GsonRequest.class.getSimpleName();
    protected static final String PROTOCOL_CHARSET = "utf-8";
    private Object mParams;
    private Listener<T> mListener;
    private Type mType;
//    private String method;

    public GsonRequest(int way, String url, Map<String, String> params, Type type, Listener<T> listener, ErrorListener errorListener) {
        super(way, url, errorListener);
        this.mParams = params;
        this.mListener = listener;
        this.mType = type;
    }

    public GsonRequest(int way, String url, String method, Map<String, String> params, Type type, Listener<T> listener, ErrorListener errorListener) {
        super(way, url, errorListener);
        this.mParams = params;
        this.mListener = listener;
        this.mType = type;
    }

    public GsonRequest(int way, String url, Object params, Type type, Listener<T> listener, ErrorListener errorListener) {
        super(way, url, errorListener);
        this.mParams = params;
        this.mListener = listener;
        this.mType = type;
    }
    
    public GsonRequest(int way, String url, Type type, Listener<T> listener, ErrorListener errorListener) {
        super(way, url, errorListener);
        this.mListener = listener;
        this.mType = type;
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(15 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return retryPolicy;
    }

    /**
     * 请求参数
     *
     * @return
     * @throws AuthFailureError
     */
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        RequestParams requestParmas = new RequestParams(mParams);
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put(Params.AK, requestParmas.getAk());
        return parmas;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
//                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            HttpHeaderParser.parseCharset(response.headers));
            Gson gson = new Gson();
            Type type = new TypeToken<ResponseParams>() {
            }.getType();
            T responseParams = gson.fromJson(jsonString, type);
            responseParams.parseObj(jsonString, mType);
            return Response.success(responseParams,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }
}
