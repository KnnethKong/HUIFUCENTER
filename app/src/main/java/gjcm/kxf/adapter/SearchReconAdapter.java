package gjcm.kxf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import gjcm.kxf.entity.MerchantStoreEntity;
import gjcm.kxf.huifucenter.R;

/**
 * Created by kxf on 2017/1/3.
 */
public class SearchReconAdapter extends BaseAdapter {
    ArrayList<MerchantStoreEntity> storeEntities;
    Context context;

    public SearchReconAdapter(ArrayList<MerchantStoreEntity> storeEntities, Context context) {
        this.storeEntities = storeEntities;
        this.context = context;
    }


    @Override
    public int getCount() {
        return storeEntities == null ? 0 : storeEntities.size();
    }

    @Override
    public Object getItem(int i) {
        return storeEntities == null ? "" : storeEntities.get(i);
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
            view = inflater.inflate(R.layout.sercha_item_layout, null);
            viewHolder.txtname = (TextView) view.findViewById(R.id.search_item_txt);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        MerchantStoreEntity storeEntity = storeEntities.get(i);
        viewHolder.txtname.setText(storeEntity.getStoreName());
        return view;
    }


    class ViewHolder {
        private TextView txtname;
    }

}