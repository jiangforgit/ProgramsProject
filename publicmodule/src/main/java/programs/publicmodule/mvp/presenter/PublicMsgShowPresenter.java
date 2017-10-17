package programs.publicmodule.mvp.presenter;

import android.content.Context;

import javax.inject.Inject;

import programs.publicmodule.mvp.model.IPublicMsgShowModel;
import programs.publicmodule.mvp.model.PublicMsgShowModel;
import programs.publicmodule.mvp.view.acts.PublicMsgShowAct;
import programs.publicmodule.mvp.view.interfaces.IPublicMsgShowView;

/**
 * Created by caijiang.chen on 2017/10/17.
 */

public class PublicMsgShowPresenter {

    IPublicMsgShowView view;
    IPublicMsgShowModel model;

    @Inject
    public PublicMsgShowPresenter(PublicMsgShowAct act, PublicMsgShowModel md){
        this.view = act;
        this.model = md;
    }

    public void getAndShowTableOrders(Context context){
        view.showOrderList(model.testTableOrder(context));
    }
}
