package com.jiyouliang.fmap.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * sqlite数据库
 */
public abstract class BaseSqliteOpenHelper extends SQLiteOpenHelper {


    public BaseSqliteOpenHelper(@androidx.annotation.Nullable Context context, @androidx.annotation.Nullable String name, @androidx.annotation.Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public BaseSqliteOpenHelper(@androidx.annotation.Nullable Context context, @androidx.annotation.Nullable String name, @androidx.annotation.Nullable SQLiteDatabase.CursorFactory factory, int version, @androidx.annotation.Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public BaseSqliteOpenHelper(@androidx.annotation.Nullable Context context, @androidx.annotation.Nullable String name, int version, @androidx.annotation.NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }
}
