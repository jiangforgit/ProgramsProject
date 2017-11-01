package programs.publicmodule.core.entity;

/**
 * Created by caijiang.chen on 2017/11/1.
 */

public class ReceivedDataEntity {

    private String packetgeId;

    private String packetVersion;

    private String dataType;

    private String dataValue;

    public String getPacketgeId() {
        return packetgeId;
    }

    public void setPacketgeId(String packetgeId) {
        this.packetgeId = packetgeId;
    }

    public String getPacketVersion() {
        return packetVersion;
    }

    public void setPacketVersion(String packetVersion) {
        this.packetVersion = packetVersion;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
}
