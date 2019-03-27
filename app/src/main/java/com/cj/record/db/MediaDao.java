package com.cj.record.db;

import android.content.Context;

import com.cj.record.baen.Media;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by
 *
 * @author XuFeng
 *         2015/8/14.
 */
public class MediaDao extends BaseDAO<Media> {


    private static MediaDao instance;

    private MediaDao() {
    }

    public synchronized static MediaDao getInstance() {
        if (instance == null) {
            instance = new MediaDao();
        }
        return instance;
    }

    @Override
    public Dao<Media, String> getDAO() throws SQLException {
        return DBManager.getInstance().getDAO(Media.class);
    }



    /**
     * 获取所有未上传的媒体
     *
     * @param holeID
     * @return
     */
    public List<Media> getNotUploadListByHoleID(String holeID) {
        List<Media> list = new ArrayList<Media>();
        try {
            QueryBuilder<Media, String> qb = instance.getDAO().queryBuilder();
            qb.where().eq("holeID", holeID).and().eq("state", "1");
            qb.orderBy("createTime", true);
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 获取某条记录的所有媒体
     *
     * @param recordID
     * @return
     */
    public List<Media> getMediaListByRecordID(String recordID) {
        List<Media> list = new ArrayList<Media>();
        try {
            QueryBuilder<Media, String> qb = instance.getDAO().queryBuilder();
            qb.where().eq("recordID", recordID).and().eq("isDelete", "0");
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void getMediaListByRecordIDSave(String recordID) {
        List<Media> list = new ArrayList<Media>();
        try {
            QueryBuilder<Media, String> qb = instance.getDAO().queryBuilder();
            qb.where().eq("recordID", recordID).and().eq("state", "0");
            list = qb.query();
            if (list != null && list.size() > 0) {
                for (Media media : list) {
                    media.setState("1");
                    instance.getDAO().update(media);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取点的所有媒体
     */

    public List<Media> getMediaListByHoleID(String holeID) {
        try {
            return instance.getDAO().queryBuilder().where().eq("holeID", holeID).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得改点的所有媒体数量
     */

    public int getMediaCountByHoleID(String holeID) {
        return getMediaListByHoleID(holeID).size();
    }

    /**
     * 获取某条记录的媒体数量
     * 偷懒了
     */
    public int getMediaCountByrdcordID(String recordID) {
        return getMediaListByRecordID(recordID).size();
    }

    /**
     * 获取某条记录的所有媒体
     *
     * @param recordID
     * @return
     */
    public List<Media> getMediaListByRecordID2(String recordID) {
        List<Media> list = new ArrayList<Media>();
//        list.add(new Media());
//        list.add(new Media("jpg"));
        try {
            GenericRawResults<Media> results = instance.getDAO().queryRaw("select id,localPath,state,name from media where recordID ='" + recordID + "' order by createTime desc", new RawRowMapper<Media>() {
                @Override
                public Media mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    Media media = new Media();
                    media.setId(resultColumns[0]);
                    media.setLocalPath(resultColumns[1]);
                    media.setState(resultColumns[2]);
                    media.setName(resultColumns[3]);
                    return media;
                }
            });

            Iterator<Media> iterator = results.iterator();
            while (iterator.hasNext()) {
                Media media = iterator.next();
                list.add(media);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取所有未上传的媒体
     *
     * @param mediaID
     * @return
     */
    public Media getMediaByID(String mediaID) {
        try {
            return instance.getDAO().queryForId(mediaID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void updateState(String projectID) {
        try {
            UpdateBuilder updateBuilder = instance.getDAO().updateBuilder();
            updateBuilder.where().eq("projectID", projectID);
            updateBuilder.updateColumnValue("state", "1");
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有未上传的媒体
     *
     * @param holeID
     * @return
     */
    public List<Media> getNotUploadListByHoleIDToZF(String holeID, String recordID) {
        List<Media> list = new ArrayList<Media>();
        try {
            QueryBuilder<Media, String> qb = instance.getDAO().queryBuilder();
            qb.where().eq("holeID", holeID).and().eq("state", "1").and().eq("recordID", recordID);
            qb.orderBy("createTime", true);
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}