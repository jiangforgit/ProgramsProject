package programs.publicmodule.dagger2.modules;

import android.app.Application;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
@Module
public class AppModule {

    Application application;

    public AppModule(Application application){
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication(){
        return application;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(String name,int mode){
        SharedPreferences sharedPreferences = application.getSharedPreferences(name,mode);
        return sharedPreferences;
    }

}
