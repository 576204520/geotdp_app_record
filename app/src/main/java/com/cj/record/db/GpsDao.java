package com.cj.record.db;

import android.content.Context;

import com.cj.record.baen.Gps;
import com.cj.record.baen.Record;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;


/**
 * Created by
 *
 * @author XuFeng
 *         2015/8/14.
 */
public class GpsDao {


    private Context context;
    private Dao<Gps, String> gpsDao;
    private DBHelper helper;

    public GpsDao(Context context) {
        this.context = context;
        try {
            helper = DBHelper.getInstance(context);
            gpsDao = helper.getDao(Gps.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加一个勘探点
     *
     * @param gps
     */
    public void add(Gps gps) {
        try {
            gpsDao.createOrUpdate(gps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点下所有gps
     */
    public List<Gps> getGpsListByHoleID(String holeID) {
        try {
            return gpsDao.queryBuilder().where().eq("holeID", holeID).query();
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
            return gpsDao.queryBuilder().orderBy("gpsTime", false).where().eq("recordID", recordID).and().eq("mediaID", "").queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Gps> getListGpsByRecord(String recordID) {
        try {
            return gpsDao.queryBuilder().orderBy("gpsTime", true).where().eq("recordID", recordID).and().eq("mediaID", "").query();
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
            return gpsDao.queryBuilder().orderBy("gpsTime", false).where().eq("holeID", holeID).queryForFirst();
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
            return gpsDao.queryBuilder().orderBy("gpsTime", true).where().eq("holeID", holeID).queryForFirst();
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
            gps = gpsDao.queryBuilder().where().eq("mediaID", mediaID).queryForFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gps;
    }


    /**
     * 更新勘探点
     *
     * @param gps
     */
    public void update(Gps gps) {
        try {
            gpsDao.update(gps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除勘探点
     *
     * @param gps
     */
    public boolean delete(Gps gps) {
        try {
            gpsDao.delete(gps);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkByID(String id, String recordID, String holeID, String projectID) {
        try {
            List<Gps> gpsList = gpsDao.queryBuilder().where().eq("id", id).and().eq("recordID", recordID).and().eq("holeID", holeID).and().eq("projectID", projectID).query();
            if (gpsList != null && gpsList.size() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Gps checkByDownloadID(String DownloadID, String recordID, String holeID, String projectID) {
        try {
            return gpsDao.queryBuilder().where().eq("DownloadID", DownloadID).and().eq("recordID", recordID).and().eq("holeID", holeID).and().eq("projectID", projectID).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}