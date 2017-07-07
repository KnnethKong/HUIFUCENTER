package gjcm.kxf.goodorder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import gjcm.kxf.adapter.BroadAdapter;
import gjcm.kxf.adapter.FoodDeatailAdapter;
import gjcm.kxf.drawview.GridSpacingItemDecoration;
import gjcm.kxf.entity.BroadEntity;
import gjcm.kxf.listener.BroadItemClick;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.NetTools;

/**
 * Created by kxf on 2017/2/21.
 * 选餐桌
 */
public class BroadChooseActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, RecyclerView.OnItemTouchListener, BroadItemClick {
    private TabLayout mTabLayout;
    private RecyclerView recyclerView;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.broadall_layout);
        initbar();
        initView();

    }

    private void initbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tb);
        TextView ttile = (TextView) findViewById(R.id.toolbar_txt);
        ttile.setText("餐桌");
        setSupportActionBar(toolbar);
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BroadChooseActivity.this.finish();
            }
        });
        findViewById(R.id.toolbar_remind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BroadChooseActivity.this, RemindActivity.class));
            }
        });
    }

    private ArrayList<BroadEntity> broadEntities;

    public void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.broad_tablayout);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.addTab(mTabLayout.newTab().setText("全部"), 0, true);
        mTabLayout.addTab(mTabLayout.newTab().setText("使用中"), 1, false);
        mTabLayout.addTab(mTabLayout.newTab().setText("空闲"), 2, false);
        mTabLayout.addOnTabSelectedListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.broad_all_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        broadEntities = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        merchantId = sharedPreferences.getString("merchantId", null);
        storeId = sharedPreferences.getString("storeId", null);
        getData();
    }

    //0空闲，1使用中',
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                dstatus = 3;
                getData();
                break;
            case 1:
                dstatus = 1;
                getData();
                break;
            case 2:
                dstatus = 0;
                getData();
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private int dstatus = 3;

    private void getData() {
        if (dialog!=null)
            dialog.dismiss();
        dialog = ProgressDialog.show(this, "", "正在查询", true, false);
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Context.MODE_PRIVATE);
        String storeid = sharedPreferences.getString("storeId", null);
        String merchantid = sharedPreferences.getString("merchantId", null);
        broadEntities.clear();
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderDesk/QueryDesk");
        requestParams.addBodyParameter("id", storeid);
        requestParams.addBodyParameter("status", dstatus + "");
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //    Log.i("kxflog", result);
                if (dialog != null)
                    dialog.dismiss();
                try {
                    JSONObject json = new JSONObject(result);
                    int back_code = json.getInt("back_code");
                    if (back_code != 200) {
                        String backmsg = json.getString("error_msg");
                        Snackbar.make(recyclerView, "错误:" + backmsg, Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    JSONArray arraylist = json.getJSONArray("typeslist");
                    BroadEntity broadEntity;
                    for (int a = 0; a < arraylist.length(); a++) {
                        JSONObject jsoneach = (JSONObject) arraylist.get(a);
                        long did = jsoneach.getLong("id");
                        String name = jsoneach.getString("dname");
                        int pnum = jsoneach.getInt("persin");
                        int dstatus = jsoneach.getInt("dstatus");
                        broadEntity = new BroadEntity(did + "", name, 0, 0, pnum, "", "02-22 11:55", dstatus);
                        broadEntities.add(broadEntity);
                    }
                    BroadAdapter broadAdapter = new BroadAdapter(broadEntities, BroadChooseActivity.this);
                    recyclerView.setAdapter(broadAdapter);
                    recyclerView.addOnItemTouchListener(BroadChooseActivity.this);
                    recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 8, true));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (dialog != null)
                    dialog.dismiss();
                Snackbar.make(recyclerView, "服务器连接异常", Snackbar.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (dialog != null)
                    dialog.dismiss();
                Snackbar.make(recyclerView, "被取消", Snackbar.LENGTH_SHORT).show();
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
    public void clickItem(int p) {
        String deskid = broadEntities.get(p).getId();
        checkDesk(deskid);
//        Intent intent = new Intent(this, PersonChoose.class);
//        intent.putExtra("deskid", deskid);
//        startActivity(intent);
    }

    @Override
    public void longClick(int p) {


        int status = broadEntities.get(p).getDstatus();
        if (status == 0) return;
        String id = broadEntities.get(p).getId();
        showServing(id);
    }


    public void showServing(final String i) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.serving_layout, null);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setView(view);
        final android.support.v7.app.AlertDialog tuidialog = builder.create();
        tuidialog.show();
        final TextView queding = (TextView) view.findViewById(R.id.serving_up);
        queding.setText("确 定");
        final TextView quxiao = (TextView) view.findViewById(R.id.serving_dwon);
        quxiao.setText("取 消");
        TextView tname = (TextView) view.findViewById(R.id.seving_name);
        tname.setText("清空桌位");
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tuidialog.dismiss();
                changeOderinfoDesk(i);
            }
        });
        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tuidialog.dismiss();
            }
        });

    }


    public void changeOderinfoDesk(final String deskid) {
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderDesk/ChangeQingZhuo");
        requestParams.addBodyParameter("deskid", deskid);
        requestParams.addBodyParameter("sid", storeId);
        requestParams.addBodyParameter("mid", merchantId);
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("kxflog", result);
                try {
                    JSONObject json = new JSONObject(result);
                    int code = json.getInt("back_code");
                    if (code != 200) {
                        Toast.makeText(BroadChooseActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String oid = json.optString("order_id");
                    if (oid == null || "null".equals(oid)) {
                        getData();
                    } else {
                        showStatusChoose(deskid, oid);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Snackbar.make(recyclerView, "服务器连接异常", Snackbar.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Snackbar.make(recyclerView, "被取消", Snackbar.LENGTH_SHORT).show();
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

    public void showStatusChoose(final String deskid, final String oid) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.broad_status_choose, null);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setView(view);
        final android.support.v7.app.AlertDialog tuidialog = builder.create();
        tuidialog.show();
        final TextView queding = (TextView) view.findViewById(R.id.broad_ok);
        final TextView quxiao = (TextView) view.findViewById(R.id.broad_cancel);
        final RadioGroup group = (RadioGroup) view.findViewById(R.id.radio_group);
        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int nowid = group.getCheckedRadioButtonId();
                String os = "5";
                if (nowid == R.id.radion_1) {
                    os = "5";
                } else if (nowid == R.id.radion_2) {
                    os = "8";
                } else {
                    os = "9";
                } tuidialog.dismiss();
                changeOderinfoStatus(deskid, oid, os);
            }
        });
        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tuidialog.dismiss();
            }
        });

    }

    public void changeOderinfoStatus(String deskid, String oid, String sts) {
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderDesk/ChangeQZChoose");
        requestParams.addBodyParameter("deskid", deskid);
        requestParams.addBodyParameter("storeid", storeId);
        requestParams.addBodyParameter("mid", merchantId);
        requestParams.addBodyParameter("orderid", oid);
        requestParams.addBodyParameter("status", sts);
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("kxflog", result);
                getData();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Snackbar.make(recyclerView, "服务器连接异常", Snackbar.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Snackbar.make(recyclerView, "被取消", Snackbar.LENGTH_SHORT).show();
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

    private String merchantId, storeId;


    public void checkDesk(final String deskid) {
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderDesk/CheckOrderId");
        requestParams.addBodyParameter("deskid", deskid);
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    long orderid = jsonObject.optLong("order_id");
                    Intent intent;
                    //    Log.i("kxflog", "orderid:" + orderid);
                    if (orderid > 0) {
                        intent = new Intent(BroadChooseActivity.this, FoodOrderDetailActivity.class);
                        intent.putExtra("orderid", orderid + "");
                        intent.putExtra("dname", "on");

                    } else {
                        intent = new Intent(BroadChooseActivity.this, PersonChooseActivity.class);
                        intent.putExtra("deskid", deskid);
                    }
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Snackbar.make(recyclerView, "服务器连接异常", Snackbar.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Snackbar.make(recyclerView, "被取消", Snackbar.LENGTH_SHORT).show();
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


//
//    public static void main(String[] a) {
//        String s = "1b4501";
//        byte[] bytes = str2Bcd(s);
//        System.out.print(bytes);
//        String back = bcd2Str(bytes);
//        System.out.print(back);
//    }
}
