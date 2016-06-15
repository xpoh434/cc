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

import java.util.Date;

/**
 * Created by shan on 5/20/16.
 */
public class PhoneCallListener extends BroadcastReceiver {

    private static final String LOG_TAG = "PhoneCallListener";

    private boolean ringing = false;

    private JunkcallQuery2 query = new JunkcallQuery2();

    private CallLab callLab;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (null == bundle) {
            return;
        }

        callLab = CallLab.get(context);

        String state = bundle.getString(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(state)) {
            ringing = true;
            String incomingNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i(LOG_TAG,"incomingNumber : "+incomingNumber);

            if(incomingNumber == null || incomingNumber.trim() == "") {
                String msg = String.format("unknown number");
                popup(msg, context);
            } else {
                Call call = callLab.queryCall(incomingNumber);
                String contact = null;
                Exception error = null;
                if (call ==null) {
                    String caller = checkContactNumber(incomingNumber, context);
                    if (caller == null) {
                        call = new Call();
                        call.setPhoneNumber(incomingNumber);
                        call.setDate(new Date());
                        try {
                            String entity = query.check(incomingNumber, context);
                            if ("".equals(entity)) {
                                call.setJunk(false);
                                call.setName("unknown");
                            } else {
                                call.setJunk(true);
                                call.setName(entity);
                            }
                        } catch (JunkcallQuery.ConnectionException e) {
                            call.setJunk(false);
                            call.setName("error");
                            error = e;
                        }
                        callLab.addCall(call);

                    } else {
                        contact= caller;
                    }
                }
                String msg = null;
                if(contact!=null) {
                    msg = String.format("number %s is in contact list (%s)", incomingNumber, contact);
                } else if (error!=null) {
                    msg = "query failed: " + error.getMessage();
                } else if (call.isJunk()) {
                    msg = String.format("number %s is junk (%s)", incomingNumber, call.getName());
                } else {
                    msg = String.format("number %s is not found in junk list", incomingNumber);
                }

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