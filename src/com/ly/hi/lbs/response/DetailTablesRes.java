package com.ly.hi.lbs.response;

import java.util.List;

/**
 * <Pre>
 * TODO 描述该文件做什么
 * </Pre>
 *
 * @author 刘阳
 * @version 1.0
 *          <p/>
 *          Create by 2015/4/1 0001 下午 5:50
 */
public class DetailTablesRes {
	private String size;//	返回数据条数
	private String total;//	全部的数据条数
	private List<DetailTable> pois;//	Poi结果列表
	
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public List<DetailTable> getPois() {
		return pois;
	}
	public void setPois(List<DetailTable> pois) {
		this.pois = pois;
	}

}
