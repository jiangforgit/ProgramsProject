package programs.publicmodule.mvp.model;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import programs.publicmodule.core.appconstant.Constant;
import programs.publicmodule.core.db.helper.DatabaseHelper;
import programs.publicmodule.core.db.tables.TableOrder;
import programs.publicmodule.mvp.view.interfaces.IPublicMsgShowView;

/**
 * Created by caijiang.chen on 2017/10/17.
 */

public class PublicMsgShowModel implements IPublicMsgShowModel {

    @Inject
    public PublicMsgShowModel(){}

    @Override
    public List<TableOrder> testTableOrder(Context context) {
        List<TableOrder> orders = null;
        try {
            TableOrder order = new TableOrder();
            order.setORDER_ID("1001");
            order.setORDER_TYPE(1);
            order.setORDER_MSG("新闻");
            DatabaseHelper helper = DatabaseHelper.getDatabaseHelper(context);
            Dao orderDao = helper.getDao(TableOrder.class);
            orderDao.create(order);
            orders = orderDao.queryForAll();
            helper.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return orders;
    }
}
