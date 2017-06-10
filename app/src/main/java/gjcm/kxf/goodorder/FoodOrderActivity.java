package gjcm.kxf.goodorder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gjcm.kxf.adapter.FoodOrderAdapter;
import gjcm.kxf.entity.DaningEntity;
import gjcm.kxf.huifucenter.LoginActivity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.NetTools;
import gjcm.kxf.wheelview.DatePickerDialog;
import library.PullToRefreshBase;
import library.PullToRefreshListView;

/**
 * Created by kxf on 2017/3/4.
 * 点餐订单 以下单 待接单 已结账 菜上齐
 */
public class FoodOrderActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemSelectedListener, View.OnClickListener {
    private PullToRefreshListView loadMoreView;
    private ListView listView;
    private List<DaningEntity> daningEntityList;
    private String orderstatus = "0";
    private Spinner spinnerStatus;
    private String storeid;
    private DatePickerDialog mChangeBirthDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.foodorder_layout);
        spinnerDate = (Button) findViewById(R.id.dingdan_date);
        spinnerDate.setOnClickListener(this);
        loadMoreView = (PullToRefreshListView) findViewById(R.id.foodorder_refresh);
        loadMoreView.setMode(PullToRefreshBase.Mode.BOTH);
        listView = loadMoreView.getRefreshableView();
        listView.setOnItemClickListener(this);
        loadMoreView.setOnRefreshListener(this);
        daningEntityList = new ArrayList<>();
        spinnerStatus = (Spinner) findViewById(R.id.foodorder_spinner);
        spinnerStatus.setOnItemSelectedListener(this);
        Calendar c = Calendar.getInstance();
        int nowyear = c.get(Calendar.YEAR);
        int nowmonth = c.get(Calendar.MONTH) + 1;
        int nowday = c.get(Calendar.DAY_OF_MONTH);
        payStartTime = nowyear + "-" + nowmonth + "-" + nowday;
        payEndTime = payStartTime + " 23:59";
        payStartTime += " 00:00";
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        storeid = sharedPreferences.getString("storeId", null);
        if (storeid == null) {
            Intent intent = new Intent(FoodOrderActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        initbar();
        getData();
//        SearchView searchView = (SearchView) findViewById(R.id.foodorder_search);
//        searchView.setOnQueryTextListener(this);
    }

    private void initbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tb);
        TextView ttile = (TextView) findViewById(R.id.toolbar_txt);
        ttile.setText("点餐订单");
        setSupportActionBar(toolbar);
        findViewById(R.id.toolbar_remind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FoodOrderActivity.this, RemindActivity.class));
            }
        });
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodOrderActivity.this.finish();
            }
        });
        mChangeBirthDialog = new DatePickerDialog(
                this);
    }

    private Button spinnerDate;
    private String payEndTime, payStartTime;

    private void showDate() {
        mChangeBirthDialog.show();
        mChangeBirthDialog.setDatePickListener(new DatePickerDialog.OnDatePickListener() {

            @Override
            public void onClick(String beiginyear, String beiginmonth, String beiginday, String endyear, String endmonth, String endday, String behours, String endhour) {
                Log.e("kxflog", "5");
                if (progressDialog != null)
                    progressDialog.dismiss();
//                dialog = ProgressDialog.show(context, "", "正在查询", true, false);
                payStartTime = beiginyear + "-" + beiginmonth + "-" + beiginday + " " + behours;
                payEndTime = endyear + "-" + endmonth + "-" + endday + " " + endhour;
                spinnerDate.setText(beiginmonth + "-" + beiginday + " 至 " + endmonth + "-" + endday);
                daningEntityList.clear();
                listView.setAdapter(null);
                nowpage = 1;
                getData();
            }

            @Override
            public void date(String begin, String end) {
                Log.e("kxflog", "5");
                if (progressDialog != null)
                    progressDialog.dismiss();
                payStartTime = begin + " 00:00";
                payEndTime = end + " 23:59";
                String[] bs = begin.split("-");
                String ss = bs[1] + "-" + bs[2];
                String[] es = end.split("-");
                String ess = es[1] + "-" + es[2];
                spinnerDate.setText(ss + " 至 " + ess);
                daningEntityList.clear();
                listView.setAdapter(null);
                nowpage = 1;
                getData();
            }
        });
    }

    private int nowpage = 1, pagecon = 8;
    ProgressDialog progressDialog;

    public void getData() {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在查询...", true, false);
        RequestParams params = new RequestParams(NetTools.HOSTURL + "OrderInfo/QueryAllCompile");
        params.addBodyParameter("id", storeid);
        params.addBodyParameter("nowpage", nowpage + "");
        params.addBodyParameter("orderstatus", orderstatus);
        params.addBodyParameter("startime", payStartTime);
        params.addBodyParameter("endtime", payEndTime);
        params.addBodyParameter("pagecon", pagecon + "");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                if (loadMoreView != null) {
                    loadMoreView.onRefreshComplete();
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    int back_code = jsonObject.getInt("back_code");
                    if (back_code != 200) {
                        String error_msg = jsonObject.getString("error_msg");
                        Toast.makeText(FoodOrderActivity.this, error_msg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int serallcount = jsonObject.optInt("allcount");
                    int serpagecount = jsonObject.optInt("pagecount");
                    int sernowpage = jsonObject.optInt("nowpage");
                    if (nowpage > serpagecount) {
                        nowpage = serpagecount;
                        Toast.makeText(FoodOrderActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONArray array = jsonObject.optJSONArray("jiedanlist");
                    DaningEntity entity;
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject eachjson = (JSONObject) array.get(i);
                        String id = eachjson.getString("id");
                        String canzhuo = eachjson.optString("canzhuo");
                        if (canzhuo == null || "".equals(canzhuo)) {
                            canzhuo = "web订单";
                        }
                        String renshu = eachjson.getString("renshu");
                        Double d = eachjson.getDouble("money");
                        String date = eachjson.getString("date");
                        String status = eachjson.getString("status");
                        entity = new DaningEntity(id, canzhuo, renshu, date, d.doubleValue(), status);
                        daningEntityList.add(entity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FoodOrderAdapter adapter = new FoodOrderAdapter(daningEntityList, FoodOrderActivity.this);
                listView.setAdapter(adapter);
                listView.setTextFilterEnabled(true);


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(FoodOrderActivity.this, "连接服务错误", Toast.LENGTH_SHORT).show();
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DaningEntity daningEntity = daningEntityList.get(i - 1);
        String id = daningEntity.getSid();
        String dn = daningEntity.getScanzhuo();
        Intent intent = new Intent(FoodOrderActivity.this, FoodOrderDetailActivity.class);
        intent.putExtra("orderid", id);
        intent.putExtra("dname", dn);
        startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
        refreshView.getLoadingLayoutProxy().setPullLabel("别再拉了");
        refreshView.getLoadingLayoutProxy().setReleaseLabel("放开我...");
        nowpage = 1;
        daningEntityList.clear();
        listView.setAdapter(null);
        getData();

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
        refreshView.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
        refreshView.getLoadingLayoutProxy().setReleaseLabel("放开我...");
        nowpage += 1;
        getData();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i > 0)
            orderstatus = i - 1 + "";
        if (i > 0) {
            if (daningEntityList != null)
                daningEntityList.clear();
            listView.setAdapter(null);
            nowpage = 1;
            getData();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        showDate();
    }
}
