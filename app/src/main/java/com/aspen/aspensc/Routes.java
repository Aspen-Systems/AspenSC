package com.aspen.aspensc;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

/**
 * Created by rfrench on 1/21/2015.
 */
public class Routes extends SQLiteOpenHelper
{

    private static final String DBName= "Routes";
    private static final int DBVer= 1;
    private static final String TableName = "OE_CodeRoutes";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "";


    public Routes(Context context)
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
