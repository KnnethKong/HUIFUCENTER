package gjcm.kxf.goodorder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import gjcm.kxf.huifucenter.R;


/**
 * Created by kxf on 2017/2/28.
 * 查询
 */
public class DateSlectActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private TextView beginTxt, endTxt, nomarlTxt, addTxt, reduceTxt;
    private RelativeLayout relative1, relative2;
    private String begintime, endtime;
    private Button tervalBtn, nowdateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.date_select_layout);
        initbar();
        tervalBtn = (Button) findViewById(R.id.date_selct_interval);
        nowdateBtn = (Button) findViewById(R.id.date_selct_nowdate);
        tervalBtn.setOnClickListener(this);
        nowdateBtn.setOnClickListener(this);
        relative1 = (RelativeLayout) findViewById(R.id.date_nomarl_1);
        relative2 = (RelativeLayout) findViewById(R.id.date_nomarl_2);
        findViewById(R.id.date_selct_ok).setOnClickListener(this);
        beginTxt = (TextView) findViewById(R.id.date_selct_beigin);
        endTxt = (TextView) findViewById(R.id.date_selct_end);
        nomarlTxt = (TextView) findViewById(R.id.date_selct_nomarl);
        addTxt = (TextView) findViewById(R.id.date_selct_add);
        reduceTxt = (TextView) findViewById(R.id.date_selct_jian);
        beginTxt.setOnClickListener(this);
        endTxt.setOnClickListener(this);
        nomarlTxt.setOnClickListener(this);
        addTxt.setOnClickListener(this);
        reduceTxt.setOnClickListener(this);
        int nowyear = c.get(Calendar.YEAR);
        int nowmonth = c.get(Calendar.MONTH) + 1;
        int nowday = c.get(Calendar.DAY_OF_MONTH);
        String strday = nowyear + "-" + nowmonth + "-" + nowday;
        nomarlTxt.setText(strday);
        beginTxt.setText(strday);
        endTxt.setText(strday);
        begintime = strday;
        endtime = strday;
    }

    private void initbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tb);
        TextView ttile = (TextView) findViewById(R.id.toolbar_txt);
        ttile.setText("营业汇总");
        setSupportActionBar(toolbar);
        findViewById(R.id.toolbar_remind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DateSlectActivity.this, RemindActivity.class));
            }
        });
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateSlectActivity.this.finish();
            }
        });
    }

    private String dateToStamp(int i) {

        String nowtxt = nomarlTxt.getText().toString();
        try {
            Date date = dateFormat.parse(nowtxt);
            c.setTime(date);
            if (i == 1) {
                c.add(Calendar.DATE, 1);
            } else {
                c.add(Calendar.DATE, -1);
            }
            nowtxt = dateFormat.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        long curren = System.currentTimeMillis();
//        if (i == 1) {
//            curren += 24 * 60 * 60 * 1000;
//        } else {
//            curren -= 24 * 60 * 60 * 1000;
//        }
//        Date da = new Date(curren);
//        String str = dateFormat.format(da);
//        return str;
//        try {
//            da = dateFormat.parse(str);
//        } catch (ParseException e) {
////            e.printStackTrace();
//        }
//        long unixTimestamp = da.getTime();
//        return unixTimestamp + "";
        return nowtxt;
    }

    String scaletime;
    private int clickFlag = 0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isSelected = false;//true interval   false nowdate

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_selct_interval:
                if (!isSelected) {
                    nowdateBtn.setBackground(null);
                    nowdateBtn.setBackgroundResource(R.mipmap.date_select);
                    tervalBtn.setBackground(null);
                    tervalBtn.setBackgroundResource(R.mipmap.date_normal);
                    relative1.setVisibility(View.GONE);
                    relative2.setVisibility(View.VISIBLE);
                }
                isSelected = true;
                break;
            case R.id.date_selct_nowdate:
                if (isSelected) {
                    tervalBtn.setBackground(null);
                    tervalBtn.setBackgroundResource(R.mipmap.date_select);
                    nowdateBtn.setBackground(null);
                    nowdateBtn.setBackgroundResource(R.mipmap.date_normal);
                    relative2.setVisibility(View.GONE);
                    relative1.setVisibility(View.VISIBLE);
                }
                isSelected = false;
                break;
            case R.id.date_selct_add:
                scaletime = dateToStamp(1);
                nomarlTxt.setText(scaletime);
                break;
            case R.id.date_selct_jian:
                scaletime = dateToStamp(2);
                nomarlTxt.setText(scaletime);
                break;
            case R.id.date_selct_nomarl:
                clickFlag = 1;
                showDate();
                break;
            case R.id.date_selct_beigin:
                clickFlag = 2;
                showDate();
                break;
            case R.id.date_selct_end:
                clickFlag = 3;
                showDate();

                break;
            case R.id.date_selct_ok:
                if (!isSelected) {
                    String nomarlstr = nomarlTxt.getText().toString();
                    begintime = nomarlstr;
                    endtime = nomarlstr;
                }
                Intent intent = new Intent(DateSlectActivity.this, StoreSummaryActivity.class);
                intent.putExtra("begintime", begintime);
                intent.putExtra("endtime", endtime);
                setResult(20, intent);
                finish();
                break;
        }
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
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String str = (year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        if (clickFlag == 1) {
            begintime = str;
            nomarlTxt.setText(str);
        } else if (clickFlag == 2) {
            begintime = str;
            beginTxt.setText(str);
        } else if (clickFlag == 3) {
            endtime = str;
            endTxt.setText(str);
        }
    }
}
