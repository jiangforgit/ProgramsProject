package programs.publicmodule.core.enums;

/**
 * Created by Jiang on 2016/5/26.
 */
public enum EnumNetStatus {

    unKnown("unKnown","未知状态"),Mobile("mobile","3g/4g网络"),Wifi("wifi","wifi网络");

    private String status = "";
    private String desc = "";

    private EnumNetStatus(String statu,String ds){
        this.status = statu;
        this.desc = ds;
    }

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
