
package com.android.kit.cache.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite数据库，该类负责数据库的创建，
 * @author Danel
 *
 */
public class CacheSqlLite extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "KitCache.db";
    
    public CacheSqlLite(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    
    public CacheSqlLite(Context context, String name ,int version){
        this(context, name, null, version);
    }
    
    public CacheSqlLite(Context context, String name){
        this(context, name , DATABASE_VERSION);
    }
    
    public CacheSqlLite(Context context){
        this(context, DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CacheEntry.getCreateTableSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CacheEntry.getDropTableSql());
        onCreate(db);
    }
    
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        onUpgrade(db, oldVersion, newVersion);
    }
}
