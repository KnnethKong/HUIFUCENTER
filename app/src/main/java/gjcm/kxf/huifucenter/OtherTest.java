package gjcm.kxf.huifucenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import gjcm.kxf.tools.OtherTools;
import gjcm.kxf.tools.PrintTools;

/**
 * Created by kxf on 2016/12/26.
 */
public class OtherTest extends AppCompatActivity {
    private String userToken, blueadress, shouyy, undis, monery;
    private TextView txtstatus, txtorderamount, txtrealamount, txtdisamount, txtundisamount, txtpaytype, txtpaytime, txtordernumber, txtalino, txtwuyong, txttypeyh, txtshanghu;
    //    Button button;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private String storeName;
    //    private ImageView imgeback;
    private RelativeLayout typeLiner, merchantLiner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scancode_layout);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        userToken = sharedPreferences.getString("usertoken", "");
        shouyy = sharedPreferences.getString("shouyy", "");
        blueadress = sharedPreferences.getString("blueadress", "");
        storeName = sharedPreferences.getString("storeName", "");
        monery = "36.00";
        undis = "0.00";
        txtstatus = (TextView) findViewById(R.id.scancode_status);
        txtorderamount = (TextView) findViewById(R.id.scancode_orderamount);
        txtrealamount = (TextView) findViewById(R.id.scancode_realamount);
        txtdisamount = (TextView) findViewById(R.id.scancode_disamount);
        txtundisamount = (TextView) findViewById(R.id.scancode_nodisamount);
        txtpaytype = (TextView) findViewById(R.id.scancode_paytype);
        txtpaytime = (TextView) findViewById(R.id.scancode_paytime);
        txtordernumber = (TextView) findViewById(R.id.scancode_payno);
        txtalino = (TextView) findViewById(R.id.scancode_alino);
        txtwuyong = (TextView) findViewById(R.id.scancode_typewuy);
        txttypeyh = (TextView) findViewById(R.id.scancode_typeyouhui);
        typeLiner = (RelativeLayout) findViewById(R.id.scancode_typerliner);
        merchantLiner = (RelativeLayout) findViewById(R.id.scancode_shangjialiner);
        txtshanghu = (TextView) findViewById(R.id.scancode_shanghu);

        datata();
//        initview();
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void datata() {
        final DecimalFormat df3 = new DecimalFormat("0.00");

        StringBuilder sb = new StringBuilder();
        AssetManager am = getApplicationContext().getAssets();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    am.open("demos.json")));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }
