package programs.publicmodule.mvp.model;

import android.content.Context;

import java.util.List;

import programs.publicmodule.core.db.tables.TableOrder;

/**
 * Created by caijiang.chen on 2017/10/17.
 */

public interface IPublicMsgShowModel {

    List<TableOrder> testTableOrder(Context context);
}
