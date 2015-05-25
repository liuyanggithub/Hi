package com.ly.hi.lbs.http.params;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.ly.hi.lbs.common.BizInterface;

/**
 * 请求的基础参数
 */
public class BaseRequestParams {
    //接口版本号
    protected String ak;      //密钥

    public BaseRequestParams() {
        this.ak = BizInterface.BAIDU_LBS_AK;
    }

	public String getAk() {
		return ak;
	}

	public void setAk(String ak) {
		this.ak = ak;
	}
    
    
}
