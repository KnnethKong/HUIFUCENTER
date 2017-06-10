package gjcm.kxf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gjcm.kxf.entity.GoodsEntity;
import gjcm.kxf.entity.MerchantStoreEntity;
import gjcm.kxf.huifucenter.R;

/**
 * Created by kxf on 2016/12/30.
 * 是否下单？
 */
public class GoodsCatPerAdapter extends BaseAdapter {

    private ArrayList<GoodsEntity> cachegoodsEntities;//huancun
    Context context;//

    public GoodsCatPerAdapter(ArrayList<GoodsEntity> cachegoodsEntities, Context context) {
        this.cachegoodsEntities = cachegoodsEntities;
        this.context = context;
    }

    @Override
    public int getCount() {
        return cachegoodsEntities == null ? 0 : cachegoodsEntities.size();
    }

    @Override
    public Object getItem(int i) {
        return cachegoodsEntities == null ? "" : cachegoodsEntities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.goods_cat_litem, null);
            viewHolder.tname = (TextView) view.findViewById(R.id.goodscat_name);
            viewHolder.tnum = (TextView) view.findViewById(R.id.goodscat_num);
            view.setTag(viewHolder);///////----->切记
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String sname = cachegoodsEntities.get(i).getGoods();
        int number = cachegoodsEntities.get(i).getNumber();
        viewHolder.tname.setText(sname);
        viewHolder.tnum.setText(number+"");
        return view;
    }

    class ViewHolder {
        private TextView tname, tnum;
    }
}
