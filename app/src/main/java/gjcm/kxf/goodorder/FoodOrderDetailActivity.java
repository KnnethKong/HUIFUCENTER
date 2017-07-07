package gjcm.kxf.goodorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gjcm.kxf.adapter.FoodDeatailAdapter;
import gjcm.kxf.entity.FoodsEntity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.NetTools;
import gjcm.kxf.tools.TempTools;

/**
 * 餐品订单详情
 * Created by kxf on 2017/3/17.
 */
public class FoodOrderDetailActivity extends AppCompatActivity implements View.OnClickListener, FoodDeatailAdapter.Delectface {
    private TextView tuser, trenshu, tdate, tstatus, tid, tmoney, tfoodnum, tphone, tzhuowei;
    private ListView listView;
    private List<FoodsEntity> foodsEntities;
    private String OrderId = "", loginId, merchantId, storeId;
    private ImageOptions imageOptions;
    private String ispeint, ishehe;
    //   private boolean isprint;
    private LinearLayout isshowLiner;//退接单
    private List<String> dellist;
    private int orderWay;// 1app 3 web

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.food_detail_layout);
        OrderId = getIntent().getStringExtra("orderid");
        ishehe = getIntent().getStringExtra("dname");
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        loginId = sharedPreferences.getString("logid", null);
        merchantId = sharedPreferences.getString("merchantId", null);
        storeId = sharedPreferences.getString("storeId", null);
        initbar();
        initViewEvent();
        getData();
    }

    private void initbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tb);
        TextView ttile = (TextView) findViewById(R.id.toolbar_txt);
        findViewById(R.id.toolbar_print).setOnClickListener(this);
        ttile.setText("菜品订单详情");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodOrderDetailActivity.this.finish();
            }
        });

        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);

        findViewById(R.id.toolbar_remind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FoodOrderDetailActivity.this, RemindActivity.class));
            }
        });
        imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setFailureDrawableId(R.mipmap.erro)
                .setLoadingDrawableId(R.mipmap.loading)
                .build();
    }

    private void initViewEvent() {
        tuser = (TextView) findViewById(R.id.order_detail_user);
        trenshu = (TextView) findViewById(R.id.order_detail_people);
        tdate = (TextView) findViewById(R.id.order_detail_date);
        tstatus = (TextView) findViewById(R.id.order_detail_status);
        tid = (TextView) findViewById(R.id.order_detail_orderno);
        tmoney = (TextView) findViewById(R.id.order_detail_orderam);
        tzhuowei = (TextView) findViewById(R.id.order_detail_canzhuo);
        tfoodnum = (TextView) findViewById(R.id.order_detail_foodnum);
        tphone = (TextView) findViewById(R.id.order_detail_phone);
        listView = (ListView) findViewById(R.id.order_detail_list);
        isshowLiner = (LinearLayout) findViewById(R.id.food_detail_showline);
        findViewById(R.id.food_detail_jiedan).setOnClickListener(this);
        findViewById(R.id.food_detail_tuidan).setOnClickListener(this);
        dellist = new ArrayList<>();

    }

    private String orderuser = null, uphone, canzhuo, odate, peple, orderid, orderstatus, deskid;
    private Double ordersum;
    private int sumnum = 0, isDesk = 0;
    private FoodDeatailAdapter deatailAdapter;
    int ordercode;
    boolean isedit = true;
    private myClickItem myclick;//上菜退菜

    public void getData() {
        sumnum = 0;
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在查询", true, false);
        //1无桌码 2/有桌码
        String status = "1";
        if (ishehe.equals("web订单"))
            status = "1";
        else
            status = "2";

        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderInfo/QueryOrderDetail");
        requestParams.addBodyParameter("OrderId", OrderId);
        requestParams.addBodyParameter("status", status);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                try {
                    //  ArrayList<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt("back_code");

                    if (code != 200) {
                        String msg = json.getString("error_msg");
                        Toast.makeText(FoodOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    orderuser = json.getString("customerId");//customerId
                    uphone = json.optString("uphone");//
                    canzhuo = json.getString("deskname");
                    if (canzhuo == null || "null".equals(canzhuo)) {
                        isDesk = 1;
                        canzhuo = "web订单";
                    }
                    odate = json.getString("date");
                    peple = json.getString("personNum");
                    boolean isarray = json.getBoolean("isarray");
                    orderid = json.getString("id");
                    deskid = json.optString("deskid");
                    ordersum = json.getDouble("orderAmount");
                    ordercode = json.getInt("orderStatus");
                    orderWay = json.getInt("orderWay");
                    switch (ordercode) {
                        case 1:
                            orderstatus = "点餐中";
                            break;
                        case 2:
                            orderstatus = "下单中";
                            isedit = false;
                            isshowLiner.setVisibility(View.VISIBLE);
                            tstatus.setBackgroundResource(R.drawable.daijiedan);
                            break;
                        case 3:
                            if (myclick == null)
                                myclick = new myClickItem();
                            orderstatus = "已下单";
                            listView.setOnItemClickListener(myclick);
                            tstatus.setBackgroundResource(R.drawable.yixiadan);
                            break;
                        case 4:
                            orderstatus = "已上齐";
                            break;
                        case 5:
                            orderstatus = "已结账";
                            tstatus.setBackgroundResource(R.drawable.yijiezhang);
//                            findViewById(R.id.toolbar_print).setVisibility(View.VISIBLE);
                            break;
                        case 6:
                            orderstatus = "已退单";
                            break;
                        case 7:
                            orderstatus = "已撤桌";
                            tstatus.setBackgroundResource(R.drawable.yichezhuo);
                            break;
                        case 8:
                            orderstatus = "已拒单";
                            tstatus.setBackgroundResource(R.drawable.jvshoudan);
                            break;
                    }
                    if (ordercode == 3) {
                        isShowMenu = true;
                        checkOptionMenu();
                    }
                    ordersum = ordersum / 100;
                    if (!isarray) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FoodOrderDetailActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                        return;
                    }
                    JSONArray list = json.getJSONArray("backlist");
                    foodsEntities = new ArrayList<>();
                    FoodsEntity foodsEntity;
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject eachjson = (JSONObject) list.get(i);
                        String oid = eachjson.getString("id");
                        String oname = eachjson.getString("oname");
                        int gstatus = eachjson.optInt("gstatus");
                        String goodstatus = "未同步";
                        if (gstatus == 0) {
                            goodstatus = "已上菜";
                        } else {
                            goodstatus = "未上菜";
                        }
                        String opic = eachjson.getString("opic");
                        String ostyle = eachjson.getString("style");
                        Double price = eachjson.getDouble("price");
                        //     int printType = eachjson.getInt("printType");
                        Double nprice = 0.00;
                        int num = eachjson.getInt("num");
                        sumnum += num;
                        foodsEntity = new FoodsEntity(oid, oname, num, price, nprice, opic, "", ostyle, goodstatus, 0);
                        foodsEntities.add(foodsEntity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                deatailAdapter = new FoodDeatailAdapter(foodsEntities, FoodOrderDetailActivity.this, imageOptions, isedit, FoodOrderDetailActivity.this);
                deatailAdapter.setDelectface(FoodOrderDetailActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tuser.setText(orderuser);
                        trenshu.setText(peple);
                        tdate.setText(odate);
                        tstatus.setText(orderstatus);
                        tid.setText(orderid);
                        tmoney.setText(ordersum.doubleValue() + "");
                        tfoodnum.setText(sumnum + "");
                        tphone.setText(uphone);
                        tzhuowei.setText(canzhuo);
                        listView.setAdapter(deatailAdapter);
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                if (progressDialog != null)
                    progressDialog.dismiss();
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

    private boolean isShowMenu = false;//显示menu
    private Menu aMenu;

    private void checkOptionMenu() {
        if (null != aMenu) {
            if (isShowMenu) {
                for (int i = 0; i < aMenu.size(); i++) {
                    aMenu.getItem(i).setVisible(true);
                    aMenu.getItem(i).setEnabled(true);
                }
            } else {
                for (int i = 0; i < aMenu.size(); i++) {
                    aMenu.getItem(i).setVisible(false);
                    aMenu.getItem(i).setEnabled(false);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.com_order, menu);
        aMenu = menu;
        checkOptionMenu();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (foodsEntities != null)
            foodsEntities = null;

    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.com_order_refound:
                    if (ordercode == 3) {
                        isDesk = 2;
                        refoundOrder();
                    }
//                        finshDishes("1");
                    break;
                case R.id.com_order_next:
                    intent = new Intent(FoodOrderDetailActivity.this, OrderScaleAcivity.class);
                    intent.putExtra("orderid", OrderId);
                    intent.putExtra("orderam", ordersum);
                    intent.putExtra("deskid", deskid);
                    intent.putExtra("orderway", orderWay);
                    intent.putExtra("isDesk", isDesk);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.com_order_addgood:
                    intent = new Intent(FoodOrderDetailActivity.this, GoodsCatActivity.class);
                    intent.putExtra("orderid", OrderId);
                    intent.putExtra("deskid", deskid);
                    startActivity(intent);
                    FoodOrderDetailActivity.this.finish();
                    break;
            }

            return true;
        }
    };


    private void finshDishes(String status) {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在查询...", true, false);
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderInfo/changeOrderstatus");
        requestParams.addBodyParameter("id", OrderId);
        requestParams.addBodyParameter("deskid", deskid);
        requestParams.addBodyParameter("scode", status);
        requestParams.addBodyParameter("realam", "0");
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                getData();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(FoodOrderDetailActivity.this, "连接服务错误", Toast.LENGTH_SHORT).show();
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

    class myClickItem implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String status = foodsEntities.get(i).getStatus();
            if (ordercode == 3) {
                if (status.equals("未上菜"))
                    showServing(i);
            }
        }
    }

    private void showServing(final int i) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.serving_layout, null);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setView(view);
        final android.support.v7.app.AlertDialog tuidialog = builder.create();
        tuidialog.show();
        final TextView shangcai = (TextView) view.findViewById(R.id.serving_up);
        shangcai.setText("上 菜");
        final TextView tuicai = (TextView) view.findViewById(R.id.serving_dwon);
        shangcai.setText("退 菜");

        String sname = foodsEntities.get(i).getFname();

        TextView tname = (TextView) view.findViewById(R.id.seving_name);
        tname.setText(sname);
        shangcai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = foodsEntities.get(i).getFid();
                tuidialog.dismiss();
                changeStats(id, 1);
            }
        });
        tuicai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = foodsEntities.get(i).getFid();
                tuidialog.dismiss();
                changeStats(id, 2);
            }
        });

    }

    ProgressDialog progressDialog;
