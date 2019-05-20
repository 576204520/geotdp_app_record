package com.cj.record.db;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.cj.record.BuildConfig;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
public class ProjectDao extends BaseDAO<Project> {
    private static ProjectDao instance;

    private ProjectDao() {
    }

    public synchronized static ProjectDao getInstance() {
        if (instance == null) {
            instance = new ProjectDao();
        }
        return instance;
    }

    @Override
    public Dao<Project, String> getDAO() throws SQLException {
        return DBManager.getInstance().getDAO(Project.class);
    }


    /**
     * 获取项目列表
     *
     * @param userID
     * @return
     */
    public List<Project> getAll(String userID, int page, int size, String search) {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            queryBuilder.where().eq("recordPerson", "").or().eq("recordPerson", userID);
            if (!TextUtils.isEmpty(search)) {
                queryBuilder.where().like("code", "%" + search + "%").or().like("fullName", "%" + search + "%");
            }
            queryBuilder.orderBy("createTime", false);
            queryBuilder.offset(page * size).limit(size);
            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取项目列表
     * debug
     *
     * @return
     */
    public List<Project> getAll(int page, int size, String search) {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            if (!TextUtils.isEmpty(search)) {
                queryBuilder.where().like("code", "%" + search + "%").or().like("fullName", "%" + search + "%");
            }
            queryBuilder.orderBy("createTime", false);
            queryBuilder.offset(page * size).limit(size);
            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取该用户下项目数量
     *
     * @param userID
     * @return
     */
    public int getAllCount(String userID) {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            queryBuilder.setCountOf(true);
            queryBuilder.where().eq("recordPerson", "").or().eq("recordPerson", userID);
            return (int) instance.getDAO().countOf(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取该用户下项目数量
     *
     * @return
     */
    public int getAllCount() {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            queryBuilder.setCountOf(true);
            return (int) instance.getDAO().countOf(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询当前用户下，该序列号是否存在
     */
    public boolean checkNumber(String userID, String number, String id) {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            queryBuilder.where().eq("recordPerson", "").or().eq("recordPerson", userID).and().eq("serialNumber", number).and().ne("id", id);
            List<Project> list = queryBuilder.query();
            int size = list.size();
            if (size > 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public HashMap getCodeMap() {
        HashMap hashmap = new HashMap();
        try {
            GenericRawResults<String> results = instance.getDAO().queryRaw("select code from project where code like '__-___'", new RawRowMapper<String>() {
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

    public HashMap getFullNameMap() {
        HashMap hashmap = new HashMap();
        try {
            GenericRawResults<String> results = instance.getDAO().queryRaw("select fullName from project where fullName like '___号项目'", new RawRowMapper<String>() {
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

    /**
     * 初始化整理数据
     */

    public List<Project> getAll(String userID) {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            queryBuilder.where().eq("recordPerson", "").or().eq("recordPerson", userID);
            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Flowable<PageBean<Project>> loadData(String userID, int page, int size, String search) {
        return Flowable.create(new FlowableOnSubscribe<PageBean<Project>>() {
            @Override
            public void subscribe(FlowableEmitter<PageBean<Project>> e) throws Exception {
                List<Project> list;
                int totleSize;
                if (BuildConfig.ISDEBUG) {
                    list = getAll(page, size, search);
                    totleSize = getAllCount();
                } else {
                    list = getAll(userID, page, size, search);
                    totleSize = getAllCount(userID);
                }
                PageBean<Project> pageBean = new PageBean<>();
                pageBean.setTotleSize(totleSize);
                pageBean.setPage(page);
                pageBean.setSize(size);
                pageBean.setList(list);
                e.onNext(pageBean);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }

    public Flowable addOrUpdate(Project project, boolean updateState) {
        return Flowable.create(new FlowableOnSubscribe<Project>() {
            @Override
            public void subscribe(FlowableEmitter<Project> e) throws Exception {
                if (updateState) {
                    HoleDao.getInstance().updateState(project.getId());
                    RecordDao.getInstance().updateState(project.getId());
                    MediaDao.getInstance().updateState(project.getId());
                    project.setState("1");
                }
                ProjectDao.getInstance().addOrUpdate(project);
                e.onNext(project);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }

    public Flowable deleteProject(Project project) {
        return Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(FlowableEmitter<Boolean> e) throws Exception {
                project.delete();
                e.onNext(true);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }
}