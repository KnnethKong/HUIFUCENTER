package gjcm.kxf.goodorder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zxing.lib.MyScanActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.NetTools;
import gjcm.kxf.tools.TempTools;


/**
 * Created by kxf on 2017/3/13.
 * 菜品结算
 */
public class OrderScaleAcivity extends AppCompatActivity implements View.OnClickListener {
    private TextView orderamTxt;
    private String orderId, deskid, allMonery, merchantId, storeId;
    private double orderam;
    private EditText actualTxt;
    private static DemoHandler handler;
    private SharedPreferences sharedPreferences;
    private int orderWay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.order_scale_layout);
        sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        merchantId = sharedPreferences.getString("merchantId", null);
        storeId = sharedPreferences.getString("storeId", null);
        orderId = getIntent().getStringExtra("orderid");
        orderam = getIntent().getDoubleExtra("orderam", 0.00);
        deskid = getIntent().getStringExtra("deskid");
        orderWay = getIntent().getIntExtra("orderway", 0);
        isDesk = getIntent().getIntExtra("isDesk", 0);
        handler = new DemoHandler();
        initbar();
        initView();
        getPayWay();
    }

    //获取门店支持的付款方式 http://dc.vikpay.com/HuiFuOrder/OrderOperate/QueryStorePayWay?storeid=4050
    String zfb, wx, xj, qt;//zfb.,wx,xj,qt;
    int isAliWX;

    private void getPayWay() {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在查询支持付款方式...", true, false);
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderOperate/QueryStorePayWay");
        requestParams.addBodyParameter("storeid", storeId);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int backcode = jsonObject.getInt("back_code");
                    if (backcode == 200) {
                        String payway = jsonObject.getString("payway");
                        zfb = payway.substring(0, 1);
                        wx = payway.substring(1, 2);
                        xj = payway.substring(2, 3);
                        qt = payway.substring(3, 4);
                    } else {
                        Toast.makeText(OrderScaleAcivity.this, "收款方式配置出错", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void initbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tb);
        TextView ttile = (TextView) findViewById(R.id.toolbar_txt);
        ttile.setText("买单结账");
        setSupportActionBar(toolbar);
        findViewById(R.id.toolbar_remind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrderScaleAcivity.this, RemindActivity.class));
            }
        });
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderScaleAcivity.this.finish();
            }
        });
    }

    private void initView() {
        findViewById(R.id.order_scale_0).setOnClickListener(this);
        findViewById(R.id.order_scale_1).setOnClickListener(this);
        findViewById(R.id.order_scale_2).setOnClickListener(this);
        findViewById(R.id.order_scale_3).setOnClickListener(this);
        findViewById(R.id.order_scale_4).setOnClickListener(this);
        findViewById(R.id.order_scale_5).setOnClickListener(this);
        findViewById(R.id.order_scale_6).setOnClickListener(this);
        findViewById(R.id.order_scale_7).setOnClickListener(this);
        findViewById(R.id.order_scale_8).setOnClickListener(this);
        findViewById(R.id.order_scale_9).setOnClickListener(this);
        findViewById(R.id.order_scale_c).setOnClickListener(this);
        findViewById(R.id.order_scale_dot).setOnClickListener(this);
        findViewById(R.id.order_scale_zfb).setOnClickListener(this);
        findViewById(R.id.order_scale_wx).setOnClickListener(this);
        findViewById(R.id.order_scale_m).setOnClickListener(this);
        orderamTxt = (TextView) findViewById(R.id.order_scale_orderam);
        actualTxt = (EditText) findViewById(R.id.order_scale_actualam);
        nowInutBuffer = df3.format(orderam);
        orderamTxt.setText(nowInutBuffer);
        actualTxt.setText(nowInutBuffer);
        actualTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                actualTxt.setSelection(actualTxt.length());
            }
        });
        actualTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int inType = actualTxt.getInputType(); // backup the input type
                actualTxt.setInputType(InputType.TYPE_NULL); // disable soft input
                actualTxt.onTouchEvent(motionEvent); // call native handler
                actualTxt.setInputType(inType); // restore input type
                return true;
            }
        });

    }


    @Override
    public void onClick(View view) {
        Double d;
        Intent intent;
        switch (view.getId()) {
            case R.id.order_scale_0:
                addNumberCode("0");
                break;
            case R.id.order_scale_1:
                addNumberCode("1");
                break;
            case R.id.order_scale_2:
                addNumberCode("2");
                break;
            case R.id.order_scale_3:
                addNumberCode("3");
                break;
            case R.id.order_scale_4:
                addNumberCode("4");
                break;
            case R.id.order_scale_5:
                addNumberCode("5");
                break;
            case R.id.order_scale_6:
                addNumberCode("6");
                break;
            case R.id.order_scale_7:
                addNumberCode("7");
                break;
            case R.id.order_scale_8:
                addNumberCode("8");
                break;
            case R.id.order_scale_9:
                addNumberCode("9");
                break;
            case R.id.order_scale_c:
                delNumber();
                break;
            case R.id.order_scale_dot:
                addNumberCode(".");
                break;
            case R.id.order_scale_zfb:
                if (zfb == null || !"1".equals(zfb)) {
                    Toast.makeText(OrderScaleAcivity.this, "不支持支付宝收款", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (zfb.equals("1")) {
                    isAliWX = 1;
                    allMonery = actualTxt.getText().toString().trim();
                    d = Double.parseDouble(allMonery);
                    if (d > 0) {
                        intent = new Intent(this, MyScanActivity.class);
                        intent.putExtra("iscard", "3");
                        startActivityForResult(intent, 0);
                    }
                }
                break;
            case R.id.order_scale_wx:
                if (wx == null || !"1".equals(wx)) {
                    Toast.makeText(OrderScaleAcivity.this, "不支持微信收款", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (wx.equals("1")) {
                    isAliWX = 2;
                    allMonery = actualTxt.getText().toString().trim();
                    d = Double.parseDouble(allMonery);
                    if (d > 0) {
                        intent = new Intent(this, MyScanActivity.class);
                        intent.putExtra("iscard", "3");
                        startActivityForResult(intent, 0);
                    }
                }
                break;
            case R.id.order_scale_m:
                isAliWX = 3;
                if (xj == null || !"1".equals(xj)) {
                    Toast.makeText(OrderScaleAcivity.this, "不支持现金收款", Toast.LENGTH_SHORT).show();
                    return;
                }
                allMonery = actualTxt.getText().toString().trim();
                d = Double.parseDouble(allMonery);
                if (d > 0.00) {
                    checkPayStatus(1, "");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (df3 != null)
            df3 = null;
        if (handler != null)
            handler = null;
        if (sharedPreferences != null)
            sharedPreferences = null;
        if (progressDialog != null)
            progressDialog = null;
    }

    private String nowInutBuffer = "";
    private DecimalFormat df3 = new DecimalFormat("0.00");

    private void addNumberCode(String paramString) {
        String tempstr = nowInutBuffer;
        tempstr += paramString;
        if (Pattern.compile("((^[1-9][0-9]{0,4})(\\.[0-9]{0,2})?$)|(^0(\\.((0[1-9]?)|([1-9][0-9]?))?)?$)").matcher(tempstr).matches()) {
            nowInutBuffer = tempstr;
            actualTxt.setText(nowInutBuffer);
        }
    }

    private void delNumber() {
        nowInutBuffer = "";
        actualTxt.setText(nowInutBuffer);

    }

    ProgressDialog progressDialog;

    private void payWay(String payNums) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("orderId", orderId);
            jsonObject.put("deskId", deskid);
            jsonObject.put("isDesk", isDesk);
            jsonObject.put("payWay", isAliWX);
            jsonObject.put("realAm", allMonery);
            jsonObject.put("payNums", payNums);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在结算...", true, false);
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderInfo/appPayChangeEnd");
        requestParams.setBodyContent(jsonObject.toString());
        requestParams.setAsJsonContent(true);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                try {
                    JSONObject json = new JSONObject(result);
                    int back_code = json.getInt("back_code");
                    if (back_code == 200) {
//                        Toast.makeText(OrderScaleAcivity.this, "收款成功", Toast.LENGTH_SHORT).show();
//                        finish();
//                        //if (isAliWX.equals("3"))
                        //prinXianJin();
                        if (orderWay == 3)
                            send2Order(orderId, "14");
                        else
                            OrderScaleAcivity.this.finish();

                    } else {
                        changeOrderFiled();
                        Toast.makeText(OrderScaleAcivity.this, "失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Log.e("kxflog", ex.getMessage());
                Toast.makeText(OrderScaleAcivity.this, "连接服务错误", Toast.LENGTH_SHORT).show();
                changeOrderFiled();

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private String userToken;

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        userToken = sharedPreferences.getString("usertoken", "");
        storeName = sharedPreferences.getString("storeName", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //    Log.i("kxflog", "onActivityResult----resultCode---" + resultCode);
            final Bundle bundle = data.getExtras();
            if (bundle != null) {
                final String result = bundle.getString("result");

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
        } else {

        }
    }

    public class DemoHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 22:
                    Bundle bundle = msg.getData();
                    String no = bundle.getString("scanno");
                    Log.i("kxflog", "onActivityResult----resultCode---" + no);
                    checkPayStatus(2, no);
                    break;
                case 78:
                    Toast.makeText(OrderScaleAcivity.this, "打印失败", Toast.LENGTH_SHORT).show();
                    break;
                case 80:
                    TempTools.foodsEntities = null;
                    OrderScaleAcivity.this.finish();
                    break;
            }
        }
    }


    //踢向ali card
    private void send2Order(final String orderid, final String status) {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在更新支付宝状态...", true, false);
        RequestParams requestParams = new RequestParams("http://weijing.f3322.net:9090/diancan/sendorder");
        requestParams.addBodyParameter("merchantId", merchantId);
        requestParams.addBodyParameter("storeId", storeId);
        requestParams.addBodyParameter("orderId", orderid);
        requestParams.addBodyParameter("status", status);

        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                progressDialog = null;
                Log.e("kxflog", "send2Order----------" + result);
                if (status.equals("14")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showASKAlet();
                        }
                    });
                }
                if (status.equals("15")) {
                    OrderScaleAcivity.this.finish();
                }


//                try {
//                    JSONObject object = new JSONObject(result);
//                    boolean success = object.getBoolean("success");
//                    if (success) {
//                        Toast.makeText(OrderScaleAcivity.this, "更新成功", Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        object.optString("err_code");
//                        String err = object.optString("err_msg");
//                        Toast.makeText(OrderScaleAcivity.this, "更新失败" + err, Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
              /*  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showASKAlet();
                    }
                });*/
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(OrderScaleAcivity.this, "更新支付宝异常", Toast.LENGTH_SHORT).show();
                OrderScaleAcivity.this.finish();

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    ///是否完成此订单
    private void showASKAlet() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(OrderScaleAcivity.this);
        View view = LayoutInflater.from(OrderScaleAcivity.this).inflate(R.layout.draworder_layout, null);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setView(view);
        builder.show();
        TextView tok = (TextView) view.findViewById(R.id.draworder_ok);
        TextView tcancel = (TextView) view.findViewById(R.id.draworder_cancel);
        tok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             send2Order(orderId,"15");
            }
        });
        tcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderScaleAcivity.this.finish();
            }
        });

    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int backcode = 0;

    private void checkPayStatus(final int i, final String no) {

        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "/OrderInfo/checkOrderPayStatusApp");
        requestParams.addBodyParameter("orderid", orderId);
        requestParams.addBodyParameter("storeid", storeId);

        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("kxflog", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    backcode = jsonObject.getInt("back_code");
                    if (backcode == 200) {
                        if (i == 1) {
                            paidiAm = nowInutBuffer;
                            payWay("0");
                        }
                        if (i == 2) {
                            uploadData(no);
                        }
                    } else {
                        Toast.makeText(OrderScaleAcivity.this, jsonObject.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    private void uploadData(String authcode) {
        final DecimalFormat df3 = new DecimalFormat("0.00");
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "请稍候", false, false);
        RequestParams requestParams = new RequestParams(NetTools.HOMEURL + "/pay/wx-pay");
        requestParams.addHeader("token", userToken);
        requestParams.addBodyParameter("body", storeName);
        requestParams.addBodyParameter("detail", "");
        requestParams.addBodyParameter("totalFee", allMonery);
        requestParams.addBodyParameter("feeType", "CNY");
        requestParams.addBodyParameter("goodsTag", "0");
        requestParams.addBodyParameter("authCode", authcode);
        requestParams.addBodyParameter("dianCanOrder", orderId);
        requestParams.addBodyParameter("deviceInfo", "android");
        requestParams.addBodyParameter("payType", 1, null);
        requestParams.addBodyParameter("channel", 2, null);
        requestParams.addBodyParameter("undiscountFee", "0.00", null);
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
                        orderNum = dataObj.getString("orderNumber");
                        Double discount = dataObj.optDouble("discountAmount");
                        Double orderamount = dataObj.optDouble("orderAmount");
                        Double realpayamount = dataObj.optDouble("realPayAmount");
                        Double paidinamount = dataObj.optDouble("paidInAmount");
                        status = dataObj.getString("statusText");
                        paidiAm = paidinamount + "";
                        status = dataObj.getString("statusText");
                        orderType = dataObj.optString("orderType");
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
                            payTime = simpleDateFormat.format(date);
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
                        typeYH = typeyh;//----zfb/wx优惠
                        merchatYH = sjyh;///商家优惠
                        orderAm = df3.format(orderamount);//
                        realAm = df3.format(realpayamount);
                        String tempNum = orderNum;
                        payWay(tempNum);
                    } else {
                        success = resultObj.getString("err_msg");
                        Toast.makeText(OrderScaleAcivity.this, "收款失败 " + success, Toast.LENGTH_SHORT).show();
                        changeOrderFiled();
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
                Toast.makeText(OrderScaleAcivity.this, "获取信息错误", Toast.LENGTH_SHORT).show();
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

    //String orderid, String status
    public void changeOrderFiled() {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "请稍候", false, false);
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "/OrderInfo/changePayNote");
        requestParams.addBodyParameter("orderid", orderId);
        requestParams.addBodyParameter("status", "3");
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                finish();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                finish();

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private String orderAm, orderNum, note, orderType, realAm, storeName, status, payTime, paidiAm;
    private double merchatYH, typeYH;
    private int isDesk = 0;

    /**
     * 更换订单信息
     *
     * @param realAm


    private void drwaOrderServer() {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在提交...", true, false);
        //String oid, String way, String pid, String did, String realAm
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderInfo/changeOrderPay");
        requestParams.addBodyParameter("oid", orderId);
        requestParams.addBodyParameter("deskid", deskid);
        requestParams.addBodyParameter("status", isDesk + "");
        requestParams.addBodyParameter("payAm", allMonery);
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (orderWay == 3) {
                    send2Order(orderId, "14");
                } else {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    finish();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(OrderScaleAcivity.this, "更新收款失败" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (progressDialog != null)
                    progressDialog.dismiss();
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
     */
  /*  private void prinXianJin() {
        new Thread() {
            @Override
            public void run() {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] initprint = new byte[]{27, 64};//初始化
                byte[] cutall = new byte[]{29, 86, 65, 0};//切纸
                String str1 = "订单金额: " + orderam + "\n";
                String str4 = "订单编号: " + orderId + "\n";
                String str2 = "实际收款: " + orderam + "\n";
                String str3 = "付款时间: " + simpleDateFormat.format(new Date()) + "\n";
                try {
                    buffer.write(initprint);
                    String title = "-----------菜品结单-----------\n";
                    buffer.write(title.getBytes("gbk"));
                    final List<Map<String, Object>> entitys = TempTools.foodsEntities;
                    int mapsize = entitys.size();
                    for (int i = 0; i < mapsize; i++) {
                        Map<String, Object> map = entitys.get(i);
                        String t1 = map.get("name").toString();
                        String t2 = map.get("num").toString();
                        String t3 = map.get("price").toString();
                        t1 = t1 + "   " + t2 + "x " + t3 + "\n";
                        buffer.write(t1.getBytes("gbk"));
                    }
                    buffer.write("\n".getBytes("gbk"));
                    buffer.write(str4.getBytes("gbk"));
                    buffer.write("收款方式: 现金".getBytes("gbk"));
                    buffer.write(str1.getBytes("gbk"));
                    buffer.write(str2.getBytes("gbk"));
                    buffer.write(str3.getBytes("gbk"));
                    buffer.write("\n".getBytes("gbk"));
                    buffer.write(cutall);
                } catch (IOException e) {
                    handler.sendEmptyMessage(78);
                }
                String ipaddress = "192.168.1.199";
                try {
                    SocketAddress ipe = new InetSocketAddress(Inet4Address.getByName(ipaddress), 9100);
                    Socket socket = new Socket();
                    socket.connect(ipe, 1800);
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(buffer.toByteArray());
                    outputStream.flush();
                    outputStream.close();
                    socket.close();
//                    handler.sendEmptyMessage(80);
                } catch (Exception e) {
                    handler.sendEmptyMessage(78);
                }
            }
        }.start();

    }*/
}
