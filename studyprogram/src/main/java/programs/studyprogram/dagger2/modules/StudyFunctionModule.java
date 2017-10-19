package programs.studyprogram.dagger2.modules;

import dagger.Module;
import dagger.Provides;
import programs.publicmodule.retrofit2.apiservices.NetApiService;
import programs.studyprogram.dagger2.scope.ActivityScope;
import programs.studyprogram.mvp.model.StudyFunctionModel;
import programs.studyprogram.mvp.view.acts.StudyFunctionAct;
import programs.studyprogram.retrofit2.apiservices.ConfigObtainService;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
@Module
public class StudyFunctionModule {

    StudyFunctionAct act;
    Retrofit retrofit;

    public StudyFunctionModule(StudyFunctionAct a,Retrofit rt){
        this.act = a;
        this.retrofit = rt;
    }

    @Provides
    @ActivityScope
    public StudyFunctionAct provideStudyFunctionAct() {
        return act;
    }

    @Provides
    @ActivityScope
    public StudyFunctionModel provideStudyFunctionModel(NetApiService ns,ConfigObtainService cs){
        StudyFunctionModel model = new StudyFunctionModel();
        model.setShowValue(provideShowValue());
        model.setNetApiService(ns);
        model.setConfigObtainService(cs);
        return model;
    }

    @Provides
    @ActivityScope
    public String provideShowValue(){
        return "MVP+DAGGER2+RETROFIT2";
    }

    @Provides
    @ActivityScope
    public ConfigObtainService provideConfigService(){
        return retrofit.create(ConfigObtainService.class);
    }

}
