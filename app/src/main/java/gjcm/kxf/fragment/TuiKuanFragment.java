package gjcm.kxf.fragment;

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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import gjcm.kxf.huifucenter.LoginActivity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.DetailAsyncTask;
import gjcm.kxf.tools.NetTools;
import gjcm.kxf.tools.OtherTools;
import gjcm.kxf.tools.PrintTools;

/**
 * Created by kxf on 2016/12/13.
 */
public class TuiKuanFragment extends Fragment implements View.OnClickListener, DetailAsyncTask.HttpTaskHandler {

    public static int RESULTCODE = 12, MSGWAHT = 10;
    private String usertoken, shouyy, blueadress;
    private ProgressDialog progressDialog;
    private TextView txtOrderAmount, txtOrderBody, txtDiscountAmount, txtRealPayAmount, txtPayTime, txtorderType, txtstatusText, txtShouyinyuan, txtOrderNumber;


    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("kxflog", "onActivityCreated--------------");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_layout, null);
        txtDiscountAmount = (TextView) view.findViewById(R.id.order_detail_damount);
        txtRealPayAmount = (TextView) view.findViewById(R.id.order_detail_rpay);
        txtOrderBody = (TextView) view.findViewById(R.id.order_detail_body);
        txtOrderAmount = (TextView) view.findViewById(R.id.order_detail_oamount);
        txtPayTime = (TextView) view.findViewById(R.id.order_detail_paytimer);
        txtorderType = (TextView) view.findViewById(R.id.order_detail_paytype);
        txtstatusText = (TextView) view.findViewById(R.id.order_detail_status);
        txtShouyinyuan = (TextView) view.findViewById(R.id.order_detail_shouyinyuan);
        txtOrderNumber = (TextView) view.findViewById(R.id.order_detail_number);
        Button btnTK = (Button) view.findViewById(R.id.order_detail_tuikuan);
        btnTK.setOnClickListener(this);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        usertoken = sharedPreferences.getString("usertoken", "");
        shouyy = sharedPreferences.getString("shouyy", "");
        blueadress = sharedPreferences.getString("blueadress", "");
        storeName = sharedPreferences.getString("storeName", "");
        if (usertoken.equals("")) {
            Toast.makeText(getContext(), "登录过期", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }
        showChoose();

        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.i("kxflog", "onViewStateRestored--------------");
    }

    private void showChoose() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = View
                .inflate(getContext(), R.layout.refound_choose, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog tuidialog = builder.create();
        TextView txtSaoma = (TextView) view.findViewById(R.id.refound_saoma);
        final EditText editNo = (EditText) view.findViewById(R.id.refound_editno);
        TextView txtShuru = (TextView) view.findViewById(R.id.refound_shuru);
        final TextView txtSubmit = (TextView) view.findViewById(R.id.refound_editsend);
        txtShuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNo.setVisibility(View.VISIBLE);
                txtSubmit.setVisibility(View.VISIBLE);
            }
        });
        txtSaoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tuidialog.dismiss();
//                startActivityForResult(new Intent(getContext(), CaptureActivity.class), 12);
            }
        });
        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tuidialog.show();


    }

    Handler tuikuanHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    progressDialog = ProgressDialog.show(getContext(), "", "正在查询...", true, false);
                    Bundle bundle = msg.getData();
                    String result = bundle.getString("result");
                    getDetail(result);
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        showAlertRefoud();
//        startActivityForResult(new Intent(getContext(), CaptureActivity.class), 12);
    }

    private DetailAsyncTask asyncTask;

    private void getDetail(String tradeno) {
        if (asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
        asyncTask = new DetailAsyncTask();
        asyncTask.execute("11", usertoken, tradeno);
        asyncTask.setTaskHandler(this);

    }

    public Handler getHandler() {
        return tuikuanHander;
    }

    private String orderNumber, orderAmount, discountAmount, storeName, statusText, paytime;

    @Override
    public void taskSuccessful(String json) {
//        Log.i("kxflog", json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            String success = jsonObject.getString("success");
            if (!success.equals("true"))
                return;
            JSONObject data = jsonObject.getJSONObject("data");
            orderid = data.optString("id");
            Log.i("kxflog", "orderid:" + orderid);
            String orderBody = data.getString("orderBody");
            orderAmount = data.getString("orderAmount");
            orderNumber = data.getString("orderNumber");
            discountAmount = data.getString("discountAmount");
            String realPayAmount = data.getString("realPayAmount");
            paytime = data.getString("payTime");
            String orderType = data.getString("orderType");
            statusText = data.getString("statusText");

            amount = realPayAmount;
            if (paytime.equals("")) {
            } else {
                long timestimp = Long.parseLong(paytime);
                Date date = new Date(timestimp);
                paytime = simpleDateFormat.format(date);
            }
            txtDiscountAmount.setText(discountAmount);
            txtOrderAmount.setText(orderAmount);
            txtOrderBody.setText(orderBody);
            txtOrderNumber.setText(orderNumber);
            txtorderType.setText(orderType);
            txtPayTime.setText(paytime);
            txtRealPayAmount.setText(realPayAmount);
            txtstatusText.setText(statusText);

        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
    }

    private String orderid, amount;

    private void tuiKuan(final String pwd, final String free) {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(getContext(), "", "正在退款...", true, false);
        final String url = NetTools.HOMEURL+"/pay/wx-pay-refund-self-store";
        new Thread() {
            @Override
            public void run() {
                OtherTools otherTools = new OtherTools();
                try {
                    String tkmsg = otherTools.tuikuanByPost(url, usertoken, orderid, pwd, free);
                    progressDialog.dismiss();
                    if (tkmsg.equals("{\"success\":true}")) {
//                        Toast.makeText(getContext(),"成功退款",Toast.LENGTH_SHORT).show();
                        printData(free);
                    }else {
//                        Toast.makeText(getContext(),"退款失败",Toast.LENGTH_SHORT).show();
                    }
                    Log.i("kxflog", tkmsg);
                } catch (Throwable throwable) {
                    progressDialog.dismiss();
                    throwable.printStackTrace();
                }
            }
        }.start();
    }

    private void showAlertRefoud() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = View
                .inflate(getContext(), R.layout.tuikuan_alert, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog tuidialog = builder.create();
        Button button = (Button) view.findViewById(R.id.alert_tuikuanbtn);
        TextView txtShow = (TextView) view.findViewById(R.id.alert_txtshow);
        txtShow.setText(amount);
        TextView txtAll = (TextView) view.findViewById(R.id.alert_txtall);
        final EditText editMonery = (EditText) view.findViewById(R.id.alert_editmonery);
        final EditText editPwd = (EditText) view.findViewById(R.id.alert_tuikuanpwd);
        txtAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editMonery.setText(amount);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editm = editMonery.getText().toString();
                String editpwd = editPwd.getText().toString();
                if (editm.equals("") || editpwd.equals("")) {
                } else {
                    tuidialog.dismiss();
                    tuiKuan(editpwd, editm);
                }
            }
        });
        tuidialog.show();

    }

    @Override
    public void taskFailed() {
        Log.i("kxflog", "shibai");
        progressDialog.dismiss();
    }

    private void printData(final String ztk) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            if (!"".equals(blueadress)) {
                final PrintTools printTools = new PrintTools(getContext(), blueadress);
                if (printTools.connect()) {
                    new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            printTools.printRefoundMonery(storeName, shouyy, orderNumber, orderAmount, statusText, ztk, discountAmount, paytime);
                        }
                    }.start();
                } else {
                    Toast.makeText(getContext(), "打印机连接失败，请检查打印机", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
