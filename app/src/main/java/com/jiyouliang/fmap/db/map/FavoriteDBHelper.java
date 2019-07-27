package com.jiyouliang.fmap.db.map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.jiyouliang.fmap.db.BaseSqliteOpenHelper;


/**
 * 我的收藏
 */
public class FavoriteDBHelper extends BaseSqliteOpenHelper {


    public FavoriteDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
