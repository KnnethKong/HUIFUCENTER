package gjcm.kxf.huifucenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.task.Priority;
import org.xutils.http.RequestParams;
import org.xutils.http.app.ResponseParser;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import gjcm.kxf.tools.DetailAsyncTask;
import gjcm.kxf.tools.NetTools;
import gjcm.kxf.tools.OtherTools;
import gjcm.kxf.tools.PrintTools;

/**
 * Created by kxf on 2016/12/27.
 */
public class OrderDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private String usertoken, shouyy, blueadress, storeName, realmoney, undiscont;
    private String refoundAmount, bftuiam;
    private TextView txtOrderNumber, txtOrderAmount, txtDiscountAmount, txtRealPayAmount, txtPayTime, txtStatus, txtcreate, txtTuiam;
    private TextView txtStore, txtsyy, txtwuyong, tuikuan, txtshanghushishou;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    private Button printBnt;

    private RelativeLayout typeLiner, merchantLiner, tuikuanamliner;
    private TextView typekey, typevalu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.showdetail_layout);//
        Toolbar toolbar = (Toolbar) findViewById(R.id.sd_liner);
        toolbar.setTitle("订单详情");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtOrderNumber = (TextView) findViewById(R.id.detail_ordernumber);
        txtOrderAmount = (TextView) findViewById(R.id.detail_orderamount);
        txtDiscountAmount = (TextView) findViewById(R.id.detail_discountamount);
        txtRealPayAmount = (TextView) findViewById(R.id.detail_realpayamount);
        txtPayTime = (TextView) findViewById(R.id.detail_ordertime);
        txtStatus = (TextView) findViewById(R.id.detail_orderstatus);
        txtcreate = (TextView) findViewById(R.id.detail_createtieme);
        txtStore = (TextView) findViewById(R.id.skd_storname);
        txtsyy = (TextView) findViewById(R.id.detail_syy);
        txtwuyong = (TextView) findViewById(R.id.detail_amount);
        tuikuan = (TextView) findViewById(R.id.order_detail_tuikuan);
        printBnt = (Button) findViewById(R.id.detail_print);
        typeLiner = (RelativeLayout) findViewById(R.id.detail_yhtypehliner);
        merchantLiner = (RelativeLayout) findViewById(R.id.detail_sjyhhliner);
        tuikuanamliner = (RelativeLayout) findViewById(R.id.order_detail_tuikuanam);
        typekey = (TextView) findViewById(R.id.sd_yhtype);
        typevalu = (TextView) findViewById(R.id.detail_typeyham);
        printBnt.setOnClickListener(this);
        tuikuan.setOnClickListener(this);
        txtTuiam = (TextView) findViewById(R.id.skd_tuikuanjine);
        txtshanghushishou = (TextView) findViewById(R.id.detail_shanghushishou);
