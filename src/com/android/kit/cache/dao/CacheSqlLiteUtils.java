package com.android.kit.cache.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * 缓存数据数据库操作工具类
 * @author Danel
 *
 */
public final class CacheSqlLiteUtils {
    private CacheSqlLiteUtils(){}
    
    /**
     * 更具key从数据库中获取所存储的字符串，若不存在那么返回null
     * @param context
     * @param key
     * @return
     */
    public synchronized static final String pullString(Context context,String key){
        CacheSqlLite mCacheSqlLite = new CacheSqlLite(context);
        SQLiteDatabase mDatabase = mCacheSqlLite.getReadableDatabase();
        if(null == mDatabase){
            return null;
        }
        mDatabase.beginTransaction();
        mDatabase.execSQL(CacheEntry.getCreateTableSql());
        try{
            Cursor mCursor = mDatabase.rawQuery(CacheEntry.getSelectSql(), new String[]{key});
            mDatabase.setTransactionSuccessful();
            if(null != mCursor && mCursor.getCount() >0 && mCursor.moveToFirst()){
                return mCursor.getString(mCursor.getColumnIndexOrThrow(CacheEntry.COLUMN_NAME_CACHE_TEXT));
            }
        }finally{
            mDatabase.endTransaction();
            mDatabase.close();
        }
        return null;
    }
    
    /**
     * 往数据库中插入一条字符串数据
     * @param context
     * @param key
     * @param value
     * @return
     */
    public synchronized static final boolean putString(Context context,String key,String value){
        CacheSqlLite mCacheSqlLite = new CacheSqlLite(context);
        SQLiteDatabase mDatabase = mCacheSqlLite.getWritableDatabase();
        if(null == mDatabase){
            return false;
        }
        mDatabase.beginTransaction();
        mDatabase.execSQL(CacheEntry.getCreateTableSql());
        try{
            
            String data = pullString(context, key);
            if(TextUtils.isEmpty(data)){
                deleteItem(context, key);
            }
            
            mDatabase.execSQL(CacheEntry.getInsertSql(), new String[]{null,key,value,null,System.currentTimeMillis()+""});
            mDatabase.setTransactionSuccessful();
            return true;
        }finally{
            mDatabase.endTransaction();
            mDatabase.close();
        }
    }
    
    /**
     * 更具key删除一箱数据
     * @param context
     * @param key
     * @return
     */
    public synchronized static final boolean deleteItem(Context context,String key){
        CacheSqlLite mCacheSqlLite = new CacheSqlLite(context);
        SQLiteDatabase mDatabase = mCacheSqlLite.getReadableDatabase();
        if(null == mDatabase){
            return false;
        }
        mDatabase.beginTransaction();
        mDatabase.execSQL(CacheEntry.getCreateTableSql());
        try{
            mDatabase.execSQL(CacheEntry.getDeleteItemSql(), new String[]{key});
            mDatabase.setTransactionSuccessful();
            return true;
        }finally{
            mDatabase.endTransaction();
            mDatabase.close();
        }
    }
    
    /**
     * 删除当前缓存表
     * @param context
     * @return
     */
    public synchronized static final boolean dropTable(Context context){
        CacheSqlLite mCacheSqlLite = new CacheSqlLite(context);
        SQLiteDatabase mDatabase = mCacheSqlLite.getReadableDatabase();
        if(null == mDatabase){
            return false;
        }
        mDatabase.beginTransaction();
        try{
            mDatabase.execSQL(CacheEntry.getDropTableSql());
            mDatabase.setTransactionSuccessful();
            return true;
        }finally{
            mDatabase.endTransaction();
            mDatabase.close();
        }
    }
    
    /**
     * 充数据库中获取字节数组，失败返回NULL
     * @param context
     * @param key
     * @return
     */
    public synchronized static final byte[] pullBlob(Context context,String key){
        CacheSqlLite mCacheSqlLite = new CacheSqlLite(context);
        SQLiteDatabase mDatabase = mCacheSqlLite.getReadableDatabase();
        if(null == mDatabase){
            return null;
        }
        mDatabase.beginTransaction();
        mDatabase.execSQL(CacheEntry.getCreateTableSql());
        try{
            Cursor mCursor = mDatabase.rawQuery(CacheEntry.getSelectSql(), new String[]{key});
            mDatabase.setTransactionSuccessful();
            if(null != mCursor && mCursor.getCount() >0 && mCursor.moveToFirst()){
                return mCursor.getBlob(mCursor.getColumnIndexOrThrow(CacheEntry.COLUMN_NAME_CACHE_BYTE));
            }
        }finally{
            mDatabase.endTransaction();
            mDatabase.close();
        }
        return null;
    }
    
    /**
     * 往数据库中插入字节
     * @param context
     * @param key
     * @param value
     * @return
     */
    public synchronized static final boolean putBlob(Context context,String key,byte[] value){
        CacheSqlLite mCacheSqlLite = new CacheSqlLite(context);
        SQLiteDatabase mDatabase = mCacheSqlLite.getWritableDatabase();
        if(null == mDatabase){
            return false;
        }
        mDatabase.beginTransaction();
        mDatabase.execSQL(CacheEntry.getCreateTableSql());
        try{
            
            byte[] data = pullBlob(context, key);
            if(null != data){ //删除重复键值
                deleteItem(context, key);
            }
            
            mDatabase.execSQL(CacheEntry.getInsertSql(), new Object[]{null,key,null,value,System.currentTimeMillis()+""});
            mDatabase.setTransactionSuccessful();
            return true;
        }finally{
            mDatabase.endTransaction();
            mDatabase.close();
        }
    }
}
