package gjcm.kxf.huifucenter;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


/**
 * 弹出提示框，下载服务组件
 */
public class ApkInstaller {
    private Context context;

    public ApkInstaller(Context context) {
        context = context;
    }

    private String myurl;
    public void install(String url,boolean iscancel) {
        Builder builder = new Builder(context);
        Log.e("kxflog", "LoginActivity--- install-----:" );
        myurl = url;
        builder.setMessage("监测到新版本！\n修复了些许app异常");
        builder.setTitle("下载提示");
        builder.setPositiveButton("确认前往", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Uri uri = Uri.parse(myurl);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(it);
            }
        });
//        builder.setCancelable(iscancel);
        builder.setNegativeButton("残忍拒绝", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        return;
    }

    /**
     * 如果服务组件没有安装打开语音服务组件下载页面，进行下载后安装。
     */
    private boolean processInstall(Context context, String url, String assetsApk) {
        //直接下载方式
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(it);
        return true;
    }
}
