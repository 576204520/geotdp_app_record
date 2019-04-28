package com.cj.record.db;

import android.content.Context;
import android.text.TextUtils;

import com.cj.record.baen.Gps;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Record;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by
 *
 * @author XuFeng
 *         2015/8/14.
 */
public class RecordDao extends BaseDAO<Record> {


    private static RecordDao instance;

    private RecordDao() {
    }

    public synchronized static RecordDao getInstance() {
        if (instance == null) {
            instance = new RecordDao();
        }
        return instance;
    }

    @Override
    public Dao<Record, String> getDAO() throws SQLException {
        return DBManager.getInstance().getDAO(Record.class);
    }


    /**
     * 获取一个勘探点下所有所有未上传的记录
     *
     * @param holeID
     * @return
     */
    public List<Record> getNotUploadListByHoleID(String holeID) {
        List<Record> list = new ArrayList<Record>();
        try {
            QueryBuilder<Record, String> queryBuilder = instance.getDAO().queryBuilder();
            Where where = queryBuilder.where();
            where.and(
                    where.eq("holeID", holeID),
                    where.eq("state", "1"),
                    where.or(
                            where.eq("type", Record.TYPE_FREQUENCY),
                            where.eq("type", Record.TYPE_LAYER),
                            where.eq("type", Record.TYPE_GET_EARTH),
                            where.eq("type", Record.TYPE_GET_WATER),
                            where.eq("type", Record.TYPE_DPT),
                            where.eq("type", Record.TYPE_SPT),
                            where.eq("type", Record.TYPE_WATER),
                            where.eq("type", Record.TYPE_SCENE)
                    )
            );
            queryBuilder.orderBy("createTime", true);
            list = queryBuilder.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 获取一个勘探点下所有所有未上传的记录 场景的
     * 查出所有未上传的机长等信息
     *
     * @param holeID
     * @return
     */
    public List<Record> getNotUploadListByHoleIDScene(String holeID) {
        List<Record> list = new ArrayList<Record>();
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            Where where = queryBuilder.where();

            where.and(
                    where.eq("holeID", holeID),
                    where.eq("state", "1"),
                    where.or(
                            where.eq("updateID", ""),
                            where.isNull("updateID")
                    ),
                    where.or(
                            where.eq("type", Record.TYPE_SCENE_OPERATEPERSON),
                            where.eq("type", Record.TYPE_SCENE_OPERATECODE),
                            where.eq("type", Record.TYPE_SCENE_RECORDPERSON),
                            where.eq("type", Record.TYPE_SCENE_SCENE),
                            where.eq("type", Record.TYPE_SCENE_PRINCIPAL),
                            where.eq("type", Record.TYPE_SCENE_TECHNICIAN),
                            where.eq("type", Record.TYPE_SCENE_VIDEO)
                    )
            );
            queryBuilder.orderBy("createTime", true);
            list = queryBuilder.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /***
     * 根据分类获取统计
     *
     * @param holeID
     * @return
     */
    public Map<Integer, Integer> getSortCountMap(String holeID) {
        Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
        try {
            QueryBuilder<Record, String> queryBuilder = instance.getDAO().queryBuilder();
            Where where1 = queryBuilder.where();
            where1.and(
                    where1.eq("holeID", holeID),
                    where1.or(
                            where1.eq("updateID", ""),
                            where1.isNull("updateID")
                    ),
                    where1.or(
                            where1.eq("type", Record.TYPE_FREQUENCY),
                            where1.eq("type", Record.TYPE_LAYER),
                            where1.eq("type", Record.TYPE_GET_EARTH),
                            where1.eq("type", Record.TYPE_GET_WATER),
                            where1.eq("type", Record.TYPE_DPT),
                            where1.eq("type", Record.TYPE_SPT),
                            where1.eq("type", Record.TYPE_WATER),
                            where1.eq("type", Record.TYPE_SCENE)
                    )
            );
            countMap.put(1, where1.query().size());

            queryBuilder.reset();
            Where where2 = queryBuilder.where();
            where2.and(
                    where2.eq("holeID", holeID),
                    where2.or(
                            where2.eq("updateID", ""),
                            where2.isNull("updateID")
                    ),
                    where2.eq("type", Record.TYPE_FREQUENCY)
            );
            countMap.put(2, where2.query().size());

            queryBuilder.reset();
            Where where3 = queryBuilder.where();
            where3.and(
                    where3.eq("holeID", holeID),
                    where3.or(
                            where3.eq("updateID", ""),
                            where3.isNull("updateID")
                    ),
                    where3.eq("type", Record.TYPE_LAYER)
            );
            countMap.put(3, where3.query().size());

            queryBuilder.reset();
            Where where4 = queryBuilder.where();
            where4.and(
                    where4.eq("holeID", holeID),
                    where4.or(
                            where4.eq("updateID", ""),
                            where4.isNull("updateID")
                    ),
                    where4.eq("type", Record.TYPE_WATER)
            );
            countMap.put(4, where4.query().size());

            queryBuilder.reset();
            Where where5 = queryBuilder.where();

            where5.and(
                    where5.eq("holeID", holeID),
                    where5.or(
                            where5.eq("updateID", ""),
                            where5.isNull("updateID")
                    ),
                    where5.eq("type", Record.TYPE_DPT)
            );
            countMap.put(5, where5.query().size());

            queryBuilder.reset();
            Where where6 = queryBuilder.where();
            where6.and(
                    where6.eq("holeID", holeID),
                    where6.or(
                            where6.eq("updateID", ""),
                            where6.isNull("updateID")
                    ),
                    where6.eq("type", Record.TYPE_SPT)
            );
            countMap.put(6, where6.query().size());

            queryBuilder.reset();
            Where where7 = queryBuilder.where();
            where7.and(
                    where7.eq("holeID", holeID),
                    where7.or(
                            where7.eq("updateID", ""),
                            where7.isNull("updateID")
                    ),
                    where7.eq("type", Record.TYPE_GET_EARTH)
            );
            countMap.put(7, where7.query().size());

            queryBuilder.reset();
            Where where8 = queryBuilder.where();
            where8.and(
                    where8.eq("holeID", holeID),
                    where8.or(
                            where8.eq("updateID", ""),
                            where8.isNull("updateID")
                    ),
                    where8.eq("type", Record.TYPE_GET_WATER)
            );
            countMap.put(8, where8.query().size());

            queryBuilder.reset();
            Where where9 = queryBuilder.where();
            where9.and(
                    where9.eq("holeID", holeID),
                    where9.or(
                            where9.eq("updateID", ""),
                            where9.isNull("updateID")
                    ),
                    where9.eq("type", Record.TYPE_SCENE)
            );
            countMap.put(9, where9.query().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countMap;
    }


    /**
     * 根据holeiID和类别查询record
     */
    public Record getRecordByType(String holeID, String type) {
        try {
            QueryBuilder<Record, String> queryBuilder = instance.getDAO().queryBuilder();
            Where where = queryBuilder.where();
            where.and(
                    where.eq("holeID", holeID),
                    where.or(
                            where.eq("updateID", ""),
                            where.isNull("updateID")
                    ),
                    where.eq("type", type)
            );
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据holeiID和类别查询record
     */
    public List<Record> getRecordListByType(String holeID, String type) {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            Where where = queryBuilder.where();
            where.and(
                    where.eq("holeID", holeID),
                    where.or(
                            where.eq("updateID", ""),
                            where.isNull("updateID")

                    ),
                    where.eq("type", type)
            );
            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询scene下record
     */
    public List<Record> getSceneRecord(String holeID) {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            Where where = queryBuilder.where();
            where.and(
                    where.eq("holeID", holeID),
                    where.or(
                            where.eq("updateID", ""),
                            where.isNull("updateID")
                    ),
                    where.or(
                            where.eq("type", Record.TYPE_SCENE_OPERATEPERSON),
                            where.eq("type", Record.TYPE_SCENE_OPERATECODE),
                            where.eq("type", Record.TYPE_SCENE_RECORDPERSON),
                            where.eq("type", Record.TYPE_SCENE_SCENE),
                            where.eq("type", Record.TYPE_SCENE_PRINCIPAL),
                            where.eq("type", Record.TYPE_SCENE_TECHNICIAN),
                            where.eq("type", Record.TYPE_SCENE_VIDEO)
                    )
            );

            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一个勘探点某个深度在类别内是否存在
     *
     * @param record
     * @return
     */
    public boolean validatorBeginDepth(Record record, String type, String depth) {
        int i = 0;
        try {
            QueryBuilder<Record, String> qb = instance.getDAO().queryBuilder();
            Where where = qb.where();
            //beginDepth <= depth < endDepth
            where.eq("holeID", record.getHoleID()).and().ne("state", "0").and().le("beginDepth", depth).and().gt("endDepth", depth).and().ne("id", record.getId()).and().eq("updateID", "");

            if (type.equals(Record.TYPE_DPT) || type.equals(Record.TYPE_SPT)) {
                where.and().in("type", Record.TYPE_DPT, Record.TYPE_SPT);
            } else {
                where.and().eq("type", type);
            }
            i = qb.query().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i > 0;
    }


    /**
     * 获取一个勘探点某个深度在类别内是否存在
     *
     * @param record
     * @return
     */
    public boolean validatorEndDepth(Record record, String type, String depth) {
        int i = 0;
        try {
            QueryBuilder<Record, String> qb = instance.getDAO().queryBuilder();
            Where where = qb.where();
            //beginDepth < depth <=endDepth
            where.eq("holeID", record.getHoleID()).and().ne("state", "0").and().lt("beginDepth", depth).and().ge("endDepth", depth).and().ne("id", record.getId()).and().eq("updateID", "");

            if (type.equals(Record.TYPE_DPT) || type.equals(Record.TYPE_SPT)) {
                where.and().in("type", Record.TYPE_DPT, Record.TYPE_SPT);
            } else {
                where.and().eq("type", type);
            }
            System.out.println("where:=========:" + where.getStatement());

            i = qb.query().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i > 0;
    }

    /**
     * 展示
     *
     * @param holeID
     * @return
     */
    public List<Record> getRecordOne(String holeID) {
        List<Record> list = new ArrayList<>();
        try {
            QueryBuilder<Record, String> qb = instance.getDAO().queryBuilder().orderBy("endDepth", true).orderBy("beginDepth", true);
            Where where = qb.where();
            where.eq("holeID", holeID).and().ne("state", "0");
            where.and().in("type", Record.TYPE_LAYER, Record.TYPE_WATER);
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 获取某勘探点的所有记录
     *
     * @param holeID
     * @return
     */
    public List<Record> getRecordListByHoleID(String holeID) {
        List<Record> list = new ArrayList<>();
        try {
            QueryBuilder<Record, String> qb = instance.getDAO().queryBuilder();
            qb.where().eq("holeID", holeID);
            list = qb.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据项目ID、类别，查询所有记录
     * 机长列表
     */
    public List<Record> getRecordListByProject(String projectID, String type) {
        try {
//            return recordDao.queryBuilder().where().eq("projectID", projectID).and().eq("type", type).query();
            //暂时不需要在项目之下查询
            return instance.getDAO().queryBuilder().where().eq("type", type).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap getCodeMap(String holeID) {
        HashMap hashmap = new HashMap();
        try {
            GenericRawResults<String> results = instance.getDAO().queryRaw("select code from record where code like '%__-___'and holeID='" + holeID + "' and state !='0'", new RawRowMapper<String>() {
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

    public List<Record> getCodeMapNew(String holeID, String type) {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            Where where = queryBuilder.where();
            where.and(
                    where.eq("holeID", holeID),
                    where.eq("type", type)
            );
            queryBuilder.orderBy("updateTime", true);
            return queryBuilder.query();
        } catch (SQLException e) {
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

    public List<Record> getRecordList(String holeID, int size, int page, String sort, String sequence) {
        try {
            QueryBuilder queryBuilder = instance.getDAO().queryBuilder();
            Where where = queryBuilder.where();
            where.and(
                    where.eq("holeID", holeID),
                    where.or(
                            where.eq("updateID", ""),
                            where.isNull("updateID")
                    ),
                    where.or(
                            where.eq("type", Record.TYPE_FREQUENCY),
                            where.eq("type", Record.TYPE_LAYER),
                            where.eq("type", Record.TYPE_GET_EARTH),
                            where.eq("type", Record.TYPE_GET_WATER),
                            where.eq("type", Record.TYPE_DPT),
                            where.eq("type", Record.TYPE_SPT),
                            where.eq("type", Record.TYPE_WATER),
                            where.eq("type", Record.TYPE_SCENE)
                    )
            );
            //筛选
            if (!TextUtils.isEmpty(sort)) {
                where.and().eq("type", sort);
            }
            //排序
            if ("1".equals(sequence)) {
                queryBuilder.orderBy("updateTime", false);
            } else if ("2".equals(sequence)) {
                queryBuilder.orderBy("updateTime", true);
            } else if ("3".equals(sequence)) {
                queryBuilder.orderBy("beginDepth", true);
            } else if ("4".equals(sequence)) {
                queryBuilder.orderBy("beginDepth", false);
            }
            queryBuilder.offset(page * size).limit(size);
            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int checkZK(String holeID) {
        int complate = 0;
        List<Record> jz = getRecordListByType(holeID, Record.TYPE_SCENE_OPERATEPERSON);
        List<Record> zj = getRecordListByType(holeID, Record.TYPE_SCENE_OPERATECODE);
        List<Record> msy = getRecordListByType(holeID, Record.TYPE_SCENE_RECORDPERSON);
        List<Record> cj = getRecordListByType(holeID, Record.TYPE_SCENE_SCENE);
        if (jz != null && jz.size() > 0) {
            complate++;
        }
        if (zj != null && zj.size() > 0) {
            complate++;
        }
        if (msy != null && msy.size() > 0) {
            complate++;
        }
        if (cj != null && cj.size() > 0) {
            complate++;
        }
        return complate;
    }

    public int checkTJ(String holeID) {
        int complate = 0;
        List<Record> msy = getRecordListByType(holeID, Record.TYPE_SCENE_RECORDPERSON);
        List<Record> cj = getRecordListByType(holeID, Record.TYPE_SCENE_SCENE);
        if (msy != null && msy.size() > 0) {
            complate++;
        }
        if (cj != null && cj.size() > 0) {
            complate++;
        }
        return complate;
    }

    public List<Record> getRecordListForJzAndZj(String holeID) {
        try {
            Where<Record, String> where = instance.getDAO().queryBuilder().where();
            where.and(
                    where.eq("holeID", holeID),
                    where.or(
                            where.eq("type", Record.TYPE_SCENE_OPERATEPERSON),
                            where.eq("type", Record.TYPE_SCENE_OPERATECODE)
                    )
            );
            return where.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询记录最大深度
     *
     * @param holeID
     * @return
     */
    public Record getCurrentDepthByHoleID(String holeID) {
        try {
            QueryBuilder<Record, String> queryBuilder = instance.getDAO().queryBuilder();
            Where where = queryBuilder.where();
            where.and(
                    where.eq("holeID", holeID),
                    where.or(
                            where.eq("updateID", ""),
                            where.isNull("updateID")
                    )
            );
            queryBuilder.orderBy("endDepth", false);
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询某个类别记录最大深度
     *
     * @param holeID
     * @return
     */
    public Record getDepthByHoleIDAndType(String holeID, String type) {
        try {
            QueryBuilder<Record, String> queryBuilder = instance.getDAO().queryBuilder();
            Where where = queryBuilder.where();
            where.and(
                    where.eq("holeID", holeID),
                    where.eq("type", type),
                    where.or(
                            where.eq("updateID", ""),
                            where.isNull("updateID")
                    )
            );
            queryBuilder.orderBy("endDepth", false);
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}