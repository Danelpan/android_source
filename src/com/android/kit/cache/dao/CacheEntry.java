package com.android.kit.cache.dao;

import android.provider.BaseColumns;
/**
 * 数据库变量和语句组装类
 * @author Danel
 *
 */
public class CacheEntry implements BaseColumns{
    protected static final String TABLE_NAME = "kit_cache";
    
    protected static final String COLUMN_NAME_CACHE_KEY = "key";
    protected static final String COLUMN_NAME_CACHE_VALUE = "value";
    protected static final String COLUMN_NAME_CACHE_TIME = "time";
    
    protected static final String TEXT_TYPE = " TEXT";
    protected static final String BLOB_TYPE = " BLOB";
    protected static final String INTEGER_TYPE = " INTEGER";
    
    protected static final String COMMA_SEP = ",";
    
    protected static final String getSelectSql(){
        return "SELECT * FROM " + TABLE_NAME +" WHERE " + COLUMN_NAME_CACHE_KEY + " LIKE ?";
    }
    
    protected static final String getInsertSql(){
        return "INSERT INTO " + TABLE_NAME + " VALUES(?,?,?,?)";
    }
    
    protected static final String getDeleteItemSql(){
        return "DELETE FROM " + TABLE_NAME + " WHERE "  + COLUMN_NAME_CACHE_KEY + " LIKE ?";
    }
    
    protected static final String getCreateTableSql(){
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_CACHE_KEY + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_CACHE_VALUE + BLOB_TYPE + COMMA_SEP +
                COLUMN_NAME_CACHE_TIME + INTEGER_TYPE +
                " )";
    }
    
    protected static final String getDropTableSql(){
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
