package gjcm.kxf.forgetpwd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gjcm.kxf.huifucenter.LoginActivity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.NetTools;

/**
 * 修改密码
 * Created by kxf on 2017/1/16.
 */
public class ChangePwdActiviy extends AppCompatActivity implements View.OnClickListener {
    private EditText editOne, editTwo, editThre;
    private Button btnOk;
    private String usertoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_pwd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.chagepwd_tbar);
        editOne = (EditText) findViewById(R.id.change_onepwd);
        editTwo = (EditText) findViewById(R.id.change_twopwd);
        editThre = (EditText) findViewById(R.id.change_thrpwd);
        btnOk = (Button) findViewById(R.id.change_sendbtn);
        btnOk.setOnClickListener(this);
//        toolbar.inflateMenu(R.menu.mytoolemenu);
//        toolbar.setNavigationIcon(R.mipmap.ic_drawer_home);
        toolbar.setTitle("修改密码");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        usertoken = sharedPreferences.getString("usertoken", "");

    }

    @Override
    public void onClick(View view) {
        if ("" .equals(usertoken)) {
            startActivity(new Intent(this, LoginActivity.class));
            Toast.makeText(this, "请先去登录", Toast.LENGTH_SHORT).show();
            this.finish();
        } else {
            String str1 = editOne.getText().toString().trim();
            String str2 = editTwo.getText().toString().trim();
            String str3 = editThre.getText().toString().trim();
            if (str1.equals("") || str2.equals("") || str3.equals(""))
                return;
            Pattern p = Pattern
                    .compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$");
            Matcher m = p.matcher(str2);
            if (m.matches())

                if (str2.equals(str3)) {
                    changePwd(str2, str1, str3);
                } else {
                    Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private ProgressDialog dialog;

    private void changePwd(String newpwd, String odlpwd, String secondpwd) {
        final boolean isnet = NetTools.isNetworkConnected(this);
        if (!isnet) {
            Toast.makeText(this, "没有网络", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = ProgressDialog.show(this, "", "正在修改密码", false, false);
        RequestParams requestParams = new RequestParams(NetTools.HOMEURL + "/main/updatePassword");
        requestParams.addHeader("token", usertoken);
        requestParams.addQueryStringParameter("wornPassword", odlpwd);
        requestParams.addQueryStringParameter("newPassword", newpwd);
        requestParams.addQueryStringParameter("verifyPassword", secondpwd);
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                Log.i("kxflog", result);
                if (dialog != null)
                    dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean isuc = jsonObject.optBoolean("success");
                    if (isuc)
                        Toast.makeText(ChangePwdActiviy.this, "修改成功", Toast.LENGTH_SHORT).show();
                    else {
                        String str = jsonObject.optString("err_msg");
                        Toast.makeText(ChangePwdActiviy.this, "错误：" + str, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (dialog != null)
                    dialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (dialog != null)
                    dialog.dismiss();
            }

            @Override
            public void onFinished() {

            }
        });

    }
}
