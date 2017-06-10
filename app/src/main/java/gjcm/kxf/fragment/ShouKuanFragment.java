package gjcm.kxf.fragment;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import gjcm.kxf.huifucenter.JumpPayRQ;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.huifucenter.RQShowPay;

/**
 * Created by kxf on 2016/12/13.
 */
public class ShouKuanFragment extends Fragment {
    private TextView showMoney, otherText, titleText;
    private String fristValue = "0";
    private String dnum = "";//
    private LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shoukuan_fragment, null);
        linearLayout = (LinearLayout) view.findViewById(R.id.shoukuan_add_liner);
        showMoney = (TextView) view.findViewById(R.id.main_showmoery);
        titleText = (TextView) view.findViewById(R.id.title_tname);
        titleText.setText("收 款");
        showMoney.setText(fristValue);
        SkScalerOne skScalerOne = new SkScalerOne();
        SkScalerTwo skScalerTwo = new SkScalerTwo();
        Button shoukuanOne, shoukuanTwo, shoukuanThre, shoukuanFour, shoukuanFive, shoukuanSix, shoukuanSeven, shoukuanEight, shoukuanNine, shoukuanDot, shoukuanZero, shoukuanDeng;
        Button shoukuanJia, shoukuanC, shoukuanTui, shoukuanScanned;
        otherText = (TextView) view.findViewById(R.id.shoukuan_other);
        shoukuanOne = (Button) view.findViewById(R.id.shoukuan_one);
        shoukuanTwo = (Button) view.findViewById(R.id.shoukuan_two);
        shoukuanThre = (Button) view.findViewById(R.id.shoukuan_three);
        shoukuanFour = (Button) view.findViewById(R.id.shoukuan_four);
        shoukuanFive = (Button) view.findViewById(R.id.shoukuan_five);
        shoukuanSix = (Button) view.findViewById(R.id.shoukuan_six);
        shoukuanSeven = (Button) view.findViewById(R.id.shoukuan_seven);
        shoukuanEight = (Button) view.findViewById(R.id.shoukuan_eight);
        shoukuanNine = (Button) view.findViewById(R.id.shoukuan_nine);
        shoukuanDot = (Button) view.findViewById(R.id.shoukuan_dot);
        shoukuanZero = (Button) view.findViewById(R.id.shoukuan_zero);
        shoukuanDeng = (Button) view.findViewById(R.id.shoukuan_deng);
        shoukuanJia = (Button) view.findViewById(R.id.shoukuan_jia);
        shoukuanC = (Button) view.findViewById(R.id.shoukuan_c);
        shoukuanTui = (Button) view.findViewById(R.id.shoukuan_huitui);
        shoukuanScanned = (Button) view.findViewById(R.id.shoukuan_scannel);
        shoukuanOne.setOnClickListener(skScalerOne);
        shoukuanTwo.setOnClickListener(skScalerOne);
        shoukuanThre.setOnClickListener(skScalerOne);
        shoukuanFive.setOnClickListener(skScalerOne);
        shoukuanFour.setOnClickListener(skScalerOne);
        shoukuanSeven.setOnClickListener(skScalerOne);
        shoukuanSix.setOnClickListener(skScalerOne);
        shoukuanEight.setOnClickListener(skScalerOne);
        shoukuanNine.setOnClickListener(skScalerOne);
        shoukuanDeng.setOnClickListener(skScalerTwo);
        shoukuanDot.setOnClickListener(skScalerOne);
        shoukuanZero.setOnClickListener(skScalerOne);
        shoukuanJia.setOnClickListener(skScalerTwo);
        shoukuanC.setOnClickListener(skScalerTwo);
        shoukuanTui.setOnClickListener(skScalerTwo);
        shoukuanScanned.setOnClickListener(skScalerTwo);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private double doubleOne = 0;

    class SkScalerOne implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            isSecondPuls = false;
            switch (v.getId()) {
                case R.id.shoukuan_one:
                    dnum += "1";
                    break;
                case R.id.shoukuan_two:
                    dnum += "2";
                    break;
                case R.id.shoukuan_three:
                    dnum += "3";
                    break;
                case R.id.shoukuan_four:
                    dnum += "4";
                    break;
                case R.id.shoukuan_five:
                    dnum += "5";
                    break;
                case R.id.shoukuan_six:
                    dnum += "6";
                    break;
                case R.id.shoukuan_seven:
                    dnum += "7";
                    break;
                case R.id.shoukuan_eight:
                    dnum += "8";
                    break;
                case R.id.shoukuan_nine:
                    dnum += "9";
                    break;
                case R.id.shoukuan_dot:
                    String str = dnum;
                    dnum += ".";
                    if (str.contains(".")) {
                        dnum = str;
                    }
                    break;
                case R.id.shoukuan_zero:
                    dnum += "0";
                    break;
                case R.id.shoukuan_scannel:
                    break;
            }
            String str = showMoney.getText().toString().trim();
            if (!str.equals(""))
                str = str.substring(0, 1);
            if (str.equals("+")) {
                if (dnum != "")
                    showMoney.setText(str + dnum);
            } else {
                showMoney.setText("" + dnum);
            }
        }
    }

    private boolean isFristPlus = false, isSecondPuls = true;

    class SkScalerTwo implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dnum = "";
            doubleOne = 0;
            String str = showMoney.getText().toString();
            showMoney.setText("");
            switch (v.getId()) {
                case R.id.shoukuan_jia:
                    if (isSecondPuls) {
//                       showMoney.setText("+");
                        break;
                    }
                    isSecondPuls = true;
                    if (isFristPlus)
                        str = "+" + str;
                    checkBack(str);
                    TextView tjia = new TextView(getContext());
                    tjia.setText(str);
                    tjia.setTextSize(20);
                    linearLayout.addView(tjia);
                    showMoney.setText(doubleTwo + "");
                    isFristPlus = true;
                    break;
                case R.id.shoukuan_c:
                    isFristPlus = false;
                    doubleOne = 0;
                    doubleTwo = 0;
                    linearLayout.removeAllViews();
                    showMoney.setText("");
                    break;
                case R.id.shoukuan_deng:
                    showMoney.setText("总共：" + doubleTwo + " 元");
                    break;
                case R.id.shoukuan_huitui:
                    if (str.equals("+")) {
                    } else {
                        if (str.length() > 0) {
                            str = str.substring(0, str.length() - 1);
                        }
                    }
                    showMoney.setText(str);
                    break;
                case R.id.shoukuan_scannel:
                    if (str != null)
                        doubleTwo = doubleTwo + Double.parseDouble(str);
                    String jumpMonery = "";
                    Log.i("kxflog", "str.length():" + jumpMonery + (doubleTwo > 0) + str);
                    if (doubleTwo > 0) {
                        jumpMonery = doubleTwo + "";
                        showScanne(jumpMonery);
                    } else {
                    }

                    break;
            }
        }
    }

    private double doubleTwo = 0;

    private void checkBack(String str) {

        if (str.contains("+")) {
            if (str.length() > 0) {
                str = str.trim().substring(1, str.length());
                doubleOne = Double.parseDouble(str);
                doubleTwo = doubleTwo + doubleOne;
            } else {
                return;
            }
        } else {
            doubleOne = Double.parseDouble(str);
            doubleTwo = doubleTwo + doubleOne;
        }
        DecimalFormat df = new DecimalFormat("0.00");//格式化
        String doublestr = df.format(doubleTwo);
        doubleTwo = Double.parseDouble(doublestr);
        Log.i("kxflog", "doubleTwo:" + doubleTwo + "doubleOne:" + doubleOne);
    }

    private void showScanne(final String jumpMonery) {
        Log.i("kxflog", "jumpMonery:" + jumpMonery);
        Builder builder = new Builder(getContext());
        builder.setMessage("“收款码”适合客户扫描商户订单二维码！\n\n\n“扫 码”商户扫描用户的支付条码");
        builder.setTitle("收款提示");

        builder.setPositiveButton("收款码", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(getContext(), RQShowPay.class);
                intent.putExtra("monery", jumpMonery);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("扫 码", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(getContext(), JumpPayRQ.class);
                intent.putExtra("monery", jumpMonery);
                startActivity(intent);
//                startActivityForResult(new Intent(getContext(), CaptureActivity.class), 0);
            }
        });
        builder.create().show();
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.i("kxflog", "onDismiss-----:");

            }
        });
        builder.setCancelable(false);

    }

}
