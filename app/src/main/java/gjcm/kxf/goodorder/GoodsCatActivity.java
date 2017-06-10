package gjcm.kxf.goodorder;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import gjcm.kxf.adapter.GoodsCatOrderAdapter;
import gjcm.kxf.adapter.GoodsCatPerAdapter;
import gjcm.kxf.adapter.GoodsItemAdapter;
import gjcm.kxf.drawview.PinnedHeaderListView;
import gjcm.kxf.entity.GoodsEntity;
import gjcm.kxf.entity.GoodsTypeEntity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.listener.ShopToDetailListener;
import gjcm.kxf.listener.onCallBackListener;
import gjcm.kxf.tools.DoubleUtil;
import gjcm.kxf.tools.NetTools;

/**
 * Created by kxf on 2017/2/22.
 * 点餐页面
 */
public class GoodsCatActivity extends AppCompatActivity implements onCallBackListener, View.OnClickListener, ShopToDetailListener {
    private ArrayList<GoodsEntity> cachegoodsEntities;//huancun
    private ListView listView;//分类
    private PinnedHeaderListView pinnedHeaderListView;//列表
    private TextView txtAm, txtGo, goodsNum, txtOrderNo;

    private ArrayList<GoodsEntity> goodsEntities;//huancun

    private ArrayList<GoodsTypeEntity> goodsTypes;//huancun
    private GoodsItemAdapter pinneadapter;
    private ArrayList<String> sType;
    private ListView orderListView;
    private View order_bgview;
    private ImageView catImag;
    private FrameLayout cardLayout;
    private LinearLayout cardShopLayout;
    private String deskid, storeid, merchantid, orderId, operid;
    private int personNum;
    private ImageOptions imageOptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.goodscat_layout);
        initbar();
        deskid = getIntent().getStringExtra("deskid");
        orderId = getIntent().getStringExtra("orderid");
        if (orderId == null) {
            personNum = getIntent().getIntExtra("personNum", 2);
            if (deskid == null) {
                Toast.makeText(this, "餐桌无效，请重选", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        SharedPreferences sharedPreferences = getSharedPreferences("gjcmcenterkxf", Context.MODE_PRIVATE);
        storeid = sharedPreferences.getString("storeId", null);
        merchantid = sharedPreferences.getString("merchantId", null);
        operid = sharedPreferences.getString("logid", null);
        initView();
    }

    ProgressDialog dialog;

    private void initbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tb);
        TextView ttile = (TextView) findViewById(R.id.toolbar_txt);
        ttile.setText("选择菜品");
        setSupportActionBar(toolbar);
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoodsCatActivity.this.finish();
            }
        });
        findViewById(R.id.toolbar_remind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GoodsCatActivity.this, RemindActivity.class));
            }
        });
    }

    private void getData() {
        dialog = ProgressDialog.show(this, "", "正在获取", true);
        RequestParams requestParams = new RequestParams(NetTools.HOSTURL + "orderGood/QueryAllGoods");
        requestParams.addBodyParameter("merchantid", merchantid);// merchantid
        requestParams.addBodyParameter("storeid", storeid);// storeid
        requestParams.addBodyParameter("deskid", deskid);// deskid
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    int backcode = json.optInt("back_code");
                    if (backcode != 200) {
                        dismissDialog();
                        return;
                    }
                    goodsTypes = new ArrayList<>();
                    JSONArray backList = json.getJSONArray("typeslist");
                    for (int i = 0; i < backList.length(); i++) {
                        JSONObject typejson = (JSONObject) backList.get(i);
                        long typeid = typejson.getLong("id");
                        String typename = typejson.getString("name");
                        JSONArray arrayList = typejson.getJSONArray("arraylist");
                        if (arrayList.length() > 0) {
                            goodsEntities = new ArrayList<>();
                            for (int j = 0; j < arrayList.length(); j++) {
                                JSONObject goodjson = arrayList.getJSONObject(j);
                                long id = goodjson.getLong("id");
//                                String goodsDesc = goodjson.getString("goodsDesc");
                                int goodPrice = goodjson.getInt("goodPrice");
                                int printType = goodjson.getInt("printType");
                                String goodName = goodjson.getString("goodName");
                                String pic = goodjson.getString("pricAdr");
                                GoodsEntity goodsEntity = new GoodsEntity(id, goodPrice, goodName, typename, 0, printType, pic);
                                goodsEntities.add(goodsEntity);
                            }
                        }
                        GoodsTypeEntity typeEntity = new GoodsTypeEntity();
                        typeEntity.setSnam(typename);
                        typeEntity.setGoodsEntities(goodsEntities);
                        goodsTypes.add(typeEntity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sType = new ArrayList<>();
                pinneadapter = new GoodsItemAdapter(GoodsCatActivity.this, goodsTypes, imageOptions);
                pinneadapter.SetOnSetHolderClickListener(new GoodsItemAdapter.HolderClickListener() {

                    @Override
                    public void onHolderClick(Drawable drawable, int[] start_location) {
//                        doAnim(drawable, start_location);
                    }

                });

                for (GoodsTypeEntity e : goodsTypes) {
                    sType.add(e.getSnam());
                }
                pinnedHeaderListView.setAdapter(pinneadapter);
                pinneadapter.setCallBackListener(GoodsCatActivity.this);
                listView.setAdapter(new ArrayAdapter<String>(GoodsCatActivity.this,
                        R.layout.goods_type_layout, sType));
                /////----已点击的
                cachegoodsEntities = new ArrayList<>();
                goodsCatOrderAdapter = new GoodsCatOrderAdapter(GoodsCatActivity.this, cachegoodsEntities);
                orderListView.setAdapter(goodsCatOrderAdapter);
                goodsCatOrderAdapter.setShopToDetailListener(GoodsCatActivity.this);
                dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(GoodsCatActivity.this, "服务器连接异常", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(GoodsCatActivity.this, "被取消", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void dismissDialog() {

        if (dialog != null)
            dialog.dismiss();
    }

    private boolean isScroll = true;

    GoodsCatOrderAdapter goodsCatOrderAdapter;

    private void initView() {
        getData();
        listView = (ListView) findViewById(R.id.goodscat_typelist);
        pinnedHeaderListView = (PinnedHeaderListView) findViewById(R.id.goodscat_pinnerlist);
        txtAm = (TextView) findViewById(R.id.goodscat_price);
        txtGo = (TextView) findViewById(R.id.goodscat_settlement);
        orderListView = (ListView) findViewById(R.id.goodscat_order_list);
        order_bgview = findViewById(R.id.bg_layout);
        catImag = (ImageView) findViewById(R.id.goodscat_shopping_img);
        goodsNum = (TextView) findViewById(R.id.goodscat_shoppingnum);
        txtOrderNo = (TextView) findViewById(R.id.goodscat_order_txt);//没有点商品
        cardLayout = (FrameLayout) findViewById(R.id.goodscat_cardlayout);
        cardShopLayout = (LinearLayout) findViewById(R.id.goodscat_orderanimlayout);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                isScroll = false;

                for (int j = 0; j < listView.getChildCount(); j++) {

                    if (j == position) {
                        listView.getChildAt(j).setBackgroundColor(Color.rgb(255, 255, 255));
                    } else {
                        listView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                    }

                }
                int rightSection = 0;
                for (int i = 0; i < position; i++) {
                    rightSection += pinneadapter.getCountForSection(i) + 1;
                }
                pinnedHeaderListView.setSelection(rightSection);
            }
        });
        pinnedHeaderListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (isScroll) {
                    for (int i = 0; i < listView.getChildCount(); i++) {

                        if (i == pinneadapter
                                .getSectionForPosition(firstVisibleItem)) {
                            listView.getChildAt(i).setBackgroundColor(
                                    Color.rgb(255, 255, 255));
                        } else {
                            listView.getChildAt(i).setBackgroundColor(
                                    Color.TRANSPARENT);
                        }
                    }

                } else {
                    isScroll = true;
                }
            }
        });

        catImag.setOnClickListener(this);
        txtGo.setOnClickListener(this);
        order_bgview.setOnClickListener(this);
        imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setFailureDrawableId(R.mipmap.erro)
                .setLoadingDrawableId(R.mipmap.loading)
                .build();
