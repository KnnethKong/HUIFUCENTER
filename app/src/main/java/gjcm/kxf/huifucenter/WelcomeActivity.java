package gjcm.kxf.huifucenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.igexin.sdk.PushManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import gjcm.kxf.fragment.MainFragment;
import gjcm.kxf.getui.GeTuiIntentService;
import gjcm.kxf.getui.GetTuiServer;
import gjcm.kxf.tools.NetTools;
import gjcm.kxf.tools.StatusBarCompat;

/**
 * Created by kxf on 2016/12/13.
 */
public class WelcomeActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        PushManager.getInstance().initialize(this.getApplicationContext(), GetTuiServer.class);
        PushManager.getInstance().registerPushIntentService(getApplicationContext(), GeTuiIntentService.class);

//        String id = PushManager.getInstance().getClientid(this);
        getData();
    }

    private void getData() {
        Intent intent = null;
        boolean isremerpwd = sharedPreferences.getBoolean("isremerpwd", false);

        if (isremerpwd) {
            String usert = sharedPreferences.getString("usertoken", "");
            if (!usert.equals("")) {
                intent = new Intent(this, MainFragment.class);
            } else {
                intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            }
        } else {
            intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public String isconnet = "1";

    private void isUpdate() {
        if (dialog != null)
            dialog.dismiss();
        dialog = ProgressDialog.show(this, "", " 请稍候", false, false);
        final boolean isnet = NetTools.isNetworkConnected(this);
        if (!isnet) {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            getData();
            if (dialog != null)
                dialog.dismiss();
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final String appversion = packInfo.versionCode + "";
        RequestParams params = new RequestParams("http://www.vikpay.com/UserDevice/getVersion");
        params.setConnectTimeout(1 * 1000);
        x.http().get(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
//                Log.i("kxflog", result);
                dialog.dismiss();
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    final String apkurl = jsonObj.optString("orderapkUrl");
                    String str = jsonObj.optString("apporderCode");
                    Log.i("kxflog", str + "    " + appversion + "" + apkurl);
                    if (!str.equals(appversion)) {
                        builder.setMessage("监测到新版本！\n修复了些许app异常");
                        builder.setTitle("版本更新");
                        builder.setPositiveButton("确认前往", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isconnet = "2";
                                Uri uri = Uri.parse(apkurl);
                                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(it);
                            }
                        });
                        builder.setNegativeButton("残忍拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getData();
                            }
                        });
                        builder.setCancelable(false);
                        builder.create().show();
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                getData();
                            }
                        });
                    } else {
                        getData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                isconnet = "3";

                Log.i("kxflog", "onError:::::" + ex.getMessage());
                dialog.dismiss();
                getData();

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
                isconnet = "4";
                Log.i("kxflog", "onCancelled:::::");
                dialog.dismiss();
                getData();

            }

            @Override
            public void onFinished() {

            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }
}