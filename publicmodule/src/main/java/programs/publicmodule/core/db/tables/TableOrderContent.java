package programs.publicmodule.core.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by caijiang.chen on 2017/10/19.
 */

@DatabaseTable(tableName = "ORDER_CONTENT")
public class TableOrderContent {
    @DatabaseField(generatedId = true)
    private int ID;

    @DatabaseField
    private int CONTENT_TYPE;

    @DatabaseField
    private String CONTENT_NAME;

    @DatabaseField
    private String CONTENT_VALUE;

    @DatabaseField
    private int MARK;

    @DatabaseField
    private int MARK_RESULT;

    @DatabaseField(foreign = true)
    private String FOREIGN_KEY;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public int getMARK() {
        return MARK;
    }

    public void setMARK(int MARK) {
        this.MARK = MARK;
    }

    public int getMARK_RESULT() {
        return MARK_RESULT;
    }

    public void setMARK_RESULT(int MARK_RESULT) {
        this.MARK_RESULT = MARK_RESULT;
    }

    public String getFOREIGN_KEY() {
        return FOREIGN_KEY;
    }

    public void setFOREIGN_KEY(String FOREIGN_KEY) {
        this.FOREIGN_KEY = FOREIGN_KEY;
    }
}
