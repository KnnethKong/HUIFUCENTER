package gjcm.kxf.entity;

/**
 * 食品entity
 * Created by kxf on 2017/2/6.
 */
public class FoodsEntity {
    private String fid;  //食品id
    private String fname;//食品名称
    private int fnum;//食品数量
    private double fprice;//现价
    private double fnpice;//原价
    private String fpic;//食品图片
    private String fnote;//食品备注
    private String fstyle;//食品规格  ---大中小、酸甜辣
    private long operid;//操作员id
    private String status;//状态
    private int printType;//食品数量

    public int getPrintType() {
        return printType;
    }

    public void setPrintType(int printType) {
        this.printType = printType;
    }

    public long getOperid() {
        return operid;
    }

    public void setOperid(long operid) {
        this.operid = operid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @param fid    食品id
     * @param fname  食品名称
     * @param fnum   食品数量
     * @param fprice 现价
     * @param fnpice 原价
     * @param fpic   食品图片
     * @param fnote  食品备注
     * @param fstyle 食品规格 大中小、酸甜辣
     */
    public FoodsEntity(String fid, String fname, int fnum, double fprice, double fnpice, String fpic, String fnote, String fstyle, String status, int printType) {
        this.fid = fid;
        this.fname = fname;
        this.fnum = fnum;
        this.fprice = fprice;
        this.fnpice = fnpice;
        this.fpic = fpic;
        this.fnote = fnote;
        this.fstyle = fstyle;
        this.status=status;
        this.printType=printType;
    }

    public double getFnpice() {
        return fnpice;
    }

    public void setFnpice(double fnpice) {
        this.fnpice = fnpice;
    }

    public FoodsEntity() {
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public int getFnum() {
        return fnum;
    }

    public void setFnum(int fnum) {
        this.fnum = fnum;
    }

    public double getFprice() {
        return fprice;
    }

    public void setFprice(double fprice) {
        this.fprice = fprice;
    }

    public String getFpic() {
        return fpic;
    }

    public void setFpic(String fpic) {
        this.fpic = fpic;
    }

    public String getFnote() {
        return fnote;
    }

    public void setFnote(String fnote) {
        this.fnote = fnote;
    }

    public String getFstyle() {
        return fstyle;
    }

    public void setFstyle(String fstyle) {
        this.fstyle = fstyle;
    }
}
