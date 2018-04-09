package programs.publicmodule.core.interfaces;

/**
 * Created by caijiang.chen on 2018/1/3.
 */

public interface ILocation {

    void addCallBackListener(ILocationCallBack callBack);

    void startLocation();

    void stopLocation();
}
