/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cj.record.baen;


import android.content.Context;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.cj.record.db.GpsDao;
import com.cj.record.utils.Common;
import com.cj.record.utils.GPSutils;
import com.cj.record.utils.RxPartMapUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Gps
 *
 * @author XuFeng
 */

@DatabaseTable(tableName = "gps")
public class Gps implements Serializable {

    private static final long serialVersionUID = 1L;
    @DatabaseField(columnName = "id", id = true)
    String id = "";             //主键
    @DatabaseField
    String projectID = "";           // char(36) DEFAULT NULL COMMENT ' 所属项目',
    @DatabaseField
    String holeID = "";           // char(36) DEFAULT NULL COMMENT '所属勘探点',
    @DatabaseField
    String recordID = "";           // char(36) DEFAULT NULL COMMENT '所属记录',
    @DatabaseField
    String mediaID = "";           // char(36) DEFAULT NULL COMMENT '所属媒体',
    @DatabaseField
    String type = "";           //
    @DatabaseField
    String longitude = "";           // double DEFAULT NULL COMMENT '经度',
    @DatabaseField
    String latitude = "";           // double DEFAULT NULL COMMENT '纬度',
    @DatabaseField
    String gpsTime = "";           // datetime DEFAULT NULL COMMENT '时间',
    @DatabaseField
    String isDelete = "";           // tinyint(1) DEFAULT '0' COMMENT '是否删除',
    @DatabaseField
    String distance = "";
    @DatabaseField
    String downloadID = "";         //下载数据的id

    public Gps() {

    }


    public Gps(Media media, AMapLocation amapLocation, String type) {
        try {
            this.id = Common.getUUID();
            this.projectID = media.getProjectID();
            this.holeID = media.getHoleID();
            this.recordID = media.getRecordID();
            this.mediaID = media.getId();
            //新加的记录的偏移量
            this.distance = String.valueOf(amapLocation.getAccuracy());
            this.longitude = String.valueOf(amapLocation.getLongitude());
            this.latitude = String.valueOf(amapLocation.getLatitude());
            this.gpsTime = GPSutils.utcToTimeZoneDate(amapLocation.getTime());
            this.type = type;

            this.isDelete = "0";       //是否删除',

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Gps(Record record, AMapLocation amapLocation, String type) {
        try {
            this.id = Common.getUUID();
            this.projectID = record.getProjectID();
            this.holeID = record.getHoleID();
            this.recordID = record.getId();
            //新加的记录的偏移量
            this.distance = String.valueOf(amapLocation.getAccuracy());
            this.longitude = String.valueOf(amapLocation.getLongitude());
            this.latitude = String.valueOf(amapLocation.getLatitude());
            this.gpsTime = GPSutils.utcToTimeZoneDate(amapLocation.getTime());
            this.type = type;

            this.isDelete = "0";       //是否删除',

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Map<String, String> getNameValuePairMap(String serialNumber) {
        Map<String, String> params = new HashMap<>();
        params.put("gps.projectID", serialNumber);

        params.put("gps.id", id);
        params.put("gps.holeID", holeID);
        params.put("gps.recordID", recordID);
        params.put("gps.mediaID", mediaID);
        params.put("gps.type", type);
        params.put("gps.longitude", longitude);
        params.put("gps.latitude", latitude);
        params.put("gps.gpsTime", gpsTime);
        params.put("gps.distance", distance);
        return params;
    }

    public static Map<String, RequestBody> getMap(List<Gps> list, String serialNumber) {
        Map<String, RequestBody> map = new ConcurrentHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Gps gps = list.get(i);
            map.put("gps[" + i + "].projectID", RxPartMapUtils.toRequestBodyOfText(serialNumber));
            map.put("gps[" + i + "].id", RxPartMapUtils.toRequestBodyOfText(gps.getId() == null ? "" : gps.getId()));
            map.put("gps[" + i + "].holeID", RxPartMapUtils.toRequestBodyOfText(gps.getHoleID() == null ? "" : gps.getHoleID()));
            map.put("gps[" + i + "].recordID", RxPartMapUtils.toRequestBodyOfText(gps.getRecordID() == null ? "" : gps.getRecordID()));
            if (!TextUtils.isEmpty(gps.getMediaID())) {
                map.put("gps[" + i + "].mediaID", RxPartMapUtils.toRequestBodyOfText(gps.getMediaID()));
            }
            if (!TextUtils.isEmpty(gps.getType())) {
                map.put("gps[" + i + "].type", RxPartMapUtils.toRequestBodyOfText(gps.getType()));
            }
            if (!TextUtils.isEmpty(gps.getLongitude())) {
                map.put("gps[" + i + "].longitude", RxPartMapUtils.toRequestBodyOfText(gps.getLongitude()));
            }
            if (!TextUtils.isEmpty(gps.getLatitude())) {
                map.put("gps[" + i + "].latitude", RxPartMapUtils.toRequestBodyOfText(gps.getLatitude()));
            }
            if (!TextUtils.isEmpty(gps.getGpsTime())) {
                map.put("gps[" + i + "].gpsTime", RxPartMapUtils.toRequestBodyOfText(gps.getGpsTime()));
            }
            if (!TextUtils.isEmpty(gps.getDistance())) {
                map.put("gps[" + i + "].distance", RxPartMapUtils.toRequestBodyOfText(gps.getDistance()));
            }
        }
        return map;
    }

    public Gps(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(String gpsTime) {
        this.gpsTime = gpsTime;
    }

    public String getHoleID() {
        return holeID;
    }

    public void setHoleID(String holeID) {
        this.holeID = holeID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMediaID() {
        return mediaID;
    }

    public void setMediaID(String mediaID) {
        this.mediaID = mediaID;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(String downloadID) {
        this.downloadID = downloadID;
    }
}
