package gjcm.kxf.goodorder;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.NetTools;

/**
 * Created by kxf on 2017/2/28.
 * 点餐营业汇总
 */
public class StoreSummaryActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private TextView txtDateMsg, txtAm, txtOrdernum, txtPerson, txtRetreat, txtDiscount;
    private String begintime, endtime, storeid;
    private LinearLayout dataLiner;
    private RelativeLayout riRelative, qujianRelative;
    private int operate;
    private TextView txtNormal, txtRi, txtQujian, txtEn, txtBegin;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.store_sum_layout);
        initbar();
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Context.MODE_PRIVATE);
        storeid = sharedPreferences.getString("storeId", null);
        TextView txtPrint = (TextView) findViewById(R.id.store_txt_print);
        TextView txtDate = (TextView) findViewById(R.id.store_txt_date);
        txtDateMsg = (TextView) findViewById(R.id.store_txt_tmsg);
        txtAm = (TextView) findViewById(R.id.store_sum_sum);
        txtOrdernum = (TextView) findViewById(R.id.store_sum_oredrnum);
        txtPerson = (TextView) findViewById(R.id.store_sum_person);
        txtRetreat = (TextView) findViewById(R.id.store_sum_refound);
        txtDiscount = (TextView) findViewById(R.id.store_sum_youhui);
        dataLiner = (LinearLayout) findViewById(R.id.store_date_liner);
        txtRi = (TextView) findViewById(R.id.store_sum_ri);
        txtRi.setOnClickListener(this);
        txtQujian = (TextView) findViewById(R.id.store_sum_qujian);
        txtQujian.setOnClickListener(this);
        txtPrint.setOnClickListener(this);
        txtDate.setOnClickListener(this);
        riRelative = (RelativeLayout) findViewById(R.id.store_sum_riliner);
        qujianRelative = (RelativeLayout) findViewById(R.id.store_sum_qujianliner);
        txtBegin = (TextView) findViewById(R.id.store_sum_be);
        txtBegin.setOnClickListener(this);
        txtEn = (TextView) findViewById(R.id.store_sum_en);
        txtEn.setOnClickListener(this);
        findViewById(R.id.store_sum_select).setOnClickListener(this);
        // findViewById(R.id.store_sum_jian).setOnClickListener(this);
        // findViewById(R.id.store_sum_add).setOnClickListener(this);
        txtNormal = (TextView) findViewById(R.id.store_sum_nomarl);
        txtNormal.setOnClickListener(this);
        int nowyear = c.get(Calendar.YEAR);
        int nowmonth = c.get(Calendar.MONTH) + 1;
        int nowday = c.get(Calendar.DAY_OF_MONTH);
        String strday = nowyear + "-" + nowmonth + "-" + nowday;
        begintime = strday;
        endtime = strday;
        // strday += "营业额（元）";
        txtDateMsg.setText(strday);
        txtNormal.setText(nowmonth + "-" + nowday);
        getData();
    }


    private void initbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tb);
        TextView ttile = (TextView) findViewById(R.id.toolbar_txt);
        ttile.setText("营业汇总");
        setSupportActionBar(toolbar);
        findViewById(R.id.toolbar_remind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StoreSummaryActivity.this, RemindActivity.class));
            }
        });
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreSummaryActivity.this.finish();
            }
        });
    }

    private Calendar c = Calendar.getInstance();

    private void showDate() {
        int nowyear = c.get(Calendar.YEAR);
        int nowmonth = c.get(Calendar.MONTH);
        int nowday = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, nowyear, nowmonth, nowday);
        datePickerDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.store_txt_date:
                int visi = dataLiner.getVisibility();
                if (visi == View.VISIBLE)
                    dataLiner.setVisibility(View.GONE);
                else {
                    dataLiner.setVisibility(View.VISIBLE);
                    qujianRelative.setVisibility(View.GONE);
                    riRelative.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.store_txt_print:
                break;
            case R.id.store_sum_ri:
                txtQujian.setTextColor(Color.parseColor("#333333"));
                txtRi.setTextColor(Color.parseColor("#3298da"));
                qujianRelative.setVisibility(View.GONE);
                riRelative.setVisibility(View.VISIBLE);
                break;
            case R.id.store_sum_qujian:
                txtRi.setTextColor(Color.parseColor("#333333"));
                txtQujian.setTextColor(Color.parseColor("#3298da"));
                riRelative.setVisibility(View.GONE);
                qujianRelative.setVisibility(View.VISIBLE);
                break;
            case R.id.store_sum_be:
                operate = 1;
                showDate();
                break;
            case R.id.store_sum_en:
                operate = 2;
                showDate();
                break;
            case R.id.store_sum_nomarl:
                operate = 3;
                showDate();
                break;
            case R.id.store_sum_select:
                String s1 = txtEn.getText().toString();
                String s2 = txtBegin.getText().toString();
                if (s1 == null || s1.length() < 5) {
                    return;
                }
                if (s2 == null || s2.length() < 5) {
                    return;
                }
                String[] ends = s1.split("-");
                String[] begs = s2.split("-");
                try {
                    Date edate = dateFormat.parse(s1);
                    Date bdate = dateFormat.parse(s2);
                    if (bdate.getTime() > edate.getTime()) {
                        Toast.makeText(StoreSummaryActivity.this, "开始时间应该小于结束时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    Toast.makeText(StoreSummaryActivity.this, "时间转化异常", Toast.LENGTH_SHORT).show();
                  //  e.printStackTrace();
                }
                begintime=s2;
                endtime=s1;
                txtDateMsg.setText(begs[1] + "-" + begs[2] + " 至 " + ends[1] + "-" + ends[2]);
                getData();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 20) {
            begintime = data.getStringExtra("begintime");
            endtime = data.getStringExtra("endtime");
            txtDateMsg.setText(begintime + "至" + endtime);

            //   getData();
        }

    }

    public void qiantaiPrint() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] initprint = new byte[]{27, 64};//初始化
        byte[] leftcenter = new byte[]{27, 97, 0};//靠左
        byte[] bigstyle = new byte[]{27, 69, 0xf};//加粗模式
        byte[] nobigstyle = new byte[]{27, 33};//取消加粗模式
        byte[] bigtxt = new byte[]{29, 33, 17};//da
        byte[] center = new byte[]{27, 97, 1};//居中
        byte[] cutall = new byte[]{29, 86, 65, 0};//切纸
        byte[] newline = new byte[]{10};//换行
        try {
            buffer.write(initprint);
            String title = "-------------营业额--------------\n";
            buffer.write(center);
            buffer.write(title.getBytes("gbk"));
            buffer.write(leftcenter);
            String am = "交易额:   " + sumam + "\n" + "总人数:   " + person + "\n" + "总订单数:   " + count + "\n";
            String date = "开始时间:   " + begintime + " 00:00" + "\n" + "结束时间:   " + endtime + " 23:59" + "\n";
            buffer.write(am.getBytes("gbk"));
            buffer.write(date.getBytes("gbk"));
            buffer.write("\n".getBytes("gbk"));
            buffer.write(cutall);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String ipaddress = "192.168.1.199";
        BufferedWriter bufferwriter;
        BufferedReader bufferreader;
        try {
            SocketAddress ipe = new InetSocketAddress(Inet4Address.getByName(ipaddress), 9100);
            Socket socket = new Socket();
            socket.connect(ipe, 1800);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(buffer.toByteArray());
            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    int count = 0, person = 0, sumam = 0, refound = 0;

    //{"orecount":1,"sid":111114,"oam":59900,"etime":"2017-03-13 23:59","ocount":3,"btime":"2017-03-13 00:00","operson":7}
    private ProgressDialog progressDialog;

    private void getData() {
        if (progressDialog != null)
            progressDialog.dismiss();
        progressDialog = ProgressDialog.show(this, "", "正在查询...", true, false);
        count = 0;
        person = 0;
        sumam = 0;
        Log.e("kxflog",begintime+endtime);
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "OrderOperate/QueryServing");
        requestParams.addBodyParameter("id", storeid);
        requestParams.addBodyParameter("begintime", begintime + " 00:00");
        requestParams.addBodyParameter("endtime", endtime + " 23:59");
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("kxflog", result);
                if (progressDialog != null)
                    progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    sumam = jsonObject.getInt("oam") ;
                    count = jsonObject.getInt("ocount");
                    person = jsonObject.getInt("operson");
                    //  refound = jsonObject.getInt("orecount");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                txtAm.setText("￥ " + sumam + "");
                txtPerson.setText(person + "");
                txtOrdernum.setText(count + "");
                // txtRetreat.setText(refound + "");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                Log.e("kxflog", ex.getMessage());
                Snackbar.make(txtDateMsg, "连接服务器异常", Snackbar.LENGTH_SHORT).show();
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String str = (year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        switch (operate) {
            case 1:
                txtBegin.setText(str);
                begintime = str;
                //   Toast.makeText(StoreSummaryActivity.this, begintime + "\n" + endtime, Toast.LENGTH_SHORT).show();
                break;
            case 2:
                txtEn.setText(str);
                endtime = str;
                break;
            case 3:
                begintime = str;
                endtime = str ;
                txtNormal.setText((monthOfYear + 1) + "-" + dayOfMonth);
              txtDateMsg.setText((monthOfYear + 1) + "-" + dayOfMonth);
                getData();
                break;
        }
        //    Log.i("kxflog", "" + str);
    }


}
