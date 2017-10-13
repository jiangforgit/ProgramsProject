package programs.publicmodule.core.appconstant;

import android.app.Application;
import android.content.res.Configuration;
import programs.publicmodule.dagger2.components.AppComponent;
import programs.publicmodule.dagger2.components.DaggerAppComponent;
import programs.publicmodule.dagger2.modules.ApiServiceModule;
import programs.publicmodule.dagger2.modules.AppModule;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class ProgramsApplication extends Application {

    private static ProgramsApplication instant = null;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instant = this;
        setupAppComponent();
    }

    public static ProgramsApplication getInstant(){
        return instant;
    }

    private void setupAppComponent(){
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .apiServiceModule(new ApiServiceModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
