package programs.studyprogram.mvp.model;

import programs.publicmodule.retrofit2.apiservices.NetApiService;
import programs.studyprogram.retrofit2.apiservices.ConfigObtainService;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public interface IStudyFunctionModel {

    String getShowValue();

    NetApiService getNetApiService();

    ConfigObtainService getConfigObtainService();

    int getVersionCode();

    void myTest();
}
