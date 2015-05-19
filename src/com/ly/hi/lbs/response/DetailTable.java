package com.ly.hi.lbs.response;

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
public class DetailTable {
	private String id;//	数据id	String
	private String geotable_id;//	geotable_id	String
	private String[] location ;//	坐标	Float
	private String title;//	名称	 String
	private String address;//	 地址	 String
	private String tags;//	 标签	 String
	private String create_time;//	用户创建日期	String
	private String modify_time;//	 用户修改时间	 String
	private String province;//	所在的省份	String
	private String district;//	所在的区	String
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGeotable_id() {
		return geotable_id;
	}
	public void setGeotable_id(String geotable_id) {
		this.geotable_id = geotable_id;
	}
	
	public String[] getLocation() {
		return location;
	}
	public void setLocation(String[] location) {
		this.location = location;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getModify_time() {
		return modify_time;
	}
	public void setModify_time(String modify_time) {
		this.modify_time = modify_time;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	
	
	

}
