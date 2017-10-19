package programs.publicmodule.dagger2.modules;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import programs.publicmodule.mvp.model.PublicMsgShowModel;
import programs.publicmodule.mvp.view.acts.PublicMsgShowAct;

/**
 * Created by caijiang.chen on 2017/10/19.
 */

@Module
public class PublicMsgShowModule {

    private PublicMsgShowAct act;

    @Inject
    PublicMsgShowModel publicMsgShowModel;

    public PublicMsgShowModule(PublicMsgShowAct act){
        this.act = act;
    }

    @Provides
    public PublicMsgShowAct providePublicMsgShowAct(){
        return this.act;
    }

    @Provides
    public PublicMsgShowModel providePublicMsgShowModel(){
        return publicMsgShowModel;
    }
}
