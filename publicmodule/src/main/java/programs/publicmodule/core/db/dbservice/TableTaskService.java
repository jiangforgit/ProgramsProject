package programs.publicmodule.core.db.dbservice;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import programs.publicmodule.core.db.tables.TableTask;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public class TableTaskService extends DbService {

    @Inject
    public TableTaskService(){}

    public int createTask(TableTask tableTask){
        try {
            return getDbHelper().getDao(TableTask.class).create(tableTask);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<TableTask> queryAllTasks(){
        try {
            return getDbHelper().getDao(TableTask.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
