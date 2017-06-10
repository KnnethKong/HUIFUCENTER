package gjcm.kxf.entity;

/**
 * Created by kxf on 2016/11/29.
 * "id":134572,
 * "orderNumber":"201611280909038251077100",
 * "orderAmount":0.01,
 * "discountAmount":0,
 * "realPayAmount":0,
 * "paidInAmount":0,
 * "status":0,
 * "type":0,
 * "refundAmount":0,
 * "channel":5,
 * "refundFlag":1,
 * "orderType":"微信",
 * "statusText":"未支付",
 * "storeName":"乙密台韩国芝士排骨",
 * "payNumber":0,
 * "refundNumber":0,
 * "payAmount":0,
 * "realname":"shayan2",
 * "qrcodeName":"ertert"
 */
public class MerchantOrderCommon {
    private String id;
    private String orderNumber;
    private String orderAmount;
    private String statusText;
    private String storeName;
    private String realname;
    private String storeNian;//16-12-05
    private String realnYue;//12:56:25 格式
private String orderType;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getStoreNian() {
        return storeNian;
    }

    public void setStoreNian(String storeNian) {
        this.storeNian = storeNian;
    }

    public String getRealnYue() {
        return realnYue;
    }

    public void setRealnYue(String realnYue) {
        this.realnYue = realnYue;
    }

//    public MerchantOrderCommon(String id, String orderNumber, String orderAmount, String statusText, String storeName, String realname) {
//        this.id = id;
//        this.orderNumber = orderNumber;
//        this.orderAmount = orderAmount;
//        this.statusText = statusText;
//        this.storeName = storeName;
//        this.realname = realname;
//    }

    /**
     * 新加了 页面对应的年月日秒格式
     */
    public MerchantOrderCommon(String id, String orderNumber, String orderAmount, String statusText, String storeNian, String realnYue, String orderType) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderAmount = orderAmount;
        this.statusText = statusText;
        this.storeNian = storeNian;
        this.realnYue = realnYue;
        this.orderType=orderType;
    }

    public MerchantOrderCommon() {
    }
}
