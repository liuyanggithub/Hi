package com.ly.hi.lbs.http.params;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by yancey on 2015/4/22 0022.
 */
public class ResponseParams<T> extends BaseResponseParams {
    private T obj;   //解析后的对象

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public void parseObj(String response, Type type) {
        try {
            Gson gson = new Gson();
            obj = gson.fromJson(response, type);
        } catch (JsonSyntaxException e) {
            obj = null;
        }
    }
}
