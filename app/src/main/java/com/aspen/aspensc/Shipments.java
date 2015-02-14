package com.aspen.aspensc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rfrench on 2/13/2015.
 */
public class Shipments extends SQLiteOpenHelper
{

    private static final String DBName= "AspenMobile.db";
    private static final int DBVer= 1;
    private static final String mTableName = "OE_Shipments";

    private static final String SQL_CREATE_TABLE ="CREATE TABLE `OE_Shipments` (\n" +
            "\t`BillOfLadingNumber`\tINTEGER NOT NULL,\n" +
            "\t`UserID_InUseBy`\tTEXT,\n" +
            "\t`UserID_CreatedBy`\tTEXT,\n" +
            "\t`UserID_LastUpdated`\tTEXT,\n" +
            "\t`VendorKey`\tINTEGER,\n" +
            "\t`ShipmentNumber`\tTEXT NOT NULL,\n" +
            "\t`ShipmentDate`\tTEXT,\n" +
            "\t`DateTimeCreated`\tTEXT,\n" +
            "\t`MinimumWeight`\tINTEGER NOT NULL,\n" +
            "\t`TotalFreightCharges`\tNUMERIC NOT NULL,\n" +
            "\t`TotalSurcharges`\tNUMERIC NOT NULL,\n" +
            "\t`TotalOtherCharges`\tNUMERIC NOT NULL,\n" +
            "\t`TotalWeight`\tNUMERIC NOT NULL,\n" +
            "\t`TotalCases`\tINTEGER,\n" +
            "\t`WarehouseCode`\tINTEGER,\n" +
            "\tPRIMARY KEY(BillOfLadingNumber)\n" +
            ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + mTableName;


    // Database creation sql statement
    private static final String DATABASE_CREATE = "";


    public Shipments(Context context)
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
