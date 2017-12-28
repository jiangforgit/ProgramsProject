package programs.studyprogram.dagger2.modules;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import programs.publicmodule.retrofit2.apiservices.NetApiService;
import programs.studyprogram.dagger2.scope.ActivityScope;
import programs.studyprogram.mvp.model.StudyFunctionModel;
import programs.studyprogram.mvp.presenter.StudyFunctionPresenter;
import programs.studyprogram.mvp.view.acts.StudyFunctionAct;
import programs.studyprogram.retrofit2.apiservices.ConfigObtainService;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
@Module
public class StudyFunctionModule {

    StudyFunctionAct act;

    @Inject
    StudyFunctionModel studyFunctionModel;

    public StudyFunctionModule(StudyFunctionAct a){
        this.act = a;
    }

    @Provides
    @ActivityScope
    public StudyFunctionAct provideStudyFunctionAct() {
        return act;
    }

    @Provides
    @ActivityScope
    public StudyFunctionModel provideStudyFunctionModel(){
        return studyFunctionModel;
    }

    @Provides
    @ActivityScope
    public StudyFunctionPresenter provideStudyFunctionPresenter(NetApiService netApiService,ConfigObtainService configObtainService){
        StudyFunctionPresenter presenter = new StudyFunctionPresenter(this.act, studyFunctionModel,netApiService,configObtainService);
        return presenter;
    }

    @Provides
    @ActivityScope
    public ConfigObtainService provideConfigService(Retrofit retrofit){
        return retrofit.create(ConfigObtainService.class);
    }

}
