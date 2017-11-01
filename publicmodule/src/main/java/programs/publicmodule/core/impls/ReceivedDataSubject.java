package programs.publicmodule.core.impls;

import programs.publicmodule.core.interfaces.IReceivedDataSubject;
import programs.publicmodule.core.interfaces.IReceivedDataVisitor;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public class ReceivedDataSubject<ReceivedDataEntity> implements IReceivedDataSubject {

    private ReceivedDataEntity receivedDataEntity;

    public ReceivedDataSubject(ReceivedDataEntity receivedDataEntity){
        this.receivedDataEntity = receivedDataEntity;
    }

    @Override
    public void accept(IReceivedDataVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ReceivedDataEntity getReceivedDataEntity() {
        return this.receivedDataEntity;
    }


}
