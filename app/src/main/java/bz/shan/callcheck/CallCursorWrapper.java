package bz.shan.callcheck;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class CallCursorWrapper extends CursorWrapper {
    public CallCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Call getCall() {
        String uuidString = getString(getColumnIndex(CallDBSchema.CallTable.Cols.UUID));
        String name = getString(getColumnIndex(CallDBSchema.CallTable.Cols.NAME));
        String number = getString(getColumnIndex(CallDBSchema.CallTable.Cols.NUMBER));
        long date = getLong(getColumnIndex(CallDBSchema.CallTable.Cols.DATE));
        int isJunk= getInt(getColumnIndex(CallDBSchema.CallTable.Cols.JUNK));

        Call call = new Call(UUID.fromString(uuidString));
        call.setName(name);
        call.setPhoneNumber(number);
        call.setDate(new Date(date));
        call.setJunk(isJunk != 0);

        return call;
    }
}