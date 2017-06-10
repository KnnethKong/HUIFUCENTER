package gjcm.kxf.entity;

/**
 * Created by kxf on 2017/3/6.
 * <p/>
 */
public class PrintEntity {
    private long id;
    private int printType;
    private int printMode;
    private int delFlag;
    private String ip;
    private String name;
    private String port;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPrintType() {
        return printType;
    }

    public void setPrintType(int printType) {
        this.printType = printType;
    }

    public int getPrintMode() {
        return printMode;
    }

    public void setPrintMode(int printMode) {
        this.printMode = printMode;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public PrintEntity(long id, int printType, int printMode, int delFlag, String ip, String name, String port) {
        this.id = id;
        this.printType = printType;
        this.printMode = printMode;
        this.delFlag = delFlag;
        this.ip = ip;
        this.name = name;
        this.port = port;
    }

    public PrintEntity() {
    }
}
