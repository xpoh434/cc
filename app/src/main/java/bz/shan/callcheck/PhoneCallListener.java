package bz.shan.callcheck;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

/**
 * Created by shan on 5/20/16.
 */
public class PhoneCallListener extends BroadcastReceiver {

    private static final String LOG_TAG = "PhoneCallListener";

    private boolean ringing = false;

    private int oldRingMode = 0;

    private JunkcallQuery2 query = new JunkcallQuery2();
    private JunkcallQuery3 query3 = new JunkcallQuery3();

//    private CallLab callLab;

    //private ConfigLab configLab;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (null == bundle) {
            return;
        }

        String state = bundle.getString(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(state)) {
            ringing = true;
            silenceRinger(context);

            String incomingNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i(LOG_TAG,"incomingNumber : "+incomingNumber);

            try {
                if(incomingNumber == null || incomingNumber.trim() == "") {
                    String msg = String.format("unknown number");
                    popup(msg, context);
                } else {
                    CallLab callLab = CallLab.get(context);

                    Call call = callLab.queryCall(incomingNumber);
                    String contact = null;
                    Exception error = null;
                    if (call == null) {
                        String caller = checkContactNumber(incomingNumber, context);
                        if (caller == null) {
                            call = new Call();
                            call.setPhoneNumber(incomingNumber);
                            call.setDate(new Date());
                            boolean done = false;
                            try {
                                QueryResult r = query3.check(incomingNumber, context);
                                if ((r.entity.length() > 0) && r.junk){
                                    call.setJunk(r.junk);
                                    call.setName(r.entity);
                                    done = true;
                                }
                            } catch(ConnectionException e) {
                                Log.i(LOG_TAG, "query3 failed");
                                //
                            }

                            if (!done) {
                                try {
                                    QueryResult r = query.check(incomingNumber, context);
                                    call.setJunk(r.junk);
                                    call.setName(r.entity);
                                } catch (ConnectionException e) {
                                    call.setJunk(false);
                                    call.setName("error");
                                    error = e;
                                }
                            }
                            callLab.addCall(call);

                        } else {
                            contact = caller;
                        }
                    }
                    String msg = null;
                    if (contact != null) {
                        msg = String.format("number %s is in contact list (%s)", incomingNumber, contact);
                        popup(msg, context);
                    } else if (error != null) {
                        msg = "query failed: " + error.getMessage();
                        popup(msg, context);
                    } else if (call.isJunk()) {
                        msg = String.format("number %s is junk (%s)", incomingNumber, call.getName());
                        UUID id = call.getId();
                        killCall(context);
                        notify(msg, context, id);
                    } else {
                        msg = String.format("number %s is not junk (%s)", incomingNumber, call.getName());
                        popup(msg, context);
                    }

                    Log.i(LOG_TAG, msg);
                }
            } finally {
                resetRinger(context);
            }
        } else {
            ringing = false;
            Log.i(LOG_TAG,"phone idle");

            dismiss(context);
        }
    }

    private void resetRinger(Context context) {
        if (ringing) {
            AudioManager amanager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            amanager.setRingerMode(oldRingMode); // normal
            Log.i(LOG_TAG, "reset ringer mode");
        }
    }

    private void silenceRinger(Context context) {
        if (ringing) {
            AudioManager amanager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            oldRingMode = amanager.getRingerMode();
            amanager.setRingerMode(0); // normal
            Log.i(LOG_TAG, "slient ringer");
        }
    }

    private void killCall(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            Object telephonyService = m.invoke(tm); // Get the internal ITelephony object
            c = Class.forName(telephonyService.getClass().getName()); // Get its class

//            m = c.getDeclaredMethod("silenceRinger"); // Get the "silenceRinger()" method
//            m.setAccessible(true); // Make it accessible
//            m.invoke(telephonyService); // invoke endCall()

            m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
            m.setAccessible(true); // Make it accessible
            m.invoke(telephonyService); // invoke endCall()
            Log.i(LOG_TAG, "hang up");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notify(String msg, Context context, UUID id) {
        ConfigLab configLab = ConfigLab.get(context);

        Resources resources = context.getResources();
        Intent i = CallPagerActivity.newIntent(context, id);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(resources.getString(R.string.incoming_call))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.incoming_call))
                .setContentText(msg)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(configLab.consumeNotId(), notification);

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
//                                Log.i(LOG_TAG,phoneNo + " " + number);
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