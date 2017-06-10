package gjcm.kxf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gjcm.kxf.entity.PrintEntity;
import gjcm.kxf.huifucenter.R;

/**
 * Created by kxf on 2017/2/7.
 */
public class StorePrintAdapter extends BaseAdapter {
    private ArrayList<PrintEntity> printEntities;
    private Context context;//

    public StorePrintAdapter(ArrayList<PrintEntity> printEntities, Context context) {
        this.printEntities = printEntities;
        this.context = context;
    }


    @Override
    public int getCount() {
        return printEntities == null ? 0 : printEntities.size();
    }

    @Override
    public Object getItem(int i) {
        return printEntities == null ? "" : printEntities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MyHolder myHolder = null;
        if (view == null) {
            myHolder = new MyHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.store_print_item, null);
            myHolder.tname = (TextView) view.findViewById(R.id.storeprint_item_name);
            myHolder.ttype = (TextView) view.findViewById(R.id.storeprint_item_type);
            myHolder.tmodel = (TextView) view.findViewById(R.id.storeprint_item_mode);
            myHolder.tip = (TextView) view.findViewById(R.id.storeprint_item_ip);
            myHolder.tdel = (TextView) view.findViewById(R.id.storeprint_item_del);
            view.setTag(myHolder);
        } else {
            myHolder = (MyHolder) view.getTag();
        }
        PrintEntity entity = printEntities.get(i);
        myHolder.tname.setText(entity.getName());
        String printtype = "未绑定";
        if (entity.getPrintType() == 1) {
            printtype = "后厨打印机";
        } else {
            printtype = "前台打印机";
        }
        myHolder.ttype.setText(printtype);
        int mode = entity.getPrintMode();
        String smode = "";
        if (mode == 1) {
            smode = "网络";
        } else {
            smode = "windows驱动";
        }
        myHolder.tmodel.setText(smode);
        myHolder.tip.setText(entity.getIp() + ":" + entity.getPort());
        if (entity.getDelFlag() != 1) {
            myHolder.tdel.setText("已删除");
        }
        return view;
    }

    class MyHolder {
        private TextView tname, ttype, tmodel, tip, tdel;
    }
}
