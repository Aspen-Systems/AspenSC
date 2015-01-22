package com.aspen.aspensc;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

/**
 * Created by rfrench on 1/21/2015.
 */
public class AspenSQLiteHelper extends SQLiteOpenHelper
{

    private static final String DBName= "Canopy";
    private static final int DBVer= 1;

    public AspenSQLiteHelper(Context context)
    {
        super(context, DBName, null, DBVer);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
