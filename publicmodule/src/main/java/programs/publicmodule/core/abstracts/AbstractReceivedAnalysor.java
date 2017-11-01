package programs.publicmodule.core.abstracts;

import programs.publicmodule.core.enums.EnumStrType;

/**
 * Created by caijiang.chen on 2017/10/24.
 */

public abstract class AbstractReceivedAnalysor<T> {

    public T analyseReceivedStr(String str){
        return isReceivedNotNull(str)?analyse(str):null;
    }

abstract public T analyse(String str);

    private boolean isReceivedNotNull(String str){
        if(null == str){
            return false;
        }else if("".equals(str)){
            return false;
        }
        return true;
    }
}
