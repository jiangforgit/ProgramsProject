package programs.publicmodule.core.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Date;

/**
 * Created by caijiang.chen on 2017/10/19.
 */

@DatabaseTable(tableName = "TASK_CONTENT")
public class TableTaskContent {

    @DatabaseField(id = true)
    private String TASK_CONTENT_ID;

    @DatabaseField
    private int CONTENT_TYPE;

    @DatabaseField
    private String CONTENT_NAME;

    @DatabaseField
    private String CONTENT_VALUE;

    @DatabaseField
    private int VALUE_TYPE;

    @DatabaseField
    private int MARK;

    @DatabaseField(foreign = true,columnName = "TASK_ID" )
    private TableTask task;

    @DatabaseField(format="DATE_STRING")
    private Date DATE_TIME;

    public TableTaskContent(){}

    public String getTASK_CONTENT_ID() {
        return TASK_CONTENT_ID;
    }

    public void setTASK_CONTENT_ID(String TASK_CONTENT_ID) {
        this.TASK_CONTENT_ID = TASK_CONTENT_ID;
    }

    public int getCONTENT_TYPE() {
        return CONTENT_TYPE;
    }

    public void setCONTENT_TYPE(int CONTENT_TYPE) {
        this.CONTENT_TYPE = CONTENT_TYPE;
    }

    public String getCONTENT_NAME() {
        return CONTENT_NAME;
    }

    public void setCONTENT_NAME(String CONTENT_NAME) {
        this.CONTENT_NAME = CONTENT_NAME;
    }

    public String getCONTENT_VALUE() {
        return CONTENT_VALUE;
    }

    public void setCONTENT_VALUE(String CONTENT_VALUE) {
        this.CONTENT_VALUE = CONTENT_VALUE;
    }

    public int getVALUE_TYPE() {
        return VALUE_TYPE;
    }

    public void setVALUE_TYPE(int VALUE_TYPE) {
        this.VALUE_TYPE = VALUE_TYPE;
    }

    public int getMARK() {
        return MARK;
    }

    public void setMARK(int MARK) {
        this.MARK = MARK;
    }

    public TableTask getTask() {
        return task;
    }

    public void setTask(TableTask task) {
        this.task = task;
    }

    public Date getDATE_TIME() {
        return DATE_TIME;
    }

    public void setDATE_TIME(Date DATE_TIME) {
        this.DATE_TIME = DATE_TIME;
    }
}
