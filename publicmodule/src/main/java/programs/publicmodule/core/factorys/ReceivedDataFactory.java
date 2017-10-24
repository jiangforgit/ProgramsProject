package programs.publicmodule.core.factorys;

import programs.publicmodule.core.abstracts.AbstractReceivedAnalysor;
import programs.publicmodule.core.impls.XMLReceivedAnalysor;
import programs.publicmodule.core.impls.XMLReceivedDispatcher;
import programs.publicmodule.core.interfaces.IDispatchReceivedStr;

/**
 * Created by caijiang.chen on 2017/10/24.
 */

public class ReceivedDataFactory {

    public static IDispatchReceivedStr dispather(){
        IDispatchReceivedStr dispatchReceivedStr = new XMLReceivedDispatcher();
        return dispatchReceivedStr;
    }

    public static AbstractReceivedAnalysor analysor(){
        AbstractReceivedAnalysor analysor = new XMLReceivedAnalysor();
        return analysor;
    }
}
