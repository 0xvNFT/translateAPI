package com.dichthuatjun88binh.jun88.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperDB extends SQLiteOpenHelper {
    SQLiteDatabase myDb;
    //public static final String DATABASE_NAME = "MyDBName.db";

    public HelperDB(Context context) {
        super(context, "Jun88.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        myDb = db;
        myDb.execSQL("CREATE  TABLE IF NOT EXISTS Translation_Data(id integer PRIMARY KEY AUTOINCREMENT," +
                "source_lan text, source_txt text, target_lan text, target_txt text);");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS Trans_Data");

        onCreate(db);
    }

    public boolean InsertRecord(String tableName, String source_language, String source_language_txt, String target_language,
                                String target_language_txt) {
        myDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("source_lan", source_language);
        contentValues.put("source_txt", source_language_txt);
        contentValues.put("target_lan", target_language);
        contentValues.put("target_txt", target_language_txt);
        long result = myDb.insert(tableName, null, contentValues);
        return result != -1;
    }

    public Cursor getTransData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Translation_Data", null);
        return res;
    }

    public Integer delTrans(String id) {
        myDb = this.getReadableDatabase();
        return myDb.delete("Translation_Data", "id= ?", new String[]{id});

    }

    public void delAll() {
        myDb = this.getReadableDatabase();
        myDb.delete("Translation_Data", null, null);
    }

}