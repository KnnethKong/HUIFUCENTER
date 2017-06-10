package gjcm.kxf.entity;

/**
 * Created by kxf on 2016/12/30.
 */
public class MerchantUserEntity {

    private int userid;
    private int storeid;
    private String realname;
    private String type11;
    private String merchantEnable;

    public MerchantUserEntity(int userid, int storeid, String realname, String type11, String merchantEnable) {
        this.userid = userid;
        this.storeid = storeid;
        this.realname = realname;
        this.type11 = type11;
        this.merchantEnable = merchantEnable;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getStoreid() {
        return storeid;
    }

    public void setStoreid(int storeid) {
        this.storeid = storeid;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getType11() {
        return type11;
    }

    public void setType11(String type11) {
        this.type11 = type11;
    }

    public String getMerchantEnable() {
        return merchantEnable;
    }

    public void setMerchantEnable(String merchantEnable) {
        this.merchantEnable = merchantEnable;
    }
}
