package programs.publicmodule.core.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by caijiang.chen on 2017/10/19.
 */

@DatabaseTable(tableName = "MEDIA_RESOURCE")
public class TableMediaResource {

    @DatabaseField(generatedId = true)
    private int ID;

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

    @DatabaseField(foreign = true)
    private String FOREIGN_KEY;
}