//        dismissDialog();
    }

    private void doAnim(Drawable drawable, int[] start_location) {
    }

    @Override
    public void updateProduct(GoodsEntity product, String type) {
        if (type.equals("1")) {
            if (!cachegoodsEntities.contains(product)) {
                cachegoodsEntities.add(product);
            } else {
                for (GoodsEntity shopProduct : cachegoodsEntities) {
                    if (product.getId() == shopProduct.getId()) {
                        shopProduct.setNumber(shopProduct.getNumber());
                    }
                }
            }
        } else if (type.equals("2")) {
            if (cachegoodsEntities.contains(product)) {
                if (product.getNumber() == 0) {
                    cachegoodsEntities.remove(product);
                } else {
                    for (GoodsEntity shopProduct : cachegoodsEntities) {
                        if (product.getId() == shopProduct.getId()) {
                            shopProduct.setNumber(shopProduct.getNumber());
                        }
                    }
                }

            }
        }

        goodsCatOrderAdapter.notifyDataSetChanged();
        setPrice();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.goodscat_shopping_img:
                if (cachegoodsEntities.isEmpty() || cachegoodsEntities == null) {
                    txtOrderNo.setVisibility(View.VISIBLE);
                } else {
                    txtOrderNo.setVisibility(View.GONE);
                }

                if (cardLayout.getVisibility() == View.GONE) {
                    cardLayout.setVisibility(View.VISIBLE);
                    // 加载动画
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
                    // 动画开始
                    cardShopLayout.setVisibility(View.VISIBLE);
                    cardShopLayout.startAnimation(animation);
                    order_bgview.setVisibility(View.VISIBLE);

                } else {
                    cardLayout.setVisibility(View.GONE);
                    order_bgview.setVisibility(View.GONE);
                    cardShopLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.goodscat_settlement:
                if (cachegoodsEntities != null) {
//                    Toast.makeText(GoodsCatActivity.this, "" + cachegoodsEntities.size(), Toast.LENGTH_SHORT).show();
                    if (cachegoodsEntities.size() > 0)
                        showAMAlert();
//                    startActivity(intent);
                }
                break;
            case R.id.bg_layout:
                hideLayout();
                break;
        }

    }

    private void hideLayout() {
        cardLayout.setVisibility(View.GONE);
        order_bgview.setVisibility(View.GONE);
        cardShopLayout.setVisibility(View.GONE);
    }

    public void setPrice() {
        double sum = 0;
        int shopNum = 0;
        for (GoodsEntity pro : cachegoodsEntities) {
            sum = DoubleUtil.sum(sum, DoubleUtil.mul((double) pro.getNumber(), pro.getPrice() / 100));
            shopNum = shopNum + pro.getNumber();
        }
        if (shopNum > 0) {
            goodsNum.setVisibility(View.VISIBLE);
        } else {
            goodsNum.setVisibility(View.GONE);
        }
        if (sum > 0) {
            txtAm.setVisibility(View.VISIBLE);
        } else {
            txtAm.setVisibility(View.GONE);
        }
        cachAm = (new DecimalFormat("0.00")).format(sum);
        txtAm.setText("¥" + " " + cachAm);
        goodsNum.setText(String.valueOf(shopNum));
    }

    private String cachAm;//订单金额

    @Override
    public void onUpdateDetailList(GoodsEntity product, String type) {
        if (type.equals("1")) {
            for (int i = 0; i < goodsTypes.size(); i++) {
                goodsEntities = goodsTypes.get(i).getGoodsEntities();
                for (GoodsEntity shopProduct : goodsEntities) {
                    if (product.getId() == shopProduct.getId()) {
                        shopProduct.setNumber(product.getNumber());
                    }
                }
            }
        } else if (type.equals("2")) {
            for (int i = 0; i < goodsTypes.size(); i++) {
                goodsEntities = goodsTypes.get(i).getGoodsEntities();
                for (GoodsEntity shopProduct : goodsEntities) {
                    if (product.getId() == shopProduct.getId()) {
                        shopProduct.setNumber(product.getNumber());
                    }
                }
            }
        }
        pinneadapter.notifyDataSetChanged();
        setPrice();
    }

    @Override
    public void onRemovePriduct(GoodsEntity product) {
        for (int i = 0; i < goodsTypes.size(); i++) {
            goodsEntities = goodsTypes.get(i).getGoodsEntities();
            for (GoodsEntity shopProduct : goodsEntities) {
                if (product.getId() == shopProduct.getId()) {
                    cachegoodsEntities.remove(product);
                    goodsCatOrderAdapter.notifyDataSetChanged();
                    shopProduct.setNumber(shopProduct.getNumber());
                }
            }
        }
        pinneadapter.notifyDataSetChanged();
        goodsCatOrderAdapter.notifyDataSetChanged();
        setPrice();
    }


    private void showAMAlert() {
        hideLayout();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.preorder_layout, null);
        builder.setView(view);
        builder.setCancelable(false);
