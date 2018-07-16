package com.cj.record.baen;

import android.text.TextUtils;

import com.cj.record.utils.Common;
import com.cj.record.utils.DateUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/7/9.
 */
@DatabaseTable(tableName = "compile_template")
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
    @DatabaseField
    String userID;

    List<TemplateDetail> detailList;

    public Template() {
    }

    public Template(String templateName, String userID, Record record) {
        this.ids = Common.getUUID();
        this.name = templateName;
        this.type = record.getType();
        this.createTime = DateUtil.date2Str(new Date());
        this.createUser = userID;
        this.userID = userID;
        if (Record.TYPE_LAYER.equals(record.getType())) {
            this.solidType = record.getLayerType();
        }
        this.detailList = new ArrayList<>();
        if (!TextUtils.isEmpty(record.getFrequencyType())) {
            detailList.add(new TemplateDetail(this.ids, "钻进方法", record.getFrequencyType()));
        }
        if (!TextUtils.isEmpty(record.getFrequencyMode())) {
            detailList.add(new TemplateDetail(this.ids, "护壁方法", record.getFrequencyMode()));
        }
        if (!TextUtils.isEmpty(record.getLayerName())) {
            detailList.add(new TemplateDetail(this.ids, "岩土定名", record.getLayerName()));
        }
        if (!TextUtils.isEmpty(record.getZycf())) {
            detailList.add(new TemplateDetail(this.ids, "主要成分", record.getZycf()));
        }
        if (!TextUtils.isEmpty(record.getCycf())) {
            detailList.add(new TemplateDetail(this.ids, "次要成分", record.getCycf()));
        }
        if (!TextUtils.isEmpty(record.getYs())) {
            detailList.add(new TemplateDetail(this.ids, "颜色", record.getYs()));
        }
        if (!TextUtils.isEmpty(record.getDjnd())) {
            detailList.add(new TemplateDetail(this.ids, "堆积年代", record.getDjnd()));
        }
        if (!TextUtils.isEmpty(record.getMsd())) {
            detailList.add(new TemplateDetail(this.ids, "密实度", record.getMsd()));
        }
        if (!TextUtils.isEmpty(record.getJyx())) {
            detailList.add(new TemplateDetail(this.ids, "均匀性", record.getJyx()));
        }
        if (!TextUtils.isEmpty(record.getCauses())) {
            detailList.add(new TemplateDetail(this.ids, "地质成因", record.getCauses()));
        }
        if (!TextUtils.isEmpty(record.getEra())) {
            detailList.add(new TemplateDetail(this.ids, "地质年代", record.getEra()));
        }
        if (!TextUtils.isEmpty(record.getZt())) {
            detailList.add(new TemplateDetail(this.ids, "状态", record.getZt()));
        }
        if (!TextUtils.isEmpty(record.getBhw())) {
            detailList.add(new TemplateDetail(this.ids, "包含物", record.getBhw()));
        }
        if (!TextUtils.isEmpty(record.getJc())) {
            detailList.add(new TemplateDetail(this.ids, "夹层", record.getJc()));
        }
        if (!TextUtils.isEmpty(record.getSd())) {
            detailList.add(new TemplateDetail(this.ids, "湿度", record.getSd()));
        }
        if (!TextUtils.isEmpty(record.getKwzc())) {
            detailList.add(new TemplateDetail(this.ids, "矿物组成", record.getKwzc()));
        }
        if (!TextUtils.isEmpty(record.getKljp())) {
            detailList.add(new TemplateDetail(this.ids, "颗粒级配", record.getKljp()));
        }
        if (!TextUtils.isEmpty(record.getKlxz())) {
            detailList.add(new TemplateDetail(this.ids, "颗粒形状", record.getKlxz()));
        }
        if (!TextUtils.isEmpty(record.getYbljx())) {
            detailList.add(new TemplateDetail(this.ids, "一般粒径小", record.getYbljx()));
        }
        if (!TextUtils.isEmpty(record.getYbljd())) {
            detailList.add(new TemplateDetail(this.ids, "一般粒径大", record.getLayerName()));
        }
        if (!TextUtils.isEmpty(record.getJdljx())) {
            detailList.add(new TemplateDetail(this.ids, "较大粒径小", record.getJdljx()));
        }
        if (!TextUtils.isEmpty(record.getJdljd())) {
            detailList.add(new TemplateDetail(this.ids, "较大粒径大", record.getJdljd()));
        }
        if (!TextUtils.isEmpty(record.getZdlj())) {
            detailList.add(new TemplateDetail(this.ids, "最大粒径", record.getZdlj()));
        }
        if (!TextUtils.isEmpty(record.getFhcd())) {
            detailList.add(new TemplateDetail(this.ids, "风化程度", record.getFhcd()));
        }
        if (!TextUtils.isEmpty(record.getMycf())) {
            detailList.add(new TemplateDetail(this.ids, "母岩成分", record.getMycf()));
        }
        if (!TextUtils.isEmpty(record.getTcw())) {
            detailList.add(new TemplateDetail(this.ids, "充填物", record.getTcw()));
        }
        if (!TextUtils.isEmpty(record.getHsl())) {
            detailList.add(new TemplateDetail(this.ids, "含水量", record.getHsl()));
        }
        if (!TextUtils.isEmpty(record.getJycd())) {
            detailList.add(new TemplateDetail(this.ids, "坚硬程度", record.getJycd()));
        }
        if (!TextUtils.isEmpty(record.getWzcd())) {
            detailList.add(new TemplateDetail(this.ids, "完整程度", record.getWzcd()));
        }
        if (!TextUtils.isEmpty(record.getJbzldj())) {
            detailList.add(new TemplateDetail(this.ids, "基本质量等级", record.getJbzldj()));
        }
        if (!TextUtils.isEmpty(record.getKwx())) {
            detailList.add(new TemplateDetail(this.ids, "可挖性", record.getKwx()));
        }
        if (!TextUtils.isEmpty(record.getJglx())) {
            detailList.add(new TemplateDetail(this.ids, "结构类型", record.getJglx()));
        }
        if (!TextUtils.isEmpty(record.getEarthType())) {
            detailList.add(new TemplateDetail(this.ids, "选择质量等级", record.getEarthType()));
        }
        if (!TextUtils.isEmpty(record.getTestType())) {
            detailList.add(new TemplateDetail(this.ids, "选择实验内容", record.getTestType()));
        }
        if (!TextUtils.isEmpty(record.getPowerType())) {
            detailList.add(new TemplateDetail(this.ids, "动探类型", record.getPowerType()));
        }
        if (!TextUtils.isEmpty(record.getWaterType())) {
            detailList.add(new TemplateDetail(this.ids, "是否添加大理石粉", record.getWaterType()));
        }
        if (Record.TYPE_GET_EARTH.equals(record.getType())) {
            if (!TextUtils.isEmpty(record.getGetMode())) {
                detailList.add(new TemplateDetail(this.ids, "选择取样工具和方法", record.getGetMode()));
            }
        }
        if (Record.TYPE_WATER.equals(record.getType())) {
            if (!TextUtils.isEmpty(record.getGetMode())) {
                detailList.add(new TemplateDetail(this.ids, "选择地下水类型", record.getGetMode()));
            }
        }
    }

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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<TemplateDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<TemplateDetail> detailList) {
        this.detailList = detailList;
    }
}
