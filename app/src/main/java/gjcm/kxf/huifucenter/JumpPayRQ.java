package gjcm.kxf.huifucenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

import gjcm.kxf.tools.NetTools;
import gjcm.kxf.tools.PrintTools;

/**
 * 扫码支付
 * Created by kxf on 2016/12/14.
 */
public class JumpPayRQ extends AppCompatActivity implements View.OnClickListener {
    //    private TextView txtNo, txtStatus;
    private DemoHandler handler;
    private SharedPreferences sharedPreferences;
    private String userToken, blueadress, shouyy, undis;
    private ProgressDialog dialog;
    private float fmonery = 0.0f;
    private TextView txtOrderNumber, txtOrderAmount, txtDiscountAmount, txtRealPayAmount, txtPayTime, txtStatus, txtcreate;
    private TextView txtStore, txtsyy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showdetail_layout);
        getSupportActionBar().hide();
        Log.i("kxflog", "onCreate:--------");
        String monery = getIntent().getStringExtra("monery");
        undis = getIntent().getStringExtra("discount");
        fmonery = Float.valueOf(monery);
        txtOrderNumber = (TextView) findViewById(R.id.detail_ordernumber);
        txtOrderAmount = (TextView) findViewById(R.id.detail_orderamount);
        txtDiscountAmount = (TextView) findViewById(R.id.detail_discountamount);
        txtRealPayAmount = (TextView) findViewById(R.id.detail_realpayamount);
        txtPayTime = (TextView) findViewById(R.id.detail_ordertime);
        txtStatus = (TextView) findViewById(R.id.detail_orderstatus);
        txtcreate = (TextView) findViewById(R.id.detail_createtieme);
        txtStore = (TextView) findViewById(R.id.skd_storname);
        txtsyy = (TextView) findViewById(R.id.detail_syy);
