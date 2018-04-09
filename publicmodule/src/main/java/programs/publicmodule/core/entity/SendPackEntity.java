package programs.publicmodule.core.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caijiang.chen on 2017/12/29.
 * <p>
 *     <h>
 *         <pid>packid</pid>
 *         <a>系统代理编码</a>
 *         <v>1.0</v>
 *         <t>包type</t>
 *         <acount>账号</acount>
 *         <dt>device type</dt>
 *         <dn>device name</dn>
 *         <did>device id</did>
 *         <time>time</time>
 *     </h>
 *     <b>
 *         <msg>
 *             <![CADATA[内容]]>
 *         </msg>
 *         <loc>
 *              <lt> 高德/百度</lt>
 *             <lat></lat>
 *             <lng></lng>
 *             <addr></addr>
 *         </loc>
 *     </b>
 * </p>
 */

public class SendPackEntity<T> {

    private String pid;
    private String a;
    private String v;
    private String t;
    private String acount;
    private String dt;
    private String dn;
    private String did;
    private String time;
    private String msg;
    private String lt;
    private String lat;
    private String lng;
    private String addr;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getAcount() {
        return acount;
    }

    public void setAcount(String acount) {
        this.acount = acount;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
