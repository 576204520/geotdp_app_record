package com.cj.record.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

public class GPSutils {

    public interface Gps {

    }

    public static String utcToTimeZoneDate(long date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone zone = TimeZone.getTimeZone("GMT+8");
        Date dateTemp = new Date(date);
        format.setTimeZone(zone);
        return format.format(dateTemp);
    }

    public static String setNewThumbnail(String oldPath, AMapLocation amapLocation) throws Exception {
        // Gps纬度
        double lat = amapLocation.getLatitude();
        // Gps经度
        double lon = amapLocation.getLongitude();
        // Gps海拔
        double alt = amapLocation.getAltitude();
        try {
            // 获取jpg文件
            ExifInterface exifInterface = new ExifInterface(oldPath);
            Log.e("TAG", lat + "--" + lon + "--" + alt);
            System.out.println("utcToTimeZoneDate(mLocation.getTime()):" + utcToTimeZoneDate(amapLocation.getTime()));
            // 日期时间
            exifInterface.setAttribute(ExifInterface.TAG_DATETIME, utcToTimeZoneDate(amapLocation.getTime()));
            //
            // System.out.println("String.valueOf(alt):"+String.valueOf((int)alt));
            // 写入海拔信息
            exifInterface.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, String.valueOf(alt));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, lon > 0 ? "0" : "1");

            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, gpsInfoConvert(lat));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, lat > 0 ? "N" : "S");
            // 写入经度信息
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, gpsInfoConvert(lon));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lon > 0 ? "E" : "W");

            // //写入日期戳
            // exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP,utcToTimeZoneDate(mLocation.getTime()));
            // //写入时间戳
            // exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP,utcToTimeZoneDate(mLocation.getTime()));

            // 这句话很重要，一定要saveAttributes才能使写入的信息生效。
            exifInterface.saveAttributes();
            // 获取纬度信息
            // String latitude =
            // exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            // 获取经度信息
            // String longitude =
            // exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String gpsInfoConvert(double gpsInfo) {
        gpsInfo = Math.abs(gpsInfo);
        String dms = Location.convert(gpsInfo, Location.FORMAT_SECONDS);
        String[] splits = dms.split(":");
        String[] secnds = (splits[2]).split("\\.");
        String seconds;
        if (secnds.length == 0) {
            seconds = splits[2];
        } else {
            seconds = secnds[0];
        }
        return splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
    }

}
