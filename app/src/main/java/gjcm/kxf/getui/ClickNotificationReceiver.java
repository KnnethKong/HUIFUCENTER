package gjcm.kxf.getui;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import gjcm.kxf.huifucenter.WelcomeActivity;

/**
 * Created by kxf on 2016/12/3.
 */
public class ClickNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (isServiceRunning(context, "gjcm.kxf.getui.GetTuiServer")) {
            Log.i("kxflog", "the app process is alive-------");
            Intent mainIntent = new Intent(context, WelcomeActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent detailIntent = new Intent(context, WelcomeActivity.class);
            Intent[] intents = {mainIntent, detailIntent};
            context.startActivities(intents);
        } else {
            Log.i("kxflog", "the app process is dead---------");
            Intent launchIntent = context.getPackageManager().
                    getLaunchIntentForPackage("gjcm.kxf.huifucenter");
            launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            Bundle args = new Bundle();
            args.putString("name", "this name");
            launchIntent.putExtras(args);
            context.startActivity(launchIntent);

        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
    }

    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.isEmpty())
            return false;
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) && TextUtils.equals(
                    serviceList.get(i).service.getPackageName(), context.getPackageName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

}