package programs.publicmodule.core.db.dbservice;

import programs.publicmodule.core.appconstant.ProgramsApplication;
import programs.publicmodule.core.db.helper.DatabaseHelper;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public abstract class DbService {

    protected DatabaseHelper getDbHelper(){
        return DatabaseHelper.getDatabaseHelper(ProgramsApplication.getInstant());
    }

}
