package com.cj.record.db;

import android.content.Context;

import com.cj.record.baen.TemplateDetail;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2018/7/10.
 */

public class TemplateDetialDao {
    private Context context;
    private Dao<TemplateDetail, String> dao;

    public TemplateDetialDao(Context context) {
        this.context = context;
        try {
            dao = DBHelper.getInstance(context).getDao(TemplateDetail.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(TemplateDetail templateDetail) {
        try {
            dao.createOrUpdate(templateDetail);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveList(List<TemplateDetail> list) {
        for (TemplateDetail templateDetail : list) {
            save(templateDetail);
        }
    }

    public List<TemplateDetail> queryList(String templateId) {
        try {
            return dao.queryBuilder().where().eq("templateId", templateId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(TemplateDetail templateDetail){
        try {
            dao.delete(templateDetail);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
