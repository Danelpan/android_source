package com.android.kit.cache.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.android.kit.utils.KitBitmapUtils;
import com.android.kit.utils.KitLog;
import com.android.kit.utils.KitStreamUtils;

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
        byte[] bs = pullBlob(context, key);
        if(null != bs){
            try {
                return KitStreamUtils.inputStream2String(KitStreamUtils.byte2InputStream(bs));
            } catch (Exception e) {
                KitLog.printStackTrace(e);
            }
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
        try {
            byte[] bs = value.getBytes("UTF-8");
            if(null != bs){
                putBlob(context, key, bs);
                return true;
            }
        } catch (Exception e) {
            KitLog.printStackTrace(e);
        }
        return false;
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
        try{
            mDatabase.execSQL(CacheEntry.getDropTableSql());
            return true;
        }finally{
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
        Cursor mCursor = null;
        try{
            mDatabase.execSQL(CacheEntry.getCreateTableSql());
            
            mCursor = mDatabase.rawQuery(CacheEntry.getSelectSql(), new String[]{key});
            if(null != mCursor && mCursor.getCount() >0 && mCursor.moveToFirst()){
                return mCursor.getBlob(mCursor.getColumnIndexOrThrow(CacheEntry.COLUMN_NAME_CACHE_VALUE));
            }
        }finally{
            if(null != mCursor){
                mCursor.close();
            }
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
        try{
            mDatabase.execSQL(CacheEntry.getCreateTableSql());
            
            byte[] data = pullBlob(context, key);
            if(null != data){ //删除重复键值
                deleteItem(context, key);
            }
            
            mDatabase.execSQL(CacheEntry.getInsertSql(), new Object[]{null,key,value,System.currentTimeMillis()});
            return true;
        }finally{
            mDatabase.close();
        }
    }
    
    /**
     * 根据key获取该条数据插入时间
     * @param context
     * @param key
     * @return
     */
    public synchronized static final long pullInsertTime(Context context,String key){
        CacheSqlLite mCacheSqlLite = new CacheSqlLite(context);
        SQLiteDatabase mDatabase = mCacheSqlLite.getReadableDatabase();
        if(null == mDatabase){
            return 0;
        }
        Cursor mCursor = null;
        try{
            mDatabase.execSQL(CacheEntry.getCreateTableSql());
            
            mCursor = mDatabase.rawQuery(CacheEntry.getSelectSql(), new String[]{key});
            if(null != mCursor && mCursor.getCount() >0 && mCursor.moveToFirst()){
                return mCursor.getLong(mCursor.getColumnIndexOrThrow(CacheEntry.COLUMN_NAME_CACHE_TIME));
            }
        }finally{
            if(null != mCursor){
                mCursor.close();
            }
            mDatabase.close();
        }
        return 0;
    }
    
    /**
     * 从数据库中查出位图
     * @param context
     * @param key
     * @return
     */
    public synchronized static final Bitmap pullBitmap(Context context,String key){
        return pullBitmap(context,key,0,0);
    }
    
    /**
     * 从数据库中查出位图
     * @param context
     * @param key
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public synchronized static final Bitmap pullBitmap(Context context,String key,int reqWidth,int reqHeight){
        byte[] bs = pullBlob(context, key);
        if(null != bs){
            return KitBitmapUtils.decodeSampledBitmapFromBytes(bs, reqWidth, reqHeight);
        }
        return null;
    } 
}
