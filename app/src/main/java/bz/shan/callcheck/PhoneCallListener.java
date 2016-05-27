package bz.shan.callcheck;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by shan on 5/20/16.
 */
public class PhoneCallListener extends BroadcastReceiver {

    private static final String LOG_TAG = "PhoneCallListener";

    private boolean ringing = false;

    private JunkcallQuery query = new JunkcallQuery();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (null == bundle) {
            return;
        }

        String state = bundle.getString(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(state)) {
            ringing = true;
            String incomingNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i(LOG_TAG,"incomingNumber : "+incomingNumber);

                String caller = checkContactNumber(incomingNumber, context);
                if(caller == null) {
                    try {
                        String entity = query.check(incomingNumber, context);
                        if("".equals(entity)) {
                            Log.i(LOG_TAG, "number " + incomingNumber + " is not junk");
                        } else {
                            String msg = String.format("number %s is junk (%s)", incomingNumber, entity);
                            Log.i(LOG_TAG, msg);
                            popup(msg, context);
                        }
                    } catch (JunkcallQuery.ConnectionException e) {
                        Log.e(LOG_TAG, "query failed: " + e.getMessage());
                        popup("network problem", context);
                    }
                } else {
                    String msg = String.format("number %s is in contact list (%s)", incomingNumber, caller);
                    Log.i(LOG_TAG, msg);
                    popup(msg, context);
                }

        } else {
            ringing = false;
            Log.i(LOG_TAG,"phone idle");
            dismiss(context);
        }
    }

    private void popup(String msg, Context context) {

        Intent i = new Intent(context, MyService.class);
        i.putExtra("msg", msg);
        context.startService(i);

//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
//                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
//
//        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
    private void dismiss(Context context) {
        //
        Intent i = new Intent(context, MyService.class);
        context.stopService(i);

    }

    private String checkContactNumber(String number, Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        try {
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        try {
                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[\\(\\)\\-\\s]","");
                                Log.i(LOG_TAG,phoneNo + " " + number);
                                if (number.equals(phoneNo)) {
                                    return name;
                                }
                            }
                        } finally {
                            pCur.close();
                        }
                    }
                }
            }
        } finally {
            cur.close();
        }
        return null;
    }
}