//        imgBack.setVisibility(View.VISIBLE);
//        imgBack.setOnClickListener(this);
        tradeno = getIntent().getStringExtra("tradeno");
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在查询...", true, false);
        sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        usertoken = sharedPreferences.getString("usertoken", "");
        shouyy = sharedPreferences.getString("shouyy", "");
        blueadress = sharedPreferences.getString("blueadress", "");
        storeName = sharedPreferences.getString("storeName", "");
        usertype = sharedPreferences.getString("usertype", "");
        txtStore.setText(storeName);

        getDetail();

    }

    private String usertype, tradeno;

    private Handler ordrhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 5:
                    tuikuan.setVisibility(View.INVISIBLE);
                    String meesage = msg.getData().getString("msg");
                    Toast.makeText(OrderDetailActivity.this, meesage, Toast.LENGTH_SHORT).show();
                    getDetail();
                    if (meesage.equals("退款成功"))
                        printData(1);
                    break;
                case 6:
                    int tuikuan = 2;
                    if (paystatus.equals("已退款")) {
                        tuikuan = 1;
                    }
                    printData(tuikuan);
                    break;
            }
        }
    };

    private DetailAsyncTask asyncTask;

    private void getDetail() {
        String strurl = "";
        if (usertype.equals("0")) {//商户
            strurl = strurl + "/order/app/info";
        } else if (usertype.equals("1")) {//店长
            strurl = strurl + "/order/app/store-info";
        } else {//店员
            strurl = strurl + "/order/app/clerk-info";
        }
        if (asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
        queryOrder(strurl, tradeno);
//        asyncTask = new DetailAsyncTask();
//        asyncTask.execute(strurl, usertoken, tradeno);
//        asyncTask.setTaskHandler(this);

    }

    private String typeyouhui, shanghushishou;

    private void queryOrder(String url, final String orderNumber2) {
        final DecimalFormat df3 = new DecimalFormat("0.00");

        url = NetTools.HOMEURL + url;
        final RequestParams params = new RequestParams(url);
        params.addHeader("token", usertoken);
        params.setConnectTimeout(10 * 1000);
        params.setAsJsonContent(true);
        String bodycon = "{" +
                "\"orderNumber\":" + "\"" + orderNumber2 + "\"" +
                "}";
        params.setBodyContent(bodycon);
        params.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        Log.e("kxflog", " params:" + orderNumber2 + params.getBodyContent()+"token:"+usertoken+ ""+ url);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
              /*  StringBuilder sb = new StringBuilder();
                AssetManager am = getApplicationContext().getAssets();
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            am.open("demos.json")));
                    String next = "";
                    while (null != (next = br.readLine())) {
                        sb.append(next);
                    }
                    am.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    sb.delete(0, sb.length());
                }*/

                Log.e("kxflog", "onSuccess params:" + result);
                if (progressDialog != null)
                    progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String success = jsonObject.getString("success");
                    if (!success.equals("true")) {
                        success = jsonObject.getString("err_msg");
                        Toast.makeText(OrderDetailActivity.this, success, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //{"success":true,"data":{"orderInfoDto":{"id":226384,"note":"","orderNumber":"201612281431303991132980","storeName":"乙密台韩国芝士排骨","realname":"shayan2","orderAmount":0.01,"status":1,"type":1,"payTime":"2016-12-28 14:31:31","createTime":"2016-12-28 14:31:30","channel":2,"sumRefundAmount":0.00,"discountAmount":0.00,"realPayAmount":0.01,"paidInAmount":0.01,"refundTime":"0000-00-00 00:00:00","orderRefundInfoDto":[null]}}}
                    JSONObject tjson = jsonObject.getJSONObject("data");
                    JSONObject data = tjson.getJSONObject("orderInfoDto");
                    orderid = data.optString("id");
                    orderAmount = data.opt("orderAmount").toString();
                    orderNumber = data.getString("orderNumber");
                    Double paidam = data.optDouble("paidInAmount");
                    Double disam = data.optDouble("discountAmount");
                    Double orderam = data.optDouble("orderAmount");
                    Double tuiam = data.optDouble("sumRefundAmount");
                    Double shangjiayh = orderam - paidam;
                    Double typeyh = disam - shangjiayh;
                    shanghushishou = paidam + "";
                    if (shangjiayh > 0.00)
                        merchantLiner.setVisibility(View.VISIBLE);
                    if (typeyh > 0.00)
                        typeLiner.setVisibility(View.VISIBLE);
                    typeyouhui = typeyh + "";
                    discountAmount = df3.format(shangjiayh);
//                    discountAmount = data.opt("discountAmount").toString();
                    realPayAmount = data.opt("realPayAmount").toString();
                    Double sumRefundAmount = data.optDouble("sumRefundAmount");
                    Double dref = Double.parseDouble(orderAmount) - sumRefundAmount;
                    DecimalFormat df3 = new DecimalFormat("0.00");
                    refoundAmount = df3.format(dref);
                    Log.i("kxflog", "sumRefundAmount:" + sumRefundAmount);
//if (sumRefundAmount)
                    Object obj = data.opt("undiscountFee");
                    if (null != obj) {
                        undiscont = obj.toString();
                    } else {
                        undiscont = "";
                    }
                    String status = data.opt("status").toString();
                    paynote = data.optString("note");
                    if (status.equals("1")) {
                        tuikuanamliner.setVisibility(View.GONE);
                        paystatus = "支付成功";
                        bftuiam = "0";
                    } else if (status.equals("3")) {
                        tuikuanamliner.setVisibility(View.GONE);
                        ztk = data.optString("sumRefundAmount").toString();
                        paystatus = "已退款";
                        bftuiam = "0";
                        tuikuan.setVisibility(View.INVISIBLE);
                    } else if (status.equals("5")) {
                        paystatus = "部分退款";
                        tuikuanamliner.setVisibility(View.VISIBLE);
                        txtTuiam.setText(tuiam + "元");
                        bftuiam = tuiam + "";
                    } else if (status.equals("0")) {
                        tuikuanamliner.setVisibility(View.GONE);
                        bftuiam = "0";
                        paystatus = "未支付";
                        tuikuan.setVisibility(View.INVISIBLE);
                        printBnt.setEnabled(false);
                        merchantLiner.setVisibility(View.GONE);
                        typeLiner.setVisibility(View.GONE);
                    } else {
                        bftuiam = "0";
                        paystatus = "支付失败";
                        tuikuan.setVisibility(View.INVISIBLE);
                        tuikuanamliner.setVisibility(View.GONE);
                        printBnt.setEnabled(false);
                        merchantLiner.setVisibility(View.GONE);
                        typeLiner.setVisibility(View.GONE);
                    }
                    paytime = data.getString("payTime");
                    orderType = data.opt("type").toString();
                    if (orderType.equals("1"))
                        orderType = "支付宝";
                    else
                        orderType = "微信";
                    String createTime = data.getString("createTime");
                    typekey.setText(orderType + "优惠:");
                    typevalu.setText(typeyouhui + "元");
                    realmoney = realPayAmount;
                    txtcreate.setText(createTime);
                    txtshanghushishou.setText(shanghushishou + "元");
                    txtsyy.setText(orderType);
                    txtwuyong.setText(orderAmount);
                    txtDiscountAmount.setText(discountAmount + "元");
                    txtOrderAmount.setText(orderAmount + "元");
                    txtOrderNumber.setText(orderNumber);
                    txtPayTime.setText(paytime);
                    txtRealPayAmount.setText(realPayAmount + "元");
                    txtStatus.setText(paystatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                progressDialog.dismiss();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("kxflog", "onError:" + ex.getMessage());
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                progressDialog.dismiss();

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (asyncTask != null)
            asyncTask.cancel(true);
        asyncTask = null;
        ordrhandler = null;
        progressDialog = null;
        sharedPreferences = null;
    }

    private String orderAmount, orderNumber, discountAmount, realPayAmount, paytime, orderType, paystatus, paynote;
/*

    @Override
    public void taskSuccessful(String json) {
        Log.i("kxflog", json);
        if (progressDialog != null)
            progressDialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String success = jsonObject.getString("success");
            if (!success.equals("true")) {
                success = jsonObject.getString("err_msg");
                Toast.makeText(OrderDetailActivity.this, success, Toast.LENGTH_SHORT).show();
                return;
            }
            //{"success":true,"data":{"orderInfoDto":{"id":226384,"note":"","orderNumber":"201612281431303991132980","storeName":"乙密台韩国芝士排骨","realname":"shayan2","orderAmount":0.01,"status":1,"type":1,"payTime":"2016-12-28 14:31:31","createTime":"2016-12-28 14:31:30","channel":2,"sumRefundAmount":0.00,"discountAmount":0.00,"realPayAmount":0.01,"paidInAmount":0.01,"refundTime":"0000-00-00 00:00:00","orderRefundInfoDto":[null]}}}
            JSONObject tjson = jsonObject.getJSONObject("data");
            JSONObject data = tjson.getJSONObject("orderInfoDto");
            orderid = data.optString("id");
            orderAmount = data.opt("orderAmount").toString();
            orderNumber = data.getString("orderNumber");
            discountAmount = data.opt("discountAmount").toString();
            realPayAmount = data.opt("realPayAmount").toString();

            Object obj = data.opt("undiscountFee");
            if (null != obj) {
                undiscont = obj.toString();
            } else {
                undiscont = "";
            }
            String status = data.opt("status").toString();
            paynote = data.optString("note");
            if (status.equals("1")) {
                paystatus = "支付成功";
            } else if (status.equals("3")) {
                ztk = data.optString("sumRefundAmount").toString();
                paystatus = "已退款";
                tuikuan.setVisibility(View.INVISIBLE);
            } else if (status.equals("5")) {
                paystatus = "部分退款";
//                tuikuan.setVisibility(View.INVISIBLE);
//                printBnt.setEnabled(false);
            } else {
                paystatus = "支付失败";
                tuikuan.setVisibility(View.INVISIBLE);
                printBnt.setEnabled(false);
            }
            paytime = data.getString("payTime");
            orderType = data.opt("type").toString();
            if (orderType.equals("1"))
                orderType = "支付宝";
            else
                orderType = "微信";
//            statusText = data.getString("statusText");
            String createTime = data.getString("createTime");

            realmoney = realPayAmount;
            txtcreate.setText(createTime);
            txtsyy.setText(orderType);
            txtwuyong.setText(orderAmount);
            txtDiscountAmount.setText(discountAmount + "元");
            txtOrderAmount.setText(orderAmount + "元");
            txtOrderNumber.setText(orderNumber);
            txtPayTime.setText(paytime);
            txtRealPayAmount.setText(realPayAmount + "元");
            txtStatus.setText(paystatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();

    }

    @Override
    public void taskFailed() {
        if (progressDialog != null)
            progressDialog.dismiss();
        Toast.makeText(this, "查询失败", Toast.LENGTH_SHORT).show();
        finish();
    }
*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_detail_tuikuan:
                showAlertRefoud();
                break;
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.detail_print:
                ordrhandler.sendEmptyMessage(6);
                break;

        }
    }

   /* private void tuikuan(final String pwd, float free) {
        String url = NetTools.HOMEURL + "/pay/wx-pay-refund-self-store";
        RequestParams params = new RequestParams(url);
        params.addHeader("token", usertoken);
        params.setConnectTimeout(10 * 1000);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", orderid);
            jsonObject.put("password", pwd);
            jsonObject.put("refundFee", free);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.setAsJsonContent(true);
//        String bodycon = "{" +
//                "\"id\":" + orderid +
//                ",\"password\":" + "\"" + pwd + "\"" +
//                ",\"refundFee\":" + free +
//                "}";
        params.setBodyContent(jsonObject.toString());
        Log.i("kxflog", "tuikuan------>" + params.getBodyContent());
//        params.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");"id=" + id + "&password=" + pwd + "&refundFee=" + free;
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                Log.i("kxflog", "tuikuan------>" + result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Log.i("kxflog", "tuikuan------>" + ex.getMessage());

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }*/

    private void showAlertRefoud() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View
                .inflate(this, R.layout.tuikuan_alert, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog tuidialog = builder.create();
        Button button = (Button) view.findViewById(R.id.alert_tuikuanbtn);
        TextView txtShow = (TextView) view.findViewById(R.id.alert_txtshow);
        Button cancelbtn = (Button) view.findViewById(R.id.alert_cancelbtn);
//        txtShow.setText(realmoney);
        txtShow.setText(refoundAmount + "");
        TextView txtAll = (TextView) view.findViewById(R.id.alert_txtall);
        final EditText editMonery = (EditText) view.findViewById(R.id.alert_editmonery);
        final EditText editPwd = (EditText) view.findViewById(R.id.alert_tuikuanpwd);
        txtAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editMonery.setText(refoundAmount + "");
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ztk = editMonery.getText().toString();
                float zt = Float.parseFloat(ztk);
                String editpwd = editPwd.getText().toString();
                if (ztk.equals("") || editpwd.equals("")) {
                } else {
                    tuidialog.dismiss();
//                    tuikuan(editpwd, zt);
                    tuiKuan(editpwd, zt + "");
                }
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tuidialog.dismiss();
            }
        });
        tuidialog.show();

    }

    private String orderid;

    private void tuiKuan(final String pwd, final String free) {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在退款...", true, false);
        final String url = NetTools.HOMEURL + "/pay/wx-pay-refund-self-store";
        new Thread() {
            @Override
            public void run() {
                OtherTools otherTools = new OtherTools();
                try {
                    String tkmsg = otherTools.tuikuanByPost(url, usertoken, orderid, pwd, free);
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(tkmsg);
                    String issuc = jsonObject.opt("success").toString();
                    String strmsg = "";
                    if (issuc.equals("true")) {
                        strmsg = "退款成功";
                    } else {
                        strmsg = jsonObject.opt("err_msg").toString();
                    }
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", strmsg);
                    message.what = 5;
                    message.setData(bundle);
                    ordrhandler.sendMessage(message);
                    Log.i("kxflog", tkmsg);
                } catch (Throwable throwable) {
                    progressDialog.dismiss();
                    throwable.printStackTrace();
                }
            }
        }.start();
    }

    private String ztk;

    private void printData(final int isrefound) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        String isprint = sharedPreferences.getString("isprint", "");
        Log.i("kxflog", isprint);
        if (("").equals(isprint) || ("off").equals(isprint)) {
            return;
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (!"".equals(blueadress)) {
                    final PrintTools printTools = new PrintTools(this, blueadress);
                    if (printTools.connect()) {
                        new Thread() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                if (isrefound == 1)
                                    printTools.printRefoundMonery(storeName, shouyy, orderNumber, orderAmount, "成功", ztk, discountAmount, paytime);
                                else
                                    printTools.printDeatail(orderAmount, orderNumber, paynote, orderType, realPayAmount, discountAmount, storeName, shouyy, paystatus, paytime, "0", typeyouhui, shanghushishou, bftuiam);

                            }
                        }.start();
                    } else {
                        Toast.makeText(this, "打印机连接失败，请检查打印机", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
