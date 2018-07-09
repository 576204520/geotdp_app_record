package com.cj.record.baen;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/7/9.
 */
@DatabaseTable(tableName = "compile_template_detail")
public class Template implements Serializable {
    private static final long serialVersionUID = 1L;
    @DatabaseField(columnName = "ids", id = true)
    String ids;//主键
    @DatabaseField
    String name;//模板名称
    @DatabaseField
    String type;//类型
    @DatabaseField
    String solidType;//岩土类型
    @DatabaseField
    String createTime;//创建时间
    @DatabaseField
    String createUser;//创建人id
    @DatabaseField
    String companyID;//公司id
    @DatabaseField
    String level;//模板级别
    @DatabaseField
    String checkUser;//审核人
    @DatabaseField
    String checkTime;//审核时间
    List<TemplateDetail> detailList;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSolidType() {
        return solidType;
    }

    public void setSolidType(String solidType) {
        this.solidType = solidType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }


    public List<TemplateDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<TemplateDetail> detailList) {
        this.detailList = detailList;
    }
}
