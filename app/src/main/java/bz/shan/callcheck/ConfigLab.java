package bz.shan.callcheck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ConfigLab {

    private static ConfigLab sConfigLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ConfigLab get(Context context) {
        if (sConfigLab == null) {
            sConfigLab = new ConfigLab(context);
        }
        return sConfigLab;
    }

    private ConfigLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CallBaseHelper(mContext).getWritableDatabase();

        //test
//        for (int i = 0; i < 100; i++) {
//            Call call = new Call();
//            call.setName("Call #" + i);
//            call.setJunk(i % 2 == 0); // Every other one
//            call.setPhoneNumber(String.format("12345%03d",i));
//            mCalls.add(call);
//        }

    }


    private ConfigCursorWrapper queryConfig() {
        Cursor cursor = mDatabase.query(
                CallDBSchema.ConfigTable.NAME,
                null, // Columns - null selects all columns
                null,
                null,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new ConfigCursorWrapper(cursor);
    }

    public void addConfig(Config c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CallDBSchema.ConfigTable.NAME, null, values);
    }

    public Config getConfig() {
        ConfigCursorWrapper cursor = queryConfig();

        try {
            if (cursor.getCount() == 0) {
                Config cfg = new Config(0);
                addConfig(cfg);
                return cfg;
            }

            cursor.moveToFirst();
            return cursor.getConfig();
        } finally {
            cursor.close();
        }
    }

    public void updateConfig(Config config) {
        ContentValues values = getContentValues(config);

        mDatabase.update(CallDBSchema.ConfigTable.NAME, values,
                null,
                null);
    }

    public int consumeNotId() {
        Config cfg = getConfig();
        int not_id = cfg.consumeNotId();
        updateConfig(cfg);
        return not_id;
    }

    private static ContentValues getContentValues(Config config) {
        ContentValues values = new ContentValues();
        values.put(CallDBSchema.ConfigTable.Cols.NOT_ID, config.getNotId());

        return values;
    }
}
