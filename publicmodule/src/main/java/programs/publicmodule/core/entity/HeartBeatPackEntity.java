package programs.publicmodule.core.entity;

/**
 * Created by Administrator on 2017/5/13 0013.
 */

public class HeartBeatPackEntity{

    private String lng = "";
    private String lat = "";
    private String radius = "";
    private String loc = "";
    private String flag = "";

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
