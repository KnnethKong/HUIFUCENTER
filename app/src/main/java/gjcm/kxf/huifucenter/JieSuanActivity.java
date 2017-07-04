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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import gjcm.kxf.entity.MerchantDetail;
import gjcm.kxf.fragment.MainFragment;
import gjcm.kxf.tools.NetTools;
import gjcm.kxf.tools.PrintTools;

/**
 * Created by kxf on 2017/1/6.
 * 日结
 */
public class JieSuanActivity extends AppCompatActivity implements View.OnClickListener {
    private Button print;
    private String totalAmount, totalOrderCount, refundAmount, realPayAmount, discountAmount, merchantTotalAmount, cardTotalAmount, refundCount, serviceAmount, beginTime, endTime;
    private String blueAdress, isprint, usertoken;
    private TextView txtMerchantAmount, txtTotalAmount, txtTotalOrderCount, txtRefundAmount, txtDiscountAmount, txtCardTotalAmount, txtRefundCount;
    private TextView txtRealPayAmount, txtServiceAmount, txtBeginTime, txtEndTime;
    private TextView zfbAm, zfbReAm, zfbCount, wchatAm, wchatReAm, wchatCount, zfbshss, wxshss;
    private ProgressDialog progressDialog;
    private String mendian, caozuoyuan;
    private static MyHandler myHandler;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.scale_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.scale_toolbar);
        toolbar.setTitle("结算");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        print = (Button) findViewById(R.id.scale_print);
        print.setOnClickListener(this);
        sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        usertoken = sharedPreferences.getString("usertoken", "");
        blueAdress = sharedPreferences.getString("blueadress", "");
        isprint = sharedPreferences.getString("isprint", "");
        mendian = sharedPreferences.getString("storeName", "");
        caozuoyuan = sharedPreferences.getString("shouyy", "");
        initView();
        myHandler = new MyHandler(this);
    }

    private String neturl;

    private void initView() {
        txtMerchantAmount = (TextView) findViewById(R.id.scale_txtshangjiashishou);
        txtTotalAmount = (TextView) findViewById(R.id.scale_txtorderam);
        txtTotalOrderCount = (TextView) findViewById(R.id.scale_txtordernum);
        txtRefundAmount = (TextView) findViewById(R.id.scale_txtream);
        txtDiscountAmount = (TextView) findViewById(R.id.scale_txtdiscount);
        txtCardTotalAmount = (TextView) findViewById(R.id.scale_txtkaxiaofei);
        txtRefundCount = (TextView) findViewById(R.id.scale_txtrenum);
        txtRealPayAmount = (TextView) findViewById(R.id.scale_txtshishoujine);
        txtServiceAmount = (TextView) findViewById(R.id.recon_txtfwf);
        txtBeginTime = (TextView) findViewById(R.id.scale_txtbegintime);
        txtEndTime = (TextView) findViewById(R.id.scale_txtendtime);
        zfbAm = (TextView) findViewById(R.id.scale_txtrezfb);
        zfbCount = (TextView) findViewById(R.id.scale_txtrezfbcount);
        zfbReAm = (TextView) findViewById(R.id.scale_txtrezfbre);
        wchatAm = (TextView) findViewById(R.id.scale_txtrewx);
        wchatCount = (TextView) findViewById(R.id.scale_txtrewxcount);
        wchatReAm = (TextView) findViewById(R.id.scale_txtrewxre);
        zfbshss = (TextView) findViewById(R.id.scale_txtrezfbshss);
        wxshss = (TextView) findViewById(R.id.scale_txtrewxshss);
        neturl = "/manager-order/predailysettle";
        initData(neturl);
    }


    private void initData(String url) {
        if (!NetTools.isNetworkConnected(this)) {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT);
            return;
        }
        progressDialog = ProgressDialog.show(JieSuanActivity.this, "", "正在查询", true, false);
        RequestParams requestParams = new RequestParams(NetTools.HOMEURL + url);
        requestParams.setConnectTimeout(10 * 1000);
        String payStartTime = "2016-12-12", payEndTime = "2016-12-12";
        int storeid = 0, storeuseid = 0;
        String params = "{" +
                "\"dto\":{" + "\"payStartTime\":" + "\"" + payStartTime + "\"" + ",\"payEndTime\":" + "\"" + payEndTime + "\"" + ",\"realname\":" + null +
                ",\"status\":" + -1 + ",\"storeId\":" + storeid + ",\"storeUserId\":" + storeuseid + "}," +
                "\"page\":{" + "\"pageNO\":" + 1000 + ",\"everyPageCount\":" + 1 + "}" +
                "}";
        requestParams.setBodyContent(params);
        requestParams.setAsJsonContent(true);
        requestParams.addHeader("token", usertoken);
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        progressDialog.dismiss();
                        ddddd(result);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ex.printStackTrace();
