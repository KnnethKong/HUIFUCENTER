package gjcm.kxf.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import gjcm.kxf.entity.MerchantOrderCommon;
import gjcm.kxf.huifucenter.R;

/**
 * Created by kxf on 2016/12/22.
 */
public class OrderCommonAdapter extends BaseAdapter {
    private ArrayList<MerchantOrderCommon> commonArrayList;
    Context context;//

    public OrderCommonAdapter(ArrayList<MerchantOrderCommon> menus, Context context) {
        this.commonArrayList = menus;
        this.context = context;
    }


    @Override
    public int getCount() {
        return commonArrayList == null ? 0 : commonArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return commonArrayList == null ? "" : commonArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private Bitmap bitmap = null;

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.order_list_item, null);
            viewHolder.txtAmount = (TextView) view.findViewById(R.id.other_amount);
            viewHolder.txtOrder = (TextView) view.findViewById(R.id.other_order);
            viewHolder.textNian = (TextView) view.findViewById(R.id.other_nian);
            viewHolder.textYue = (TextView) view.findViewById(R.id.other_yue);
            viewHolder.txtStatus = (TextView) view.findViewById(R.id.other_satus);
            viewHolder.imageType = (ImageView) view.findViewById(R.id.other_icontype);
            view.setTag(viewHolder);///////----->切记
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        MerchantOrderCommon merchantOrderCommo = commonArrayList.get(i);
        String status = merchantOrderCommo.getStatusText();
        viewHolder.textYue.setText(merchantOrderCommo.getRealnYue());
        viewHolder.textNian.setText(merchantOrderCommo.getStoreNian());
        viewHolder.txtAmount.setText(merchantOrderCommo.getOrderAmount());
        viewHolder.txtStatus.setText(status);
        viewHolder.txtOrder.setText(merchantOrderCommo.getOrderNumber());
        if (merchantOrderCommo.getOrderType().equals("1"))
            bitmap = readBitMap(context, R.mipmap.zfbbiao);
        else
            bitmap = readBitMap(context, R.mipmap.icon_wechat);
        viewHolder.imageType.setImageBitmap(bitmap);
        return view;
    }

    private Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    class ViewHolder {
        private TextView txtAmount, txtOrder, txtStatus, textNian, textYue;
        private ImageView imageType;
    }
}
