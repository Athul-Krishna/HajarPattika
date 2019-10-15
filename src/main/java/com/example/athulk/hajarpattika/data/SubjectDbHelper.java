package com.example.athulk.hajarpattika.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.athulk.hajarpattika.data.SubjectContract.SubjectEntry;

public class SubjectDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "attendance.db";
    public static final int DATABASE_VERSION = 1;
    public SubjectDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE "+SubjectEntry.TABLE_NAME+" ("+
                SubjectEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                SubjectEntry.COLUMN_NAME+" TEXT NOT NULL, "+
                SubjectEntry.COLUMN_ATTEND+" INTEGER, "+
                SubjectEntry.COLUMN_BUNK+" INTEGER, "+
                SubjectEntry.COLUMN_UPDATE+ " TEXT)";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