//                        Log.i("kxflog", "onError--------" + ex.getMessage());
                        dismisProgres("获取失败");
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        dismisProgres("已被取消");

                    }

                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }
                }

        );
    }

    private String wxPaySum, wxRefundSum, wxPayCount, AliPaySum, AliPayRefundSum, aliPayCount,aliPayPaidAmount,wxPayPaidAmount;

    private void ddddd(String result) {
        print.setEnabled(true);
        try {
            JSONObject jsonObject = new JSONObject(result);
            String issuc = jsonObject.get("success").toString();
            if (issuc.equals("false")) {
                String err = jsonObject.opt("err_msg").toString();
                dismisProgres(err);
                return;
            }//订单-退款=商户实收
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject merchantCountOrderCommon = data.getJSONObject("merchantCountOrderCommon");
            totalAmount = merchantCountOrderCommon.optString("totalAmount");
            totalOrderCount = merchantCountOrderCommon.optString("totalOrderCount");
            refundAmount = merchantCountOrderCommon.optString("refundAmount");
            realPayAmount = merchantCountOrderCommon.optString("realPayAmount");
            discountAmount = merchantCountOrderCommon.optString("discountAmount");
            merchantTotalAmount = merchantCountOrderCommon.optString("merchantTotalAmount");
            cardTotalAmount = merchantCountOrderCommon.optString("cardTotalAmount");
            refundCount = merchantCountOrderCommon.optString("refundCount");
            serviceAmount = merchantCountOrderCommon.optString("serviceAmount");
            beginTime = merchantCountOrderCommon.optString("beginTime");
            endTime = merchantCountOrderCommon.optString("endTime");
            wxPaySum = merchantCountOrderCommon.optString("wxPaySum");
            wxRefundSum = merchantCountOrderCommon.optString("wxRefundSum");
            wxPayCount = merchantCountOrderCommon.optString("wxPayCount");
            AliPaySum = merchantCountOrderCommon.optString("aliPaySum");
            AliPayRefundSum = merchantCountOrderCommon.optString("aliPayRefundSum");
            aliPayCount = merchantCountOrderCommon.optString("aliPayCount");
            wxPayPaidAmount = merchantCountOrderCommon.optString("wxPayPaidAmount");
            aliPayPaidAmount = merchantCountOrderCommon.optString("aliPayPaidAmount");
            if ("".equals(totalAmount))
                totalAmount = "0.00";
            if ("".equals(discountAmount))
                discountAmount = "0.00";
            if ("".equals(realPayAmount))
                realPayAmount = "0.00";
            if ("".equals(merchantTotalAmount))
                merchantTotalAmount = "0.00";
            wxPaySum = "".equals(wxPaySum) ? "0.00" : wxPaySum;
            wxRefundSum = "".equals(wxRefundSum) ? "0.00" : wxRefundSum;
            wxPayCount = "".equals(wxPayCount) ? "0" : wxPayCount;
            AliPaySum = "".equals(AliPaySum) ? "0.00" : AliPaySum;
            AliPayRefundSum = "".equals(AliPayRefundSum) ? "0.00" : AliPayRefundSum;
            aliPayCount = "".equals(aliPayCount) ? "0" : aliPayCount;
            if (isPrint > 0) {
                myHandler.sendEmptyMessage(36);
                JSONArray details = data.getJSONArray("detail");
                printData(1);
                if (details.length() <= 0) {
                    isnull = 1;
                    myHandler.sendEmptyMessage(37);
                } else {
                    merchantDetails = new ArrayList<MerchantDetail>();
                    MerchantDetail merchantDetail = null;
                    for (int k = 0; k < details.length(); k++) {
                        JSONObject de = (JSONObject) details.get(k);
                        String orderNumber = de.optString("orderNumber");
                        String payTime = de.optString("payTime");
                        String type = de.optInt("type") == 1 ? "支付宝" : "微信";
                        int payst = de.optInt("status");
                        Double dss = de.optDouble("sumRefundAmount");
                        String sumRefundAmount = "0";
                        String status = "";
                        if (payst == 1) {
                            status = "支付成功";
                            sumRefundAmount = "0";
                        } else if (payst == 3) {
                            status = "已退款";
                            sumRefundAmount = "0";
                        } else if (payst == 5) {
                            status = "部分退款";
                            sumRefundAmount = dss + "";
                        } else {
                            status = "支付失败";
                            sumRefundAmount = "0";

                        }
                        String orderAmount = de.optDouble("orderAmount") + "";
                        merchantDetail
                                = new MerchantDetail(orderNumber, orderAmount, status, payTime, type, sumRefundAmount);
                        merchantDetails.add(merchantDetail);
                    }
                    myHandler.sendEmptyMessage(33);
                }
            } else {
                showTextview();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int isnull = 0;

    private void showTextview() {
        txtMerchantAmount.setText(merchantTotalAmount + "元");
        txtBeginTime.setText(beginTime);
        txtCardTotalAmount.setText(cardTotalAmount + "元");
        txtDiscountAmount.setText(discountAmount + "元");
        txtRealPayAmount.setText(realPayAmount + "元");
        txtRefundCount.setText(refundCount);
        txtServiceAmount.setText(serviceAmount + "元");
        txtTotalOrderCount.setText(totalOrderCount);
        txtTotalAmount.setText(totalAmount + "元");
        txtRefundAmount.setText(refundAmount + "元");
        txtEndTime.setText(endTime);
        zfbCount.setText(aliPayCount);
        zfbReAm.setText(AliPayRefundSum + " 元");
        zfbAm.setText(AliPaySum + " 元");
        wchatAm.setText(wxPaySum + " 元");
        wchatCount.setText(wxPayCount);
        wchatReAm.setText(wxRefundSum + " 元");
        wxshss.setText(wxPayPaidAmount+" 元");
        zfbshss.setText(aliPayPaidAmount+" 元");
    }

    private void dismisProgres(String str) {
        print.setEnabled(false);
        if (progressDialog != null)
            progressDialog.dismiss();
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    private int isPrint = 0;

    @Override
    public void onClick(View view) {
        String url;
        switch (view.getId()) {
            case R.id.scale_print:
                isPrint = 2;
                url = "/manager-order/merchantdailysettle";
                initData(url);
                break;
        }
    }

    private void showLoginOut() {
        final AlertDialog logoutAlert = new AlertDialog.Builder(this).create();
        View view = View.inflate(this, R.layout.login_out, null);
        Button okBtn = (Button) view.findViewById(R.id.alert_okbtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutAlert.dismiss();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("usertoken", "");
                editor.commit();
                Intent intent = new Intent(JieSuanActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("isout", "yes");
                startActivity(intent);
                finish();
            }
        });
        Button cancelBtn = (Button) view.findViewById(R.id.alert_cancelbtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutAlert.dismiss();
            }
        });
        logoutAlert.setView(view);
        logoutAlert.show();
    }

    private List<MerchantDetail> merchantDetails;

    private void printData(final int i) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (("").equals(isprint)) {
            return;
        } else if (("on").equals(isprint)) {
            if (bluetoothAdapter.isEnabled()) {
                if (!"".equals(blueAdress)) {
                    final PrintTools printTools = new PrintTools(JieSuanActivity.this, blueAdress);
                    if (printTools.connect()) {
                        new Thread() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                if (i == 1) {
                                    printTools.printScale(mendian, caozuoyuan, totalAmount, totalOrderCount, refundAmount, realPayAmount, discountAmount, merchantTotalAmount, cardTotalAmount, refundCount, serviceAmount, beginTime, endTime,
                                            wxPaySum, wxRefundSum, wxPayCount, AliPaySum, AliPayRefundSum, aliPayCount,wxPayPaidAmount,aliPayPaidAmount);
                                } else {
                                    printTools.printList(merchantDetails);
                                }
                            }
                        }.start();
                    } else {
                        Toast.makeText(JieSuanActivity.this, "打印机连接失败，请检查打印机", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    private static class MyHandler extends Handler {
        WeakReference<JieSuanActivity> weakReference;

        public MyHandler(JieSuanActivity activity) {
            weakReference = new WeakReference<JieSuanActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 33:
                    weakReference.get().showPrintDatail();
                    break;
                case 34:
                    Toast.makeText(weakReference.get(), "无", Toast.LENGTH_SHORT).show();
//                    showPrintDatail();
                    break;
                case 36:
                    Toast.makeText(weakReference.get(), "已日结", Toast.LENGTH_SHORT).show();
                    break;
                case 37:
                    weakReference.get().showLoginOut();
//                    showLoginOut();
                    break;
            }
//            if (weakReference.get() != null) {
//            Log.d("mmmmmmmm", "handler " + msg.what);
//            }
        }

    }

    private void showPrintDatail() {
        final AlertDialog isprintAlert = new AlertDialog.Builder(this).create();
        View view = View.inflate(this, R.layout.isprintdetail_alert, null);
        Button okBtn = (Button) view.findViewById(R.id.alert_okbtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printData(2);
                isprintAlert.dismiss();
                myHandler.sendEmptyMessage(37);

            }
        });
        Button cancelBtn = (Button) view.findViewById(R.id.alert_cancelbtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isprintAlert.dismiss();
                myHandler.sendEmptyMessage(37);

            }
        });
        isprintAlert.setView(view);
        isprintAlert.show();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
        myHandler = null;
        progressDialog = null;
        sharedPreferences = null;
    }
}
