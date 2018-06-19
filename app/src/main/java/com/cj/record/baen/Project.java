package com.cj.record.baen;

import android.content.Context;

import com.cj.record.adapter.ProjectAdapter;
import com.cj.record.db.ProjectDao;
import com.cj.record.utils.Common;
import com.cj.record.utils.DateUtil;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.Urls;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 项目表
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    @DatabaseField(columnName = "id", id = true)
    String id = "";              //主键
    @DatabaseField
    String code = "";             //代码1
    @DatabaseField
    String companyID = "";        //(勘察单位)
    @DatabaseField
    String designCompanyID = "";  //设计单位
    @DatabaseField
    String workCompany = "";      //作业单位(施工单位)(建设单位)
    @DatabaseField
    String designationID = "";    //审图机构
    @DatabaseField
    String owner = "";            //业主1
    @DatabaseField
    String leader = "";           //负责人1
    @DatabaseField
    String fullName = "";         //全称1
    @DatabaseField
    String referred = "";         //简称
    @DatabaseField
    String type = "0";            //类型
    @DatabaseField
    String recordPerson = "";     //记录负责人(描述员)  UserID
    @DatabaseField
    String operatePerson = "";    //操作负责人(机长)
    @DatabaseField
    String province = "";         //省份1
    @DatabaseField
    String city = "";             //城市1
    @DatabaseField
    String district = "";         //区县1
    @DatabaseField
    String address = "";          //地址1
    @DatabaseField
    String level = "";            //级别
    @DatabaseField
    String describe = "";         //项目描述1
    @DatabaseField
    String createTime = "";       //添加时间1
    @DatabaseField
    String updateTime = "";       //更新时间
    @DatabaseField
    String uploadTime = "";       //提交时间
    @DatabaseField
    String beginTime = "";        //勘查开始时间(第一个空,在客户端添加的时间)1
    @DatabaseField
    String endTime = "";          //勘查结束时间(最晚的更新时间)1
    @DatabaseField
    String state = "1";           //状态1.未开始2.进行中3.已结束
    @DatabaseField
    String stage = "1";           //阶段1.预查2.普查3.详查4.勘查
    @DatabaseField
    String annex = "";            //指导资料
    @DatabaseField
    String participants = "";     //参与者
    @DatabaseField
    String serialNumber = "";     //序列号1
    @DatabaseField
    String isCheck = "0";         //是否审核1
    @DatabaseField
    String isDelete = "0";        //是否删除
    @DatabaseField
    String remark = "";           //备注1
    @DatabaseField
    String companyName = "";      //工程设计单位(勘察单位)
    @DatabaseField
    String designCompanyName = "";      //工程设计单位(勘察单位)
    @DatabaseField
    String proName = "";          //省1
    @DatabaseField
    String cityName = "";         //工区市1
    @DatabaseField
    String disName = "";         //区县1
    @DatabaseField
    String leaderName = "";      //负责人姓名1
    @DatabaseField
    String mapLocation = "";      //在地图大概所在
    @DatabaseField
    String mapZoom = "";          //地图缩放程度
    @DatabaseField
    String mapLatitude = "";      //地图中心纬度
    @DatabaseField
    String mapLongitude = "";    //地图中心经度
    @DatabaseField
    String mapPic = "";          //所在地截图
    @DatabaseField
    String holeCount = "0";      //孔数

    String realName = "";         //负责人姓名

    int uploadedCount;          //已上传
    int notUploadCount;         //未上传

    public Project() {

    }

    public Project(int i) {
    }

    public Project(Context context) {
        this.serialNumber = "";
        this.id = Common.getUUID();
        ProjectDao projectDao = new ProjectDao(context);
        HashMap codeMap = projectDao.getCodeMap();
        DecimalFormat df = new DecimalFormat("000");
        int codeInt = 1;
        String codeStr0 = "XM-";
        String codeStr = codeStr0 + df.format(codeInt);

        HashMap fullNameMap = projectDao.getFullNameMap();
        String fullNameStr1 = "号项目";
        String fullNameStr = df.format(codeInt) + fullNameStr1;

        while (codeMap.containsKey(codeStr) || fullNameMap.containsKey(fullNameStr)) {
            codeInt += 1;
            codeStr = codeStr0 + df.format(codeInt);
            fullNameStr = df.format(codeInt) + fullNameStr1;
        }
        this.code = codeStr;
        this.fullName = "未关联项目";

        this.leader = "";

        this.companyName = "";
        this.owner = "";
        this.proName = "";
        this.cityName = "";
        this.disName = "";
        this.address = "";

        this.state = "1";
        this.holeCount = "0";

        this.createTime = DateUtil.date2Str(new Date());       //添加时间
        this.updateTime = DateUtil.date2Str(new Date());       //更新时间

        this.mapLocation = "";
        this.mapZoom = "";
        this.mapLatitude = "";
        this.mapLongitude = "";
        this.mapPic = "";
        //到添加项目这里，必定是登录过了 这里添加的的userid
        this.recordPerson = (String) SPUtils.get(context, Urls.SPKey.USER_ID, "");
    }

    //给该加密的数据加密
    public void jiaProject() {
//        try {
//            this.code = Key.jiaMi(this.code);
//            this.fullName = Key.jiaMi(this.fullName);
//            this.leader = Key.jiaMi(this.leader);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    //给该解密的数据解密
    public void jieProject() {
//        try {
//            this.code = Key.jieMi(this.code);
//            this.fullName = Key.jieMi(this.fullName);
//            this.leader = Key.jieMi(this.leader);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getWorkCompany() {
        return workCompany;
    }

    public void setWorkCompany(String workCompany) {
        this.workCompany = workCompany;
    }

    public String getDesignationID() {
        return designationID;
    }

    public void setDesignationID(String designationID) {
        this.designationID = designationID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getReferred() {
        return referred;
    }

    public void setReferred(String referred) {
        this.referred = referred;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecordPerson() {
        return recordPerson;
    }

    public void setRecordPerson(String recordPerson) {
        this.recordPerson = recordPerson;
    }

    public String getOperatePerson() {
        return operatePerson;
    }

    public void setOperatePerson(String operatePerson) {
        this.operatePerson = operatePerson;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//    public String getLevel() {
//        return level;
//    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getState() {
        return state;
    }

    public String getStateName() {
        if (state.equals("2")) {
            return "进行中";
        } else if (state.equals("3")) {
            return "已结束";
        }
        return "未开始";
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getAnnex() {
        return annex;
    }

    public void setAnnex(String annex) {
        this.annex = annex;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDisName() {
        return disName;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public String getHoleCount() {
        return holeCount;
    }

    public void setHoleCount(String holeCount) {
        this.holeCount = holeCount;
    }

    public int getHoleCount2Int() {
        return Integer.valueOf(holeCount);
    }

    public void setHoleCount2Int(int holeCount) {
        this.holeCount = String.valueOf(holeCount);
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getMapLatitude() {
        return mapLatitude;
    }

    public void setMapLatitude(String mapLatitude) {
        this.mapLatitude = mapLatitude;
    }

    public double getMapLatitude2Double() {
        return Double.valueOf(mapLatitude);
    }

    public double getMapLongitude2Double() {
        return Double.valueOf(mapLongitude);
    }


    public String getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(String mapLocation) {
        this.mapLocation = mapLocation;
    }

    public String getMapLongitude() {
        return mapLongitude;
    }

    public void setMapLongitude(String mapLongitude) {
        this.mapLongitude = mapLongitude;
    }

    public String getMapPic() {
        return mapPic;
    }

    public void setMapPic(String mapPic) {
        this.mapPic = mapPic;
    }

    public String getMapZoom() {
        return mapZoom;
    }

    public void setMapZoom(String mapZoom) {
        this.mapZoom = mapZoom;
    }

    public String getDesignCompanyID() {
        return designCompanyID;
    }

    public void setDesignCompanyID(String designCompanyID) {
        this.designCompanyID = designCompanyID;
    }

    public String getDesignCompanyName() {
        return designCompanyName;
    }

    public void setDesignCompanyName(String designCompanyName) {
        this.designCompanyName = designCompanyName;
    }

    public int getUploadedCount() {
        return uploadedCount;
    }

    public void setUploadedCount(int uploadedCount) {
        this.uploadedCount = uploadedCount;
    }

    public int getNotUploadCount() {
        return notUploadCount;
    }

    public void setNotUploadCount(int notUploadCount) {
        this.notUploadCount = notUploadCount;
    }
}
