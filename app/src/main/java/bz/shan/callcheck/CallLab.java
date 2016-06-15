package bz.shan.callcheck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static bz.shan.callcheck.CallDBSchema.*;

/**
 * Created by shan on 6/2/16.
 */
public class CallLab {
    private static CallLab sCallLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CallLab get(Context context) {
        if (sCallLab == null) {
            sCallLab = new CallLab(context);
        }
        return sCallLab;
    }

    private CallLab(Context context) {
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

    public List<Call> getCalls() {
        List<Call> calls = new ArrayList<>();

        CallCursorWrapper cursor = queryCalls(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                calls.add(cursor.getCall());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return calls;
    }

    public Call getCall(UUID id) {
        CallCursorWrapper cursor = queryCalls(
                CallTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCall();
        } finally {
            cursor.close();
        }
    }

    public Call queryCall(String number) {
        CallCursorWrapper cursor = queryCalls(
                CallTable.Cols.NUMBER + " = ?",
                new String[] { number }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCall();
        } finally {
            cursor.close();
        }
    }

    public void addCall(Call c) {
        if ((c.getName()!=null && !"".equals(c.getName().trim())) || (c.getPhoneNumber()!=null  && !"".equals(c.getPhoneNumber().trim()))) {
            ContentValues values = getContentValues(c);
            mDatabase.insert(CallTable.NAME, null, values);
        }
    }

    public void deleteCall(Call c) {
        String uuidString = c.getId().toString();

        mDatabase.delete(CallTable.NAME, CallTable.Cols.UUID + " = ?",
                new String[] { uuidString });

    }


    public void updateCall(Call call) {
        String uuidString = call.getId().toString();
        ContentValues values = getContentValues(call);

        mDatabase.update(CallTable.NAME, values,
                CallTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Call call) {
        ContentValues values = new ContentValues();
        values.put(CallTable.Cols.UUID, call.getId().toString());
        values.put(CallTable.Cols.NAME, call.getName());
        values.put(CallTable.Cols.NUMBER, call.getPhoneNumber());
        values.put(CallTable.Cols.DATE, call.getDate().getTime());
        values.put(CallTable.Cols.JUNK, call.isJunk() ? 1 : 0);

        return values;
    }

    private CallCursorWrapper queryCalls(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CallTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new CallCursorWrapper(cursor);
    }


}