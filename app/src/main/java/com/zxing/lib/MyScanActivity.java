package com.zxing.lib;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import gjcm.kxf.goodorder.OrderScaleAcivity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.huifucenter.ScanCodeAcivity;

/**
 * Created by kxf on 2017/1/4.
 */
public class MyScanActivity extends AppCompatActivity implements QRCodeView.Delegate, View.OnClickListener {
    private QRCodeView mQRCodeView;

    private String strTitle;
    //3
    private String jumpStatus;//3----点菜付款1----退款  2----收款

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("kxflog", "----MyScanActivity----onCreate-");
        getSupportActionBar().hide();
        jumpStatus = getIntent().getStringExtra("iscard");
        if (jumpStatus.equals("1")) {
            setContentView(R.layout.my_card_layout);
            strTitle = "退款";
        } else if (jumpStatus.equals("2")) {
            setContentView(R.layout.my_rqcode_layout);
            strTitle = "收款";
        } else if (jumpStatus.equals("3")) {
            setContentView(R.layout.my_rqcode_layout);
            strTitle = "餐品收款";
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
        TextView txtskm = (TextView) findViewById(R.id.my_card_fkw);
        txtskm.setOnClickListener(this);
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.my_card_tb);
       /* editNo = (EditText) findViewById(R.id.edit_number);
        editNo.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    if (isFirst = true) {
                        String str = editNo.getText().toString();
                        Bundle bundle = new Bundle();
                        Intent resultIntent = new Intent();
                        bundle.putString("result", str);
                        resultIntent.putExtras(bundle);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                    isFirst = true;
                    Log.i("kxflog", "---------" + editNo.getText().toString());
                    return true;
                } else {
                    return false;
                }
            }
        });*/
//        toolbar.setTitle("扫码支付");
//        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setResult(RESULT_CANCELED);
//                finish();
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        mQRCodeView = null;
        super.onDestroy();
        Log.i("kxflog", "MyScanActivity----onDestroy");
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i("kxflog", "onScanQRCodeSuccess----onScanQRCodeSuccess");
        vibrate();
        Bundle bundle = new Bundle();
        Intent resultIntent = new Intent();
        bundle.putString("result", result);
        resultIntent.putExtras(bundle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.i("kxflog", "onScanQRCodeOpenCameraError----onScanQRCodeOpenCameraError");
        Toast.makeText(this, "打开相机出错", Toast.LENGTH_SHORT).show();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.setResult(RESULT_CANCELED);
        this.finish();

    }

    @Override
    public void onClick(View view) {
        showSKM();
    }

    //输入收款码
    private void showSKM() {
        mQRCodeView.stopSpot();
        final AlertDialog callAlert = new AlertDialog.Builder(this).create();
        callAlert.setCancelable(false);
        View view = View.inflate(this, R.layout.editskm_layout, null);
        final EditText editno = (EditText) view.findViewById(R.id.editskm_editno);
        TextView txtTitle = (TextView) view.findViewById(R.id.editskm_title);
        txtTitle.setText(strTitle);
        TextView txtcancle = (TextView) view.findViewById(R.id.editskm_btncancle);
        TextView txtok = (TextView) view.findViewById(R.id.editskm_btnok);
        txtok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = editno.getText().toString().trim();
                if (str.length() < 17) {
                    Toast.makeText(MyScanActivity.this, "格式不对", Toast.LENGTH_SHORT).show();
                } else {
                    callAlert.dismiss();
                    Log.i("kxflog", "MyScanActivity----result" + str);
                    Bundle bundle = new Bundle();
                    Intent resultIntent ;
                    if(jumpStatus.equals("3")){
                        resultIntent = new Intent(MyScanActivity.this, OrderScaleAcivity.class);
                    }else{
                        resultIntent = new Intent(MyScanActivity.this, ScanCodeAcivity.class);
                    }
                    bundle.putString("result", str);
                    resultIntent.putExtras(bundle);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
        txtcancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAlert.dismiss();
                mQRCodeView.startSpot();
            }
        });
        callAlert.setView(view);

        callAlert.show();


    }
}
