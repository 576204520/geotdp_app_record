package com.cj.record.db;

import android.content.Context;

import com.cj.record.baen.Gps;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;


/**
 * Created by
 *
 * @author XuFeng
 *         2015/8/14.
 */
public class GpsDao extends BaseDAO<Gps> {


    private static GpsDao instance;

    private GpsDao() {
    }

    public synchronized static GpsDao getInstance() {
        if (instance == null) {
            instance = new GpsDao();
        }
        return instance;
    }

    @Override
    public Dao<Gps, String> getDAO() throws SQLException {
        return DBManager.getInstance().getDAO(Gps.class);
    }

    /**
     * 点下所有gps
     */
    public List<Gps> getGpsListByHoleID(String holeID) {
        try {
            return instance.getDAO().queryBuilder().where().eq("holeID", holeID).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根基记录获取对应的Gps
     *
     * @param recordID
     * @return
     */
    public Gps getGpsByRecord(String recordID) {
        try {
            return instance.getDAO().queryBuilder().orderBy("gpsTime", false).where().eq("recordID", recordID).and().eq("mediaID", "").or().isNull("mediaID").queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Gps> getListGpsByRecord(String recordID) {
        try {
            return instance.getDAO().queryBuilder().orderBy("gpsTime", true).where().eq("recordID", recordID).and().eq("mediaID", "").query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据holeID，获取最后一条
     */
    public Gps getGpsByHoleID(String holeID) {
        try {
            return instance.getDAO().queryBuilder().orderBy("gpsTime", false).where().eq("holeID", holeID).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据holeID，获取第一条
     */
    public Gps getGpsByHoleIDFrist(String holeID) {
        try {
            return instance.getDAO().queryBuilder().orderBy("gpsTime", true).where().eq("holeID", holeID).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据媒体获取对应的Gps
     *
     * @param mediaID
     * @return
     */
    public Gps getGpsByMedia(String mediaID) {
        Gps gps = null;
        try {
            gps = instance.getDAO().queryBuilder().where().eq("mediaID", mediaID).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gps;
    }


}