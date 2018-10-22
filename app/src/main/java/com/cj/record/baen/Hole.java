package com.cj.record.baen;

import android.content.Context;
import android.text.TextUtils;

import com.cj.record.db.DBHelper;
import com.cj.record.db.HoleDao;
import com.cj.record.db.RecordDao;
import com.cj.record.utils.Common;
import com.cj.record.utils.DateUtil;
import com.cj.record.utils.L;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.Urls;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地用户
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "hole")//"井孔规则表")
public class Hole implements Serializable {

    private static final long serialVersionUID = 1L;
    @DatabaseField(columnName = "id", id = true)
    String id = "";                //勘探点ID
    @DatabaseField
    String code = "";                //勘探点编号
    @DatabaseField
    String projectID = "";        //所属项目
    @DatabaseField
    String attribute = "";        //勘探点性质
    @DatabaseField
    String createTime = "";        //勘探点添加时间
    @DatabaseField
    String updateTime = "";        //勘探点更新时间  新加
    @DatabaseField
    String uploadTime = "";        //数据库录入时间
    @DatabaseField
    String description = "";        //勘探点描述
    @DatabaseField
    String type = "1";            //勘探点类型1.探井2.钻孔
    @DatabaseField
    String depth = "";            //钻孔深度--设计孔深
    @DatabaseField
    String elevation = "";        //孔口高程(标高)
    @DatabaseField
    String shownWaterLevel = "";    //初见水位
    @DatabaseField
    String stillWaterLevel = "";    //静止水位
    @DatabaseField
    String beginTime = "";        //钻孔开始时间
    @DatabaseField
    String endTime = "";            //钻孔结束时间
    @DatabaseField
    String state = "1";            //钻孔状态  0临时 1.记录中、未开始 2.已上传 3.已提交验收
    @DatabaseField
    String inputPerson = "";        //记录员
    @DatabaseField
    String operatePerson = "";    //机长
    @DatabaseField
    String operateCode = "";        //设备编号
    @DatabaseField
    String isDelete = "0";        //是否删除

    @DatabaseField
    String longitude = "";        //勘察点经度
    @DatabaseField
    String latitude = "";         //勘察点纬度
    @DatabaseField
    String radius = "";            //靶值

    @DatabaseField
    String locationState = "0";    //定位状态0.未定位、1.已定位

    @DatabaseField
    String projectName = "";        //项目名称
    @DatabaseField
    String recordsCount = "0";    //拥有记录数

    @DatabaseField
    String mapLocation = "";      //在地图大概所在
    @DatabaseField
    String mapZoom = "";          //地图缩放程度
    @DatabaseField
    String mapLatitude = "";      //基准地图中心纬度
    @DatabaseField
    String mapLongitude = "";    //基准地图中心经度
    @DatabaseField
    String mapTime = "";          //基准时间
    @DatabaseField
    String mapPic = "";          //所在地截图

    int uploadedCount;          //已上传
    int notUploadCount;         //未上传

    String currentDepth;            //进尺深度
    @DatabaseField
    String relateID = "";         //储存关联到的ID，
    @DatabaseField
    String relateCode = "";         //关联的code
    @DatabaseField
    String userID;         //下载的hole中带有userID，判断是否是自己的项目

    List<LocalUser> userList; //关联勘察点获取的已关联用户
    List<Record> recordList; //下载hole时，封装数据
    @DatabaseField
    String uploaded = "0"; //0没上传过、1已经上传过
    int userCount;//relateHoleDialog 中对list进行排序
    @DatabaseField
    String downloadID = "";         //下载数据的id

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public String getUploaded() {
        return uploaded;
    }

    public void setUploaded(String uploaded) {
        this.uploaded = uploaded;
    }

    public Hole() {

    }

    //生成map类型参数
    public Map<String, String> getNameValuePairMap(String serialNumber) {
        Map<String, String> params = new ConcurrentHashMap<>();
        params.put("hole.projectID", serialNumber);
        params.put("hole.relateID", relateID);
        params.put("hole.id", id);
        params.put("hole.code", code);
        if (!TextUtils.isEmpty(attribute)) {
            params.put("hole.attribute", attribute);
        }
        if (!TextUtils.isEmpty(createTime)) {
            params.put("hole.createTime", createTime);
        }
        if (!TextUtils.isEmpty(updateTime)) {
            params.put("hole.updateTime", updateTime);
        }
        if (!TextUtils.isEmpty(type)) {
            params.put("hole.type", type);
        }
        if (!TextUtils.isEmpty(elevation)) {
            params.put("hole.elevation", elevation);
        }
        if (!TextUtils.isEmpty(beginTime)) {
            params.put("hole.beginTime", beginTime);
        }
        if (!TextUtils.isEmpty(endTime)) {
            params.put("hole.endTime", endTime);
        }
        if (!TextUtils.isEmpty(inputPerson)) {
            params.put("hole.inputPerson", inputPerson);
        }
        if (!TextUtils.isEmpty(operatePerson)) {
            params.put("hole.operatePerson", operatePerson);
        }
        if (!TextUtils.isEmpty(operateCode)) {
            params.put("hole.operateCode", operateCode);
        }
        if (!TextUtils.isEmpty(longitude)) {
            params.put("hole.longitude", longitude);
        }
        if (!TextUtils.isEmpty(latitude)) {
            params.put("hole.latitude", latitude);
        }
        if (!TextUtils.isEmpty(radius)) {
            params.put("hole.radius", radius);
        }
        if (!TextUtils.isEmpty(locationState)) {
            params.put("hole.locationState", locationState);
        }
        if (!TextUtils.isEmpty(projectName)) {
            params.put("hole.projectName", projectName);
        }
        if (!TextUtils.isEmpty(recordsCount)) {
            params.put("hole.recordsCount", recordsCount);
        }

        if (!TextUtils.isEmpty(mapLatitude)) {
            params.put("hole.mapLatitude", mapLatitude);
        }
        if (!TextUtils.isEmpty(mapLongitude)) {
            params.put("hole.mapLongitude", mapLongitude);
        }
        if (!TextUtils.isEmpty(mapTime)) {
            params.put("hole.mapTime", mapTime);
        }
//        DateUtil.traversal(params);
        return params;

    }


