package programs.publicmodule.dagger2.components;

import dagger.Component;
import programs.publicmodule.dagger2.modules.PublicMsgShowModule;
import programs.publicmodule.dagger2.scope.PerActivityScope;
import programs.publicmodule.mvp.view.acts.PublicMsgShowAct;

/**
 * Created by caijiang.chen on 2017/10/19.
 */
@PerActivityScope
@Component(dependencies = AppComponent.class,modules = PublicMsgShowModule.class)
public interface PublicMsgShowComponent {
    void inject(PublicMsgShowAct act);
}
