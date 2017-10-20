package programs.publicmodule.mvp.model;

import android.content.Context;

import java.util.List;

import programs.publicmodule.core.db.tables.TableTask;

/**
 * Created by caijiang.chen on 2017/10/17.
 */

public interface IPublicMsgShowModel {

    List<TableTask> testTableOrder(Context context);
}
