package gjcm.kxf.listener;


import gjcm.kxf.entity.GoodsEntity;

/**
 * 购物车添加接口回调
 */
public interface ShopToDetailListener {
    /**
     * Type表示添加或减少
     * @param product
     * @param type
     */
    void onUpdateDetailList(GoodsEntity product, String type);

    void onRemovePriduct(GoodsEntity product);
}
