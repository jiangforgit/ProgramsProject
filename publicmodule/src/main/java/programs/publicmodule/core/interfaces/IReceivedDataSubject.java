package programs.publicmodule.core.interfaces;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public interface IReceivedDataSubject<T> {

    void accept(IReceivedDataVisitor visitor);

    T getReceivedDataEntity();

}
