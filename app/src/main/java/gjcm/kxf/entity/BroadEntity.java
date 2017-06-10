package gjcm.kxf.entity;

/**
 * Created by kxf on 2017/2/22.
 */
public class BroadEntity {

    private String id;
    private String broadname;
    private int broadstus;
    private double money;
    private int personnum;
    private String areawaiter;
    private String date;
    private int dstatus;

    public int getDstatus() {
        return dstatus;
    }

    public void setDstatus(int dstatus) {
        this.dstatus = dstatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBroadname() {
        return broadname;
    }

    public void setBroadname(String broadname) {
        this.broadname = broadname;
    }

    public int getBroadstus() {
        return broadstus;
    }

    public void setBroadstus(int broadstus) {
        this.broadstus = broadstus;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getPersonnum() {
        return personnum;
    }

    public void setPersonnum(int personnum) {
        this.personnum = personnum;
    }

    public String getAreawaiter() {
        return areawaiter;
    }

    public void setAreawaiter(String areawaiter) {
        this.areawaiter = areawaiter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BroadEntity(String id, String broadname, int broadstus, double money, int personnum, String areawaiter, String date, int dstatus) {
        this.id = id;
        this.broadname = broadname;
        this.broadstus = broadstus;
        this.money = money;
        this.personnum = personnum;
        this.areawaiter = areawaiter;
        this.date = date;
        this.dstatus = dstatus;
    }

    public BroadEntity() {
    }
}
