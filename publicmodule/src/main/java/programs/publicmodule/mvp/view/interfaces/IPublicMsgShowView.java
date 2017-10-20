package programs.publicmodule.mvp.view.interfaces;

import java.util.List;

import programs.publicmodule.core.db.tables.TableTask;

/**
 * Created by caijiang.chen on 2017/10/17.
 */

public interface IPublicMsgShowView {
    void showOrderList(List<TableTask> orders);
}
