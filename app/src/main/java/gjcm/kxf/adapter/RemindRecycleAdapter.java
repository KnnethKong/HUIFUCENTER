package gjcm.kxf.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import gjcm.kxf.entity.RemindEntity;
import gjcm.kxf.huifucenter.R;

/**
 * 消息查看
 * Created by kxf on 2017/6/6.
 */
public class RemindRecycleAdapter extends RecyclerView.Adapter<RemindRecycleAdapter.RemindViewHoder> {
    private ArrayList<RemindEntity> printEntities;

    public RemindRecycleAdapter(ArrayList<RemindEntity> printEntities) {
        this.printEntities = printEntities;
    }

    @Override
    public RemindViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.remind_item_layout, null);
        return new RemindViewHoder(itemView);
    }

    @Override
    public void onBindViewHolder(RemindViewHoder myHolder, int position) {
        RemindEntity entity = printEntities.get(position);
        myHolder.tid.setText("  " + entity.getId());
        long operid = entity.getOperId();
        String printtype = "none";
        if (operid > 0) {
            printtype = "已处理";
            myHolder.tstatus.setTextColor(Color.parseColor("#3498DB"));
        } else {
            printtype = "未处理";
            myHolder.tstatus.setTextColor(Color.parseColor("#E94A52"));
        }
        myHolder.tstatus.setText(printtype);
        myHolder.tinfo.setText(entity.getMsgInfo());
        myHolder.tdate.setText(entity.getCreateTime());
    }

    @Override
    public int getItemCount() {
        return printEntities == null ? 0 : printEntities.size();
    }

    class RemindViewHoder extends RecyclerView.ViewHolder {
        private TextView tid, tstatus, tinfo, tdate;

        public RemindViewHoder(View itemView) {
            super(itemView);
            tid = (TextView) itemView.findViewById(R.id.remind_item_id);
            tdate = (TextView) itemView.findViewById(R.id.remind_item_date);
            tinfo = (TextView) itemView.findViewById(R.id.remind_item_info);
            tstatus = (TextView) itemView.findViewById(R.id.remind_item_status);
        }
    }
}
