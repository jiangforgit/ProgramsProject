package programs.publicmodule.core.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caijiang.chen on 2017/12/29.
 */

public class SendPackEntity<T> {

    private String packid;
    private String packtype;
    private String packversion;
    private String agent;
    private String account;
    private String devicetype;
    private String deviceid;

    private List<T> contents = new ArrayList<T>();

    public String getPackid() {
        return packid;
    }

    public void setPackid(String packid) {
        this.packid = packid;
    }

    public String getPacktype() {
        return packtype;
    }

    public void setPacktype(String packtype) {
        this.packtype = packtype;
    }

    public String getPackversion() {
        return packversion;
    }

    public void setPackversion(String packversion) {
        this.packversion = packversion;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public List<T> getContents() {
        return contents;
    }

}
