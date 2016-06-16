package bz.shan.callcheck;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class ConfigCursorWrapper extends CursorWrapper {
    public ConfigCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Config getConfig() {
        int notId = getInt(getColumnIndex(CallDBSchema.ConfigTable.Cols.NOT_ID));

        Config cfg = new Config(notId);

        return cfg;
    }
}