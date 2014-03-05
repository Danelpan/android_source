package com.android.kit.cache.dao;

import android.provider.BaseColumns;
/**
 * 数据库变量和语句组装类
 * @author Danel
 *
 */
public class CacheEntry implements BaseColumns{
    private static final String TABLE_NAME = "cache";
    
    private static final String COLUMN_NAME_CACHE_ID = "content_id";
    private static final String COLUMN_NAME_CACHE_TEXT = "content_text";
    private static final String COLUMN_NAME_CACHE_BYTE = "content_byte";
    private static final String COLUMN_NAME_CACHE_DATE = "content_date";
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String BYTE_TYPE = " BLOB";
    private static final String DATE_TYPE = " DATE";
    
    private static final String COMMA_SEP = ",";
    
    protected static final String getSelectSql(){
        return "SELECT * FROM " + TABLE_NAME +" WHERE " + COLUMN_NAME_CACHE_ID + "=?";
    }
    
    protected static final String getInsertSql(){
        return "INSERT INTO " + TABLE_NAME + " VALUES(?,?,?,?,?)";
    }
    
    protected static final String getDeleteItemSql(){
        return "DELETE FROM " + TABLE_NAME + " WHERE "  + COLUMN_NAME_CACHE_ID + "=?";
    }
    
    protected static final String getCreateTableSql(){
        return "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_CACHE_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_CACHE_TEXT + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_CACHE_BYTE + BYTE_TYPE + COMMA_SEP +
                COLUMN_NAME_CACHE_DATE + DATE_TYPE +
                " )";
    }
    
    protected static final String getDropTableSql(){
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
