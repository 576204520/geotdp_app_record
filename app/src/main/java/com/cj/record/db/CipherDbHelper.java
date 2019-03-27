package com.cj.record.db;

import android.content.Context;

import com.alibaba.idst.nls.internal.utils.L;
import com.cj.record.baen.Dictionary;
import com.cj.record.baen.LocalUser;
import com.cj.record.baen.Template;
import com.cj.record.baen.TemplateDetail;
import com.cj.record.utils.SqlcipherUtil;
import com.cj.record.utils.Urls;
import com.j256.ormlite.sqlcipher.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Stay on 29/10/15.
 * Powered by www.stay4it.com
 */
public class CipherDbHelper extends OrmLiteSqliteOpenHelper {
    public static final int databaseVersion = 10;

    private String databaseName;
    private String password;

    public CipherDbHelper(Context context, String databaseName, String password) {
        super(context, databaseName, null, databaseVersion);
        this.databaseName = databaseName;
        this.password = password;
        getWritableDatabase();
    }

    public synchronized SQLiteDatabase getWritableDatabase() {
        try {
            return super.getWritableDatabase(password);
        } catch (Exception e) {
            e.printStackTrace();
            //尝试加密后再打开
            encrypt();
            return super.getWritableDatabase(password);
        }
    }

    private void encrypt() {
        try {
            SqlcipherUtil.encrypt(databaseName, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        L.e("CipherDbHelper:onCreate:");
        try {
            TableUtils.createTableIfNotExists(connectionSource, LocalUser.class);
            TableUtils.createTableIfNotExists(connectionSource, Dictionary.class);
            TableUtils.createTableIfNotExists(connectionSource, Template.class);
            TableUtils.createTableIfNotExists(connectionSource, TemplateDetail.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String ALTER_RECORD_UPDATEID = "ALTER TABLE `record` ADD COLUMN updateID VARCHAR(45) default '';";
    private String UPDATE_DICTIONARY_LAYERNAME = "update dictionary set sort = dictionary.type  where sort = '土的名称'";
    private String ADD_DICTIONARY_LAYERTYPE = "insert into dictionary (name,sort,sortNo) values ('填土','岩土类型','1'),('冲填土','岩土类型','2'),('黏性土','岩土类型','3'),('粉土','岩土类型','4'),('粉黏互层','岩土类型','5'),('黄土状黏性土','岩土类型','6'),('黄土状粉土','岩土类型','7'),('淤泥','岩土类型','8'),('碎石土','岩土类型','9'),('砂土','岩土类型','10'),('岩石','岩土类型','11')";
    private String ADD_DICTIONARY_LAYER_TT = "insert into dictionary (name,sort,sortNo) values ('卵石','填土_主要成分','1'),('角砾','填土_主要成分','2'),('黏性土','填土_主要成分','3'),('粉土','填土_主要成分','4'),('建筑垃圾','填土_次要成分','1'),('生活垃圾','填土_次要成分','2'),('碎砖','填土_次要成分','3'),('块石','填土_次要成分','4')";
    private String ADD_DICTIONARY_LAYER_CTT = "insert into dictionary (name,sort,sortNo) values ('以泥沙为主','冲填土_状态','1')";
    private String ADD_DICTIONARY_LAYER_YN = "insert into dictionary (name,sort,sortNo) values ('流塑','淤泥_状态','1')";
    private String ADD_DICTIONARY_LAYER_NXT = "insert into dictionary (name,sort,sortNo) values ('硬可塑','黏性土_状态','8'),('软可塑','黏性土_状态','7')";
    private String ALTER_DICTIONARY_RELATEID = "ALTER TABLE `dictionary` ADD COLUMN relateID VARCHAR(45) default '';";
    private String ALTER_DICTIONARY_FORM = "ALTER TABLE `dictionary` ADD COLUMN form VARCHAR(45) default '';";
    private String ALTER_HOLE_RELATEID = "ALTER TABLE `hole` ADD COLUMN relateID VARCHARD(45) default '';";
    private String ALTER_HOLE_RELATECODE = "ALTER TABLE `hole` ADD COLUMN relateCode VARCHARD(45) default '';";
    private String ALTER_HOLE_UPLOADED = "ALTER TABLE `hole` ADD COLUMN uploaded VARCHARD(1) default '0';";
    private String UPDATE_DICTIONARY_TYPE = "update dictionary set type = '0'";
    //修改岩土类型的sortNo，顺序问题
    private String UPDATE_DICTIONARY_YT2 = "update dictionary set sortNo = '2' where name='黏性土'";
    private String UPDATE_DICTIONARY_YT3 = "update dictionary set sortNo = '3' where name='粉土'";
    private String UPDATE_DICTIONARY_YT4 = "update dictionary set sortNo = '4' where name='砂土'";
    private String UPDATE_DICTIONARY_YT5 = "update dictionary set sortNo = '5' where name='碎石土'";
    private String UPDATE_DICTIONARY_YT6 = "update dictionary set sortNo = '6' where name='冲填土'";
    private String UPDATE_DICTIONARY_YT7 = "update dictionary set sortNo = '7' where name='粉黏互层'";
    private String UPDATE_DICTIONARY_YT8 = "update dictionary set sortNo = '8' where name='黄土状黏性土'";
    private String UPDATE_DICTIONARY_YT9 = "update dictionary set sortNo = '9' where name='黄土状粉土'";
    private String UPDATE_DICTIONARY_YT10 = "update dictionary set sortNo = '10' where name='淤泥'";
    private String UPDATE_DICTIONARY_YT11 = "update dictionary set sortNo = '11' where name='岩石'";

    private String ALTER_HOLE_USERID = "ALTER TABLE `hole` ADD COLUMN userID VARCHARD(45) default '';";

    //version==3

    private String ADD_INDEX_HOLE = "create index if not exists index_hole  on hole (projectID,state,isDelete)";
    private String ADD_INDEX_RECORD = "create index if not exists index_record  on record (holeID,projectID,state,isDelete)";
    private String ADD_INDEX_MEDIA = "create index if not exists index_media  on media (recordID,holeID,projectID,state,isDelete)";
    //修改字段长度
    private String UPDATE_PROJECT_DES = "alter table project alter column describe varchar(2000)";
    //version==5
    private String ADD_YS_JC = "insert into dictionary (name,sort,sortNo,type) " +
            "values " +
            "('黏土','砂土_夹层','1','0')," +
            "('重粉质黏土','砂土_夹层','2','0')," +
            "('粉质黏土','砂土_夹层','3','0')," +
            "('砂质粉土','砂土_夹层','4','0')," +
            "('黏质粉土','砂土_夹层','5','0')," +
            "('圆砾','砂土_夹层','6','0')," +
            "('卵石','砂土_夹层','7','0')," +
            "('角砾','砂土_夹层','8','0')," +
            "('碎石','砂土_夹层','9','0')," +
            "('杂色','填土_颜色','1','0')," +
            "('褐黄色','填土_颜色','2','0')," +
            "('黄褐色','填土_颜色','3','0')," +
            "('褐黄色（暗）','填土_颜色','4','0')," +
            "('浅灰色','填土_颜色','5','0')," +
            "('灰色','填土_颜色','6','0')," +
            "('灰褐色','填土_颜色','7','0')," +
            "('灰黑色','填土_颜色','8','0')," +
            "('杂色','冲填土_颜色','1','0')," +
            "('褐黄色','冲填土_颜色','2','0')," +
            "('黄褐色','冲填土_颜色','3','0')," +
            "('褐黄色（暗）','冲填土_颜色','4','0')," +
            "('浅灰色','冲填土_颜色','5','0')," +
            "('灰色','冲填土_颜色','6','0')," +
            "('灰褐色','冲填土_颜色','7','0')," +
            "('灰黑色','冲填土_颜色','8','0')";
    //version==6 分层编号
    private String MAIN_LAYER_CODE = "ALTER TABLE `record` ADD COLUMN mainLayerCode VARCHAR(50) default '0'";
    private String SUB_LAYER_CODE = "ALTER TABLE `record` ADD COLUMN subLayerCode VARCHAR(50) default '0'";
    private String SECONDSUB_LAYER_CODE = "ALTER TABLE `record` ADD COLUMN secondSubLayerCode VARCHAR(50) default '0'";
    //模板表
    private String ADD_TEMPLATE = "CREATE TABLE `compile_template` (" +
            "  `ids` char(32) PRIMARY KEY  NOT NULL ," +
            "  `name` varchar(100)," +
            "  `type` varchar(20)," +
            "  `solidType` varchar(20)," +
            "  `createTime` datetime," +
            "  `createUser` char(32)," +
            "  `companyID` char(32)," +
            "  `level` char(1) ," +
            "  `checkUser` char(32)," +
            "  `checkTime` datetime," +
            "  `userID` varchar(50)" +
            ")";
    private String ADD_TEMPLATE_DETAIL = "CREATE TABLE `compile_template_detail` (" +
            "  `ids` char(32) PRIMARY KEY NOT NULL," +
            "  `templateId` char(32)," +
            "  `fieldKey` varchar(100)," +
            "  `fieldValue` varchar(100)," +
            "  `sort` tinyint(2)" +
            ") ";
    private String ADD_DICTIONARY1 = "insert into dictionary (name,sort,sortNo) values ('冲填土','填土','05')";
    private String ADD_DICTIONARY2 = "insert into dictionary (name,sort,sortNo) values ('圆砾','填土_主要成分','4'),('泥岩','填土_主要成分','5')";
    private String ADD_DICTIONARY3 = "insert into dictionary (name,sort,sortNo) values ('冲填','土的成因类型','20'),('其他成因','土的成因类型','21')";
    private String ADD_DICTIONARY4 = "insert into dictionary (name,sort,sortNo) values ('黄色','黏性土_颜色','5'),('红色','黏性土_颜色','6'),('红褐色','黏性土_颜色','7'),('黄红色','黏性土_颜色','8'),('红黄色','黏性土_颜色','9')";
    private String ADD_DICTIONARY5 = "insert into dictionary (name,sort,sortNo) values ('石英','黏性土_包含物','4'),('铁锰结核','黏性土_包含物','5'),('岩石碎块','黏性土_包含物','6'),('砾石','黏性土_包含物','7')";
    private String ADD_DICTIONARY6 = "insert into dictionary (name,sort,sortNo) values ('黄色','粉土_颜色','5'),('灰色','粉土_颜色','6'),('灰黑色','粉土_颜色','7'),('灰黄色','粉土_颜色','8')";
    private String ADD_DICTIONARY7 = "insert into dictionary (name,sort,sortNo) values ('白色','砂土_颜色','5'),('黄色','砂土_颜色','6'),('灰色','砂土_颜色','7'),('黄白色','砂土_颜色','8'),('灰白色','砂土_颜色','9')";
    private String ADD_DICTIONARY8 = "insert into dictionary (name,sort,sortNo) values ('白色','碎石土_颜色','4'),('黄色','碎石土_颜色','5'),('灰色','碎石土_颜色','6'),('黄白色','碎石土_颜色','7'),('灰白色','碎石土_颜色','8'),('灰黄色','碎石土_颜色','9')";
    private String ADD_DICTIONARY9 = "insert into dictionary (name,sort,sortNo) values ('饱和','碎石土_湿度','5')";
    private String ADD_DICTIONARY10 = "insert into dictionary (name,sort,sortNo) values ('黏土质泥岩','岩石','33'),('粉砂质泥岩','岩石','34'),('泥质粉砂岩','岩石','35'),('石灰岩','岩石','36'),('花岗岩','岩石','37'),('溶洞','岩石','38')";
    //7
    private String ADD_DOWNLOADID = "ALTER TABLE `hole` ADD COLUMN downloadID VARCHARD(45) default ''";
    //8
    private String ADD_DOWNLOADID_R = "ALTER TABLE `record` ADD COLUMN downloadID VARCHARD(45) default ''";
    private String ADD_DOWNLOADID_G = "ALTER TABLE `gps` ADD COLUMN downloadID VARCHARD(45) default ''";
    //9       双数据流
    private String ADD_ISUPLOAD = "ALTER TABLE `project` ADD COLUMN isUpload tinyint(1);";
    private String ADD_PROJECTID = "ALTER TABLE `project` ADD COLUMN projectID char(32) default '';";
    private String ADD_LABORUNIT = "ALTER TABLE `project` ADD COLUMN laborUnit varchar(45) default '';";
    private String ADD_REALNAME = "ALTER TABLE `project` ADD COLUMN realName varchar(50) default '';";
    private String ADD_UPLOADID = "ALTER TABLE `hole` ADD COLUMN uploadID char(32) default '';";
    private String ADD_STATEGW = "ALTER TABLE `hole` ADD COLUMN stateGW tinyint(1);";
    private String ADD_STATEGW_UD = "update `hole` set stateGW=state;";

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        L.e("CipherDbHelper:onUpgrade:old=" + oldVersion + "  new=" + newVersion);
        if (oldVersion < 2) {
            //record添加updateID字段
            sqLiteDatabase.execSQL(ALTER_RECORD_UPDATEID);
            //删除没用的表
            sqLiteDatabase.execSQL("DROP TABLE record_get;");
            sqLiteDatabase.execSQL("DROP TABLE record_frequency;");
            sqLiteDatabase.execSQL("DROP TABLE record_layer;");
            sqLiteDatabase.execSQL("DROP TABLE record_power;");
            sqLiteDatabase.execSQL("DROP TABLE record_scene;");
            sqLiteDatabase.execSQL("DROP TABLE record_water;");
            sqLiteDatabase.execSQL("DROP TABLE layer_name;");
            sqLiteDatabase.execSQL("DROP TABLE layer_type;");
            //修改字典库，（layer_name）
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_LAYERNAME);
            //修改字典库，（layer_type）
            sqLiteDatabase.execSQL(ADD_DICTIONARY_LAYERTYPE);
            //修改字典库，（layer_layer_tt）
            sqLiteDatabase.execSQL(ADD_DICTIONARY_LAYER_TT);
            //修改字典库，（layer_layer_ctt）
            sqLiteDatabase.execSQL(ADD_DICTIONARY_LAYER_CTT);
            //修改字典库，（layer_layer_nxt）
            sqLiteDatabase.execSQL(ADD_DICTIONARY_LAYER_NXT);
            //修改字典库，（layer_layer_yn）
            sqLiteDatabase.execSQL(ADD_DICTIONARY_LAYER_YN);
            //dictionary添加relateID字段，上传下载关联
            sqLiteDatabase.execSQL(ALTER_DICTIONARY_RELATEID);
            //dictionary添加form字段，区分岩土、取水等类型
            sqLiteDatabase.execSQL(ALTER_DICTIONARY_FORM);
            //hole添加isRelated字段，判断是否关联
            sqLiteDatabase.execSQL(ALTER_HOLE_RELATEID);
            //修改岩土类型的sortNo，顺序问题
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT2);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT3);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT4);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT5);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT6);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT7);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT8);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT9);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT10);
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_YT11);
            //hole添加isRelated字段，判断是否关联
            sqLiteDatabase.execSQL(ALTER_HOLE_RELATECODE);
            //hole添加upload字段，判断是否上传过
            sqLiteDatabase.execSQL(ALTER_HOLE_UPLOADED);
            //初始化type字段设置为0
            sqLiteDatabase.execSQL(UPDATE_DICTIONARY_TYPE);
            //hole添加userID字段
            sqLiteDatabase.execSQL(ALTER_HOLE_USERID);
        }
        if (oldVersion < 4) {
            sqLiteDatabase.execSQL(ADD_INDEX_HOLE);
            sqLiteDatabase.execSQL(ADD_INDEX_RECORD);
            sqLiteDatabase.execSQL(ADD_INDEX_MEDIA);
        }
        if (oldVersion < 5) {
            sqLiteDatabase.execSQL(ADD_YS_JC);
        }
        if (oldVersion < 6) {
            sqLiteDatabase.execSQL(MAIN_LAYER_CODE);
            sqLiteDatabase.execSQL(SUB_LAYER_CODE);
            sqLiteDatabase.execSQL(SECONDSUB_LAYER_CODE);
            sqLiteDatabase.execSQL(ADD_TEMPLATE);
            sqLiteDatabase.execSQL(ADD_TEMPLATE_DETAIL);
            sqLiteDatabase.execSQL(ADD_DICTIONARY1);
            sqLiteDatabase.execSQL(ADD_DICTIONARY2);
            sqLiteDatabase.execSQL(ADD_DICTIONARY3);
            sqLiteDatabase.execSQL(ADD_DICTIONARY4);
            sqLiteDatabase.execSQL(ADD_DICTIONARY5);
            sqLiteDatabase.execSQL(ADD_DICTIONARY6);
            sqLiteDatabase.execSQL(ADD_DICTIONARY7);
            sqLiteDatabase.execSQL(ADD_DICTIONARY8);
            sqLiteDatabase.execSQL(ADD_DICTIONARY9);
            sqLiteDatabase.execSQL(ADD_DICTIONARY10);
        }
        if (oldVersion < 7) {
            sqLiteDatabase.execSQL(ADD_DOWNLOADID);
        }
        if (oldVersion < 8) {
            sqLiteDatabase.execSQL(ADD_DOWNLOADID_R);
            sqLiteDatabase.execSQL(ADD_DOWNLOADID_G);
        }
        if (oldVersion < 9) {
            sqLiteDatabase.execSQL(ADD_ISUPLOAD);
            sqLiteDatabase.execSQL(ADD_PROJECTID);
            sqLiteDatabase.execSQL(ADD_LABORUNIT);
            sqLiteDatabase.execSQL(ADD_REALNAME);
            sqLiteDatabase.execSQL(ADD_UPLOADID);
            sqLiteDatabase.execSQL(ADD_STATEGW);
            sqLiteDatabase.execSQL(ADD_STATEGW_UD);
        }

    }

}
