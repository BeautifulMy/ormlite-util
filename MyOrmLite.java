package com.mqscience.myapplication.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;

/**
 *     MyOrmLite
 */

public class MyOrmLite extends OrmLiteSqliteOpenHelper {
    private static final String DBNAME = "MyOrmlite.db";
    private static final int DBVERSION = 1;

    private static MyOrmLite myOrmLite = null;
    public HashMap<String,Dao> daos =new HashMap<>();

    /**
     * 构造方法
     * @param context
     */
    private MyOrmLite(Context context) {
        super(context, DBNAME ,null,DBVERSION);
    }

    /**
     * 单例创建对象
     * @param context
     * @return
     */
    public static MyOrmLite newInstance(Context context){
        if(myOrmLite == null){
            synchronized (MyOrmLite.class) {
                if(myOrmLite == null)
                    myOrmLite = new MyOrmLite(context);
            }
        }
        return myOrmLite;
    }

    /**
     * 创建数据库
     * @param database
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

    }

    /**
     * 更新数据库
     * @param database
     * @param connectionSource
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase getDB(){
        return getReadableDatabase();
    }
    /**
     * 动态添加表
     * @param clazz
     */
    public void createTable(Class clazz){
        try {
            TableUtils.createTable(connectionSource,clazz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取DAO库管对象
     * @param clazz
     * @return
     */
    public Dao getDao(Class clazz){

        Dao dao = null;
        //获取类的名称
        String simpleName = clazz.getSimpleName();

        //如果包含这个类，获取这个类的库管
        if (daos.containsKey(simpleName)) {
            dao = daos.get(simpleName);
        }else{//没有这个类，添加这个类

            try {
                //获取类的DAO
                dao = super.getDao(clazz);
                daos.put(simpleName,dao);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dao;
    }
}
