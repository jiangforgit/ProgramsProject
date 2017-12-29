package programs.publicmodule.core.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Date;

/**
 * Created by caijiang.chen on 2017/10/19.
 */

@DatabaseTable(tableName = "MEDIA_RESOURCE")
public class TableMediaResource {

    @DatabaseField(id = true)
    private String RESOURCE_ID;

    @DatabaseField
    private int RESOURCE_TYPE;

    @DatabaseField
    private String RESOURCE_NAME;

    @DatabaseField
    private String RESOURCE_VALUE;

    @DatabaseField
    private String RESOURCE_PATH;

    @DatabaseField
    private int STATUS;

    @DatabaseField(foreign = true,columnName = "TASK_CONTENT_ID")
    private TableTaskContent tableTaskContent;

    @DatabaseField(format="DATE_STRING")
    private Date DATE_TIME;

    public TableMediaResource(){}

    public String getRESOURCE_ID() {
        return RESOURCE_ID;
    }

    public void setRESOURCE_ID(String RESOURCE_ID) {
        this.RESOURCE_ID = RESOURCE_ID;
    }

    public int getRESOURCE_TYPE() {
        return RESOURCE_TYPE;
    }

    public void setRESOURCE_TYPE(int RESOURCE_TYPE) {
        this.RESOURCE_TYPE = RESOURCE_TYPE;
    }

    public String getRESOURCE_NAME() {
        return RESOURCE_NAME;
    }

    public void setRESOURCE_NAME(String RESOURCE_NAME) {
        this.RESOURCE_NAME = RESOURCE_NAME;
    }

    public String getRESOURCE_VALUE() {
        return RESOURCE_VALUE;
    }

    public void setRESOURCE_VALUE(String RESOURCE_VALUE) {
        this.RESOURCE_VALUE = RESOURCE_VALUE;
    }

    public String getRESOURCE_PATH() {
        return RESOURCE_PATH;
    }

    public void setRESOURCE_PATH(String RESOURCE_PATH) {
        this.RESOURCE_PATH = RESOURCE_PATH;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public TableTaskContent getTableTaskContent() {
        return tableTaskContent;
    }

    public void setTableTaskContent(TableTaskContent tableTaskContent) {
        this.tableTaskContent = tableTaskContent;
    }

    public Date getDATE_TIME() {
        return DATE_TIME;
    }

    public void setDATE_TIME(Date DATE_TIME) {
        this.DATE_TIME = DATE_TIME;
    }
}
