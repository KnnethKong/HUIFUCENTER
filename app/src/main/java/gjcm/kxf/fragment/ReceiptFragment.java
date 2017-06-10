package gjcm.kxf.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

import gjcm.kxf.huifucenter.R;
import gjcm.kxf.huifucenter.RQShowPay;
import gjcm.kxf.huifucenter.ScanCodeAcivity;

/**
 * 收款-> ui第一版
 * Created by kxf on 2016/12/26.
 */
public class ReceiptFragment extends Fragment implements View.OnClickListener {

    LinearLayout linerShoukuan;
    private EditText countMoney;
    private Boolean editTextStatus = Boolean.valueOf(true);
    private String inputCount = "";
    private Double inputCountN = Double.valueOf(0.00D);
    private String methodStatus = "";
    private String nowInput = "";
    private Double nowInputN = Double.valueOf(0.00D);
    private String nowInutBuffer = "";
    private EditText editNodiscount;
    private String undisNow = "", undisOld = "";
    public boolean isundis = false;
    private LinearLayout linerShouSum;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shoukuan_newlayout, null);
        initview(view);
        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        linerShoukuan=null;
        linerShouSum=null;
        buttonplus=null;
        countMoney=null;editNodiscount=null;ttile=null;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUserVisibleHint(true);
        String storename = MainFragment.storeName;
        if ("".equals(storename)) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
            storename = sharedPreferences.getString("storeName", "");
        }
        ttile.setText(storename);
    }

    private TextView ttile;


    @Override
    public void onResume() {
        super.onResume();
        countMoney.requestFocus();
        countMoney.findFocus();//获取焦点
        buttonplus.setEnabled(true);
        editNodiscount.clearFocus();
    }

    @Override
    public void onStart() {
        super.onStart();
        clearAll();
//        countMoney.requestFocus();
//        countMoney.findFocus();//获取焦点
    }

    /**
     * 二维码
     */
    private void showPayRQ() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View
                .inflate(getActivity(), R.layout.rqshow_layout, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog ewm = builder.create();

    }

    private Button buttonplus;

    private void initview(View view) {
        ImageView skRQ;//收款二维码
        final Button button1, button2, button3, button4, button5, button6, button7, button8, button9, buttonzero, buttondot, showewm;
        TextView qingkong, shanchu;
        ttile = (TextView) view.findViewById(R.id.title_storename);
        ttile.setVisibility(View.VISIBLE);
        skRQ = (ImageView) view.findViewById(R.id.title_skewm);
        linerShouSum = (LinearLayout) view.findViewById(R.id.sk_shoukuansum);// =
        button1 = (Button) view.findViewById(R.id.sk_number1);
        button2 = (Button) view.findViewById(R.id.sk_number2);
        button3 = (Button) view.findViewById(R.id.sk_number3);
        button4 = (Button) view.findViewById(R.id.sk_number4);
        button5 = (Button) view.findViewById(R.id.sk_number5);
        button6 = (Button) view.findViewById(R.id.sk_number6);
        button7 = (Button) view.findViewById(R.id.sk_number7);
        button8 = (Button) view.findViewById(R.id.sk_number8);
        button9 = (Button) view.findViewById(R.id.sk_number9);
        buttonzero = (Button) view.findViewById(R.id.sk_numberzero);
        buttonplus = (Button) view.findViewById(R.id.sk_numberplus);
        buttondot = (Button) view.findViewById(R.id.sk_numberdot);
        qingkong = (TextView) view.findViewById(R.id.sk_qingkong);
        shanchu = (TextView) view.findViewById(R.id.sk_delete);
        countMoney = (EditText) view.findViewById(R.id.shoukuan_sacle);
        linerShoukuan = (LinearLayout) view.findViewById(R.id.sk_shoukuan);// 收款
//        showewm = (Button) view.findViewById(R.id.title_skewm);
        editNodiscount = (EditText) view.findViewById(R.id.shoukuan_nodiscount);

        countMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                countMoney.setSelection(countMoney.length());
            }
        });
        countMoney.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isundis = false;
                int inType = countMoney.getInputType(); // backup the input type
                countMoney.setInputType(InputType.TYPE_NULL); // disable soft input
                countMoney.onTouchEvent(motionEvent); // call native handler
                countMoney.setInputType(inType); // restore input type
                buttonplus.setEnabled(true);

                return true;
            }
        });

        editNodiscount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isundis = true;
                int inType = editNodiscount.getInputType(); // backup the input type
                editNodiscount.setInputType(InputType.TYPE_NULL); // disable soft input
                editNodiscount.onTouchEvent(motionEvent); // call native handler
                editNodiscount.setInputType(inType); // restore input type
                linerShouSum.setVisibility(View.GONE);
                linerShoukuan.setVisibility(View.VISIBLE);
                buttonplus.setEnabled(false);
                return true;
            }
        });

        editNodiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editNodiscount.setSelection(editNodiscount.length());
            }
        });

        skRQ.setVisibility(View.VISIBLE);
        skRQ.setOnClickListener(this);
        linerShoukuan.setOnClickListener(this);
        linerShouSum.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        buttonzero.setOnClickListener(this);
        buttonplus.setOnClickListener(this);
        buttondot.setOnClickListener(this);
        qingkong.setOnClickListener(this);
        shanchu.setOnClickListener(this);
    }

    private DecimalFormat df3 = new DecimalFormat("0.00");
    private Intent intent;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sk_number1:
                addNumber("1");
                break;
            case R.id.sk_number2:
                addNumber("2");
                break;
            case R.id.sk_number3:
                addNumber("3");
                break;
            case R.id.sk_number4:
                addNumber("4");
                break;
            case R.id.sk_number5:
                addNumber("5");
                break;
            case R.id.sk_number6:
                addNumber("6");
                break;
            case R.id.sk_number7:
                addNumber("7");
                break;
            case R.id.sk_number8:
                addNumber("8");
                break;
            case R.id.sk_number9:
                addNumber("9");
                break;
            case R.id.sk_numberzero:
                addNumber("0");
                break;
            case R.id.sk_numberdot:
                addNumber(".");
                break;
            case R.id.sk_numberplus:
                linerShouSum.setVisibility(View.VISIBLE);
                linerShoukuan.setVisibility(View.GONE);
                caculaterMethod("+");
                break;
            case R.id.sk_qingkong:
                clearText();
                break;
            case R.id.sk_delete:
                numberDel();
                break;
            case R.id.sk_shoukuan:
                checkSK(2);
                break;
            case R.id.title_skewm:
                checkSK(1);
                break;
            case R.id.sk_shoukuansum:
                linerShouSum.setVisibility(View.GONE);
                linerShoukuan.setVisibility(View.VISIBLE);
                caculaterMethod("=");
                break;
        }

    }

    private void checkSK(int i) {
        String str1 = countMoney.getText().toString();
        String str2 = editNodiscount.getText().toString();
        Log.i("kxflog", str1 + " ------- nodis= " + str2);
        if ("".equals(str1))
            return;
        String amount = "";
        DecimalFormat df3 = new DecimalFormat("0.00");
        if (ispulus) {
            try {
                Double cny = Double.parseDouble(inputCount);
                amount = df3.format(cny);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Double s = Double.parseDouble(str1);
            s = Double.valueOf(inputCountN.doubleValue() + s.doubleValue());
            if (s > 0.00)
                amount = df3.format(s);
        }
        if (("").equals(str2)) {
            str2 = "0.00";
        } else {
            Double cny = Double.parseDouble(str2);
            str2 = df3.format(cny);
        }
        if (Double.valueOf(amount) < Double.valueOf(str2)) {
            Toast.makeText(getContext(), "不参与优惠金额输入有误", Toast.LENGTH_SHORT).show();
            return;
        }
        clearAll();
        if (i == 1)
            intent = new Intent(getContext(), RQShowPay.class);
        else
            intent = new Intent(getContext(), ScanCodeAcivity.class);
        intent.putExtra("monery", amount);
        intent.putExtra("discount", str2);
        startActivity(intent);
    }

    private void numberDel() {
        if (editNodiscount.hasFocus()) {
            if (undisNow.length() > 0) {
                undisOld = undisOld.substring(0, this.undisOld.length() - 1);
                editNodiscount.setText(undisOld);
                undisNow = undisOld;
            }

            return;
        }
        if (countMoney.hasFocus()) {
            if (nowInput.length() > 0) {
                nowInutBuffer = nowInput.substring(0, nowInput.length() - 1);
                nowInput = nowInutBuffer;
                countMoney.setText(nowInutBuffer);
            }
            return;
        }
        nowInput = "";
        nowInutBuffer = nowInput;
        countMoney.setText(nowInutBuffer);
        inputCountN = Double.valueOf(0.00D);
    }

    private void addNumber(String paramString) {

        if (editNodiscount.hasFocus()) {
//            undisNow = paramString;
            setundisEdit(paramString);
        }
        if (countMoney.hasFocus()) {
            ispulus = false;
            if (editTextStatus.booleanValue()) {
                nowInutBuffer = nowInput;
                addNumberCode(paramString);
                return;
            }
            nowInput = "";
            editTextStatus = Boolean.valueOf(true);
            nowInutBuffer = nowInput;
            if ("+".equals(methodStatus)) {
                addNumberCode(paramString);
            }
        }
    }

    private void clearText() {
        linerShouSum.setVisibility(View.GONE);
        linerShoukuan.setVisibility(View.VISIBLE);
        if (editNodiscount.hasFocus()) {
            undisOld = "";
            undisNow = "";
            editNodiscount.setText(undisNow);
        }
        if (countMoney.hasFocus()) {
            nowInput = "";
            nowInutBuffer = nowInput;
            inputCount = nowInput;
            countMoney.setText(nowInput);
            inputCountN = Double.valueOf(0.0D);
            inputCount = "";
            editTextStatus = Boolean.valueOf(true);
            ispulus = false;
        }
    }

    private void addNumberCode(String paramString) {
        nowInutBuffer += paramString;
        if (Pattern.compile("((^[1-9][0-9]{0,4})(\\.[0-9]{0,2})?$)|(^0(\\.((0[1-9]?)|([1-9][0-9]?))?)?$)").matcher(nowInutBuffer).matches()) {
            nowInput = nowInutBuffer;
            countMoney.setText(nowInutBuffer);
        }
    }

    private void clearAll() {
        linerShouSum.setVisibility(View.GONE);
        linerShoukuan.setVisibility(View.VISIBLE);
        undisOld = "";
        undisNow = "";
        editNodiscount.setText(undisNow);
        nowInput = "";
        nowInutBuffer = nowInput;
        inputCount = nowInput;
        countMoney.setText(nowInput);
        inputCountN = Double.valueOf(0.0D);
        inputCount = "";
        editTextStatus = Boolean.valueOf(true);
        ispulus = false;
    }

    private void setundisEdit(String param) {
        undisNow += param;
        if (Pattern.compile("((^[1-9][0-9]{0,4})(\\.[0-9]{0,2})?$)|(^0(\\.((0[1-9]?)|([1-9][0-9]?))?)?$)").matcher(undisNow).matches()) {
            undisOld = undisNow;
            editNodiscount.setText(undisOld);
        } else {
            undisNow = undisOld;
        }


    }

    private boolean ispulus = false;

    private void caculaterMethod(String paramString) {
        DecimalFormat localDecimalFormat = new DecimalFormat("0.00");
        if ("+".equals(paramString)) {
            methodStatus = "+";
            if (nowInput.length() > 0) {
                nowInputN = Double.valueOf(Double.parseDouble(nowInput));
                inputCountN = Double.valueOf(inputCountN.doubleValue() + nowInputN.doubleValue());
                inputCount = localDecimalFormat.format(inputCountN);
                countMoney.setText(inputCount);
                nowInput = "";
                ispulus = true;
            }
            return;
        }

        if ("=".equals(paramString)) {
            if (nowInput.length() > 0) {
                nowInputN = Double.valueOf(Double.parseDouble(nowInput));
                inputCountN = Double.valueOf(inputCountN.doubleValue() + nowInputN.doubleValue());
                inputCount = localDecimalFormat.format(inputCountN);
                countMoney.setText(inputCount);
                ispulus = true;
            }
            methodStatus = "=";
            nowInput = "";
        }
    }


}
