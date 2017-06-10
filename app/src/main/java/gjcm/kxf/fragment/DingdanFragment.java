package gjcm.kxf.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import gjcm.kxf.adapter.OrderCommonAdapter;
import gjcm.kxf.entity.MerchantOrderCommon;
import gjcm.kxf.huifucenter.LoginActivity;
import gjcm.kxf.huifucenter.OrderDetailActivity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.MyAsyncTask;
import gjcm.kxf.tools.NetTools;
import gjcm.kxf.tools.OtherTools;
import gjcm.kxf.wheelview.DatePickerDialog;
import library.PullToRefreshBase;
import library.PullToRefreshListView;

/**
 * Created by kxf on 2016/12/13.
 */
public class DingdanFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener {
    private String usertoken, usertype;
    private TextView titleText, amoText, reamoText, countText;
    private Spinner spinnerType;
    private Button spinnerDate;
    private ArrayList<MerchantOrderCommon> lists;
    private PullToRefreshListView refreshListView;
    private ListView listPullview;
    private int nowPage = 1;
    private OrderCommonAdapter adapter;
    private String payEndTime, payStartTime, umengNo, appversion, storeid, storeName, orderstatus = "-1";
    private static String path;
    private int everyPageCount = 12;
    private int TOTALCOUNT;

