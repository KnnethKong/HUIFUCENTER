package gjcm.kxf.entity;

/**
 * "orderNumber":"201701091345098845261490",
 * "orderAmount":0.03,
 * "status":1,
 * "payTime":"2017-01-09 13:45:12.0",
 * "type":1,
 * "discountAmount":0,
 * "realPayAmount":0.03,
 * "paidInAmount":0.03
 * Created by kxf on 2017/1/9.
 */
public class MerchantDetail {
    private String orderNumber;
    private String orderAmount;
    private String status;
    private String payTime;
    private String type;
    private String refoundAmount;


    public String getRefoundAmount() {
        return refoundAmount;
    }

    public void setRefoundAmount(String refoundAmount) {
        this.refoundAmount = refoundAmount;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    public String getDiscountAmount() {
//        return discountAmount;
//    }

//    public void setDiscountAmount(String discountAmount) {
//        this.discountAmount = discountAmount;
//    }
//
//    public String getRealPayAmount() {
//        return realPayAmount;
//    }
//
//    public void setRealPayAmount(String realPayAmount) {
//        this.realPayAmount = realPayAmount;
//    }
//
//    public String getPaidInAmount() {
//        return paidInAmount;
//    }
//
//    public void setPaidInAmount(String paidInAmount) {
//        this.paidInAmount = paidInAmount;
//    }

    public MerchantDetail(String orderNumber, String orderAmount, String status, String payTime, String type,String refoundAmount) {
        this.orderNumber = orderNumber;
        this.orderAmount = orderAmount;
        this.status = status;
        this.payTime = payTime;
        this.refoundAmount=refoundAmount;
        this.type = type;
    }
}
