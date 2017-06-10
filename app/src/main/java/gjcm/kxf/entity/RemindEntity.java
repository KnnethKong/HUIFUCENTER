package gjcm.kxf.entity;

/**
 * Created by kxf on 2017/3/8.
 */
public class RemindEntity {

    private long id;
    private long operId;
    private int msgType;
    private String createTime;
    private String msgInfo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOperId() {
        return operId;
    }

    public void setOperId(long operId) {
        this.operId = operId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMsgInfo() {
        return msgInfo;
    }

    public void setMsgInfo(String msgInfo) {
        this.msgInfo = msgInfo;
    }

    public RemindEntity(long id, long operId, int msgType, String createTime, String msgInfo) {
        this.id = id;
        this.operId = operId;
        this.msgType = msgType;
        this.createTime = createTime;
        this.msgInfo = msgInfo;
    }

    public RemindEntity() {
    }
}
