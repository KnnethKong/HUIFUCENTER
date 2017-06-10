package gjcm.kxf.entity;

/**
 * Created by kxf on 2016/12/30.
 */
public class MerchantStoreEntity {
    private int storeid;
    private String storeName;

    public MerchantStoreEntity(int storeid, String storeName) {
        this.storeid = storeid;
        this.storeName = storeName;
    }

    public int getStoreid() {
        return storeid;
    }

    public void setStoreid(int storeid) {
        this.storeid = storeid;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

}
