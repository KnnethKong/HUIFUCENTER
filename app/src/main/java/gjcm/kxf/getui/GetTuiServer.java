package gjcm.kxf.getui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.igexin.sdk.GTServiceManager;

/**
 * Created by kxf on 2016/12/23
 */
public class GetTuiServer extends Service {
    String TAG = "kxflog";

    @Override
    public void onCreate() {
        Log.d(TAG, "GetTuiServer    -> onCreate");
        super.onCreate();
        GTServiceManager.getInstance().onCreate(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "GetTuiServer    -> onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return GTServiceManager.getInstance().onStartCommand(this, intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "GetTuiServer    -> onBind");
        return GTServiceManager.getInstance().onBind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "GetTuiServer    -> onDestroy");
        super.onDestroy();
        GTServiceManager.getInstance().onDestroy();
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "GetTuiServer    -> onLowMemory");
        super.onLowMemory();
        GTServiceManager.getInstance().onLowMemory();
    }
}