package com.ly.hi.lbs.request;

/**
 * <Pre>
 * TODO 描述该文件做什么
 * </Pre>
 *
 * @author 刘阳
 * @version 1.0
 *          <p/>
 *          Create by 2015/4/3 0003 下午 8:18
 */
public class CreateTableReq{
    private String name;	//Geotable的中文名称	String(45)	必选
    private String geotype;	//Geotable持有数据的类型	Int32	必选 1：点poi 2：线poi 3：面poi 默认为1

    private String is_published;	//是否发布到检索	Int32

    public CreateTableReq(String name, String geotype, String is_published) {
        this.name = name;
        this.geotype = geotype;
        this.is_published = is_published;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeotype() {
        return geotype;
    }

    public void setGeotype(String geotype) {
        this.geotype = geotype;
    }

    public String getIs_published() {
        return is_published;
    }

    public void setIs_published(String is_published) {
        this.is_published = is_published;
    }
}
