package com.ly.hi.lbs.common;

/**
 * <Pre>
 * TODO 描述该文件做什么
 * </Pre>
 *
 * @author 刘阳
 * @version 1.0
 *          <p/>
 *          Create by 2015/4/1 0001 下午 2:52
 */
public interface BizInterface {
	String BAIDU_LBS_GEOTABLE_ID = "98950";
	String BAIDU_LBS_GEOTABLE_TYPE = "1";
    String BAIDU_LBS_AK = "5SudMsMqw27P38x7G8WGTyyc";

    
    String CREATE_GEOTABLE = "http://api.map.baidu.com/geodata/v3/geotable/create";

    /**
     * 创建坐标点
     */
    String CREATE_POI = "http://api.map.baidu.com/geodata/v3/poi/create";
    
    /**
     * 更新坐标点
     */
    String UPDATE_POI = "http://api.map.baidu.com/geodata/v3/poi/update";
    
}