    private ProgressDialog dialog;
    private TextView textStorename;
    private Context context;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private long timestimp;
    private Date date;
    private Spinner paystaty;


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("kxflog", "*********onDestroy");
        if (myAsyncTask != null)
            myAsyncTask.cancel(true);
        lists = null;
        listPullview = null;
        adapter = null;
        date = null;
//        showMeroory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diangdan_fragment, null);
        context = getContext();
        titleText = (TextView) view.findViewById(R.id.title_storename);
        spinnerDate = (Button) view.findViewById(R.id.dingdan_date);
        spinnerType = (Spinner) view.findViewById(R.id.dingdan_type);
        refreshListView = (PullToRefreshListView) view.findViewById(R.id.dingdan_pullrorefewsh);
        amoText = (TextView) view.findViewById(R.id.dingdan_amount);
        countText = (TextView) view.findViewById(R.id.dingdan_count);
        reamoText = (TextView) view.findViewById(R.id.dingdan_reamount);
        refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        listPullview = refreshListView.getRefreshableView();
        listPullview.setOnItemClickListener(this);
        refreshListView.setOnRefreshListener(this);
        titleText.setVisibility(View.VISIBLE);
        init();
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.i("kxflog", "onViewStateRestored---------:");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("kxflog", "onStart---------:");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("kxflog", "onResume---------:");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        spinnerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 1:
                        orderstatus = "-1";
                        break;
                    case 2:
                        orderstatus = "1";
                        break;
                    case 3:
                        orderstatus = "4";
                        break;
                    case 4:
                        orderstatus = "0";
                        break;
                    case 5:
                        orderstatus = "3";
                        break;
                }
                if (i > 0) {
                    if (lists != null)
                        lists.clear();
                    if (adapter != null)
                        listPullview.setAdapter(null);
                    nowPage = 1;
                    getData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showDate() {
        DatePickerDialog mChangeBirthDialog = new DatePickerDialog(
                context);
        mChangeBirthDialog.show();
        mChangeBirthDialog.setDatePickListener(new DatePickerDialog.OnDatePickListener() {

            @Override
            public void onClick(String beiginyear, String beiginmonth, String beiginday, String endyear, String endmonth, String endday, String behours, String endhour) {
                if (dialog != null)
                    dialog.dismiss();
//                dialog = ProgressDialog.show(context, "", "正在查询", true, false);
                payStartTime = beiginyear + "-" + beiginmonth + "-" + beiginday + " " + behours;
                payEndTime = endyear + "-" + endmonth + "-" + endday + " " + endhour;
                spinnerDate.setText(payStartTime + "\n" + payEndTime);
                lists.clear();
                listPullview.setAdapter(null);
                nowPage = 1;
                orderstatus = "-1";
//                loadAsync();
                getData();
                spinnerType.setSelection(0);
            }

            @Override
            public void date(String begin, String end) {
                if (dialog != null)
                    dialog.dismiss();
//                dialog = ProgressDialog.show(context, "", "正在查询", true, false);

                payStartTime = begin + " 00:00";
                payEndTime = end + " 23:59";

//                begin = begin.substring(0, begin.length() - 3);
//                end = end.substring(0, end.length() - 3);
////                Log.i("kxflog", end + "-------------------" + begin);
//                payStartTime = begin;
//                payEndTime = end;
                spinnerDate.setText(begin + "\n" + end);
                lists.clear();
                listPullview.setAdapter(null);
                nowPage = 1;
                orderstatus = "-1";
                getData();
//                loadAsync();
                spinnerType.setSelection(0);

            }
        });
    }

    private void init() {
        String[] mItems = getResources().getStringArray(R.array.orderstatus);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, mItems);
        arrayAdapter.setDropDownViewResource(R.layout.spiner_style_liner);
        spinnerType.setAdapter(arrayAdapter);
//        dialog = ProgressDialog.show(context, "", "正在查询", true, false);
        Calendar c = Calendar.getInstance();
        int nowyear = c.get(Calendar.YEAR);
        int nowmonth = c.get(Calendar.MONTH) + 1;
        int nowday = c.get(Calendar.DAY_OF_MONTH);
        payStartTime = nowyear + "-" + nowmonth + "-" + nowday;
        payEndTime = payStartTime + " 23:59:59";
        lists = new ArrayList<>();
        usertoken = MainFragment.usertoken;
        usertype = MainFragment.usertype;
        storeName = MainFragment.storeName;
        if (usertoken.equals("") || usertype.equals("")) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
            usertoken = sharedPreferences.getString("usertoken", "");
            usertype = sharedPreferences.getString("usertype", "");
            storeName = sharedPreferences.getString("storeName", "");
        }
        if (usertoken.equals("")) {
            Toast.makeText(getContext(), "登录过期", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }
        titleText.setText(storeName);
        payStartTime = nowyear + "-" + nowmonth + "-" + nowday;
        payEndTime = payStartTime + " 23:59:59";
        if (usertype.equals("1")) {
            path = NetTools.HOMEURL + "/manager-order/search";
        } else if (usertype.equals("0")) {
            path = NetTools.HOMEURL + "/manager-order/app/search";///
        } else {
            path = NetTools.HOMEURL + "/order-cashier/search";
        }

        final boolean isnet = NetTools.isNetworkConnected(getContext());
        if (!isnet) {
            if (dialog != null)
                dialog.dismiss();
            Toast.makeText(getContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
        }
        getData();
//        loadAsync();
    }

    private MyAsyncTask myAsyncTask;


    private void getData() {
        dialog = ProgressDialog.show(context, "", "正在查询", true, false);
        RequestParams requestParams = new RequestParams(path);
        requestParams.setConnectTimeout(10 * 1000);
        requestParams.setAsJsonContent(true);
        String params = "{" +
                "\"dto\":{" + "\"payStartTime\":" + "\"" + payStartTime + "\"" + ",\"payEndTime\":" + "\"" + payEndTime + "\"" + ",\"status\":" + orderstatus + "}," +
                "\"page\":{" + "\"pageNO\":" + nowPage + ",\"everyPageCount\":" + everyPageCount + "}" +
                "}";
        requestParams.addHeader("token", usertoken);
        requestParams.setBodyContent(params);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                taskSuccessful(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                taskFailed();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                taskFailed();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
//        showMeroory();
    }

    //
//    private void loadAsync() {
//        if (myAsyncTask != null) {
//            myAsyncTask.cancel(true);
//            myAsyncTask = null;
//        }
//        myAsyncTask = new MyAsyncTask();
//        myAsyncTask.setTaskHandler(this);
//        myAsyncTask.execute(new String[]{path, usertoken, nowPage + "", everyPageCount + "", payEndTime, payStartTime, orderstatus});
//
//    }

    private int pageNo;

    public void taskSuccessful(String json) {
        refreshListView.onRefreshComplete();
        JSONObject jsonObject = null;
        if (dialog != null)
            dialog.dismiss();
        try {
            jsonObject = new JSONObject(json);
            String success = jsonObject.getString("success").toString();
            if (!success.equals("true")) {
                String err_msg = jsonObject.getString("err_msg").toString();

                Toast.makeText(context,
                        err_msg, Toast.LENGTH_SHORT).show();
                String err_code = jsonObject.getString("err_code").toString();
                if (err_code.equals("000006")) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("islogout", "true");
                    startActivity(intent);
                    getActivity().finish();
                }
                return;
            }
            JSONObject dataJson = jsonObject.getJSONObject("data");
            JSONObject commJson = dataJson.getJSONObject("merchantCountOrderCommon");
            String total_amount = commJson.optString("totalAmount");
            String total_count = commJson.optString("totalOrderCount");
            String refund_amount = commJson.optString("refundAmount");
            countText.setText(total_count);
            reamoText.setText(refund_amount);
            amoText.setText(total_amount);
            JSONObject pageJson = dataJson.getJSONObject("page");
            TOTALCOUNT = (int) pageJson.opt("totalCount");
            if (TOTALCOUNT > everyPageCount * everyPageCount) {
            }
            pageNo = (int) pageJson.opt("pageNO");
            JSONArray listArray = dataJson.optJSONArray("list");
            if (listArray == null) {
                dialog.dismiss();
                Toast.makeText(context, "没有数据", Toast.LENGTH_SHORT).show();
                return;
            }
            MerchantOrderCommon common;
            for (int i = 0; i < listArray.length(); i++) {
                JSONObject listObject = listArray.getJSONObject(i);
                String id = listObject.getString("id");
                String orderNumber = listObject.getString("orderNumber");
                String orderAmount = listObject.getString("orderAmount");
                Double cny = Double.parseDouble(orderAmount);//转换成Double
                DecimalFormat df = new DecimalFormat("0.00");//格式化
                orderAmount = df.format(cny);
                String statusText = listObject.getString("statusText");
                String orderType = listObject.getString("type");
                String orderStatus = listObject.getString("status");
                String payTime = listObject.optString("payTime");
                String str_nian = "", str_yue = "";
                if (payTime.equals("")) {
                    str_nian = " ";
                    str_yue = " ";
                } else {
                    timestimp = Long.parseLong(payTime);
                    date = new Date(timestimp);
                    payTime = simpleDateFormat.format(date);
                    str_nian = payTime.substring(2, 10);
                    str_yue = payTime.substring(11, 19);
                }
                common = new MerchantOrderCommon(id, orderNumber, orderAmount, statusText, str_nian, str_yue, orderType);
                lists.add(common);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new OrderCommonAdapter(lists, context);
        listPullview.setAdapter(adapter);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (dialog != null)
            dialog.dismiss();
        int nowcur = (nowPage - 1) * everyPageCount;
        if (nowPage >= 2) {
            nowcur = nowcur - 4;
        }
        refreshListView.getRefreshableView().setSelection(nowcur);
//        showMeroory();
//        Log.i("kxflog","nowcur:"+nowcur);
    }


    public void taskFailed() {
        if (dialog != null)
            dialog.dismiss();
        Toast.makeText(context, "加载失败", Toast.LENGTH_SHORT).show();
        refreshListView.onRefreshComplete();

        //        countText.setText(0);
//        reamoText.setText(0);
//        amoText.setText(0);
    }

    private void showMeroory() {
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(info);
        Log.i("kxflog", "系统剩余内存:" + (info.availMem >> 10) + "k");
        Log.i("kxflog", "系统是否处于低内存运行：" + info.lowMemory);
        Log.i("kxflog", "当系统剩余内存低于" + info.threshold + "时就看成低内存运行");
    }


    //下拉
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//        if (dialog != null)
//            dialog.dismiss();
//        dialog = ProgressDialog.show(context, "", "正在查询", false, false);
        refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
        refreshView.getLoadingLayoutProxy().setPullLabel("别再拉了");
        refreshView.getLoadingLayoutProxy().setReleaseLabel("放开我...");
        if (lists != null)
            lists.clear();
        if (adapter != null)
            listPullview.setAdapter(null);
        nowPage = 1;
//        loadAsync();

        getData();
    }

    private int isdwon = 2;

    //上拉
    @Override
    public void onPullUpToRefresh(final PullToRefreshBase<ListView> refreshView) {
        isdwon = 2;
//        if (dialog != null)
//            dialog.dismiss();
//        dialog = ProgressDialog.show(context, "", "正在查询", false, false);
        refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
        refreshView.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
        refreshView.getLoadingLayoutProxy().setReleaseLabel("放开我...");
        if ((pageNo * everyPageCount) > TOTALCOUNT) {
            dialog.dismiss();
            refreshView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshView.onRefreshComplete();
                }
            }, 200);
        } else {
            nowPage++;
            getData();
//            loadAsync();
        }

    }

    private int clickOun = 0;


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MerchantOrderCommon merchantOrderCommon = lists.get(i - 1);
        Log.i("kxflog", "getOrderNumber:" + merchantOrderCommon.getOrderNumber());
        String ordernumber = merchantOrderCommon.getOrderNumber();
        Intent intent = new Intent(getContext(), OrderDetailActivity.class);
        intent.putExtra("tradeno", ordernumber);
        startActivity(intent);
    }
}
