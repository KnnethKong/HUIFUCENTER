package gjcm.kxf.listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created by kxf on 2017/6/6.
 */
public class RecyclerScrollListener extends RecyclerView.OnScrollListener {
    private MyScrollImpl myScrollimpl;
    private int TILEHEIGHT = 20;
    private int distance = 0;
    private boolean isShow = true;

    public RecyclerScrollListener(MyScrollImpl myScroll) {
        this.myScrollimpl = myScroll;
    }

//    @Override
//    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//        super.onScrollStateChanged(recyclerView, newState);
//    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {// 水平、垂直滚动距离
        //dx >0 左  dx<0  右  dy>0 上  dy<0 下
        super.onScrolled(recyclerView, dx, dy);
        if (distance > TILEHEIGHT && isShow) {
            distance = 0;
            isShow = false;
            if (myScrollimpl != null)
                myScrollimpl.hide();
        } else if (distance < -TILEHEIGHT && !isShow) {
            distance = 0;
            isShow = true;
            if (myScrollimpl != null)
                myScrollimpl.show();
        }
        if (isShow && dy > 0 || (!isShow) && dy < 0)
            distance += dy;
    }
}