//            am.close();
        } catch (IOException e) {
            e.printStackTrace();
            sb.delete(0, sb.length());
        }
        try {
            JSONObject resultObj = new JSONObject(sb.toString());
            Log.i("kxflog", "" + resultObj.toString());
            String success = resultObj.getString("success");
            if (success.equals("true")) {
                JSONObject dataObj = resultObj.getJSONObject("data");
                String ordernumber = dataObj.getString("orderNumber");
                Double discount = dataObj.optDouble("discountAmount");
                Double orderamount = dataObj.optDouble("orderAmount");
                Double realpayamount = dataObj.optDouble("realPayAmount");
                Double paidinamount = dataObj.optDouble("paidInAmount");
//                        String discountAmount = dataObj.opt("discountAmount").toString();
//                        String orderAmount = dataObj.opt("orderAmount").toString();
                String status = dataObj.getString("statusText");
//                        String realPayAmount = dataObj.optString("realPayAmount");
//                        String realPayAmount = dataObj.optString("realPayAmount");3
                String orderType = dataObj.optString("orderType");
                int paytype = dataObj.optInt("type");
                String tradeNo;
                if (paytype == 0) {
                    tradeNo = dataObj.optString("transactionId");
                } else {
                    tradeNo = dataObj.optString("tradeNo");
                }
                String paytime = dataObj.optString("payTime");
//                        String tradeNo = dataObj.optString("tradeNo");
//                        String paidInAmount = dataObj.opt("paidInAmount").toString();
                if (paytime.equals("")) {
                } else {
                    long timestimp = Long.parseLong(paytime);
                    Date date = new Date(timestimp);
                    paytime = simpleDateFormat.format(date);
                }
                Double typeyh = 0.00, sjyh = 0.00;
                try {
                    sjyh = (orderamount - paidinamount);
                    if (sjyh < 0.01)
                        sjyh = 0.00;
                    typeyh = discount - (orderamount - paidinamount);
                    if (typeyh < 0.01)
                        typeyh = 0.00;
                    Log.e("kxflog", "支付类型优惠的金额---result:" + typeyh);
                } catch (NumberFormatException e) {
                    Log.e("kxflog", " 计算优惠金额NumberFormatException");
                }
                String s1 = df3.format(typeyh);//----zfb/wx优惠
                String s2 = df3.format(sjyh);///商家优惠
                String s3 = df3.format(orderamount);
                String s4 = df3.format(realpayamount);
                txtstatus.setText(status);
//                        Double cny = Double.parseDouble(discountAmount);//转换成Double
//                        discountAmount = df3.format(cny);
                if (s1.equals("0.00"))
                    typeLiner.setVisibility(View.GONE);
                if (s2.equals("0.00"))
                    merchantLiner.setVisibility(View.GONE);
                txtdisamount.setText(s2 + " 元");
                txtorderamount.setText(s3 + " 元");
                txtordernumber.setText(ordernumber);
                txtpaytime.setText(paytime);
                txtshanghu.setText(paidinamount + "元");

                txtrealamount.setText(s4 + " 元");
                txtpaytype.setText(orderType);
                txtwuyong.setText(orderType + "优惠");
                txtundisamount.setText(undis + " 元");
                txttypeyh.setText(s1+"元");
                txtalino.setText(tradeNo);
//                printData(s3, ordernumber, "无", orderType, s4, s2, storeName, shouyy, status, paytime, s1, paidinamount + "");
            } else {
                success = resultObj.getString("err_msg");
                Toast.makeText(OtherTest.this, "收款失败 " + success, Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void printData(final String orderAm, final String orderNumberStr, final String note, final String payType, final String realAm, final String youhui, final String mendian, final String shouyy, final String success,
                           final String paytime, final String typeyh, final String shshihou) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        String isprint = sharedPreferences.getString("isprint", "");
        if (("").equals(isprint)) {
            return;
        } else if (("on").equals(isprint)) {
            if (bluetoothAdapter.isEnabled()) {
                if (!"".equals(blueadress)) {
                    final PrintTools printTools = new PrintTools(this, blueadress);
                    if (printTools.connect()) {
                        new Thread() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                Bitmap bitmap;
                                if (payType.equals("支付宝")) {
                                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.twotwo);
                                } else if (payType.equals("微信")) {
                                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timgwcat);
                                } else {
                                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.huifu);
                                }
                                printTools.txtcom(bitmap, orderAm, orderNumberStr, note, payType, realAm, youhui, mendian, shouyy, success, paytime, undis, typeyh, shshihou);
//                                printTools.printDeatail(orderAm, orderNumberStr, note, payType, realAm, youhui, mendian, shouyy, success, paytime, undis, typeyh);
                            }
                        }.start();
                    } else {
                        Toast.makeText(this, "打印机连接失败，请检查打印机", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    public void otherOpenNoti(View view) {

    }

  /*  private void initview() {
        ArrayList<String> list = new ArrayList<>();
        for (int k = 0; k < 10; k++) {
            list.add(k + "wiosduiwaudhyduiauieeyuawdaswee");
        }
        onerSpiner = (Spinner) findViewById(R.id.other_spiner2);
        ArrayAdapter<String> arrays = new ArrayAdapter<String>(this, R.layout.spriner_txt, list);
        arrays.setDropDownViewResource(R.layout.spiner_style_liner);
        onerSpiner.setAdapter(arrays);
    }
*/
/*
    private void initview() {
        titleText = (TextView) findViewById(R.id.title_storename);
        titleText.setVisibility(View.VISIBLE);
        titleText.setText("");
        button1 = (Button) findViewById(R.id.sk_number1);
        button2 = (Button) findViewById(R.id.sk_number2);
        button3 = (Button) findViewById(R.id.sk_number3);
        button4 = (Button) findViewById(R.id.sk_number4);
        button5 = (Button) findViewById(R.id.sk_number5);
        button6 = (Button) findViewById(R.id.sk_number6);
        button7 = (Button) findViewById(R.id.sk_number7);
        button8 = (Button) findViewById(R.id.sk_number8);
        button9 = (Button) findViewById(R.id.sk_number9);
        buttonzero = (Button) findViewById(R.id.sk_numberzero);
        buttonplus = (Button) findViewById(R.id.sk_numberplus);
        buttondot = (Button) findViewById(R.id.sk_numberdot);
        qingkong = (TextView) findViewById(R.id.sk_qingkong);
        shanchu = (TextView) findViewById(R.id.sk_delete);
        shoukuan = (LinearLayout) findViewById(R.id.sk_shoukuan);
        //
        countMoney = (EditText) findViewById(R.id.shoukuan_sacle);

        undisedit = (EditText) findViewById(R.id.shoukuan_nodiscount);

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
                return true;
            }
        });
      *//*  countMoney.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {//点击软键盘完成控件时触发的行为
                    Log.i("kxflog", "setOnEditorActionListener" + "------");
                    //关闭光标并且关闭软键盘
                    countMoney.setCursorVisible(false);
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(countMoney.getWindowToken(), 0);
                }
                return true;
            }
        });*//*
        undisedit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isundis = true;
                int inType = undisedit.getInputType(); // backup the input type
                undisedit.setInputType(InputType.TYPE_NULL); // disable soft input
                undisedit.onTouchEvent(motionEvent); // call native handler
                undisedit.setInputType(inType); // restore input type
                return true;
            }
        });
        undisedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                undisedit.setSelection(undisedit.length());

            }
        });
        //
        //

        shoukuan.setOnClickListener(this);
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
                caculaterMethod("+");
                break;
            case R.id.sk_qingkong:
                clearText();
                break;
            case R.id.sk_delete:
                numberDel();
                break;
            case R.id.sk_shoukuan:
                checkSK();
                clearAll();
                break;

        }
//        this.countMoney.setText("");
//        this.countMoney.clearFocus();
    }


    private void checkSK(){
        String str1 = this.countMoney.getText().toString();
        if ("".equals(str1))
            return;
        String amount = "";
        DecimalFormat df3 = new DecimalFormat("0.00");
        if (ispulus) {
            try {
                amount = df3.parse(inputCount).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Double s = Double.parseDouble(str1);
            s = Double.valueOf(inputCountN.doubleValue() + s.doubleValue());
            if (s > 0.00)
                amount = df3.format(s);
        }
        if (Double.valueOf(amount) < Double.valueOf(undisOld)) {
            Toast.makeText(this, "不参与优惠金额输入有误", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, amount + "----" + undisOld, Toast.LENGTH_SHORT).show();

    }
    private void numberDel() {
        if (isundis) {
            Log.i("kxflog","numberDel:----------"+undisNow);
            if (undisNow.length() > 0) {
                undisOld = undisOld.substring(0, this.undisOld.length() - 1);
                undisedit.setText(undisOld);
                undisNow=undisOld;
            }

            return;
        }//---------------------

        if (this.editTextStatus.booleanValue()) {
            if (this.nowInput.length() > 0) {
                this.nowInutBuffer = this.nowInput.substring(0, this.nowInput.length() - 1);
                this.nowInput = this.nowInutBuffer;
                this.countMoney.setText(this.nowInutBuffer);
            }
            return;
        }
        this.nowInput = "";
        this.nowInutBuffer = this.nowInput;
        this.countMoney.setText(this.nowInutBuffer);
        this.inputCountN = Double.valueOf(0.00D);
    }

    private void addNumber(String paramString) {
        if (isundis) {
//            undisNow = paramString;
            setundisEdit(paramString);
        } else {
            ispulus = false;
            if (editTextStatus.booleanValue()) {
                this.nowInutBuffer = this.nowInput;
                addNumberCode(paramString);
                return;
            }
            this.nowInput = "";
            this.editTextStatus = Boolean.valueOf(true);
            this.nowInutBuffer = this.nowInput;
            if ("+".equals(this.methodStatus)) {
                addNumberCode(paramString);
            }
        }

    }

    private void clearText() {
        if (isundis) {
            undisOld = "";
            undisNow = "";
            undisedit.setText(undisNow);
        } else {
            this.nowInput = "";
            this.nowInutBuffer = this.nowInput;
            this.inputCount = this.nowInput;
            this.countMoney.setText(this.nowInput);
            this.inputCountN = Double.valueOf(0.0D);
            this.inputCount = "";
            this.editTextStatus = Boolean.valueOf(true);
            ispulus = false;
        }
    }

    private void clearAll() {
        undisOld = "";
        undisNow = "";
        undisedit.setText(undisNow);
        this.nowInput = "";
        this.nowInutBuffer = this.nowInput;
        this.inputCount = this.nowInput;
        this.countMoney.setText(this.nowInput);
        this.inputCountN = Double.valueOf(0.0D);
        this.inputCount = "";
        this.editTextStatus = Boolean.valueOf(true);
        ispulus = false;
    }

    private void addNumberCode(String paramString) {
        nowInutBuffer += paramString;
        if (Pattern.compile("((^[1-9][0-9]{0,4})(\\.[0-9]{0,2})?$)|(^0(\\.((0[1-9]?)|([1-9][0-9]?))?)?$)").matcher(nowInutBuffer).matches()) {
            nowInput = nowInutBuffer;
            countMoney.setText(nowInutBuffer);
        }
    }

    private void setundisEdit(String param) {
        undisNow += param;
        if (Pattern.compile("((^[1-9][0-9]{0,4})(\\.[0-9]{0,2})?$)|(^0(\\.((0[1-9]?)|([1-9][0-9]?))?)?$)").matcher(undisNow).matches()) {
            undisOld = undisNow;
            undisedit.setText(undisOld);
        }else {
            undisNow=undisOld;
        }


    }

    private boolean ispulus = false;

    private void caculaterMethod(String paramString) {
        if (!isundis) {
            DecimalFormat localDecimalFormat = new DecimalFormat("#.##");
            if ("+".equals(paramString)) {
                this.methodStatus = "+";
                if (this.nowInput.length() > 0) {
                    this.nowInputN = Double.valueOf(Double.parseDouble(this.nowInput));
                    this.inputCountN = Double.valueOf(this.inputCountN.doubleValue() + this.nowInputN.doubleValue());
                    this.inputCount = localDecimalFormat.format(this.inputCountN);
                    this.countMoney.setText(this.inputCount);
                    this.nowInput = "";
                    ispulus = true;
                }
                return;
            }

            if ("=".equals(paramString)) {
                if (this.nowInput.length() > 0) {
                    this.nowInputN = Double.valueOf(Double.parseDouble(this.nowInput));
                    this.inputCountN = Double.valueOf(this.inputCountN.doubleValue() + this.nowInputN.doubleValue());
                    this.inputCount = localDecimalFormat.format(this.inputCountN);
                    this.countMoney.setText(this.inputCount);
                }
                this.methodStatus = "=";
                this.nowInput = "";
            }
        }
    }*/
}



