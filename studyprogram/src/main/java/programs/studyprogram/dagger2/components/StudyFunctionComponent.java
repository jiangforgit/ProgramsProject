package programs.studyprogram.dagger2.components;

import javax.inject.Singleton;

import dagger.Component;
import programs.publicmodule.dagger2.components.AppComponent;
import programs.publicmodule.retrofit2.apiservices.NetApiService;
import programs.studyprogram.dagger2.modules.StudyFunctionModule;
import programs.studyprogram.dagger2.scope.ActivityScope;
import programs.studyprogram.mvp.model.StudyFunctionModel;
import programs.studyprogram.mvp.view.acts.StudyFunctionAct;

/**
 * Created by Administrator on 2017/2/15 0015.
 */
@ActivityScope
@Component(modules = StudyFunctionModule.class,dependencies = AppComponent.class)
public interface StudyFunctionComponent {
    void inject(StudyFunctionAct act);
}
