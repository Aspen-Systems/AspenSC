package com.aspen.aspensc;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rfrench on 1/21/2015.
 */
public class AspenSQLiteHelper extends SQLiteOpenHelper
{

    public MySQLiteHelper(Context context)
    {
        super(context, "Canopy", null, 1);
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
