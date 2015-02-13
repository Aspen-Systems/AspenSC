package com.aspen.aspensc;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

/**
 * Created by rfrench on 1/21/2015.
 */
public class Routes extends SQLiteOpenHelper
{

    private static final String DBName= "AspenMobile.db";
    private static final int DBVer= 1;
    private static final String mTableName = "OE_CodeRoutes";

    private static final String SQL_CREATE_TABLE ="CREATE TABLE `OE_CodeRoutes` (\n" +
            "\t`RouteCode`\tINTEGER NOT NULL,\n" +
            "\t`Description1`\tTEXT NOT NULL,\n" +
            "\t`Description2`\tTEXT,\n" +
            "\t`Display`\tTEXT NOT NULL,\n" +
            "\t`TruckCode`\tINTEGER,\n" +
            "\t`USDA_Destination`\tINTEGER,\n" +
            "\t`USDA_FOBPrice`\tNUMERIC NOT NULL,\n" +
            "\t`DeliverOnMonday`\tINTEGER NOT NULL,\n" +
            "\t`DeliverOnTuesday`\tINTEGER NOT NULL,\n" +
            "\t`DeliverOnWednesday`\tINTEGER NOT NULL,\n" +
            "\t`DeliverOnThursday`\tINTEGER NOT NULL,\n" +
            "\t`DeliverOnFriday`\tINTEGER NOT NULL,\n" +
            "\t`DeliverOnSaturday`\tINTEGER NOT NULL,\n" +
            "\t`DeliverOnSunday`\tINTEGER NOT NULL,\n" +
            "\tPRIMARY KEY(RouteCode)\n" +
            ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + mTableName;


    // Database creation sql statement
    private static final String DATABASE_CREATE = "";


    public Routes(Context context)
    {
        super(context, DBName, null, DBVer);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //db.execSQL();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
