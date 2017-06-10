package gjcm.kxf.entity;

import java.util.ArrayList;

/**
 * Created by kxf on 2017/2/22.
 */
public class GoodsTypeEntity {

    private String snam;
    private String id;
    private ArrayList<GoodsEntity> goodsEntities;

    public String getSnam() {
        return snam;
    }

    public void setSnam(String snam) {
        this.snam = snam;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<GoodsEntity> getGoodsEntities() {
        return goodsEntities;
    }

    public void setGoodsEntities(ArrayList<GoodsEntity> goodsEntities) {
        this.goodsEntities = goodsEntities;
    }

    public GoodsTypeEntity(String snam, String id, ArrayList<GoodsEntity> goodsEntities) {
        this.snam = snam;
        this.id = id;
        this.goodsEntities = goodsEntities;
    }

    public GoodsTypeEntity() {
    }
}
