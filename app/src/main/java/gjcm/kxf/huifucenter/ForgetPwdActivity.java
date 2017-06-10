package gjcm.kxf.huifucenter;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import gjcm.kxf.tools.NetTools;

/**
 * Created by kxf on 2017/1/7.
 */
public class ForgetPwdActivity extends AppCompatActivity implements View.OnClickListener {

    private static TextView txtTimer;
    private EditText editPhone, editVerification;
    private Button nextBtn;
    private TimeCount timeCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_pwd_layout);
        editPhone = (EditText) findViewById(R.id.forget_editname);
//        editVerification = (EditText) findViewById(R.id.forget_edityzm);
//        nextBtn = (Button) findViewById(R.id.forget_next);
//        nextBtn.setOnClickListener(this);
//        txtTimer = (TextView) findViewById(R.id.forget_txtyzm);
//        txtTimer.setOnClickListener(this);
//        timeCount = new TimeCount(60000, 1000);
    }

    private static class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕

            txtTimer.setClickable(true);
            txtTimer.setText("获取验证码");
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            txtTimer.setClickable(false);
            txtTimer.setText(millisUntilFinished / 1000 + "");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forget_txtyzm:
                timeCount.start();
                String phonemuber = editPhone.getText().toString();

                break;
            case R.id.forget_next:
                String stryzm = editVerification.getText().toString();
                chekVerifition();

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeCount != null)
            timeCount.cancel();
    }

    private void chekVerifition() {
        RequestParams params = new RequestParams(NetTools.HOMEURL + "/main/app/login");
        params.setConnectTimeout(3 * 1000);
//        params.addQueryStringParameter("username", uname);
//        params.addQueryStringParameter("password", upwd);
//        params.addQueryStringParameter("cid", deviceno);

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }
}

