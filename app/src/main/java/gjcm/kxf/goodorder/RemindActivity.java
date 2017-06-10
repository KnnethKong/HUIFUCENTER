package gjcm.kxf.goodorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import gjcm.kxf.adapter.RemindRecycleAdapter;
import gjcm.kxf.entity.RemindEntity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.listener.MyScrollImpl;
import gjcm.kxf.tools.NetTools;

/**
 * Created by kxf on 2017/3/8.
 * 提醒activity
 */
public class RemindActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, MyScrollImpl {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private int mToolbarBottomMargin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remind_layout);
        initbar();
        recyclerView = (RecyclerView) findViewById(R.id.remind_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //  recyclerView.addOnScrollListener(new RecyclerScrollListener(this));
        getData();
    }


    private void initbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_tb);
        toolbar.setTitle("");
        TextView ttile = (TextView) findViewById(R.id.toolbar_txt);
        ttile.setText("消息提醒");
        setSupportActionBar(toolbar);
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemindActivity.this.finish();
            }
        });
        mToolbarBottomMargin = ((ViewGroup.MarginLayoutParams) toolbar.getLayoutParams()).bottomMargin;
        Log.e("kxflog", mToolbarBottomMargin + "mToolbarBottomMargin");

    }

    private ProgressDialog dialog;
    private ArrayList<RemindEntity> remindList;

    private void getData() {
        dialog = ProgressDialog.show(this, "", "正在查询", true, false);
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Context.MODE_PRIVATE);
        String storeid = sharedPreferences.getString("storeId", null);
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "orderMessage/QueryMessage");
        requestParams.addBodyParameter("storeid", storeid);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (dialog != null)
                    dialog.dismiss();
                try {
                    JSONObject jsonobj = new JSONObject(result);
                    int back_code = jsonobj.getInt("back_code");
                    if (back_code == 200) {
                        remindList = new ArrayList<>();
                        JSONArray array = jsonobj.getJSONArray("arraylist");
                        RemindEntity remindentity;
                        for (int i = 0; i < array.length(); i++) {
                            remindentity = null;
                            JSONObject objectEach = (JSONObject) array.get(i);
                            String msgInfo = objectEach.getString("msgInfo");
                            String createTime = objectEach.getString("createTime");
                            long id = objectEach.getLong("id");
                            long operId = objectEach.getLong("operId");
                            int msgType = objectEach.getInt("msgType");
                            remindentity = new RemindEntity(id, operId, msgType, createTime, msgInfo);
                            remindList.add(remindentity);
                        }
                        RemindRecycleAdapter remindRecycleAdapter = new RemindRecycleAdapter(remindList);
                        recyclerView.setAdapter(remindRecycleAdapter);
                        //  RemindAdapter adapter = new RemindAdapter(remindList, RemindActivity.this);
                        //listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(RemindActivity.this, jsonobj.optString("err_msg"), Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (dialog != null)
                    dialog.dismiss();
                Toast.makeText(RemindActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (dialog != null)
                    dialog.dismiss();
            }

            @Override
            public void onFinished() {

            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        long id = remindList.get(i).getId();
        updateChanges(id + "");
    }

    private void updateChanges(String mid) {
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Context.MODE_PRIVATE);
        String storeid = sharedPreferences.getString("storeId", null);
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "orderMessage/changeOper");
        requestParams.addBodyParameter("mid", mid);
        requestParams.addBodyParameter("oid", storeid);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                getData();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
            recyclerView = null;
        }
        if (remindList != null)
            remindList = null;
    }

    @Override
    public void hide() {
        //  Log.e("kxflog", "hide");
        toolbar.animate().translationY(-(toolbar.getHeight() + mToolbarBottomMargin)).setInterpolator(new AccelerateInterpolator(3));
    }

    @Override
    public void show() {
        //    Log.e("kxflog", "hide");
        toolbar.animate().translationY(0).setInterpolator(new AccelerateInterpolator(3));
    }
}
