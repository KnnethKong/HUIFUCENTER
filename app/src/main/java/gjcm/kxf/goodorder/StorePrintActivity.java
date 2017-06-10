package gjcm.kxf.goodorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import gjcm.kxf.adapter.StorePrintAdapter;
import gjcm.kxf.entity.PrintEntity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.NetTools;

/**
 * Created by kxf on 2017/3/4.
 * //点菜打印列表
 */
public class StorePrintActivity extends AppCompatActivity {
    private ListView storeListview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.storeprint_layout);
        initbar();
        getData();
    }

    private ArrayList<PrintEntity> printEntities;

    private void initbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tb);
        toolbar.setTitle("打印设备列表");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorePrintActivity.this.finish();
            }
        });       storeListview = (ListView) findViewById(R.id.store_print_list);
    }

    private void getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Context.MODE_PRIVATE);
        String storeid = sharedPreferences.getString("storeId", null);
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "StorePrint/pByStoreid");
        requestParams.addBodyParameter("sid", storeid);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonobj = new JSONObject(result);
                    int back_code = jsonobj.getInt("back_code");
                    if (back_code == 200) {
                        printEntities = new ArrayList<>();
                        JSONArray array = jsonobj.getJSONArray("typeslist");
                        PrintEntity printEntity;
                        for (int i = 0; i < array.length(); i++) {
                            printEntity = null;
                            JSONObject objectEach = (JSONObject) array.get(i);
                            String ip = objectEach.getString("IP");
                            String name = objectEach.getString("name");
                            String port = objectEach.getString("port");
                            int printType = objectEach.getInt("printType");
                            int printMode = objectEach.getInt("printMode");
                            long id = objectEach.getLong("id");
                            int delFlag = objectEach.getInt("delFlag");
                            printEntity = new PrintEntity(id, printType, printMode, delFlag, ip, name, port);
                            printEntities.add(printEntity);
                        }
                        StorePrintAdapter adapter = new StorePrintAdapter(printEntities, StorePrintActivity.this);
                        storeListview.setAdapter(adapter);
                    } else {
                        storeListview.setEmptyView(findViewById(android.R.id.empty));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
}
