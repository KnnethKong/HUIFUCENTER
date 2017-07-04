package gjcm.kxf.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.NetTools;
import gjcm.kxf.wheelview.DatePickerDialog;
import library.PullToRefreshBase;
import library.PullToRefreshScrollView;

/**
 * 对账->UI
 * Created by kxf on 2016/12/27.
 */
public class ReconciliationFragment extends Fragment implements View.OnClickListener {
    private TextView titlename, txtsjss, xtxtskje, txtssje, txtddje, txtddbs, txttkje, txttkbs, txtfwf, txtdiscount;
    private TextView zfbAm, zfbReAm, zfbCount, wchatAm, wchatReAm, wchatCount, zfbshss, wxshss;
    private Button storeSpiner, syySpiner;
    private Button dateSpiner;
    private String usertoken, usetype;
    private ProgressDialog dialog;
    private Context context;
    private int storeid = 0, storeuseid = 0;
    private PullToRefreshScrollView pullToRefreshScrollView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reconciloation_layout, null);
        context = getContext();
        initview(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        showMeroory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pullToRefreshScrollView = null;
        reconhandler.removeCallbacksAndMessages(null);
    }

    android.os.Handler reconhandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result;
            switch (msg.what) {
                case 6:
                    result = bundle.getString("result");
                    storeid = bundle.getInt("storeid");
                    storeSpiner.setText(result);
                    break;
                case 12:
                    result = bundle.getString("result");
                    storeuseid = bundle.getInt("storeuserid");
                    syySpiner.setText(result);
                    getOrder(payStartTime, payEndTime, storeid, storeuseid);
                    break;
            }

        }
    };

    public Handler getHandler() {
        return reconhandler;
    }

    private void initview(View view) {
        dialog = ProgressDialog.show(context, "", "正在查询", false, false);
        titlename = (TextView) view.findViewById(R.id.title_storename);
        titlename.setVisibility(View.VISIBLE);
        titlename.setText("对账");
        titlename.setTextSize(18);
        storeSpiner = (Button) view.findViewById(R.id.recon_spinerstore);
        syySpiner = (Button) view.findViewById(R.id.recon_spinershouyy);
        dateSpiner = (Button) view.findViewById(R.id.recon_spinerdate);
        txtddbs = (TextView) view.findViewById(R.id.recon_txtordernum);
        txtddje = (TextView) view.findViewById(R.id.recon_txtorderam);
        txtsjss = (TextView) view.findViewById(R.id.recon_txtshangjiashishou);
        txtssje = (TextView) view.findViewById(R.id.recon_txtshishoujine);
//        xtxtskje = (TextView) view.findViewById(R.id.recon_txtkaxiaofei);
        txttkbs = (TextView) view.findViewById(R.id.recon_txtrenum);
        txttkje = (TextView) view.findViewById(R.id.recon_txtream);
        txtfwf = (TextView) view.findViewById(R.id.recon_txtfwf);
        txtdiscount = (TextView) view.findViewById(R.id.recon_txtdiscount);
        zfbAm = (TextView) view.findViewById(R.id.recon_txtzfb);
        zfbCount = (TextView) view.findViewById(R.id.recon_txtzfbcount);
        zfbReAm = (TextView) view.findViewById(R.id.recon_txtzfbrefound);
        wchatAm = (TextView) view.findViewById(R.id.recon_txtwx);
        wchatCount = (TextView) view.findViewById(R.id.recon_txtwxcount);
        wchatReAm = (TextView) view.findViewById(R.id.recon_txtwxrefound);
        zfbshss = (TextView) view.findViewById(R.id.recon_txtzfbss);
        wxshss = (TextView) view.findViewById(R.id.recon_txtwxss);
        dateSpiner.setOnClickListener(this);
        pullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.recon_scroll);
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                dialog = ProgressDialog.show(context, "", "正在查询", false, false);
                if (usetype.equals("0")) {
                    storeid = 0;
                    storeuseid = 0;
                }
                if (storeid != 0 && storeuseid != 0) {
                    getOrder(payStartTime, payEndTime, storeid, storeuseid);
                } else {
                    getOrder(payStartTime, payEndTime, storeid, storeuseid);
                }
            }
        });
        pullToRefreshScrollView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
        pullToRefreshScrollView.getLoadingLayoutProxy().setPullLabel("别再拉了");
        pullToRefreshScrollView.getLoadingLayoutProxy().setReleaseLabel("松开加载...");
        final boolean isnet = NetTools.isNetworkConnected(getContext());
        if (!isnet) {
            if (dialog != null)
                dialog.dismiss();
            Toast.makeText(getContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
        }

        storeSpiner.setOnClickListener(this);
        syySpiner.setOnClickListener(this);
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(now.getTimeInMillis());
        payStartTime = str + " 00:00";
        payEndTime = str + " 23:59";
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        usertoken = sharedPreferences.getString("usertoken", "");
        usetype = sharedPreferences.getString("usertype", "");
        if (usetype.equals("1")) {
            String str1 = sharedPreferences.getString("storeId", "");
            storeid = Integer.parseInt(str1);
            String storeUserId = sharedPreferences.getString("storeUserId", "");
            storeuseid = Integer.parseInt(storeUserId);
        } else if (usetype.equals("2")) {
            String str1 = sharedPreferences.getString("storeId", "");
            storeid = Integer.parseInt(str1);
            String storeUserId = sharedPreferences.getString("storeUserId", "");
            storeuseid = Integer.parseInt(storeUserId);
        } else {
            storeuseid = 0;
            storeid = 0;
        }

        getOrder(payStartTime, payEndTime, storeid, storeuseid);

    }

    private void showData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            String issuc = jsonObject.optString("success");
            if (issuc.equals("false")) {
                Toast.makeText(context, "查询失败", Toast.LENGTH_SHORT).show();//
                return;
            }
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject merchantCountOrderCommon = data.getJSONObject("merchantCountOrderCommon");
            String totalAmount = merchantCountOrderCommon.optString("totalAmount");
            String totalOrderCount = merchantCountOrderCommon.optString("totalOrderCount");
            String refundAmount = merchantCountOrderCommon.optString("refundAmount");
            String realPayAmount = merchantCountOrderCommon.optString("realPayAmount");
            String discountAmount = merchantCountOrderCommon.optString("discountAmount");
            String merchantTotalAmount = merchantCountOrderCommon.optString("merchantTotalAmount");
            String refundCount = merchantCountOrderCommon.optString("refundCount");
            String serviceAmount = merchantCountOrderCommon.optString("serviceAmount");
            String cardTotalAmount = merchantCountOrderCommon.optString("cardTotalAmount");
            String wxPaySum = merchantCountOrderCommon.optString("wxPaySum");
            String wxPayPaidAmount = merchantCountOrderCommon.optString("wxPayPaidAmount");
            String aliPayPaidAmount = merchantCountOrderCommon.optString("aliPayPaidAmount");
            String wxRefundSum = merchantCountOrderCommon.optString("wxRefundSum");
            String wxPayCount = merchantCountOrderCommon.optString("wxPayCount");
            String AliPaySum = merchantCountOrderCommon.optString("aliPaySum");
            String AliPayRefundSum = merchantCountOrderCommon.optString("aliPayRefundSum");
            String aliPayCount = merchantCountOrderCommon.optString("aliPayCount");
            aliPayPaidAmount = "".equals(aliPayPaidAmount) ? "0.00" : aliPayPaidAmount;
            wxPayPaidAmount = "".equals(wxPayPaidAmount) ? "0.00" : wxPayPaidAmount;

            realPayAmount = "".equals(realPayAmount) ? "0.00" : realPayAmount;
            refundAmount = "".equals(refundAmount) ? "0.00" : refundAmount;
            totalOrderCount = "".equals(totalOrderCount) ? "0" : totalOrderCount;
            discountAmount = "".equals(discountAmount) ? "0.00" : discountAmount;
            refundCount = "".equals(refundCount) ? "0" : refundCount;
            serviceAmount = "".equals(serviceAmount) ? "0.00" : serviceAmount;
