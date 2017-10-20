package programs.publicmodule.mvp.model;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import programs.publicmodule.core.db.helper.DatabaseHelper;
import programs.publicmodule.core.db.tables.TableTask;

/**
 * Created by caijiang.chen on 2017/10/17.
 */

public class PublicMsgShowModel implements IPublicMsgShowModel {

    @Inject
    public PublicMsgShowModel(){}

    @Override
    public List<TableTask> testTableOrder(Context context) {
        List<TableTask> orders = null;
        try {
            TableTask task = new TableTask();
            task.setORDER_ID("1001");
            task.setTASK_ID(UUID.randomUUID().toString());
            task.setTASK_TYPE(1);
            task.setTASK_VALUE("VALUE");
            DatabaseHelper helper = DatabaseHelper.getDatabaseHelper(context);
            Dao orderDao = helper.getDao(TableTask.class);
            orderDao.create(task);
            orders = orderDao.queryForAll();
            helper.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return orders;
    }
}
