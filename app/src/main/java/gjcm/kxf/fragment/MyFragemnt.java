package gjcm.kxf.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import gjcm.kxf.forgetpwd.ChangePwdActiviy;
import gjcm.kxf.huifucenter.AbuoutActivity;
import gjcm.kxf.huifucenter.BluetoothSeeting;
import gjcm.kxf.huifucenter.JieSuanActivity;
import gjcm.kxf.huifucenter.LoginActivity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.DetailAsyncTask;
import gjcm.kxf.tools.NetTools;
import gjcm.kxf.tools.OtherTools;
import gjcm.kxf.tools.PrintTools;


/**
 * Created by kxf on 2016/12/13.
 */
public class MyFragemnt extends Fragment implements View.OnClickListener {
    private TextView txtTitle, txtstorname, txtusertype, txtuname;
    private ProgressDialog progressDialog;
    private String usertoken, storename, usetype, strurl, ordernumber, storesyy, orderid;
    private Context context;
    private Button loginout;
    private String servicename, servicephone;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_fragment, null);
        context = getContext();
        innitView(view);
        return view;
    }

    private void showMeroory() {
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(info);
        Log.i("kxflog", "系统剩余内存:" + (info.availMem >> 10) + "k");
        Log.i("kxflog", "系统是否处于低内存运行：" + info.lowMemory);
        Log.i("kxflog", "当系统剩余内存低于" + info.threshold + "时就看成低内存运行");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        showMeroory();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        storename = sharedPreferences.getString("storeName", "");
        servicename = sharedPreferences.getString("sername", "");
        servicephone = sharedPreferences.getString("serphone", "");
        usertoken = sharedPreferences.getString("usertoken", "");
        usetype = sharedPreferences.getString("usertype", "");
        storesyy = sharedPreferences.getString("shouyy", "");
//        Log.i("kxflog", "MyFragemnt->usetype:" + usetype);
        txtstorname.setText(storename);
        txtuname.setText(storesyy);
        if (usetype.equals("1")) {
            usetype = "店长";
            strurl = "/order/app/store-info";
        } else if (usetype.equals("0")) {
            strurl = "/order/app/info";
            usetype = "商户";
        } else {
            strurl = "/order/app/clerk-info";
            usetype = "店员";
        }
        txtusertype.setText(usetype);

    }

    private void innitView(View view) {
        txtstorname = (TextView) view.findViewById(R.id.my_fragment_storename);
        txtusertype = (TextView) view.findViewById(R.id.my_fragment_usetype);
        txtuname = (TextView) view.findViewById(R.id.my_fragment_usename);
        loginout = (Button) view.findViewById(R.id.myfragment_loginout);
        loginout.setOnClickListener(this);
        RelativeLayout seting = (RelativeLayout) view.findViewById(R.id.myfragment_setingblue);
        seting.setOnClickListener(this);
        txtTitle = (TextView) view.findViewById(R.id.title_storename);
        txtTitle.setText("我的");
        txtTitle.setTextSize(18);
        txtTitle.setVisibility(View.VISIBLE);
        RelativeLayout tuikuanliner = (RelativeLayout) view.findViewById(R.id.my_fragment_tuikuan);
        tuikuanliner.setOnClickListener(this);
        RelativeLayout callliner = (RelativeLayout) view.findViewById(R.id.my_fragment_call);
        callliner.setOnClickListener(this);
        RelativeLayout shangwu = (RelativeLayout) view.findViewById(R.id.my_shangwu);
        shangwu.setOnClickListener(this);
        RelativeLayout about = (RelativeLayout) view.findViewById(R.id.my_fragment_aboutliner);
        about.setOnClickListener(this);
        RelativeLayout jiesuan = (RelativeLayout) view.findViewById(R.id.my_jiesuan);
        jiesuan.setOnClickListener(this);
        RelativeLayout changePwd = (RelativeLayout) view.findViewById(R.id.my_changepwd);
        changePwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.myfragment_loginout:
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("usertoken", "");
                editor.commit();
                intent = new Intent(context, LoginActivity.class);
                intent.putExtra("isout", "yes");
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.myfragment_setingblue:
                startActivity(new Intent(context, BluetoothSeeting.class));
                break;
            case R.id.my_fragment_tuikuan:
                if (usetype.equals("店员")) {
                    Toast.makeText(context, "店员不能退款", Toast.LENGTH_SHORT).show();

                } else {
                    intent = new Intent(context, MyScanActivity.class);
                    intent.putExtra("iscard", "1");
                    startActivityForResult(intent, 12);
                }
                break;
            case R.id.my_fragment_call:
                Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + "4008267100"));
                startActivity(phoneIntent);
                break;
            case R.id.my_fragment_aboutliner:
                Intent aboutintent = new Intent(context, AbuoutActivity.class);
                startActivity(aboutintent);
                break;
            case R.id.my_shangwu:
                showServiceCall();
                break;
            case R.id.my_jiesuan:
                Intent jiesuan = new Intent(context, JieSuanActivity.class);
                startActivity(jiesuan);
                break;
            case R.id.my_changepwd:
                startActivity(new Intent(context, ChangePwdActiviy.class));
                break;
        }

    }

    Handler tuikuanHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    progressDialog = ProgressDialog.show(context, "", "正在查询...", true, false);
                    Bundle bundle = msg.getData();
                    String result = bundle.getString("result");
                    getDetail(result);
                    break;

                case 14:
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    String str = msg.getData().getString("msg");
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                    break;
                case 18:
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    Toast.makeText(context, "不支持退款", Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    Bundle b = msg.getData();
                    String free = b.getString("free");
                    String strmsg = b.getString("strmsg");
                    if (strmsg.equals("1")) {
                        Toast.makeText(context, "成功退款", Toast.LENGTH_SHORT).show();
                        printData(free);
                    } else {
                        Toast.makeText(context, strmsg, Toast.LENGTH_SHORT).show();

                    }

                    break;
            }
        }
    };
    private DetailAsyncTask asyncTask;

    public Handler getHandler() {
        return tuikuanHander;
    }

    private void getDetail(String tradeno) {
//        if (asyncTask != null) {
//            asyncTask.cancel(true);
//            asyncTask = null;
//        }
//        asyncTask = new DetailAsyncTask();
//        asyncTask.execute(strurl, usertoken, tradeno);
//        asyncTask.setTaskHandler(this);

        RequestParams params = new RequestParams(NetTools.HOMEURL + strurl);
        params.addHeader("token", usertoken);
        params.setConnectTimeout(10 * 1000);
        params.setAsJsonContent(true);
        String bodycon = "{" +
                "\"orderNumber\":" + "\"" + tradeno + "\"" +
                "}";
        params.setBodyContent(bodycon);
        params.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        Log.i("kxflog", "queryOrder params:" + params.getBodyContent());
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                taskSuccessful(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                String msg = ex.getMessage();
                taskFailed(msg);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private void taskSuccessful(String json) {
//        Log.i("kxflog", json);
        if (progressDialog != null)
            progressDialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String success = jsonObject.getString("success");
            if (!success.equals("true")) {
                success = jsonObject.getString("err_msg");
                Bundle b = new Bundle();
                b.putString("msg", success);
                Message me = new Message();
                me.setData(b);
                me.what = 14;
                tuikuanHander.sendMessage(me);
//                Toast.makeText(context, success, Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject tjson = jsonObject.getJSONObject("data");
            JSONObject data = tjson.getJSONObject("orderInfoDto");
            orderid = data.optString("id");
            ordernumber = data.optString("orderNumber");
            String status = data.optString("status");
            paytime = data.getString("payTime");
            discountAmount = data.getString("discountAmount");
            orderAmount = data.optString("orderAmount");
            /*if (!status.equals("1")) {
                tuikuanHander.sendEmptyMessage(18);
                return;
            }*/
            DecimalFormat df3 = new DecimalFormat("0.00");
            Double sumRefundAmount = data.optDouble("sumRefundAmount");
            Double refoundAmount = Double.parseDouble(orderAmount) - sumRefundAmount;
            String tuiam = df3.format(refoundAmount);
//            String realPayAmount = data.getString("realPayAmount");
            showAlertRefoud(orderid, tuiam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String paytime, discountAmount, orderAmount;

    private void showAlertRefoud(String orderid, final String tuiam) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View
                .inflate(getContext(), R.layout.tuikuan_alert, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog tuidialog = builder.create();
        Button button = (Button) view.findViewById(R.id.alert_tuikuanbtn);
        Button cancelbtn = (Button) view.findViewById(R.id.alert_cancelbtn);
        TextView txtShow = (TextView) view.findViewById(R.id.alert_txtshow);
        txtShow.setText(tuiam);
        TextView txtAll = (TextView) view.findViewById(R.id.alert_txtall);
        final EditText editMonery = (EditText) view.findViewById(R.id.alert_editmonery);
        final EditText editPwd = (EditText) view.findViewById(R.id.alert_tuikuanpwd);
        txtAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editMonery.setText(tuiam);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editm = editMonery.getText().toString();
                float zt = Float.parseFloat(editm);
                String editpwd = editPwd.getText().toString();
                if (editm.equals("") || editpwd.equals("")) {
                } else {
                    tuidialog.dismiss();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (asyncTask != null)
            asyncTask.cancel(true);
        tuikuanHander.removeCallbacksAndMessages(null);
    }

    private void tuiKuan(final String pwd, final String free) {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(context, "", "正在退款...", true, false);
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
                        strmsg = "1";
                    } else {
                        strmsg = jsonObject.opt("err_msg").toString();
                    }
//                        String strmsg = "";
//                    if (tkmsg.equals("{\"success\":true}")) {
//                        strmsg = "1";
////                        Toast.makeText(getContext(),"成功退款",Toast.LENGTH_SHORT).show();
////                        printData(free);
//                    } else {
//                        strmsg = "2";
////                        Toast.makeText(getContext(),"退款失败",Toast.LENGTH_SHORT).show();
//                    }
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("free", free);
                    bundle.putString("strmsg", strmsg);
                    message.what = 8;
                    message.setData(bundle);
                    tuikuanHander.sendMessage(message);
                    Log.i("kxflog", tkmsg);
                } catch (Throwable throwable) {
                    progressDialog.dismiss();
                    throwable.printStackTrace();
                }
            }
        }.start();
    }

    private void taskFailed(String msg) {
        if (progressDialog != null)
            progressDialog.dismiss();
        Toast.makeText(context, "异常:" + msg, Toast.LENGTH_SHORT).show();
    }

    private void showServiceCall() {
        final AlertDialog.Builder callAlert = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.contact_service_dialog, null);
        TextView callname = (TextView) view.findViewById(R.id.contact_ser_ywy);
        ImageView callbtn = (ImageView) view.findViewById(R.id.contact_ser_call);
        callname.setText("业务员：" + servicename);
        TextView phone = (TextView) view.findViewById(R.id.contact_number);
        phone.setText(servicephone);
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (("").equals(servicephone)) {
                    Toast.makeText(context, "未找到业务员的联系方式", Toast.LENGTH_SHORT).show();
                } else {
                    Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + servicephone));
                    startActivity(phoneIntent);
                }
            }
        });
        callAlert.setView(view);
        callAlert.show();

    }

    private void printData(final String ztk) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        String blueadress = sharedPreferences.getString("blueadress", "");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        String isprint = sharedPreferences.getString("isprint", "");
        if (("").equals(isprint)) {
            return;
        } else if (("on").equals(isprint)) {
        } else
            return;
        if (bluetoothAdapter.isEnabled()) {
            if (!"" .equals(blueadress)) {
                final PrintTools printTools = new PrintTools(context, blueadress);
                if (printTools.connect()) {
                    new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            printTools.printRefoundMonery(storename, storesyy, ordernumber, orderAmount, "退款成功", ztk, discountAmount, paytime);
                        }
                    }.start();
                } else {
                    Toast.makeText(context, "打印机连接失败，请检查打印机", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
