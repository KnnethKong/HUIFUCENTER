package gjcm.kxf.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import gjcm.kxf.entity.FoodsEntity;
import gjcm.kxf.huifucenter.R;

/**
 * 餐品订单详情
 */
public class FoodDeatailAdapter extends BaseAdapter {
    private List<FoodsEntity> foodsEntities;//
    private Context context;//
    private ImageOptions imageOptions;
    private boolean isedit;
    private Delectface delectface;

    public FoodDeatailAdapter(List<FoodsEntity> foodsEntities, Context context, ImageOptions imageOptions, boolean isedit, Delectface delectface) {
        this.foodsEntities = foodsEntities;
        this.context = context;
        this.isedit = isedit;
        this.imageOptions = imageOptions;
        this.delectface = delectface;
    }


    @Override
    public int getCount() {
        return foodsEntities == null ? 0 : foodsEntities.size();
    }

    @Override
    public Object getItem(int i) {
        return foodsEntities == null ? "" : foodsEntities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setDelectface(Delectface delectface) {
        this.delectface = delectface;
    }

    public Delectface getDelectface() {
        return delectface;
    }//end

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        MyHolder myHolder = null;
        if (view == null) {
            myHolder = new MyHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            if (isedit) {
                view = inflater.inflate(R.layout.food_detail_item, null);
                myHolder.oname = (TextView) view.findViewById(R.id.food_detail_item_name);
                myHolder.oprice = (TextView) view.findViewById(R.id.food_detail_item_price);
                myHolder.onum = (TextView) view.findViewById(R.id.food_detail_item_num);
                myHolder.otyle = (TextView) view.findViewById(R.id.food_detail_item_style);
                myHolder.oimg = (ImageView) view.findViewById(R.id.food_detail_item_img);
                myHolder.ostatus = (TextView) view.findViewById(R.id.food_detail_item_status);
            } else {
                view = inflater.inflate(R.layout.edit_food_item, null);
                myHolder.oname = (TextView) view.findViewById(R.id.edit_item_name);
                myHolder.onote = (TextView) view.findViewById(R.id.edit_item_note);
                myHolder.oprice = (TextView) view.findViewById(R.id.edit_item_price);
                myHolder.onum = (TextView) view.findViewById(R.id.edit_item_num);
                myHolder.odel = (TextView) view.findViewById(R.id.edit_item_del);
                myHolder.otyle = (TextView) view.findViewById(R.id.edit_item_style);
                myHolder.oadd = (TextView) view.findViewById(R.id.edit_item_add);
                myHolder.otract = (TextView) view.findViewById(R.id.edit_item_cle);
                myHolder.oimg = (ImageView) view.findViewById(R.id.edit_item_img);
            }
            view.setTag(myHolder);
        } else {
            myHolder = (MyHolder) view.getTag();
        }
        FoodsEntity entity = foodsEntities.get(i);
        myHolder.oname.setText(entity.getFname());
        x.image().bind(myHolder.oimg, entity.getFpic(), imageOptions);
        myHolder.oprice.setText(entity.getFprice() + "");
        myHolder.otyle.setText(entity.getFstyle());
        if (isedit) {
            myHolder.onum.setText(entity.getFnum() + "");
            String status = entity.getStatus();
            if ("已上菜".equals(status)) {
                myHolder.ostatus.setText(status);
                myHolder.ostatus.setTextColor(Color.parseColor("#999999"));
            } else {myHolder.ostatus.setText(status);
                myHolder.ostatus.setTextColor(Color.parseColor("#333333"));
            }
        } else {
            int s = entity.getFnum();
            myHolder.onum.setText(s + "");
            final MyHolder finalMyHolder = myHolder;
            myHolder.oadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view = (View) view.getParent();
                    String txt = finalMyHolder.onum.getText().toString();
                    delectface.add(i, txt, view);
                }
            });
            myHolder.otract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view = (View) view.getParent();
                    String txt = finalMyHolder.onum.getText().toString();
                    delectface.subtract(i, txt, view);
                }
            });
            myHolder.odel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delectface.delscale(i);

                }
            });
        }
        return view;
    }


    class MyHolder {
        private TextView oname, onote, oprice, onum, otyle, ostatus, oadd, otract, odel;
        private ImageView oimg;
    }

    public interface Delectface {
        void delscale(int i);


        void add(int i, String value, View view);


        void subtract(int i, String value, View view);
    }

}
