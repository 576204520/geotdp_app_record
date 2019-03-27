package com.cj.record.db;

import android.content.Context;

import com.cj.record.baen.TemplateDetail;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2018/7/10.
 */

public class TemplateDetialDao extends BaseDAO<TemplateDetail> {
    private static TemplateDetialDao instance;

    private TemplateDetialDao() {
    }

    public synchronized static TemplateDetialDao getInstance() {
        if (instance == null) {
            instance = new TemplateDetialDao();
        }
        return instance;
    }

    @Override
    public Dao<TemplateDetail, String> getDAO() throws SQLException {
        return DBManager.getInstance().getDAO(TemplateDetail.class);
    }


    public void saveList(List<TemplateDetail> list) {
        for (TemplateDetail templateDetail : list) {
            addOrUpdate(templateDetail);
        }
    }

    public List<TemplateDetail> queryList(String templateId) {
        try {
            return instance.getDAO().queryBuilder().where().eq("templateId", templateId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
