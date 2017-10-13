package programs.publicmodule.dagger2.components;

import android.app.Application;
import javax.inject.Singleton;
import dagger.Component;
import programs.publicmodule.dagger2.modules.ApiServiceModule;
import programs.publicmodule.dagger2.modules.AppModule;
import programs.publicmodule.retrofit2.apiservices.NetApiService;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/2/12 0012.
 */
@Singleton
@Component(modules = {AppModule.class, ApiServiceModule.class})
public interface AppComponent {

    Application getApplication();

    Retrofit getRetrofit();

    NetApiService getNetApiService();

}
