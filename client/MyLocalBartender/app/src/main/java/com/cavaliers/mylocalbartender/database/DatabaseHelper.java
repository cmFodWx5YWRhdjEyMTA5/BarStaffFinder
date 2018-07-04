package com.cavaliers.mylocalbartender.database;

import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JamesRich on 25/03/2017.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * The class is for the intended use by the GUI to make sure that
 * objects such as bitmaps and strings can be reused when the fragmemnt
 * or activity lifecycle restarts.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "barstaff_profile"; // the name of our database
    private static final int DB_VERSION = 1; // the version of the database

    private static final String DB_TABLE = "barstaff_table";

    private static final String PRIMARY_KEY_ID = "id";

    private static final String KEY_IMAGE = "image_data";

    public DatabaseHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    /**
     * creates a new table designed for storing images.
     * @param db the SQLite helper builds a database when it is created
     */
    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL("CREATE TABLE BARSTAFFDETAILS ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "IMAGE_RESOURCE_ID INTEGER);");

    }

    /**
     * In order to update, the current table is dropped
     * and a new one is created
     * @param db database passed in gets it's command to Drop itself,
     * @param oldVersion currently unused
     * @param newVersion currently unused
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);

    }

    /**
     *Details are inseerted into each row.
     * @param db database passed in and has a row added to the table
     * @param resourceId attaches an id, to each item added onto the table
     */
    private static void insertDetailsRow(SQLiteDatabase db, int resourceId) {

        ContentValues BarstaffValues = new ContentValues();
        BarstaffValues.put("IMAGE_RESOURCE_ID", resourceId);
        db.insert("DETAILS", null, BarstaffValues);

    }

}
