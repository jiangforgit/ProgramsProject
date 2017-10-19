package programs.publicmodule.core.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by caijiang.chen on 2017/10/17.
 */

@DatabaseTable(tableName = "ORDER")
public class TableOrder {
    @DatabaseField(generatedId = true)
    private int ID;

    @DatabaseField
    private String PACKET_ID;

    @DatabaseField
    private String ORDER_ID;

    @DatabaseField
    private int ORDER_TYPE;

    @DatabaseField
    private String ORDER_MSG;

    @DatabaseField
    private int MSG_TYPE;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getORDER_ID() {
        return ORDER_ID;
    }

    public void setORDER_ID(String ORDER_ID) {
        this.ORDER_ID = ORDER_ID;
    }

    public int getORDER_TYPE() {
        return ORDER_TYPE;
    }

    public void setORDER_TYPE(int ORDER_TYPE) {
        this.ORDER_TYPE = ORDER_TYPE;
    }

    public String getORDER_MSG() {
        return ORDER_MSG;
    }

    public void setORDER_MSG(String ORDER_MSG) {
        this.ORDER_MSG = ORDER_MSG;
    }

    public int getMSG_TYPE() {
        return MSG_TYPE;
    }

    public void setMSG_TYPE(int MSG_TYPE) {
        this.MSG_TYPE = MSG_TYPE;
    }

    public String getPACKET_ID() {
        return PACKET_ID;
    }

    public void setPACKET_ID(String PACKET_ID) {
        this.PACKET_ID = PACKET_ID;
    }
}
