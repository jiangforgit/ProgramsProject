package programs.studyprogram.mvp.view.interfaces;

import java.util.List;

import programs.publicmodule.retrofit2.responsepack.RequestResPack;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public interface IStudyFunctionView {

    void showTextValue(String value);

    void requestResponse(List<RequestResPack> res);

    void requestFailure(String failure);

    void myTestView();
}
