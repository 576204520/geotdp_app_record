package com.cj.record.db;

import android.content.Context;

import com.cj.record.baen.Dictionary;
import com.cj.record.baen.DropItemVo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15.
 */
public class DictionaryDao extends BaseDAO<Dictionary> {


    private static DictionaryDao instance;

    private DictionaryDao() {
    }

    public synchronized static DictionaryDao getInstance() {
        if (instance == null) {
            instance = new DictionaryDao();
        }
        return instance;
    }



    @Override
    public Dao<Dictionary, String> getDAO() throws SQLException {
        return DBManager.getInstance().getDAO(Dictionary.class);
    }


    public List<DropItemVo> getDropItemList(String query) {
        List<DropItemVo> list = new ArrayList<DropItemVo>();
        try {
            GenericRawResults<DropItemVo> results = instance.getDAO().queryRaw(query, new RawRowMapper<DropItemVo>() {
                @Override
                public DropItemVo mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    DropItemVo dropItemVo = new DropItemVo();
                    dropItemVo.setId(resultColumns[0]);
                    dropItemVo.setName(resultColumns[1]);
                    dropItemVo.setValue(resultColumns[1]);
                    return dropItemVo;
                }
            });

            Iterator<DropItemVo> iterator = results.iterator();
            while (iterator.hasNext()) {
                DropItemVo dropItemVo = iterator.next();
                list.add(dropItemVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 按集合添加字典
     *
     * @param list
     */
    public void addDictionaryList(List<Dictionary> list) {
        if (list != null && list.size() > 0) {
            for (Dictionary dictionary : list) {
                add(dictionary);
            }
        }
    }

    /**
     * 字典库管理，查询所有自定义字典
     */
    public List<Dictionary> getDictionary() {
        try {
            return instance.getDAO().queryBuilder().orderByRaw("sort").where().eq("type", "1").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除对象
     */
    public void deleteByDictionary(Dictionary dictionary) {
        try {
            DeleteBuilder builder = instance.getDAO().deleteBuilder();
            builder.where().eq("name", dictionary.getName());
            builder.prepare();
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除对象集合
     */
    public void deleteByDicList(List<Dictionary> list) {
        if (list != null && list.size() > 0) {
            for (Dictionary dictionary : list) {
                deleteByDictionary(dictionary);
            }
        }
    }

    /**
     * 删除所有根据relateID
     */
    public void deleteAll() {
        deleteByDicList(getDictionary());
    }
}



