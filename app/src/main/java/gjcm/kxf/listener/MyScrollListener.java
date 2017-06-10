package gjcm.kxf.listener;

import android.widget.AbsListView;

/**
 * Created by kxf on 2017/6/5.
 * 无用
 */
public class MyScrollListener implements AbsListView.OnScrollListener {

    private static final int THRESHOLD = 20;
    private int distance = 0;
    private boolean visiable = true;

    private MyScrollImpl myScroll;

    public MyScrollListener(MyScrollImpl myScroll) {
        this.myScroll = myScroll;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
/**
 *scrollState有三种状态，分别是SCROLL_STATE_IDLE、SCROLL_STATE_TOUCH_SCROLL、SCROLL_STATE_FLING
 *SCROLL_STATE_IDLE是当屏幕停止滚动时
 *SCROLL_STATE_TOUCH_SCROLL是当用户在以触屏方式滚动屏幕并且手指仍然还在屏幕上时
 *SCROLL_STATE_FLING是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时（
 */
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        /**
         * firstVisibleItem 表示在当前屏幕显示的第一个listItem在整个listView里面的位置（下标从0开始）
         * visibleItemCount表示在现时屏幕可以见到的ListItem(部分显示的ListItem也算)总数
         * totalItemCount表示ListView的ListItem总数
         * listView.getLastVisiblePosition()表示在现时屏幕最后一个ListItem
         * (最后ListItem要完全显示出来才算)在整个ListView的位置（下标从0开始）
         */

        if (totalItemCount > visibleItemCount && !visiable) {
            visiable = true;
            if (myScroll != null)
                myScroll.show();

        } else if (visibleItemCount == totalItemCount) {
            visiable = false;
            if (myScroll != null)
                myScroll.hide();
        }

    }
}
