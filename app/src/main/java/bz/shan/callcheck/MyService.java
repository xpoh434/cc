package bz.shan.callcheck;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.UUID;

public class MyService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private View view;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onCreate();

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (view != null) {
            wm.removeView(view);
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                0,
                0,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_popup,null);
        TextView textview = (TextView) view.findViewById(R.id.popup_text);
        textview.setText(intent.getStringExtra("msg"));

        wm.addView(view, params);


        return START_STICKY;
    }

    public void onDestroy() {

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.removeView(view);
        view = null;
    }

}