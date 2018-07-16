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

public class TemplateDao {
    private Context context;
    private Dao<Template, String> dao;

    public TemplateDao(Context context) {
        this.context = context;
        try {
            dao = DBHelper.getInstance(context).getDao(Template.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(Template template) {
        try {
            dao.createOrUpdate(template);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveList(List<Template> list) {
        for (Template template : list) {
            save(template);
        }
    }

    public List<Template> queryList(String userID) {
        try {
            return dao.queryBuilder().where().eq("userID", userID).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Template queryByID(String id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(Template template) {
        try {
            dao.delete(template);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
