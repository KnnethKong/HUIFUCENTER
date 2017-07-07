package gjcm.kxf.adapter;

/**
 * Created by kxf on 2017/6/8.
 */

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import gjcm.kxf.entity.BroadEntity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.listener.BroadItemClick;

/**
 * Created by kxf on 2017/2/22.
 */
public class BroadAdapter extends RecyclerView.Adapter<BroadAdapter.BoradHolder> {
    private BroadItemClick broadItemClick;

    public BroadItemClick getBroadItemClick() {
        return broadItemClick;
    }

    public void setBroadItemClick(BroadItemClick broadItemClick) {
        this.broadItemClick = broadItemClick;
    }

    ArrayList<BroadEntity> broadEntities;

    public BroadAdapter(ArrayList<BroadEntity> broadEntities, BroadItemClick broadItemClick) {
        this.broadEntities = broadEntities;
        this.broadItemClick = broadItemClick;
    }

    @Override
    public BoradHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.broad_item_layout2, parent, false);

        // View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.broad_item_layout, parent, false);
        BoradHolder boradHolder = new BoradHolder(itemView);

        return boradHolder;
    }

    @Override
    public void onBindViewHolder(final BoradHolder holder, final int position) {

        BroadEntity entity = broadEntities.get(position);
        int status = entity.getDstatus();
        if (status == 0) {
            holder.linearLayout.setBackground(null);
//            holder.linearLayout.setBackgroundResource(R.drawable.broadnobg);
            holder.linearLayout.setBackgroundColor(Color.parseColor("#c8c8c8"));

        } else {
            holder.linearLayout.setBackground(null);
            holder.linearLayout.setBackgroundColor(Color.parseColor("#004c94"));
            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (broadItemClick != null) {
                        int p = holder.getLayoutPosition();
                        broadItemClick.longClick(p);
                        return true;
                    } else
                        return false;
                }
            });
        }
        //        holder.txtwt.setText(entity.getAreawaiter());
        holder.txtcz.setText(entity.getBroadname() + "  " + entity.getPersonnum());
        //        holder.txtam.setText(entity.getMoney() + "");
        //        holder.txtdt.setText(entity.getDate());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (broadItemClick != null) {
                    int p = holder.getLayoutPosition();
                    broadItemClick.clickItem(p);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return (broadEntities == null) ? 0 : broadEntities.size();
    }

    class BoradHolder extends RecyclerView.ViewHolder {
        private TextView txtcz, txtam, txtwt, txtdt;//餐桌 价格 负责人 时间
        private LinearLayout linearLayout;

        public BoradHolder(View itemView) {
            super(itemView);
            txtcz = (TextView) itemView.findViewById(R.id.broad_item_cz);
            //            txtam = (TextView) itemView.findViewById(R.id.broad_item_am);
            //            txtwt = (TextView) itemView.findViewById(R.id.broad_item_wt);
            //            txtdt = (TextView) itemView.findViewById(R.id.broad_item_dt);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.broad_item_liner);
        }
    }
}
