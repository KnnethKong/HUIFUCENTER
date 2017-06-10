package gjcm.kxf.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import gjcm.kxf.entity.GoodsEntity;
import gjcm.kxf.entity.GoodsTypeEntity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.listener.onCallBackListener;

/**
 * Created by kxf on 2017/2/22.
 */
public class GoodsItemAdapter extends SectionedBaseAdapter {
    List<GoodsTypeEntity> pruductCagests;
    private HolderClickListener mHolderClickListener;
    private Context context;
    private LayoutInflater mInflater;
private ImageOptions imageOptions;
    private onCallBackListener callBackListener;

    public void setCallBackListener(onCallBackListener callBackListener) {
        this.callBackListener = callBackListener;
    }


    public GoodsItemAdapter(Context context, List<GoodsTypeEntity> pruductCagests,ImageOptions imageOptions) {
        this.context = context;
        this.pruductCagests = pruductCagests;
        mInflater = LayoutInflater.from(context);
        this.imageOptions=imageOptions;
    }

    @Override
    public Object getItem(int section, int position) {
        return pruductCagests.get(section).getGoodsEntities().get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return position;
    }

    @Override
    public int getSectionCount() {
        return pruductCagests == null ? 0 : pruductCagests.size();
    }

    @Override
    public int getCountForSection(int section) {
        ArrayList<GoodsEntity> entity = pruductCagests.get(section).getGoodsEntities();
        if (entity == null)
            return 0;
        else
            return pruductCagests.get(section).getGoodsEntities().size();
    }

    @Override
    public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.goods_item, null);
            viewHolder = new ViewHolder();
            viewHolder.head = (ImageView) convertView.findViewById(R.id.good_item_img);
            viewHolder.name = (TextView) convertView.findViewById(R.id.good_item_name);
            viewHolder.price = (TextView) convertView.findViewById(R.id.good_item_am);
            viewHolder.increase = (TextView) convertView.findViewById(R.id.good_item_add);
            viewHolder.reduce = (TextView) convertView.findViewById(R.id.good_item_del);
            viewHolder.shoppingNum = (TextView) convertView.findViewById(R.id.good_item_number);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final GoodsEntity product = pruductCagests.get(section).getGoodsEntities().get(position);
        viewHolder.name.setText(product.getGoods());
        double price = product.getPrice() / 100;
        viewHolder.price.setText(String.valueOf(price));
        viewHolder.shoppingNum.setText(String.valueOf(product.getNumber()));
       String sp= product.getPicture();
        x.image().bind(viewHolder.head,sp,imageOptions);
        viewHolder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = product.getNumber();
                num++;
                product.setNumber(num);
                viewHolder.shoppingNum.setText(product.getNumber() + "");
                if (callBackListener != null) {
                    callBackListener.updateProduct(product, "1");
                } else {
                }
                if (mHolderClickListener != null) {
                    int[] start_location = new int[2];
                    viewHolder.shoppingNum.getLocationInWindow(start_location);//获取点击商品图片的位置
                    Drawable drawable = context.getResources().getDrawable(R.mipmap.adddetail, null);//复制一个新的商品图标
                    //TODO:解决方案，先监听到左边ListView的Item中，然后在开始动画添加
//                    mHolderClickListener.onHolderClick(drawable, start_location);
                }
            }
        });
        viewHolder.reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = product.getNumber();
                if (num > 0) {
                    num--;
                    product.setNumber(num);
                    viewHolder.shoppingNum.setText(product.getNumber() + "");
                    if (callBackListener != null) {
                        callBackListener.updateProduct(product, "2");
                    } else {
                    }
                }
            }
        });

        viewHolder.shoppingNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    int shoppingNum = Integer.parseInt(viewHolder.shoppingNum.getText().toString());
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        /**
         * 商品图片
         */
        public ImageView head;
        /**
         * 商品名称
         */
        public TextView name;
        /**
         * 商品价格
         */
        public TextView price;
        /**
         * 增加
         */
        public TextView increase;
        /**
         * 商品数目
         */
        public TextView shoppingNum;
        /**
         * 减少
         */
        public TextView reduce;
    }

    public void SetOnSetHolderClickListener(HolderClickListener holderClickListener) {
        this.mHolderClickListener = holderClickListener;
    }

    public interface HolderClickListener {
        public void onHolderClick(Drawable drawable, int[] start_location);
    }


    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflator.inflate(R.layout.goods_header_item, null);
        } else {
            layout = (LinearLayout) convertView;
        }
        layout.setClickable(false);
        String sname = pruductCagests.get(section).getSnam();
        ((TextView) layout.findViewById(R.id.gooditem_header)).setText(sname);
        return layout;
    }

}
