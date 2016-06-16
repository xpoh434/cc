package bz.shan.callcheck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CallBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "callBase.db";

    public CallBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CallDBSchema.CallTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CallDBSchema.CallTable.Cols.UUID + " TEXT UNIQUE NOT NULL, " +
                CallDBSchema.CallTable.Cols.NAME + " TEXT, " +
                CallDBSchema.CallTable.Cols.NUMBER + " TEXT, " +
                CallDBSchema.CallTable.Cols.DATE + " INT, " +
                CallDBSchema.CallTable.Cols.JUNK + " INT" +
                ")");

        db.execSQL("create index " + CallDBSchema.CallTable.NAME + "_number_idx on "+CallDBSchema.CallTable.NAME+" (" + CallDBSchema.CallTable.Cols.NUMBER + ")");
    }

    public void createConfigTable(SQLiteDatabase db) {
        db.execSQL("create table " + CallDBSchema.ConfigTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CallDBSchema.ConfigTable.Cols.NOT_ID + " INT DEFAULT 0" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <= 1) {
            createConfigTable(db);
        }

    }
}
