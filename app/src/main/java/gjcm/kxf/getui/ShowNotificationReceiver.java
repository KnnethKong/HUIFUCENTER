package gjcm.kxf.getui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import gjcm.kxf.huifucenter.R;

/**
 * Created by kxf on 2017/1/14.
 */
public class ShowNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getExtras().getString("msg");
        String sound = intent.getExtras().getString("sound");
        Uri urisound = Uri.parse(sound);
        Intent broadcastIntent = new Intent(context, ClickNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("款项提醒")
                .setContentText(msg)
                .setWhen(System.currentTimeMillis())
                .setTicker(msg)
                .setContentIntent(pendingIntent)
                .setSound(urisound)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.push);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(2, builder.build());

    }
}