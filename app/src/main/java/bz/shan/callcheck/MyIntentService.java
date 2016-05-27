package bz.shan.callcheck;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    public static final String ACTION_START = "bz.shan" +
            ".callcheck.action.START";
    private static final String TAG = "MyIntentService";

    public MyIntentService() {
        super("MyIntentService");
    }

    public static void startActionStart(Context context) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        setIntentRedelivery(true);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("callcheck")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("callcheck")
                .setContentText("callcheck started")
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        startForeground(0, notification);

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                handleActionStart();
            }
        }
    }

    private void handleActionStart() {
        // hmm...
//        while (true) {
//            try {
//                Log.i(TAG, "running...");
//                Thread.sleep(60000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

}
