package programs.publicmodule.core.impls;

import programs.publicmodule.core.abstracts.AbstractReceivedDataVisitor;
import programs.publicmodule.core.interfaces.IReceivedDataSubject;
import programs.publicmodule.core.interfaces.IReceivedDataVisitor;

/**
 * Created by Administrator on 2017/11/5 0005.
 */

public class ReceivedDataBusiVisitor extends AbstractReceivedDataVisitor implements IReceivedDataVisitor {

    @Override
    public void visit(IReceivedDataSubject subject) {
        subject.getReceivedDataEntity();
    }

}
