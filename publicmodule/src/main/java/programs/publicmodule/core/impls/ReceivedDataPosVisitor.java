package programs.publicmodule.core.impls;

import javax.inject.Inject;

import programs.publicmodule.core.db.dbservice.TableTaskService;
import programs.publicmodule.core.db.tables.TableTask;
import programs.publicmodule.core.entity.ReceivedDataEntity;
import programs.publicmodule.core.interfaces.IReceivedDataSubject;
import programs.publicmodule.core.interfaces.IReceivedDataVisitor;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public class ReceivedDataPosVisitor implements IReceivedDataVisitor {

    @Inject
    TableTaskService taskService;

    @Override
    public void visit(IReceivedDataSubject subject) {
        ReceivedDataEntity dataEntity = (ReceivedDataEntity) subject.getReceivedDataEntity();
        taskService.createTask(convert2TableTask(dataEntity));
    }

    private TableTask convert2TableTask(ReceivedDataEntity dataEntity){


        return null;
    }

}