//        startActivityForResult(new Intent(this, CaptureActivity.class), 0);
        Button btnRefound = (Button) findViewById(R.id.detail_tuikuan);
        btnRefound.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("kxflog", "onStart:--------");
        sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        userToken = sharedPreferences.getString("usertoken", "");
        shouyy = sharedPreferences.getString("shouyy", "");
        blueadress = sharedPreferences.getString("blueadress", "");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_tuikuan:
                break;
        }
    }

    private class DemoHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 22:
                    Bundle bundle = msg.getData();
                    String no = bundle.getString("scanno");
                    getLoginType(no);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("kxflog", "onResume:-------");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("kxflog", "onActivityResult:--------" + resultCode);
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
                        Log.i("kxf", "result: " + result);
                        Message message = new Message();
                        message.what = 22;
                        message.setData(bundle1);
                        handler.sendMessage(message);
                    }
                }.start();
            }
        }
    }

    private long timestimp;
    private Date date;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void getLoginType(String authcode) {
        dialog = ProgressDialog.show(this, "", "请稍候", false, false);
        RequestParams requestParams = new RequestParams(NetTools.HOMEURL + "/pay/wx-pay");
//        requestParams.addBodyParameter("token", userToken);
        requestParams.addHeader("token", userToken);
        requestParams.addBodyParameter("body", "body测试");
        requestParams.addBodyParameter("detail", "测试一笔");
        requestParams.addBodyParameter("totalFee", fmonery, null);
        requestParams.addBodyParameter("feeType", "CNY");
        requestParams.addBodyParameter("goodsTag", "0");
        requestParams.addBodyParameter("authCode", authcode);
        requestParams.addBodyParameter("deviceInfo", "kxf");
        requestParams.addBodyParameter("payType", 1, null);
        requestParams.addBodyParameter("channel", 2, null);
        requestParams.setConnectTimeout(10 * 1000);
        requestParams.addBodyParameter("undiscountFee", undis, null);
        Log.i("kxflog", requestParams.toString());
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (dialog != null)
                    dialog.dismiss();
                //{"success":true,"data":{"id":187023,"orderNumber":"201612150848001667433300","storeId":4050,"storeUserId":4426,"merchantId":5070,"orderAmount":0.01,"discountAmount":0.00,"realPayAmount":0.01,"paidInAmount":0.01,"status":1,"orderBody":"测试","type":1,"createTime":1481762880600,"payTime":1481762881557,"channel":2,"payWay":0,"merchantUserId":9390,"discountableAmount":0,"attach":"","orderDetail":"晚上测试一笔","refundFlag":0,"orderType":"支付宝","statusText":"支付成功","storeName":"乙密台韩国芝士排骨","payNumber":0,"refundNumber":0,"payAmount":0,"realname":"shayan2","merchantName":"康丰超市","tradeNo":"2016121521001004590202417686"}}
                Log.e("kxflog", "result:" + result);
                try {
                    JSONObject resultObj = new JSONObject(result);
                    String success = resultObj.getString("success");
                    if (success.equals("true")) {
                        JSONObject dataObj = resultObj.getJSONObject("data");
                        String ordernumber = dataObj.getString("orderNumber");
                        String discountAmount = dataObj.getString("discountAmount");
                        String orderAmount = dataObj.getString("orderAmount");
//                        String orderBody = dataObj.getString("orderBody");
                        String realPayAmount = dataObj.getString("realPayAmount");
                        String tradeNo = dataObj.getString("tradeNo");
                        String orderType = dataObj.getString("orderType");
                        String storeName = dataObj.getString("storeName");
                        String realname = dataObj.getString("realname");
                        String createTime = dataObj.getString("createTime");
                        if (createTime.equals("")) {
                        } else {
                            timestimp = Long.parseLong(createTime);
                            date = new Date(timestimp);
                            createTime = simpleDateFormat.format(date);
                        }
                        txtsyy.setText(realname);
                        txtStore.setText(storeName);
                        txtcreate.setText(createTime);
                        txtOrderNumber.setText(ordernumber);
                        txtDiscountAmount.setText(discountAmount);
                        txtOrderAmount.setText(orderAmount);
//                        txtOrderBody.setText(orderBody);
//                        txtTradeNo.setText(realPayAmount);
                        txtPayTime.setText(tradeNo);
                        txtRealPayAmount.setText(dataObj.getString("realPayAmount"));
                        String payTime = dataObj.optString("payTime");
                        if (payTime.equals("")) {
                        } else {
                            timestimp = Long.parseLong(payTime);
                            date = new Date(timestimp);
                            payTime = simpleDateFormat.format(date);
                        }
                        String status = dataObj.getString("statusText");
                        txtPayTime.setText(payTime);
                        txtStatus.setText(status);
                        printData(orderAmount, ordernumber, "无", orderType, realPayAmount, discountAmount, storeName, shouyy, status, payTime);
//                        String storeUserId = dataObj.getString("storeId");///门店编号
////                        senDeviceInfo(storeUserId, tokenStr);
//                        String type = dataObj.getString("type");
////                        String merchantName = dataObj.getString("merchantName");
//                        String storeName = dataObj.getString("username");
//                        String dianpuming = dataObj.getString("storeName");
//                        String name = dataObj.getString("name");
//                        String username = dataObj.getString("username");
//                        editorData(type, merchantName, storeName, name, username, storeUserId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("kxflog", "ex:" + ex);
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

    private void printData(final String orderAm, final String orderNumberStr, final String note, final String payType, final String realAm, final String youhui, final String mendian, final String shouyy, final String success, final String paytime) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        String isprint = sharedPreferences.getString("isprint", "");
        if (("").equals(isprint)) {
            return;
        } else if (("on").equals(isprint)) {
        } else
            return;
        if (bluetoothAdapter.isEnabled()) {
            if (!"".equals(blueadress)) {
                final PrintTools printTools = new PrintTools(this, blueadress);
                if (printTools.connect()) {
                    new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();
//                            printTools.printDeatail(orderAm, orderNumberStr, note, payType, realAm, youhui, mendian, shouyy, success, paytime,"","");
                        }
                    }.start();
                } else {
                    Toast.makeText(this, "打印机连接失败，请检查打印机", Toast.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(this, "请到我的-打印机页面设置", Toast.LENGTH_SHORT).show();

        }
    }


}
