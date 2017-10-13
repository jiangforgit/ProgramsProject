package programs.studyprogram.dagger2.modules;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import programs.publicmodule.dagger2.components.AppComponent;
import programs.publicmodule.dagger2.modules.ApiServiceModule;
import programs.publicmodule.retrofit2.apiservices.NetApiService;
import programs.publicmodule.retrofit2.responsepack.RequestResPack;
import programs.studyprogram.dagger2.scope.ActivityScope;
import programs.studyprogram.mvp.model.StudyFunctionModel;
import programs.studyprogram.mvp.view.acts.StudyFunctionAct;
import programs.studyprogram.retrofit2.apiservices.ConfigObtainService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
