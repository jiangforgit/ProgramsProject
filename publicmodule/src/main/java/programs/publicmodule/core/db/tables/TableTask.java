package programs.publicmodule.core.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Date;

/**
 * Created by caijiang.chen on 2017/10/17.
 */

@DatabaseTable(tableName = "TASK")
public class TableTask {

    @DatabaseField(id = true)
    private String TASK_ID;

    @DatabaseField
    private String ORDER_ID;

    @DatabaseField
    private int TASK_TYPE;

    @DatabaseField
    private String TASK_VALUE;

    @DatabaseField
    private int VALUE_TYPE;

    @DatabaseField
    private Date DATE_TIME;

    public String getTASK_ID() {
        return TASK_ID;
    }

    public void setTASK_ID(String TASK_ID) {
        this.TASK_ID = TASK_ID;
    }

    public String getORDER_ID() {
        return ORDER_ID;
    }

    public void setORDER_ID(String ORDER_ID) {
        this.ORDER_ID = ORDER_ID;
    }

    public int getTASK_TYPE() {
        return TASK_TYPE;
    }

    public void setTASK_TYPE(int TASK_TYPE) {
        this.TASK_TYPE = TASK_TYPE;
    }

    public String getTASK_VALUE() {
        return TASK_VALUE;
    }

    public void setTASK_VALUE(String TASK_VALUE) {
        this.TASK_VALUE = TASK_VALUE;
    }

    public int getVALUE_TYPE() {
        return VALUE_TYPE;
    }

    public void setVALUE_TYPE(int VALUE_TYPE) {
        this.VALUE_TYPE = VALUE_TYPE;
    }

    public Date getDATE_TIME() {
        return DATE_TIME;
    }

    public void setDATE_TIME(Date DATE_TIME) {
        this.DATE_TIME = DATE_TIME;
    }
}