//        builder.setView(view);
        final AlertDialog tuidialog = builder.create();
        tuidialog.show();
        ListView tui = (ListView) view.findViewById(R.id.preordr_list);
        GoodsCatPerAdapter adapter = new GoodsCatPerAdapter(cachegoodsEntities, this);
        tui.setAdapter(adapter);
        tui.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                }
            }
        });
        final EditText edit = (EditText) view.findViewById(R.id.preordr_note);

        final TextView ishow = (TextView) view.findViewById(R.id.per_edit);
        ishow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ishow.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
            }
        });
        LinearLayout outliner = (LinearLayout) view.findViewById(R.id.out_liner);
        outliner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Log.e("kxflog","click");
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                }
            }
        });
        TextView txtam = (TextView) view.findViewById(R.id.preordr_am);
        txtam.setText(cachAm);
        Button backbtn = (Button) view.findViewById(R.id.preordr_back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tuidialog.dismiss();
            }
        });
        Button gobtn = (Button) view.findViewById(R.id.preordr_go);
        gobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderId == null) {
                    String note = edit.getText().toString();
                    tuidialog.dismiss();
                    inserData(note);
                } else {
                    addGoosNext();
                }
            }
        });
    }


    private void inserData(String note) {
        dialog = ProgressDialog.show(this, "", "正在下单", true);
        JSONObject json = new JSONObject();
        try {
            JSONArray cacharray = new JSONArray();
            for (int i = 0; i < cachegoodsEntities.size(); i++) {
                JSONObject ecahjson = new JSONObject();
                GoodsEntity entity = cachegoodsEntities.get(i);
                ecahjson.put("number", entity.getNumber());
                ecahjson.put("goods", entity.getGoods());
                ecahjson.put("id", entity.getId());
                cacharray.put(ecahjson);
            }
            json.put("merchantId", merchantid);
            json.put("storeId", storeid);
            json.put("deskId", deskid);
            json.put("customerId", "0");
            json.put("personNum", personNum);
            json.put("createTime", new Date() + "");
            json.put("orderWay", 1);
            json.put("orderAmount", cachAm);
            json.put("orderStatus", 3);
            json.put("payWay", "0");
            json.put("payOrderId", "0");
            json.put("operId", operid);
            json.put("remark", note);
            json.put("details", cacharray);
        } catch (Exception e) {
            Log.e("kxflog", "inserData  error");
        }
        RequestParams re = new RequestParams(NetTools.HOSTURL + "orderGood/AddOrders");
        re.setAsJsonContent(true);
        re.setBodyContent(json.toString());
        x.http().post(re, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //  Log.e("kxflog", result);
                try {
                    dismissDialog();
                    JSONObject jsonObject = new JSONObject(result);
                    int backcode = jsonObject.getInt("back_code");
                    if (backcode == 200) {
                        Toast.makeText(GoodsCatActivity.this, "下单成功", Toast.LENGTH_SHORT).show();
                        long id = jsonObject.getLong("orderid");
                        Intent intent = new Intent(GoodsCatActivity.this, FoodOrderDetailActivity.class);
                        intent.putExtra("orderid", String.valueOf(id));
                        intent.putExtra("isprint", "on");
                        intent.putExtra("dname", "on");
                        intent.putExtra("isapp", true);
                        startActivity(intent);
                        GoodsCatActivity.this.finish();
                    } else {
                        String backmsg = jsonObject.optString("error_msg");
                        Toast.makeText(GoodsCatActivity.this, backmsg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                dismissDialog();

            }

            @Override
            public void onFinished() {
                dismissDialog();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cachegoodsEntities = null;//huancun
        listView = null;//分类
//        pinnedHeaderListView = null;//列表
        if (pinnedHeaderListView != null) {
            pinnedHeaderListView.setAdapter(null);
            pinnedHeaderListView = null;
        }
        goodsEntities = null;//huancun
        goodsTypes = null;//huancun
        pinneadapter = null;
        sType = null;
        orderListView = null;
        order_bgview = null;
        catImag = null;
        cardLayout = null;
        cardShopLayout = null;

    }

    /**
     * 加菜
     */
    public void addGoosNext() {
        dialog = ProgressDialog.show(this, "", "正在加菜", true);
        JSONObject json = new JSONObject();
        try {
            JSONArray cacharray = new JSONArray();
            for (int i = 0; i < cachegoodsEntities.size(); i++) {
                JSONObject ecahjson = new JSONObject();
                GoodsEntity entity = cachegoodsEntities.get(i);
                ecahjson.put("number", entity.getNumber());
                ecahjson.put("goods", entity.getGoods());
                ecahjson.put("id", entity.getId());
                cacharray.put(ecahjson);
            }
            json.put("goods", cacharray);
            json.put("orderid", orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final RequestParams re = new RequestParams(NetTools.HOSTURL + "orderGood/addGoods");
        re.setAsJsonContent(true);
        re.setBodyContent(json.toString());
        x.http().post(re, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    dismissDialog();
                    JSONObject jsonObject = new JSONObject(result);
                    int backcode = jsonObject.getInt("back_code");
                    if (backcode == 200) {
                        Toast.makeText(GoodsCatActivity.this, "下单成功", Toast.LENGTH_SHORT).show();
                        long id = jsonObject.getLong("orderid");
                        Intent intent = new Intent(GoodsCatActivity.this, FoodOrderDetailActivity.class);////
                        intent.putExtra("orderid", String.valueOf(id));
                        intent.putExtra("isprint", "on");
                        intent.putExtra("dname", "on");
                        startActivity(intent);
                        GoodsCatActivity.this.finish();
                    } else {
                        String backmsg = jsonObject.optString("error_msg");
                        Toast.makeText(GoodsCatActivity.this, backmsg, Toast.LENGTH_SHORT).show();
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

    private void showMeroory() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(info);
        Log.i("kxflog", "系统剩余内存:" + (info.availMem >> 10) + "k");
        Log.i("kxflog", "系统是否处于低内存运行：" + info.lowMemory);
        Log.i("kxflog", "当系统剩余内存低于" + info.threshold + "时就看成低内存运行");
    }


}
