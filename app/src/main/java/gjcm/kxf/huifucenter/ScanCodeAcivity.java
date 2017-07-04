package gjcm.kxf.huifucenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zxing.lib.MyScanActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import gjcm.kxf.tools.NetTools;
import gjcm.kxf.tools.PrintTools;

/**
 * 扫码支付
 * Created by kxf on 2016/12/28.
 */
public class ScanCodeAcivity extends AppCompatActivity {
    private String userToken, blueadress, shouyy, undis, monery;
    private TextView txtstatus, txtorderamount, txtrealamount, txtdisamount, txtundisamount, txtpaytype, txtpaytime, txtordernumber, txtalino, txtwuyong, txttypeyh, txtshanghu;
    private SharedPreferences sharedPreferences;
    private String storeName;
    private RelativeLayout typeLiner, merchantLiner;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (printTools!=null){
            printTools.disconnect();
        printTools=null;}
        progressDialog = null;
        handler = null;
        sharedPreferences = null;
        typeLiner = null;
        merchantLiner = null;
        Log.i("kxflog", "MyScanActivity----onDestroy");
    }

    //    private  DemoHandler handler;
    private static DemoHandler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.scancode_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toobal);
        toolbar.setTitle("收款详情");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        monery = getIntent().getStringExtra("monery");
        undis = getIntent().getStringExtra("discount");
        txtstatus = (TextView) findViewById(R.id.scancode_status);
        txtorderamount = (TextView) findViewById(R.id.scancode_orderamount);
        txtrealamount = (TextView) findViewById(R.id.scancode_realamount);
        txtdisamount = (TextView) findViewById(R.id.scancode_disamount);
        txtundisamount = (TextView) findViewById(R.id.scancode_nodisamount);
        txtpaytype = (TextView) findViewById(R.id.scancode_paytype);
        txtpaytime = (TextView) findViewById(R.id.scancode_paytime);
        txtordernumber = (TextView) findViewById(R.id.scancode_payno);
        txtalino = (TextView) findViewById(R.id.scancode_alino);
        txtwuyong = (TextView) findViewById(R.id.scancode_typewuy);
        txttypeyh = (TextView) findViewById(R.id.scancode_typeyouhui);
        typeLiner = (RelativeLayout) findViewById(R.id.scancode_typerliner);
        merchantLiner = (RelativeLayout) findViewById(R.id.scancode_shangjialiner);
        txtshanghu = (TextView) findViewById(R.id.scancode_shanghu);
        Intent intent = new Intent(this, MyScanActivity.class);
        intent.putExtra("iscard", "2");
        startActivityForResult(intent, 0);
    }

    public class DemoHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 22:
                    Bundle bundle = msg.getData();
                    String no = bundle.getString("scanno");
                    Log.i("kxflog", "onActivityResult----resultCode---" + no);
                    uploadData(no);
                    break;
                case 55:
                    Toast.makeText(ScanCodeAcivity.this, "打印机连接失败，请检查打印机", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        userToken = sharedPreferences.getString("usertoken", "");
        shouyy = sharedPreferences.getString("shouyy", "");
        blueadress = sharedPreferences.getString("blueadress", "");
        storeName = sharedPreferences.getString("storeName", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("kxflog", "onActivityResult----resultCode---" + resultCode);
        if (resultCode == RESULT_OK) {
            final Bundle bundle = data.getExtras();
            if (bundle != null) {
                final String result = bundle.getString("result");
                handler = new DemoHandler();
                new Thread() {
                    @Override
                    public void run() {
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("scanno", result);
                        Log.i("kxflog", "result: " + result);
                        Message message = Message.obtain();
                        message.what = 22;
                        message.setData(bundle1);
                        handler.sendMessage(message);
                    }
                }.start();
            }
        } else if (resultCode == RESULT_CANCELED) {
            this.finish();
        }
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private ProgressDialog progressDialog;

    private void uploadData(String authcode) {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在查询...", true, false);
        final DecimalFormat df3 = new DecimalFormat("0.00");
        RequestParams requestParams = new RequestParams(NetTools.HOMEURL + "/pay/wx-pay");
        requestParams.addHeader("token", userToken);
        requestParams.addBodyParameter("body", storeName);
        requestParams.addBodyParameter("detail", "");
        requestParams.addBodyParameter("totalFee", monery, null);
        requestParams.addBodyParameter("feeType", "CNY");
        requestParams.addBodyParameter("goodsTag", "0");
        requestParams.addBodyParameter("authCode", authcode);
        requestParams.addBodyParameter("deviceInfo", "android");
        requestParams.addBodyParameter("payType", 1, null);
        requestParams.addBodyParameter("channel", 2, null);
        requestParams.addBodyParameter("undiscountFee", undis, null);
        requestParams.setConnectTimeout(65 * 1000);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                try {
                    JSONObject resultObj = new JSONObject(result);
                    String success = resultObj.getString("success");
                    if (success.equals("true")) {
                        JSONObject dataObj = resultObj.getJSONObject("data");
                        String ordernumber = dataObj.getString("orderNumber");
                        Double discount = dataObj.optDouble("discountAmount");
                        Double orderamount = dataObj.optDouble("orderAmount");
                        Double realpayamount = dataObj.optDouble("realPayAmount");
                        Double paidinamount = dataObj.optDouble("paidInAmount");
//                        String discountAmount = dataObj.opt("discountAmount").toString();
//                        String orderAmount = dataObj.opt("orderAmount").toString();
                        String status = dataObj.getString("statusText");
//                        String realPayAmount = dataObj.optString("realPayAmount");
//                        String realPayAmount = dataObj.optString("realPayAmount");3
                        String orderType = dataObj.optString("orderType");
                        int paytype = dataObj.optInt("type");
                        String tradeNo;
                        if (paytype == 0) {
                            tradeNo = dataObj.optString("transactionId");
                        } else {
                            tradeNo = dataObj.optString("tradeNo");
                        }
                        String paytime = dataObj.optString("payTime");
                        if (paytime.equals("")) {
                        } else {
                            long timestimp = Long.parseLong(paytime);
                            Date date = new Date(timestimp);
                            paytime = simpleDateFormat.format(date);
                        }
                        Double typeyh = 0.00, sjyh = 0.00;
                        try {
                            sjyh = (orderamount - paidinamount);
                            if (sjyh < 0.01)
                                sjyh = 0.00;
                            typeyh = discount - (orderamount - paidinamount);
                            if (typeyh < 0.01)
                                typeyh = 0.00;
                            Log.e("kxflog", "支付类型优惠的金额---result:" + typeyh);
                        } catch (NumberFormatException e) {
                            Log.e("kxflog", " 计算优惠金额NumberFormatException");
                        }
                        String s1 = df3.format(typeyh);//----zfb/wx优惠
                        String s2 = df3.format(sjyh);///商家优惠
                        String s3 = df3.format(orderamount);
                        String s4 = df3.format(realpayamount);
                        txtstatus.setText(status);
                        if (s1.equals("0.00"))
                            typeLiner.setVisibility(View.GONE);
                        if (s2.equals("0.00"))
                            merchantLiner.setVisibility(View.GONE);
                        txtdisamount.setText(s2 + " 元");
                        txtorderamount.setText(s3 + " 元");
                        txtshanghu.setText(paidinamount + "元");
                        txtordernumber.setText(ordernumber);
                        txtpaytime.setText(paytime);
                        txtrealamount.setText(s4 + " 元");
                        txtpaytype.setText(orderType);
                        txtwuyong.setText(orderType + "优惠");
                        txtundisamount.setText(undis + " 元");
                        txttypeyh.setText(s1 + "元");
                        txtalino.setText(tradeNo);
                        printData(s3, ordernumber, "无", orderType, s4, s2, storeName, shouyy, status, paytime, s1, paidinamount + "");
                    } else {
                        success = resultObj.getString("err_msg");
                        Toast.makeText(ScanCodeAcivity.this, "收款失败 " + success, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("kxflog", "ex:" + ex);
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(ScanCodeAcivity.this, "获取信息错误", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (progressDialog != null)
                    progressDialog.dismiss();
            }

            @Override
            public void onFinished() {

            }
        });


    }
    private PrintTools printTools;

    private void printData(final String orderAm, final String orderNumberStr, final String note, final String payType, final String realAm, final String youhui, final String mendian, final String shouyy, final String success,
                           final String paytime, final String typeyh, final String shshihou) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        String isprint = sharedPreferences.getString("isprint", "");
        if (("").equals(isprint)) {
            return;
        } else if (("on").equals(isprint)) {
            if (bluetoothAdapter.isEnabled()) {
                if (!"".equals(blueadress)) {

                    new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            printTools     = new PrintTools(getApplicationContext(), blueadress);
                            if (printTools.connect()) {
                                Bitmap bitmap;
                                if (payType.equals("支付宝")) {
                                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.twotwo);
                                } else if (payType.equals("微信")) {
                                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timgwcat);
                                } else {
                                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.huifu);
                                }
                                printTools.txtcom(bitmap, orderAm, orderNumberStr, note, payType, realAm, youhui, mendian, shouyy, success, paytime, undis, typeyh, shshihou);
                            } else {
                                handler.sendEmptyMessage(55);
                            }
                        }
                    }.start();

                }
            }
        }

    }
}
