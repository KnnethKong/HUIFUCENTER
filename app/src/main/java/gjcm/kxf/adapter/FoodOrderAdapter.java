package gjcm.kxf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import gjcm.kxf.entity.DaningEntity;
import gjcm.kxf.huifucenter.R;

/**
 * 餐品item->
 * Created by kxf on 2017/2/6.
 */
public class FoodOrderAdapter extends BaseAdapter {
    private List<DaningEntity> daningEntityList;
    private Context context;//

    public FoodOrderAdapter(List<DaningEntity> daningEntityList, Context context) {
        this.daningEntityList = daningEntityList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return daningEntityList == null ? 0 : daningEntityList.size();
    }

    @Override
    public Object getItem(int i) {
        return daningEntityList == null ? "" : daningEntityList.get(i);
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
            view = inflater.inflate(R.layout.foodorder_item, null);
            myHolder.tdate = (TextView) view.findViewById(R.id.food_item_shijian);
            myHolder.trenshu = (TextView) view.findViewById(R.id.food_item_renshu);
            myHolder.tzhuoshu = (TextView) view.findViewById(R.id.food_item_zhuo);
            myHolder.tid = (TextView) view.findViewById(R.id.food_item_ordernum);
            myHolder.tstatus = (TextView) view.findViewById(R.id.food_item_status);
            myHolder.tmoney = (TextView) view.findViewById(R.id.food_item_money);
            view.setTag(myHolder);
        } else {
            myHolder = (MyHolder) view.getTag();
        }
        DaningEntity entity = daningEntityList.get(i);
        myHolder.trenshu.setText(entity.getSrenshu());
        myHolder.tdate.setText(entity.getSdate());
        myHolder.tzhuoshu.setText(entity.getScanzhuo());
        myHolder.tid.setText(entity.getSid());
        String temps = entity.getStatus();
        myHolder.tstatus.setBackground(null);
        switch (temps) {
            case "已下单":
                myHolder.tstatus.setBackgroundResource(R.drawable.yixiadan);
                break;
            case "已完成":
            case "已结账":
                myHolder.tstatus.setBackgroundResource(R.drawable.yijiezhang);
                break;
            case "待接单":
                myHolder.tstatus.setBackgroundResource(R.drawable.daijiedan);
                break;
            case "拒收单":
                myHolder.tstatus.setBackgroundResource(R.drawable.jvshoudan);
                break;
            case "已撤桌":
                myHolder.tstatus.setBackgroundResource(R.drawable.yichezhuo);
                break;
        }
//        if ("已结账".equals(temps) || "已完成".equals(temps)) {
//        } else if ("待接单".equals(temps)) {
//            myHolder.tstatus.setBackgroundResource(R.drawable.daijiedan);
//        } else if ("拒收单".equals(temps)) {
//            myHolder.tstatus.setBackgroundResource(R.drawable.jvshoudan);
//        } else if ("已撤桌".equals(temps)) {
//            myHolder.tstatus.setBackgroundResource(R.drawable.yichezhuo);
//
//        }
        myHolder.tstatus.setText(temps);
        myHolder.tmoney.setText("" + entity.getSmoney());
        return view;
    }

    class MyHolder {
        private TextView tzhuoshu, trenshu, tdate, tstatus, tid, tmoney;
    }

}
