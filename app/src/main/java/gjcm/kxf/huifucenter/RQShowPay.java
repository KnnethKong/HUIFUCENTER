package gjcm.kxf.huifucenter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import gjcm.kxf.tools.NetTools;
import gjcm.kxf.tools.PostUtils;
import gjcm.kxf.tools.QRCodeUtil;

/**
 * 二维码收款-> ui第一版
 * Created by kxf on 2016/12/14.
 */
public class RQShowPay extends AppCompatActivity implements View.OnClickListener {
    private static ImageView imageView;
    private String userID, monery, undis;
    private TextView txtRefrsh, txtRqmsg;
    private int widthPixels, heightPixels;
    private Timer timer;
    private Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    imageView.setImageBitmap(null);
                    getRQCode();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rqshow_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.rqshow_toolbar);
        toolbar.setTitle("二维码收款");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView != null)
                    imageView = null;
                if (timer != null)
                    timer.cancel();
                if (myhandler != null)
                    myhandler = null;
                finish();
            }
        });

        monery = getIntent().getStringExtra("monery");
        undis = getIntent().getStringExtra("discount");
        imageView = (ImageView) findViewById(R.id.rqshow_img);
//        button = (Button) findViewById(R.id.rqshow_btn);
//        button.setOnClickListener(this);
        txtRefrsh = (TextView) findViewById(R.id.rqshow_refesh);
        txtRefrsh.setOnClickListener(this);
        txtRqmsg = (TextView) findViewById(R.id.rqshow_msg);
        txtRqmsg.setText("收款  " + monery + "  元");
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density1 = dm.density;
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels;
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                myhandler.sendEmptyMessage(2);
            }
        };

        timer.schedule(timerTask, 0, 1 * 60 * 1000);

    }

    private String dateToStamp() throws ParseException {
        long curren = System.currentTimeMillis();
        curren += 15 * 60 * 1000;
        Date da = new Date(curren);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = dateFormat.format(da);
        da = dateFormat.parse(str);
        long unixTimestamp = da.getTime();
        return unixTimestamp + "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (imageView != null)
            imageView = null;
        if (timer != null)
            timer.cancel();
        if (myhandler != null)
            myhandler = null;
        this.finish();
    }

    private void getRQCode() {
        String timestamp = "";
        try {
            timestamp = dateToStamp();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        float fmonery = Float.valueOf(monery);
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        userID = sharedPreferences.getString("userId", "");
        if (userID.equals("")) {
            Toast.makeText(this, "身份登录过期", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();//qrcode merchant
        stringBuffer.append(NetTools.HOMEURL+"/api/redirect/pay-qrcode-redirect?userId=");
        stringBuffer.append(userID + "&totalFee=" + fmonery + "&channel=2&endTime=" + timestamp + "&undiscountFee=" + undis);
        Log.i("kxflog", " ----  " + stringBuffer.toString());
        try {
            Bitmap bitmap = QRCodeUtil.createQRCode(stringBuffer.toString(), widthPixels / 2);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageView != null)
            imageView = null;
        if (timer != null)
            timer.cancel();
        if (myhandler != null)
            myhandler = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rqshow_refesh:
                myhandler.sendEmptyMessage(2);
                break;
//            case R.id.rqshow_close:
//                if (timer != null)
//                    timer.cancel();
//                this.finish();
//                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (timer != null)
//            timer.cancel();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
