package programs.publicmodule.core.factorys;

import programs.publicmodule.core.abstracts.AbstractReceivedAnalysor;
import programs.publicmodule.core.impls.XMLReceivedAnalysor;

/**
 * Created by caijiang.chen on 2017/10/24.
 */

public class ReceivedDataFactory {

    public static AbstractReceivedAnalysor analysor(){
        AbstractReceivedAnalysor analysor = new XMLReceivedAnalysor();
        return analysor;
    }

}