//                    cardTotalAmount = "".equals(cardTotalAmount) ? "0.00" : cardTotalAmount;
            wxPaySum = "".equals(wxPaySum) ? "0.00" : wxPaySum;
            wxRefundSum = "".equals(wxRefundSum) ? "0.00" : wxRefundSum;
            wxPayCount = "".equals(wxPayCount) ? "0" : wxPayCount;
            AliPaySum = "".equals(AliPaySum) ? "0.00" : AliPaySum;
            AliPayRefundSum = "".equals(AliPayRefundSum) ? "0.00" : AliPayRefundSum;
            aliPayCount = "".equals(aliPayCount) ? "0" : aliPayCount;
            txtssje.setText(realPayAmount + " 元");
            txttkje.setText(refundAmount + " 元");
            txtddbs.setText(totalOrderCount);
            txtddje.setText(totalAmount + " 元");
            txtdiscount.setText(discountAmount + " 元");
//                    txtddbs.setText(merchantTotalAmount);
            txtsjss.setText(merchantTotalAmount + " 元");
            txttkbs.setText(refundCount);
            txtfwf.setText(serviceAmount + " 元");
            float faliam = Float.valueOf(AliPaySum);
            if (faliam > 0) {
                zfbCount.setText(aliPayCount);
                zfbReAm.setText(AliPayRefundSum + " 元");
                zfbAm.setText(AliPaySum + " 元");
            }
            wchatAm.setText(wxPaySum + " 元");
            wchatCount.setText(wxPayCount);
            wchatReAm.setText(wxRefundSum + " 元");
            zfbshss.setText(aliPayPaidAmount + " 元");
            wxshss.setText(wxPayPaidAmount + " 元");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getOrder(String payStartTime, String payEndTime, int storeid, int storeuserid) {
        Log.e("kxflog", storeid + "  " + storeuserid);
        RequestParams requestParams = new RequestParams(NetTools.HOMEURL + "/manager-order/searchCount");
        requestParams.addHeader("token", usertoken);
        String params = "{" +//"storeid":0,"storeuserid":0
                "\"dto\":{" + "\"payStartTime\":" + "\"" + payStartTime + "\"" + ",\"payEndTime\":" + "\"" + payEndTime + "\"" + ",\"realname\":" + null +
                ",\"status\":" + -1 + ",\"storeId\":" + storeid + ",\"storeUserId\":" + storeuserid + "}," +
                "\"page\":{" + "\"pageNO\":" + 1000 + ",\"everyPageCount\":" + 1 + "}" +
                "}";
        requestParams.setAsJsonContent(true);
        requestParams.setConnectTimeout(10 * 1000);
        requestParams.setBodyContent(params);

        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                pullToRefreshScrollView.onRefreshComplete();
                if (dialog != null)
                    dialog.dismiss();
//                Log.i("kxflog", "onSuccess--------" + result);
                showData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                pullToRefreshScrollView.onRefreshComplete();
                if (dialog != null)
                    dialog.dismiss();
                Toast.makeText(context, "获取信息错误", Toast.LENGTH_SHORT).show();
                showNull();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                pullToRefreshScrollView.onRefreshComplete();
                if (dialog != null)
                    dialog.dismiss();
                showNull();
            }

            @Override
            public void onFinished() {
                if (dialog != null)
                    dialog.dismiss();

            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });

    }

    private void showNull() {
        txtssje.setText("0.00 元");
        txttkje.setText("0.00 元");
        txtddbs.setText("0");
        txtddje.setText("0.00 元");
        txtdiscount.setText("0.00 元");
        txtsjss.setText("0.00 元");
        txttkbs.setText("0");
        txtfwf.setText("0.00 元");
        xtxtskje.setText("0.00 元");
        zfbCount.setText("0");
        zfbReAm.setText("0.00 元");
        zfbAm.setText("0.00元");
        wchatAm.setText("0.00 元");
        wchatCount.setText("0");
        wchatReAm.setText("0.00 元");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("kxflog", "onResume:" + usertoken);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("kxflog", "onStop:" + usertoken);
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.recon_spinerstore:
                intent = new Intent(context, ReconciliationSearch.class);
                intent.putExtra("token", usertoken);
                startActivityForResult(intent, 5);
                break;
            case R.id.recon_spinershouyy:
                if (storeid != 0) {
//                    Toast.makeText(context, "请选择门店", Toast.LENGTH_SHORT).show();
                    intent = new Intent(context, ReconUserSearch.class);
                    intent.putExtra("token", usertoken);
                    intent.putExtra("storeid", storeid);
                    startActivityForResult(intent, 6);
                }
                break;
            case R.id.recon_spinerdate:
                if (storeuseid != 0 && storeid != 0)
                    showDate();
                break;
        }
    }


    private String payEndTime, payStartTime;
    private void showDate() {
        DatePickerDialog mChangeBirthDialog = new DatePickerDialog(
                context);
        mChangeBirthDialog.show();
        mChangeBirthDialog.setDatePickListener(new DatePickerDialog.OnDatePickListener() {

            @Override
            public void onClick(String beiginyear, String beiginmonth, String beiginday, String endyear, String endmonth, String endday, String behours, String endhour) {
                if (dialog != null)
                    dialog.dismiss();
                dialog = ProgressDialog.show(context, "", "正在查询账单...", true, false);
                payStartTime = beiginyear + "-" + beiginmonth + "-" + beiginday + " " + behours + ":00";
                payEndTime = endyear + "-" + endmonth + "-" + endday + " " + endhour + ":59";
                if (usetype.equals("0")) {
                    storeid = 0;
                    storeuseid = 0;
                }
                getOrder(payStartTime, payEndTime, storeid, storeuseid);
                String payend = payEndTime.substring(0, 10);
                String paystart = payStartTime.substring(0, 10);
                dateSpiner.setText(paystart + "\n" + payend);
            }

            @Override
            public void date(String begin, String end) {
                if (dialog != null)
                    dialog.dismiss();
                dialog = ProgressDialog.show(context, "", "正在查询账单...", true, false);
                payStartTime = begin + " 00:00";
                payEndTime = end + " 23:59";
                if (usetype.equals("0")) {
                    storeid = 0;
                    storeuseid = 0;
                }
                getOrder(payStartTime, payEndTime, storeid, storeuseid);
                dateSpiner.setText(begin + "\n" + end);
            }
        });
    }


}
