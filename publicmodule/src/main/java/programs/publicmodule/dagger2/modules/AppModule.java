package programs.publicmodule.dagger2.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Named;
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
    @Named("default")
    SharedPreferences provideDefaultPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    @Named("key-value")
    SharedPreferences provideSharedPreferences(String name,int mode){
        SharedPreferences sharedPreferences = application.getSharedPreferences(name,mode);
        return sharedPreferences;
    }

}
