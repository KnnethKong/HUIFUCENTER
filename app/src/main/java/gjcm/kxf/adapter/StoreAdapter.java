package gjcm.kxf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import gjcm.kxf.entity.MerchantStoreEntity;
import gjcm.kxf.huifucenter.R;

/**
 * Created by kxf on 2016/12/30.
 */
public class StoreAdapter extends BaseAdapter {

    private List<MerchantStoreEntity> storeEntityList;
    Context context;//

    public StoreAdapter(List<MerchantStoreEntity> storeEntityList, Context context) {
        this.storeEntityList = storeEntityList;
        this.context = context;
    }


    @Override
    public int getCount() {
        return storeEntityList == null ? 0 : storeEntityList.size();
    }

    @Override
    public Object getItem(int i) {
        return storeEntityList == null ? "" : storeEntityList.get(i);
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
            view = inflater.inflate(R.layout.spriner_txt, null);
            viewHolder.txtname = (TextView) view.findViewById(R.id.spiner_txt);
            view.setTag(viewHolder);///////----->切记
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String sname = storeEntityList.get(i).getStoreName();
        viewHolder.txtname.setText(sname);
        return view;
    }

    class ViewHolder {
        private TextView txtname;
    }
}
