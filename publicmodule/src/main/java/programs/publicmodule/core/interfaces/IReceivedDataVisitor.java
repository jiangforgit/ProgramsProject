package programs.publicmodule.core.interfaces;

import programs.publicmodule.core.entity.ReceivedDataEntity;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public interface IReceivedDataVisitor {

    void visit(IReceivedDataSubject subject);

}
