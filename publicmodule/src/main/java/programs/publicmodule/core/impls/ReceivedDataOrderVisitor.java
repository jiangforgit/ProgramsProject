package programs.publicmodule.core.impls;

import programs.publicmodule.core.interfaces.IReceivedDataSubject;
import programs.publicmodule.core.interfaces.IReceivedDataVisitor;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public class ReceivedDataOrderVisitor implements IReceivedDataVisitor {

    @Override
    public void visit(IReceivedDataSubject subject) {
        subject.getReceivedDataEntity();
    }

}
