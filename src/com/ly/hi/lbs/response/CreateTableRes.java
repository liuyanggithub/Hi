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
public class CreateTableRes {

    private String id;    //新增的数据的id	String	必须


    public CreateTableRes(int status, String message, String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
