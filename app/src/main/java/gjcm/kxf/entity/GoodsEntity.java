package gjcm.kxf.entity;

/**
 * Created by kxf on 2017/2/22.
 */
public class GoodsEntity {
    private long id;
    private int price;//单价
    private String goods;//名称
    private String picture;//图片
    private String type;//
    private String createtime;
    private int number;
    private String money;
    private int printType;

    public int getPrintType() {
        return printType;
    }

    public void setPrintType(int printType) {
        this.printType = printType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public GoodsEntity(long id, int price, String goods, String type, int number, int printType, String picture) {
        this.id = id;
        this.price = price;
        this.goods = goods;
        this.type = type;
        this.number = number;
        this.printType = printType;
        this.picture = picture;


    }

    public GoodsEntity(long id, int price, String goods, String picture, String type, String createtime, int number, String money) {
        this.id = id;
        this.price = price;
        this.goods = goods;
        this.picture = picture;
        this.type = type;
        this.createtime = createtime;
        this.number = number;
        this.money = money;
    }

    public GoodsEntity() {
    }
}
