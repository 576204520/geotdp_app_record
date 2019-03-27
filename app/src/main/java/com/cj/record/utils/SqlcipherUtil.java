package com.cj.record.utils;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

public class SqlcipherUtil {
    public static void encrypt(String dbName, String passphrase) throws IOException {
        L.e("SqlcipherUtil------------------>>>>>>>>>>>>>encrypt");
        File originalFile = new File(dbName);
        if (originalFile.exists()) {
            File newFile = new File(Urls.DATABASE_PATH + File.separator + "encrypted.db");
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            //打开未加密的数据库，密码是空的
            SQLiteDatabase db = SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(), "", null, SQLiteDatabase.OPEN_READWRITE);
            //新建一个加密的数据库
            db.rawExecSQL(String.format("ATTACH DATABASE '" + newFile.getAbsolutePath() + "' AS encrypted KEY '" + passphrase + "';"));
            //拷贝数据到加密的数据库中
            db.rawExecSQL("SELECT sqlcipher_export('encrypted')");
            //断开链接
            db.rawExecSQL("DETACH DATABASE encrypted;");
            //获取老数据的版本
            int version = db.getVersion();
            L.e("encrypt:version=" + version);
            db.close();
            //打开加密的数据库
            db = SQLiteDatabase.openDatabase(newFile.getAbsolutePath(), passphrase, null, SQLiteDatabase.OPEN_READWRITE);
            //设置版本号
            db.setVersion(version);
            db.close();
            //删除老文件，重命名
            originalFile.delete();
            newFile.renameTo(originalFile);
        }
    }

    public static void decrypt(String dbName, String passphrase) throws IOException {
        L.e("SqlcipherUtil------------------>>>>>>>>>>>>>decrypt");
        File originalFile = new File(dbName);
        if (originalFile.exists()) {
            File newFile = new File(Urls.DATABASE_PATH + File.separator + "decrypt.db");
            if (newFile.exists()) {
                newFile.delete();
            }
            newFile.createNewFile();
            SQLiteDatabase db = SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(), passphrase, null, SQLiteDatabase.OPEN_READWRITE);
            //新建一个没有密码的数据库
            db.rawExecSQL(String.format("ATTACH DATABASE '" + newFile.getAbsolutePath() + "' AS decrypt KEY '';"));
            //拷贝数据到没有密码的数据中
            db.rawExecSQL("SELECT sqlcipher_export('decrypt')");
            //断开链接
            db.rawExecSQL("DETACH DATABASE decrypt;");
            L.e("decrypt:version=" + db.getVersion());
            db.close();
        }


    }

}
