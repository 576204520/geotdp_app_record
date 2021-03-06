package com.cj.record.db;

import android.content.Context;
import android.text.TextUtils;

import com.cj.record.baen.Hole;
import com.cj.record.baen.Media;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.cj.record.baen.Record;
import com.cj.record.utils.Common;
import com.cj.record.utils.L;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.Urls;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;


/**
 * Created by
 *
 * @author XuFeng
 *         2015/8/14.
 */
public class HoleDao extends BaseDAO<Hole> {


    private static HoleDao instance;

    private HoleDao() {
    }

    public synchronized static HoleDao getInstance() {
        if (instance == null) {
            instance = new HoleDao();
        }
        return instance;
    }

    @Override
    public Dao<Hole, String> getDAO() throws SQLException {
        return DBManager.getInstance().getDAO(Hole.class);
    }


    public List<Hole> getHoleListByProjectIDUserDelete(String projectID) {
        List<Hole> list = new ArrayList<>();
        try {
            QueryBuilder<Hole, String> qb = instance.getDAO().queryBuilder();
            qb.where().eq("projectID", projectID);
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取项目下所有勘探点
     *
     * @param projectID
     * @return
     */
    public int getAllCount(String projectID) {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            queryBuilder.setCountOf(true);
            queryBuilder.where().eq("projectID", projectID);
            return (int) instance.getDAO().countOf(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获取某项目的所有勘探点
     *
     * @param projectID
     * @return
     */
    public List<Hole> getHoleListByProjectID(String projectID) {
        List<Hole> list = new ArrayList<>();
        try {
            QueryBuilder<Hole, String> qb = instance.getDAO().queryBuilder();
            qb.where().eq("projectID", projectID).and().eq("isDelete", "0");
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public void updateState(String projectID) {
        try {
            UpdateBuilder updateBuilder = instance.getDAO().updateBuilder();
            updateBuilder.where().eq("projectID", projectID);
            updateBuilder.updateColumnValue("state", "1");
            updateBuilder.updateColumnValue("stateGW", "1");
            updateBuilder.updateColumnValue("relateID", "");
            updateBuilder.updateColumnValue("relateCode", "");
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkRelated(String relateID, String projectID) {
        try {
            List<Hole> list = instance.getDAO().queryBuilder().where().eq("relateID", relateID).and().eq("projectID", projectID).query();
            if (list == null || list.size() == 0) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkRelatedNoHole(String id, String relateID, String projectID) {
        try {
            List<Hole> list = instance.getDAO().queryBuilder().where().eq("relateID", relateID).and().eq("projectID", projectID).query();
            if (list == null || list.size() == 0) {
                return false;
            } else if (list.get(0).getId().equals(id)) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 想办法验证将要保存的编号数据库里是否存在
     *
     * @param context
     * @return
     */
    public List<Hole> getHoleByCode(Context context, String code, String projectID) {
        try {
            return instance.getDAO().queryBuilder().where().eq("code", code).and().eq("projectID", projectID).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取项目列表
     *
     * @return
     */
    public List<Hole> getAll(String projectID, int page, int size, String search, String sort) {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            queryBuilder.where().eq("projectID", projectID);
            if (!TextUtils.isEmpty(search)) {
                queryBuilder.where().like("code", "%" + search + "%").or().like("relateCode", "%" + search + "%");
            }
            if ("1".equals(sort)) {//时间
                queryBuilder.orderBy("updateTime", false);
            } else if ("2".equals(sort)) {//名字
                queryBuilder.orderBy("code", true);
            } else if ("3".equals(sort)) {//上传  去掉
            } else if ("4".equals(sort)) {//定位
                queryBuilder.orderBy("mapTime", false);
                queryBuilder.orderBy("updateTime", false);
            } else if ("5".equals(sort)) {//关联
                queryBuilder.orderBy("relateID", false);
                queryBuilder.orderBy("updateTime", false);
            } else if ("6".equals(sort)) {
                queryBuilder.orderBy("relateCode", true);
            }
            queryBuilder.offset(page * size).limit(size);
            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Flowable<PageBean<Hole>> loadData(String projectID, int page, int size, String search, String sort) {
        return Flowable.create(new FlowableOnSubscribe<PageBean<Hole>>() {
            @Override
            public void subscribe(FlowableEmitter<PageBean<Hole>> e) throws Exception {
                List<Hole> list = getAll(projectID, page, size, search, sort);
                //查询每一个勘探点下记录的最大深度，并赋值
                if (list != null && list.size() > 0) {
                    for (Hole hole : list) {
                        //获取每一个hole 的最大深度
                        Record record = RecordDao.getInstance().getCurrentDepthByHoleID(hole.getId());
                        if (record != null && !TextUtils.isEmpty(record.getEndDepth())) {
                            hole.setCurrentDepth(record.getEndDepth());
                        } else {
                            hole.setCurrentDepth("0");
                        }
                        //获取record 不包含场景等信息
                        List<Record> recordList = RecordDao.getInstance().getNotUploadListByHoleID(hole.getId());
                        //获取record 只有场景等信息 该信息不包含历史记录
                        List<Record> recordListScene = RecordDao.getInstance().getNotUploadListByHoleIDScene(hole.getId());
                        hole.setNotUploadCount(recordList.size() + recordListScene.size());
                    }
                }
                int totleSize = HoleDao.getInstance().getAllCount(projectID);
                PageBean<Hole> pageBean = new PageBean<>();
                pageBean.setTotleSize(totleSize);
                pageBean.setPage(page);
                pageBean.setSize(size);
                pageBean.setList(list);
                e.onNext(pageBean);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }

    public Flowable deleteHole(Hole hole) {
        return Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(FlowableEmitter<Boolean> e) throws Exception {
                hole.delete();
                e.onNext(true);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }

}