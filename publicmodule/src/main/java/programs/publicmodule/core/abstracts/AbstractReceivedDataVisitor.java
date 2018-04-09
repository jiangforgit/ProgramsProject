package programs.publicmodule.core.abstracts;

import programs.publicmodule.core.interfaces.IReceivedDataListener;

/**
 * Created by caijiang.chen on 2018/1/3.
 */

public abstract class AbstractReceivedDataVisitor {

    protected IReceivedDataListener listener;

    public void setListener(IReceivedDataListener listener){
        this.listener = listener;
    }

}
