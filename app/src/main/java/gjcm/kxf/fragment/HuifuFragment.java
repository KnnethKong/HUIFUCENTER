package gjcm.kxf.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import gjcm.kxf.drawview.CarouselView;
import gjcm.kxf.entity.RemindEntity;
import gjcm.kxf.goodorder.BroadChooseActivity;
import gjcm.kxf.goodorder.RemindActivity;
import gjcm.kxf.goodorder.StoreSummaryActivity;
import gjcm.kxf.huifucenter.CollectionActivity;
import gjcm.kxf.goodorder.FoodOrderActivity;
import gjcm.kxf.huifucenter.R;
import gjcm.kxf.tools.NetTools;

/**
 * Created by kxf on 2017/3/30.
 */
public class HuifuFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ImageOptions imageOptions;
    private String[] mImagesSrc = {
            NetTools.HOSTURL+"img/s1.jpg",
            NetTools.HOSTURL+ "img/s2.jpg",
            NetTools.HOSTURL+"img/s3.jpg",
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_huifu, null);
        context = getContext();
        view.findViewById(R.id.fragment_huifu_dcdd).setOnClickListener(this);

        TextView titletxt = (TextView) view.findViewById(R.id.store_txt);
        titletxt.setText(MainFragment.storeName);
        view.findViewById(R.id.store_remind).setOnClickListener(this);
        view.findViewById(R.id.huifu_diancai).setOnClickListener(this);
        view.findViewById(R.id.huifu_yyhz).setOnClickListener(this);
        CarouselView carouselView = (CarouselView) view.findViewById(R.id.main_fragment_carousel);
        imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setFailureDrawableId(R.mipmap.erro)
                .setLoadingDrawableId(R.mipmap.loading)
                .build();
        carouselView.setAdapter(new CarouselView.Adapter() {
            @Override
            public boolean isEmpty() {
                return false;
            }
            @Override
            public View getView(int position) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                x.image().bind(imageView, mImagesSrc[position], imageOptions);
                return imageView;
            }

            @Override
            public int getCount() {
                return mImagesSrc.length;
            }
        });
        LinearLayout collectionLiner = (LinearLayout) view.findViewById(R.id.main_fragment_collection);
        collectionLiner.setOnClickListener(this);
        return view;
    }

    @Override//生成
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_choose, menu);
    }

    @Override//相应
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_fragment_collection:
                startActivity(new Intent(getContext(), CollectionActivity.class));
                break;
            case R.id.fragment_huifu_dcdd:
                startActivity(new Intent(getContext(), FoodOrderActivity.class));
                break;
            case R.id.huifu_diancai:
                startActivity(new Intent(getContext(), BroadChooseActivity.class));
                break;
            case R.id.store_remind:
                startActivity(new Intent(getContext(), RemindActivity.class));
                break;
            case R.id.huifu_yyhz:
                startActivity(new Intent(getContext(), StoreSummaryActivity.class));
                break;
        }
    }
}


