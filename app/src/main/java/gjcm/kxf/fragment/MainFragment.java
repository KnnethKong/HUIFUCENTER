package gjcm.kxf.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.StatusBarCompat;


/**
 * Created by Administrator on 2016/10/25.
 */
public class MainFragment extends FragmentActivity implements View.OnClickListener {
    private FragmentManager fragmentManager;
    private FrameLayout frameLayout;
    private ReconciliationFragment reconciliationFragment;//对账
    private DingdanFragment dingdanFragment;
    private MyFragemnt myFragemnt;
    public static String usertoken, usertype, storeName, storesyy;
    //    private TextView sktxt, dztxt, ordertxt, mytxt;
//    private ImageView skimg, dzimg, orderimg, myimg;
    private TextView txtHuifu, txtOrder, txtReconcilition, txtMy;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Activity.MODE_PRIVATE);
        usertoken = sharedPreferences.getString("usertoken", "");
        usertype = sharedPreferences.getString("usertype", "");
        storeName = sharedPreferences.getString("storeName", "");
        storesyy = sharedPreferences.getString("shouyy", "");
        fragmentManager = getSupportFragmentManager();
        initViewEvent();
        currentFrame(0);
    }

    private void initViewEvent() {
        txtHuifu = (TextView) findViewById(R.id.main_fragment_huifu);
        txtOrder = (TextView) findViewById(R.id.main_fragment_order);
        txtMy = (TextView) findViewById(R.id.main_fragment_my);
        txtReconcilition = (TextView) findViewById(R.id.main_fragment_recon);
        frameLayout = (FrameLayout) findViewById(R.id.main_frame);

        txtHuifu.setOnClickListener(this);
        txtOrder.setOnClickListener(this);
        txtMy.setOnClickListener(this);
        txtReconcilition.setOnClickListener(this);
//        showUpdate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_fragment_huifu:
                currentFrame(0);
                break;
            case R.id.main_fragment_my:
                currentFrame(3);
                break;
            case R.id.main_fragment_recon:
                currentFrame(2);
                break;
            case R.id.main_fragment_order:
                currentFrame(1);
                break;

        }

    }

//    private ReceiptFragment huifuFragment;
private HuifuFragment huifuFragment;
    private void currentFrame(int index) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        clearChioce(); // 清空, 重置选项, 隐藏所有Fragment
        Drawable drawable;
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:

                 huifuFragment = new HuifuFragment();
                drawable = null;
                txtHuifu.setTextColor(ContextCompat.getColor(this, R.color.dodgerblue));
                drawable = getResources().getDrawable(R.mipmap.sk_se, null);
                txtHuifu.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                fragmentTransaction.replace(R.id.main_frame, huifuFragment);
                break;
            case 1:
                txtOrder.setTextColor(ContextCompat.getColor(this, R.color.dodgerblue));
                drawable = null;
                drawable = getResources().getDrawable(R.mipmap.dd_se, null);
                txtOrder.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                dingdanFragment = new DingdanFragment();
                fragmentTransaction.replace(R.id.main_frame, dingdanFragment);

                break;
            case 2:
                txtReconcilition.setTextColor(ContextCompat.getColor(this, R.color.dodgerblue));
                drawable = null;
                drawable = getResources().getDrawable(R.mipmap.dz_se, null);
                txtReconcilition.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                reconciliationFragment = new ReconciliationFragment();
                fragmentTransaction.replace(R.id.main_frame, reconciliationFragment);
                break;
            case 3:
                txtMy.setTextColor(ContextCompat.getColor(this, R.color.dodgerblue));
                drawable = null;
                drawable = getResources().getDrawable(R.mipmap.my_se, null);
                txtMy.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                myFragemnt = new MyFragemnt();
                fragmentTransaction.replace(R.id.main_frame, myFragemnt);
//                    fragmentTransaction.add(R.id.main_frame, myFragemnt);
//                } else {
//                    fragmentTransaction.show(myFragemnt);
//                }
                break;
        }
        fragmentTransaction.commit(); // 提交
    }

    /**
     * 当选中其中一个选项卡时，其他选项卡重置为默认
     */
    private void clearChioce() {
        txtMy.setTextColor(ContextCompat.getColor(this, R.color.qianhui));
        txtReconcilition.setTextColor(ContextCompat.getColor(this, R.color.qianhui));
        txtOrder.setTextColor(ContextCompat.getColor(this, R.color.qianhui));
        txtHuifu.setTextColor(ContextCompat.getColor(this, R.color.qianhui));
        Drawable drawable;
        drawable = getResources().getDrawable(R.mipmap.my_no, null);
        txtMy.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        drawable = null;
        drawable = getResources().getDrawable(R.mipmap.dd_no, null);
        txtOrder.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        drawable = null;
        drawable = getResources().getDrawable(R.mipmap.dz_no, null);
        txtReconcilition.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        drawable = null;
        drawable = getResources().getDrawable(R.mipmap.sk_no, null);
        txtHuifu.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);


    }

    /**
     * 隐藏Fragment
     *
     * @param fragmentTransaction
     */
    private void hideFragments(FragmentTransaction fragmentTransaction) {
//        fragmentTransaction.
        if (huifuFragment != null) {
            fragmentTransaction.remove(huifuFragment);
        }
        if (myFragemnt != null) {
            fragmentTransaction.remove(myFragemnt);
        }
        if (dingdanFragment != null) {
            fragmentTransaction.remove(dingdanFragment);
        }
        if (reconciliationFragment != null) {
            fragmentTransaction.remove(reconciliationFragment);
        }
    }


    long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
//                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
//        Log.i("kxflog", "result:" + resultCode);
        if (resultCode == RESULT_OK) {
            final Bundle bundle = data.getExtras();
            if (bundle != null) {
                final String result = bundle.getString("result");

                Message message = new Message();
                message.setData(bundle);
                message.what = 10;
                jumpTuikuan(message);
            }
        }
        if (resultCode == 8) {
            String result = data.getStringExtra("result");
            int storeid = data.getIntExtra("storeid", 0);
            Bundle bundle = new Bundle();
            bundle.putString("result", result);
            bundle.putInt("storeid", storeid);
            Message message = new Message();
            message.what = 6;
            message.setData(bundle);
            if (reconciliationFragment != null)
                reconciliationFragment.reconhandler.sendMessage(message);
        }
        if (resultCode == 11) {
            String result = data.getStringExtra("result");
            int storeid = data.getIntExtra("storeuserid", 0);
            Bundle bundle = new Bundle();
            bundle.putString("result", result);
            bundle.putInt("storeuserid", storeid);
            Message message = new Message();
            message.what = 12;
            message.setData(bundle);
            if (reconciliationFragment != null)
                reconciliationFragment.reconhandler.sendMessage(message);
        }
    }

//        Log.i("kxflog", "resultCode:" + resultCode + "requestCode:" + requestCode + "data:" + data.toString());

    private void jumpTuikuan(Message message) {
        if (myFragemnt != null)
            myFragemnt.tuikuanHander.sendMessage(message);
    }
}