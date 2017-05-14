package com.mqscience.myapplication.model.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.mqscience.myapplication.model.db.MyOrmLite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ConstantDao
 * 此类中包含以下功能：
 * 1.添加一行
 * 2.删除制定的行以及进行and or计算的行，删除所有行
 * 3.更改制定的行以及进行and or计算的行，更改所有行
 * 4.查找制定的行以及进行and or计算的行，查找所有行
 */

public class ConstantDao {

    private Dao dao = null;
    private final MyOrmLite myOrmLite;
    private SQLiteDatabase db;

    /**
     * 创建这个DAO，可以动态添加表
     *
     * @param context
     * @param clazz
     * @param isAdd
     */
    public ConstantDao(Context context, Class clazz, boolean isAdd) {
        myOrmLite = MyOrmLite.newInstance(context);
        if (isAdd) {
            myOrmLite.createTable(clazz);
        }
        dao = myOrmLite.getDao(clazz);
    }

    public SQLiteDatabase getDB() {
        db = myOrmLite.getDB();
        return db;
    }

    /**
     * 删除表
     *
     * @param tableName
     * @return
     */
    public boolean dropTable(String tableName) {

        tableName = "[" + tableName + "]";
        if (db == null) {
            getDB();
        }
        db.execSQL("drop table " + tableName);
        try {
            List list = queryAll();
            if (list == null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return true;
        }

    }


    /**
     * 添加一列
     * @param rawName
     * @param leixing
     */
    public void addRaw(String rawName, String leixing,String leixing_length) {

        if (db == null) {
            getDB();
        }
        if (leixing.toLowerCase().equals("int") | leixing.toLowerCase().equals("integer"))
            db.execSQL("alter table [MQScience.db] add " + rawName + " " + leixing);
        else {
            db.execSQL("alter table [MQScience.db] add " + rawName + " " + leixing + "("+leixing_length+")");

        }

    }

    /**
     * 懒得写了，直接返回dao，自己做去
     *
     * @return
     */
    public Dao getDao() {
        return dao;
    }

    /**
     * 添加
     *
     * @param object 添加的对象
     * @return 返回的结果是改变的行数
     */
    public int add(Object object) {
        int spanChange = 0;
        try {
            spanChange = dao.create(object);
        } catch (SQLException e) {
            Log.e("ConDao-add", e.getMessage());
        }
        return spanChange;
    }

    /**
     * 通过字段相等删除行。不能直接删除行。
     *
     * @param name
     * @param value
     * @return
     */
    public int deleteByName(String name, String value) {
        int spanChange = 0;
        try {
            DeleteBuilder deleteBuilder = dao.deleteBuilder();
            Where eq = deleteBuilder.where().eq(name, value);
            spanChange = deleteBuilder.delete();

        } catch (SQLException e) {
            Log.e("ConDao-deleteByName", e.getMessage());
        }
        return spanChange;
    }

    /**
     * 多条件删除
     *
     * @param andMap and 列名和值
     * @param orMap  or 列名和值
     * @return
     */

    public int deleteByName(Map<String, String> andMap, Map<String, String> orMap) {
        int spanChange = 0;
        try {

            if (!check(andMap, orMap)) {
                return 0;
            }

            DeleteBuilder deleteBuilder = dao.deleteBuilder();
            Where where = deleteBuilder.where();

            if (andMap != null) {
                Set<String> keys = andMap.keySet();
                Iterator<String> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = andMap.get(key);

                    where.eq(key, value);

                    if (iterator.hasNext())
                        where.and();
                }

            }

            if (andMap != null && andMap.size() > 0 && orMap != null && orMap.size() > 0) {
                where.or();
            }

            if (orMap != null) {
                Set<String> orKeys = orMap.keySet();
                Iterator<String> orIterator = orKeys.iterator();

                while (orIterator.hasNext()) {
                    String key = orIterator.next();
                    String value = orMap.get(key);

                    where.eq(key, value);

                    if (orIterator.hasNext())
                        where.or();
                }

            }
            spanChange = deleteBuilder.delete();
        } catch (Exception e) {
            Log.e("ConDao-deleteByName", e.getMessage());
        }
        return spanChange;
    }


    /**
     * 删除所有行
     *
     * @return 返回影响的行数
     */
    public int deleteAll() {
        int spanChange = 0;
        try {
            List list = dao.queryForAll();
            spanChange = dao.delete(list);
        } catch (SQLException e) {
            Log.e("ConDao-deleteAll", e.getMessage());
        }
        return spanChange;
    }