//14 结账 15 关闭
    public void changeStats(String id, int s) {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在更改...", true, false);
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "orderGood/changeGS");
        requestParams.addBodyParameter("isud", s, null);
        requestParams.addBodyParameter("id", id);
        requestParams.addBodyParameter("orderid", OrderId);
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                JSONObject json = null;
                try {
                    json = new JSONObject(result);
                    int code = json.getInt("back_code");
                    if (code == 200) {
                        listView.setAdapter(null);
                        foodsEntities = null;
                        getData();
                    } else {
                        Toast.makeText(FoodOrderDetailActivity.this, "失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(FoodOrderDetailActivity.this, "连接服务错误", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.food_detail_tuidan:
                refoundOrder();
                break;
            case R.id.food_detail_jiedan:
                showAsk();
                break;
            case R.id.toolbar_print:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getData();
    }

    public void showAsk() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("\n 是否确认以上菜品？\n");
        builder.setTitle("温馨提醒:");
        builder.setPositiveButton(" 确认下单 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                progressDialog = ProgressDialog.show(FoodOrderDetailActivity.this, "", "正在提交", true, false);
                sendAllFinsh();
            }
        });
        builder.setNegativeButton(" 取消下单 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                refoundOrder();
                     dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();

    }

    @Override
    public void delscale(int i) {
        String fid = foodsEntities.get(i).getFid();
        dellist.add(fid);
        foodsEntities.remove(i);
        deatailAdapter.notifyDataSetChanged();
        doScale();
    }

    private DecimalFormat df3 = new DecimalFormat("0.00");

    private void doScale() {
        double sum = 0.00;
        int sumnum = 0;
        int num;
        for (FoodsEntity d : foodsEntities) {
            num = d.getFnum();
            double s = num * d.getFprice();
            sum += s;
            sumnum += num;
        }
        String ams = df3.format(sum);
        tmoney.setText(ams);
        tfoodnum.setText(sumnum + "");
    }

    @Override
    public void add(int i, String value, View view) {

        int s = Integer.parseInt(value);
        s += 1;
        foodsEntities.get(i).setFnum(s);
        TextView txt = (TextView) view.findViewById(R.id.edit_item_num);
        txt.setText(s + "");
        doScale();
    }

    public void sendAllFinsh() {
        double sum = 0.00;
        for (FoodsEntity d : foodsEntities) {
            double s = d.getFnum() * d.getFprice();
            sum += s;
        }
        String ams = df3.format(sum);
        JSONArray array = new JSONArray();
        JSONObject each = null;
        try {
            for (FoodsEntity fe : foodsEntities) {
                each = new JSONObject();
                String id = fe.getFid();
                int num = fe.getFnum();
                each.put("id", id);
                each.put("num", num);
                array.put(each);
            }
            each = new JSONObject();
            each.put("orderid", OrderId);
            each.put("orderam", ams);
            each.put("content", array);
            each.put("deskId", deskid);
            each.put("operid", loginId);
            array = new JSONArray(dellist);
            each.put("dellist", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //  Log.e("kxflog", "each----" + each.toString());
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderInfo/sendAll");
        requestParams.setAsJsonContent(true);
        requestParams.setBodyContent(each.toString());
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(FoodOrderDetailActivity.this, "接单成功", Toast.LENGTH_SHORT).show();
                send2Order(OrderId, "12");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(FoodOrderDetailActivity.this, "接单失败:" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //踢向ali card
    private void send2Order(String orderid, String status) {
        //  Log.i("kxflog", "---踢向--");
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(FoodOrderDetailActivity.this, "", "正在更新到支付宝", true, false);
        RequestParams requestParams = new RequestParams(NetTools.HOMEURL + "/diancan/sendorder");
     //   RequestParams requestParams = new RequestParams("http://weijing.f3322.net:9090/diancan/sendorder");
        requestParams.addBodyParameter("merchantId", merchantId);
        requestParams.addBodyParameter("storeId", storeId);
        requestParams.addBodyParameter("orderId", orderid);
        requestParams.addBodyParameter("status", status);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Log.e("kxflog", "send2Order----------" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    boolean success = object.getBoolean("success");
                    if (success) {
                        Toast.makeText(FoodOrderDetailActivity.this, "成功", Toast.LENGTH_SHORT).show();
                        FoodOrderDetailActivity.this
                                .finish();
                    } else {
                        object.optString("err_code");
                        String err = object.optString("err_msg");
                        Toast.makeText(FoodOrderDetailActivity.this, "" + err, Toast.LENGTH_SHORT).show();
                        FoodOrderDetailActivity.this
                                .finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(FoodOrderDetailActivity.this, "更新到支付宝失败:" + ex.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void refoundOrder() {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(FoodOrderDetailActivity.this, "", "正在拒绝...", true, false);
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderInfo/refuseOrder");
        requestParams.addBodyParameter("orderid", OrderId);
        requestParams.addBodyParameter("staus", isDesk + "");
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                JSONObject json = null;
                try {
                    json = new JSONObject(result);
                    int code = json.getInt("back_code");
                    if (code == 200) {
                        Toast.makeText(FoodOrderDetailActivity.this, "成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // Toast.makeText(FoodOrderDetailActivity.this, json.getString("error_msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(FoodOrderDetailActivity.this, "失败:" + ex.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void subtract(int i, String value, View view) {
        int s = Integer.parseInt(value);
        if (s > 1) {
            s -= 1;
            foodsEntities.get(i).setFnum(s);
            TextView txt = (TextView) view.findViewById(R.id.edit_item_num);
            txt.setText(s + "");
            doScale();
        }
    }

}

