package com.ly.hi.lbs.request;

public abstract class BaseRequestParams {
    protected String AK;

    public BaseRequestParams(String AK) {
        this.AK = AK;
    }

    public String getAK() {
        return AK;
    }

    public void setAK(String AK) {
        this.AK = AK;
    }
}
