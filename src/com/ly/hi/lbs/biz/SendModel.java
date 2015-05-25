package com.ly.hi.lbs.biz;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;
import com.ly.hi.lbs.bean.LBSVolleyError;
import com.ly.hi.lbs.biz.base.BaseModel;
import com.ly.hi.lbs.common.BizInterface;
import com.ly.hi.lbs.request.CreatePoiReq;
import com.ly.hi.lbs.request.CreateTableReq;
import com.ly.hi.lbs.request.UpdatePoiReq;
import com.ly.hi.lbs.response.BaseResponseParams;
import com.ly.hi.lbs.response.CreatePoiRes;
import com.ly.hi.lbs.response.CreateTableRes;
import com.ly.hi.lbs.response.DeletePoiRes;
import com.ly.hi.lbs.response.DetailTablesRes;

public class SendModel extends BaseModel {
    private static final String TAG = "SendModel";
    private CreateTableReq mRequest;

    public SendModel(Handler handler, Context ctx, String tag, RequestQueue requestQueue) {
        super(handler, ctx, tag, requestQueue);
    }

    public void createGeotable(final CreateTableReq req) {
        String url = BizInterface.CREATE_GEOTABLE;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        BaseResponseParams<CreateTableRes> responseParams = new BaseResponseParams<CreateTableRes>();
                        CreateTableRes data = responseParams.parseResponseData(response, CreateTableRes.class);
                        responseParams.setObj(data);

                        BaseModel.onSuccess(handler, responseParams);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error", error.getCause());
                BaseModel.onFailed(handler, new LBSVolleyError(error));
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", req.getName());
                params.put("geotype", req.getGeotype());
                params.put("is_published", req.getIs_published());
                params.put("ak", BizInterface.BAIDU_LBS_AK);
                return params;
            }
        };

        addRequest(request);
    }


    public void createPoi(final CreatePoiReq req) {
        String url = BizInterface.CREATE_POI;


        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        BaseResponseParams<CreatePoiRes> responseParams = new BaseResponseParams<CreatePoiRes>();
                        CreatePoiRes data = responseParams.parseResponseData(response, CreatePoiRes.class);
                        responseParams.setObj(data);

                        BaseModel.onSuccess(handler, responseParams);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error", error.getCause());
                BaseModel.onFailed(handler, new LBSVolleyError(error));
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", req.getTitle());
                params.put("address", req.getAddress());
                params.put("tags", req.getTags());
                params.put("latitude", req.getLatitude());
                params.put("longitude", req.getLongitude());
                params.put("coord_type", req.getCoord_type());
                params.put("geotable_id", req.getGeotable_id());
                params.put("ak", BizInterface.BAIDU_LBS_AK);
                return params;
            }
        };

        addRequest(request);
    }
    
    public void updatePoi(final UpdatePoiReq req) {
        String url = BizInterface.UPDATE_POI;


        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        BaseResponseParams<UpdatePoiReq> responseParams = new BaseResponseParams<UpdatePoiReq>();
                        UpdatePoiReq data = responseParams.parseResponseData(response, UpdatePoiReq.class);
                        responseParams.setObj(data);

                        BaseModel.onSuccess(handler, responseParams);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error", error.getCause());
                BaseModel.onFailed(handler, new LBSVolleyError(error));
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", req.getId());
                params.put("title", req.getTitle());
                params.put("address", req.getAddress());
//                params.put("tags", req.getTags());
                params.put("latitude", req.getLatitude());
                params.put("longitude", req.getLongitude());
                params.put("coord_type", req.getCoord_type());
                params.put("geotable_id", req.getGeotable_id());
                params.put("ak", BizInterface.BAIDU_LBS_AK);
                return params;
            }
        };

        addRequest(request);
    }

    
    public  void detailGeotable(String title, String tags){
    	String url = BizInterface.DETAIL_GEOTABLE + title + "&tags=" + tags;
    	
    	StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        BaseResponseParams<DetailTablesRes> responseParams = new BaseResponseParams<DetailTablesRes>();
                        DetailTablesRes data = responseParams.parseResponseData(response, new TypeToken<DetailTablesRes>(){}.getType());
                        responseParams.setObj(data);

                        BaseModel.onSuccess(handler, responseParams);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error", error.getCause());
                BaseModel.onFailed(handler, new LBSVolleyError(error));
            }
        });
    	
    	addRequest(request);
    }
    
    public void deletePoiByTitle(final String title) {
        String url = BizInterface.DELETE_POI;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        BaseResponseParams<DeletePoiRes> responseParams = new BaseResponseParams<DeletePoiRes>();
                        DeletePoiRes data = responseParams.parseResponseData(response, DeletePoiRes.class);
                        responseParams.setObj(data);

                        BaseModel.onSuccess(handler, responseParams);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error", error.getCause());
                BaseModel.onFailed(handler, new LBSVolleyError(error));
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", title);
                params.put("geotable_id", BizInterface.BAIDU_LBS_GEOTABLE_ID);
                params.put("ak", BizInterface.BAIDU_LBS_AK);
                return params;
            }
        };
        
        addRequest(request);
    }
}

