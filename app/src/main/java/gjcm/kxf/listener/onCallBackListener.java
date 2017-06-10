package gjcm.kxf.listener;



import gjcm.kxf.entity.GoodsEntity;

/**
 * 食品列表中+-
 * 接口回调
 */
public interface onCallBackListener {
    /**
     * Type表示添加或减少
     * @param product
     * @param type
     */
    void updateProduct(GoodsEntity product, String type);
}
