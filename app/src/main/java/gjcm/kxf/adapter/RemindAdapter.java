package gjcm.kxf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gjcm.kxf.entity.RemindEntity;
import gjcm.kxf.huifucenter.R;

/**
 * Created by kxf on 2017/2/7.
 * 点餐消息列表
 */
public class RemindAdapter extends BaseAdapter {
    private ArrayList<RemindEntity> printEntities;
    private Context context;//

    public RemindAdapter(ArrayList<RemindEntity> printEntities, Context context) {
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
            view = inflater.inflate(R.layout.remind_item_layout, null);
            myHolder.tid = (TextView) view.findViewById(R.id.remind_item_id);
            myHolder.tdate = (TextView) view.findViewById(R.id.remind_item_date);
            myHolder.tinfo = (TextView) view.findViewById(R.id.remind_item_info);
            myHolder.tstatus = (TextView) view.findViewById(R.id.remind_item_status);
            view.setTag(myHolder);
        } else {
            myHolder = (MyHolder) view.getTag();
        }
        RemindEntity entity = printEntities.get(i);
        myHolder.tid.setText("  " + entity.getId());
        long operid = entity.getOperId();
        String printtype = "none";
        if (operid > 0) {
            printtype = "已处理";
        } else {
            printtype = "未处理";
        }
        myHolder.tstatus.setText(printtype);
        myHolder.tinfo.setText(entity.getMsgInfo());
        myHolder.tdate.setText(entity.getCreateTime());
        return view;
    }

    class MyHolder {
        private TextView tid, tstatus, tinfo, tdate;
    }
}
