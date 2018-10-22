package com.cj.record.db;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cj.record.baen.Project;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Created by
 *
 * @author XuFeng
 *         2015/8/14.
 */
public class ProjectDao {


    private Dao<Project, String> projectDao;

    public ProjectDao(Context context) {
        try {
            projectDao = DBHelper.getInstance(context).getDao(Project.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加一个项目
     *
     * @param project
     */
    public void add(Project project) {
        try {
            projectDao.createOrUpdate(project);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改一个项目
     */
    public void update(Project project) {
        try {
            projectDao.update(project);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个项目
     *
     * @param projectID
     */
    public Project queryForId(String projectID) {
        try {
            return projectDao.queryForId(projectID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取项目序列号
     */
    public String queryStrForId(String projectID) {
        try {
            return projectDao.queryForId(projectID).getSerialNumber();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 删除项目
     *
     * @param project
     */
    public boolean delete(Project project) {
        try {
            projectDao.delete(project);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取项目列表
     *
     * @param userID
     * @return
     */
    public List<Project> getAll(String userID, int page, int size, String search) {
        try {
            QueryBuilder queryBuilder = projectDao.queryBuilder();
            queryBuilder.where().eq("recordPerson", "").or().eq("recordPerson", userID);
            if (!TextUtils.isEmpty(search)) {
                queryBuilder.where().like("code", "%" + search + "%").or().like("fullName","%" + search + "%");
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
            QueryBuilder queryBuilder = projectDao.queryBuilder();
            queryBuilder.setCountOf(true);
            queryBuilder.where().eq("recordPerson", "").or().eq("recordPerson", userID);
            return (int) projectDao.countOf(queryBuilder.prepare());
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
            QueryBuilder queryBuilder = projectDao.queryBuilder();
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
            GenericRawResults<String> results = projectDao.queryRaw("select code from project where code like '__-___'", new RawRowMapper<String>() {
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
            GenericRawResults<String> results = projectDao.queryRaw("select fullName from project where fullName like '___号项目'", new RawRowMapper<String>() {
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

}