    /**
     * 更新数据库
     *
     * @param columnString 表名
     * @param oldValue     之前的数值
     * @param newValue     更改后的数值
     * @return 多少行刷新
     */
    public int update(String columnString, String oldValue, String newValue) {
        int spanChange = 0;

        try {
            UpdateBuilder updateBuilder = dao.updateBuilder();
            updateBuilder.updateColumnValue(columnString, newValue);
            updateBuilder.where().eq(columnString, oldValue);
            spanChange = updateBuilder.update();

        } catch (SQLException e) {
            Log.e("ConDao-update", e.getMessage());
        }
        return spanChange;
    }

    /**
     * 更新制定列下所有的值
     *
     * @param columnString
     * @param value
     * @return
     */
    public int updataAll(String columnString, String value) {
        int spanChange = 0;

        try {
            spanChange = dao.updateBuilder().updateColumnValue(columnString, value).update();

        } catch (SQLException e) {
            Log.e("ConDao-updataAll", e.getMessage());
        }
        return spanChange;
    }

    /**
     * 根据字段更新
     *
     * @param columnName 列名
     * @param newValue   新的值
     * @param andMap     and 存放的列名和值
     * @param orMap      or 存放的列名和值
     * @return
     */
    public int updateByName(String columnName, String newValue, Map<String, String> andMap, Map<String, String> orMap) {
        int spanChange = 0;
        try {
            if (!check(andMap, orMap)) {
                return 0;
            }
            UpdateBuilder updateBuilder = dao.updateBuilder();
            Where where = updateBuilder.where();

            if (andMap != null) {
                Set<String> keys = andMap.keySet();
                Iterator<String> iterator = keys.iterator();
                updateBuilder.updateColumnValue(columnName, newValue);

                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = andMap.get(key);

                    where.eq(key, value);

                    if (iterator.hasNext())
                        where.and();
                }
            }

            if (andMap != null && andMap.size() > 0 && orMap != null && orMap.size() > 0) {
                where.or();
            }

            if (orMap != null) {
                Set<String> orKeys = orMap.keySet();
                Iterator<String> orIterator = orKeys.iterator();

                while (orIterator.hasNext()) {
                    String key = orIterator.next();
                    String value = orMap.get(key);

                    where.eq(key, value);

                    if (orIterator.hasNext())
                        where.or();
                }

            }


            spanChange = updateBuilder.update();
        } catch (Exception e) {
            Log.e("ConDao-updateByName", e.getMessage());
        }

        return spanChange;
    }

    /**
     * 查询所有
     *
     * @return 结果集
     */
    public List queryAll() {
        List array = new ArrayList();
        try {
            array = dao.queryForAll();

        } catch (SQLException e) {
            Log.e("ConDao-queryAll", e.getMessage());
        }
        return array;
    }

    /**
     * 通过名称查询，
     *
     * @param name  表名
     * @param value 值
     * @return 结果集
     */
    public List queryByName(String name, String value) {
        List list = new ArrayList();
        try {
            list = dao.queryBuilder().where().eq(name, value).query();
        } catch (SQLException e) {
            Log.e("ConDao-queryByName", e.getMessage());
        }
        return list;
    }

    /**
     * 根据and计算 和or 计算查询
     *
     * @param andMap
     * @param orMap
     * @return
     */
    public List queryByName(Map<String, String> andMap, Map<String, String> orMap) {
        List list = new ArrayList();

        if (!check(andMap, orMap)) {
            return queryAll();
        }
        try {
            QueryBuilder queryBuilder = dao.queryBuilder();
            Where where = queryBuilder.where();
            if (andMap != null) {
                Set<String> keys = andMap.keySet();
                Iterator<String> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = andMap.get(key);

                    where.eq(key, value);

                    if (iterator.hasNext())
                        where.and();
                }

            }
            if (andMap != null && andMap.size() > 0 && orMap != null && orMap.size() > 0) {
                where.or();
            }

            if (orMap != null) {
                Set<String> orKeys = orMap.keySet();
                Iterator<String> orIterator = orKeys.iterator();

                while (orIterator.hasNext()) {
                    String key = orIterator.next();
                    String value = orMap.get(key);

                    where.eq(key, value);

                    if (orIterator.hasNext())
                        where.or();
                }

            }

            list = queryBuilder.query();
        } catch (Exception e) {
            Log.e("ConDao-updateByName", e.getMessage());
        }

        return list;
    }


    /**
     * 匹配是否符合条件
     *
     * @param andMap
     * @param orMap
     * @return
     */
    public boolean check(Map<String, String> andMap, Map<String, String> orMap) {
        if (andMap == null && orMap == null) {
            return false;
        }
        if (andMap == null && orMap != null & orMap.size() == 0) {
            return false;
        }
        if (andMap != null && andMap.size() == 0 && orMap == null) {
            return false;
        }
        if (andMap != null && orMap != null & andMap.size() == 0 && orMap.size() == 0) {
            return false;
        }
        return true;
    }
}
