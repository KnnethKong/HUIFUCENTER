package gjcm.kxf.entity;

/**
 * 接单list entity
 * Created by kxf on 2017/2/6.
 */
public class DaningEntity {
    private String sid;
    private String scanzhuo;
    private String srenshu;
    private String sdate;
    private double smoney;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public DaningEntity(String sid, String scanzhuo, String srenshu, String sdate, double smoney, String status) {
        this.sid = sid;
        this.scanzhuo = scanzhuo;
        this.srenshu = srenshu;
        this.sdate = sdate;
        this.smoney = smoney;
        this.status = status;
    }

    public DaningEntity() {
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getScanzhuo() {
        return scanzhuo;
    }

    public void setScanzhuo(String scanzhuo) {
        this.scanzhuo = scanzhuo;
    }

    public String getSrenshu() {
        return srenshu;
    }

    public void setSrenshu(String srenshu) {
        this.srenshu = srenshu;
    }

    public double getSmoney() {
        return smoney;
    }

    public void setSmoney(double smoney) {
        this.smoney = smoney;
    }
}
