package programs.publicmodule.core.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by caijiang.chen on 2017/10/19.
 */

@DatabaseTable(tableName = "SENDING_LOG")
public class TableSendingLog {

    @DatabaseField(generatedId = true)
    private int ID;

    @DatabaseField
    private int LOG_TYPE;

    @DatabaseField
    private String LOG_VALUE;

    @DatabaseField
    private int SEND_STATUS;

    @DatabaseField(foreign = true)
    private String FOREIGN_KEY;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getLOG_TYPE() {
        return LOG_TYPE;
    }

    public void setLOG_TYPE(int LOG_TYPE) {
        this.LOG_TYPE = LOG_TYPE;
    }

    public String getLOG_VALUE() {
        return LOG_VALUE;
    }

    public void setLOG_VALUE(String LOG_VALUE) {
        this.LOG_VALUE = LOG_VALUE;
    }

    public int getSEND_STATUS() {
        return SEND_STATUS;
    }

    public void setSEND_STATUS(int SEND_STATUS) {
        this.SEND_STATUS = SEND_STATUS;
    }

    public String getFOREIGN_KEY() {
        return FOREIGN_KEY;
    }

    public void setFOREIGN_KEY(String FOREIGN_KEY) {
        this.FOREIGN_KEY = FOREIGN_KEY;
    }
}
