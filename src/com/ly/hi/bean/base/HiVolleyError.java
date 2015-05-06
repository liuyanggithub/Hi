package com.ly.hi.bean.base;

import com.android.volley.VolleyError;

public class HiVolleyError {
	
	private VolleyError error;
	private String method;
	public HiVolleyError(VolleyError error, String method) {
		super();
		this.error = error;
		this.method = method;
	}
	public VolleyError getError() {
		return error;
	}
	public void setError(VolleyError error) {
		this.error = error;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	

}
