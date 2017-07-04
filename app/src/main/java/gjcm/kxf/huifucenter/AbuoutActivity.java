package gjcm.kxf.huifucenter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import gjcm.kxf.tools.NetTools;

/**
 * Created by kxf on 2016/12/29.
 */
public class AbuoutActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout relativeLayout;
    private ProgressDialog dialog;
    private TextView netVersion, localVersion;
    private String appversion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_layout);
        relativeLayout = (RelativeLayout) findViewById(R.id.about_updateliner);
        netVersion = (TextView) findViewById(R.id.about_showversion);
        relativeLayout.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toobal);
        localVersion = (TextView) findViewById(R.id.about_txt_msg);
//        toolbar.inflateMenu(R.menu.mytoolemenu);
//        toolbar.setNavigationIcon(R.mipmap.ic_drawer_home);
        toolbar.setTitle("关于");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getLocalVersion();
    }

    private void getLocalVersion() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appversion = packInfo.versionCode + "";
        localVersion.setText("当前版本：" + packInfo.versionName);
        final String deviceno = PushManager.getInstance().getClientid(this);
        Log.e("kxflog", deviceno);
    }

    @Override
    public void onClick(View view) {
        isUpdate();
    }

    private void isUpdate() {
        if (dialog != null)
            dialog.dismiss();
        dialog = ProgressDialog.show(this, "", " 正在检查", false, false);
        final boolean isnet = NetTools.isNetworkConnected(this);
        if (!isnet) {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            if (dialog != null)
                dialog.dismiss();
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        RequestParams params = new RequestParams(NetTools.HOMEURL + "/store/app/getVersion");
        params.setConnectTimeout(5 * 1000);
        x.http().get(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    final String apkurl = jsonObj.optString("orderapkUrl");
                    String str = jsonObj.optString("apporderCode");
                    String upversion = jsonObj.optString("apporderVersionName");
                    netVersion.setText(upversion);
                    //Log.i("kxflog", str + "    " + appversion + "" + apkurl);
                    if (!str.equals(appversion)) {
                        builder.setMessage("检测到新版本！\n修复了些许app异常");
                        builder.setTitle("版本更新");
                        builder.setPositiveButton("确认前往", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse(apkurl);
                                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(it);
                            }
                        });
                        builder.setNegativeButton("残忍拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setCancelable(false);
                        builder.create().show();
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                            }
                        });
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (dialog != null)
                    dialog.dismiss();
                Toast.makeText(AbuoutActivity.this, "出现错误" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("kxflog", "onError:::::" + ex.getMessage());

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
                Log.i("kxflog", "onCancelled:::::");//201701060940513730540440
                if (dialog != null)
                    dialog.dismiss();

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
