package com.cj.record.db;

import android.content.Context;

import com.cj.record.baen.Hole;
import com.cj.record.baen.Template;
import com.cj.record.baen.TemplateDetail;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2018/7/10.
 */

public class TemplateDao extends BaseDAO<Template> {

    private static TemplateDao instance;

    private TemplateDao() {
    }

    public synchronized static TemplateDao getInstance() {
        if (instance == null) {
            instance = new TemplateDao();
        }
        return instance;
    }

    @Override
    public Dao<Template, String> getDAO() throws SQLException {
        return DBManager.getInstance().getDAO(Template.class);
    }


    public void saveList(List<Template> list) {
        for (Template template : list) {
            addOrUpdate(template);
        }
    }

    public List<Template> queryList(String userID) {
        try {
            return instance.getDAO().queryBuilder().where().eq("userID", userID).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