    public Hole(Context context, String projectID) {
        try {
            this.id = Common.getUUID();
            HashMap codeMap = getCodeMap(context, projectID);
            DecimalFormat df = new DecimalFormat("000");
            int codeInt = 1;
            String codeStr0 = "ZK-";
            String codeStr = codeStr0 + df.format(codeInt);

            while (codeMap.containsKey(codeStr)) {
                codeInt += 1;
                codeStr = codeStr0 + df.format(codeInt);
            }
            this.code = codeStr;

            this.projectID = projectID;

            this.type = "钻孔";

            this.state = "0";//新建一个临时点
            this.locationState = "0";//为定位
            this.recordsCount = "0";

            this.createTime = DateUtil.date2Str(new Date());       //添加时间
            this.updateTime = DateUtil.date2Str(new Date());       //更新时间
            this.radius = "15";

            this.mapLocation = "";
            this.mapZoom = "";
            this.mapLatitude = "";
            this.mapLongitude = "";
            this.mapPic = "";
            this.radius = "15";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除勘探点
     *
     * @param context
     */
    public boolean delete(Context context) {
        try {
            //先删除所有记录.
            List<Record> records = new RecordDao(context).getRecordListByHoleID(id);
            for (Record record : records) {
                record.delete(context);
            }
            L.e("记录删除成功");
            if (new HoleDao(context).delete(this)) {
                L.e("勘探点数据删除成功");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public HashMap getCodeMap(Context context, String projectID) {
        HashMap hashmap = new HashMap();
        DBHelper dbHelper = DBHelper.getInstance(context);
        try {
            Dao<Hole, String> dao = dbHelper.getDao(Hole.class);
            GenericRawResults<String> results = dao.queryRaw("select code from hole where code like '__-___'and projectID='" + projectID + "'", new RawRowMapper<String>() {
                @Override
                public String mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    String s = resultColumns[0];
                    return s;
                }
            });

            Iterator<String> iterator = results.iterator();
            while (iterator.hasNext()) {
                String string = iterator.next();
                hashmap.put(string, string);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashmap;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInputPerson() {
        return inputPerson;
    }

    public void setInputPerson(String inputPerson) {
        this.inputPerson = inputPerson;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOperateCode() {
        return operateCode;
    }

    public void setOperateCode(String operateCode) {
        this.operateCode = operateCode;
    }

    public String getOperatePerson() {
        return operatePerson;
    }

    public void setOperatePerson(String operatePerson) {
        this.operatePerson = operatePerson;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getHoleName() {
        return projectName;
    }

    public void setHoleName(String projectName) {
        this.projectName = projectName;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(String recordsCount) {
        this.recordsCount = recordsCount;
    }

    public int getRecordsCount2Int() {
        return Integer.valueOf(recordsCount);
    }

    public void setRecordsCount2Int(int recordsCount) {
        this.recordsCount = String.valueOf(recordsCount);
    }

    public String getShownWaterLevel() {
        return shownWaterLevel;
    }

    public void setShownWaterLevel(String shownWaterLevel) {
        this.shownWaterLevel = shownWaterLevel;
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

    public String getStillWaterLevel() {
        return stillWaterLevel;
    }

    public void setStillWaterLevel(String stillWaterLevel) {
        this.stillWaterLevel = stillWaterLevel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getLocationState() {
        return locationState;
    }

    public void setLocationState(String locationState) {
        this.locationState = locationState;
    }

    public String getMapLatitude() {
        return mapLatitude;
    }

    public void setMapLatitude(String mapLatitude) {
        this.mapLatitude = mapLatitude;
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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getMapTime() {
        return mapTime;
    }

    public void setMapTime(String mapTime) {
        this.mapTime = mapTime;
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

    public String getCurrentDepth() {
        return currentDepth;
    }

    public void setCurrentDepth(String currentDepth) {
        this.currentDepth = currentDepth;
    }

    public String getRelateID() {
        return relateID;
    }

    public void setRelateID(String relateID) {
        this.relateID = relateID;
    }

    public String getRelateCode() {
        return relateCode;
    }

    public void setRelateCode(String relateCode) {
        this.relateCode = relateCode;
    }

    public List<LocalUser> getUserList() {
        return userList;
    }

    public void setUserList(List<LocalUser> userList) {
        this.userList = userList;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(String downloadID) {
        this.downloadID = downloadID;
    }
}
