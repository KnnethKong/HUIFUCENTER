package gjcm.kxf.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import gjcm.kxf.adapter.SearchUserAdapter;
import gjcm.kxf.entity.MerchantUserEntity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.NetTools;

/**
 * Created by kxf on 2017/1/3.
 */
public class ReconUserSearch extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private String usertoken;
    private ProgressDialog dialog;
    private int storeidback;
    private ArrayList<MerchantUserEntity> userEntityList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_reconciliation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.recon_store_toobal);
        toolbar.setTitle("选择店员");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        usertoken = getIntent().getStringExtra("token");
        storeidback = getIntent().getIntExtra("storeid", 0);
        listView = (ListView) findViewById(R.id.search_recon_list);
        listView.setOnItemClickListener(this);
        userEntityList = new ArrayList<>();
        dialog = ProgressDialog.show(this, "", "正在查询", false, false);
        getData();
    }

    private void getData() {
        RequestParams requestParams = new RequestParams(NetTools.HOMEURL + "/manager-order/search-storeanduser");
        requestParams.addHeader("token", usertoken);

        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
//                Log.i("kxflog", "onSuccess:" + result);
                if (dialog != null)
                    dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String suc = jsonObject.opt("success").toString();
                    if (!suc.equals("true")) {
                        Toast.makeText(ReconUserSearch.this, "查询失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject jsonData = jsonObject.optJSONObject("data");
                    JSONArray merchantStoreUserCommons = jsonData.optJSONArray("merchantStoreUserCommons");
                    String realname, type, merchantEnable;
                    int userid, storeid;
                    MerchantUserEntity userEntity;
                    if (merchantStoreUserCommons.length() <= 0) {
                        Toast.makeText(ReconUserSearch.this, "没有查到门店用户", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0; i < merchantStoreUserCommons.length(); i++) {
                        JSONObject erachuser = (JSONObject) merchantStoreUserCommons.get(i);
                        storeid = erachuser.optInt("storeid");
                        if (storeidback == storeid) {
                            userid = erachuser.optInt("id");
                            realname = erachuser.opt("realname").toString();
                            type = erachuser.opt("type11").toString();
                            merchantEnable = erachuser.opt("merchantEnable").toString();
                            userEntity = new MerchantUserEntity(userid, storeid, realname, type, merchantEnable);
                            userEntityList.add(userEntity);
                        }
                    }
//                    JSONArray storearray = jsonData.optJSONArray("merchantStoreCommons");
//                    MerchantStoreEntity merchantStore;
//                    String storename;
//                    int storeidt;
//                    for (int i = 0; i < storearray.length(); i++) {
//                        JSONObject strorjson = (JSONObject) storearray.get(i);
//                        storeidt = strorjson.optInt("id");
//                        storename = strorjson.opt("storeName").toString();
////                        storno = strorjson.opt("storeNo").toString();
//                        merchantStore = new MerchantStoreEntity(storeidt, storename);
//                        storeEntityList.add(merchantStore);
//                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SearchUserAdapter adapter = new SearchUserAdapter(userEntityList, ReconUserSearch.this);
                listView.setAdapter(adapter);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("kxflog", "onError:" + ex.getMessage());
                if (dialog != null)
                    dialog.dismiss();
                Toast.makeText(ReconUserSearch.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (dialog != null)
                    dialog.dismiss();
            }

            @Override
            public void onFinished() {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i("kxflog", i + "-------user-----");  String name = userEntityList.get(i).getRealname();
        int uid = userEntityList.get(i).getUserid();
        Intent intent = new Intent(this, MainFragment.class);
        intent.putExtra("result", name);
        intent.putExtra("storeuserid", uid);
        setResult(11, intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }
}