package programs.publicmodule.mvp.view.acts;

import android.os.Bundle;
import android.widget.TextView;
import java.util.List;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import programs.publicmodule.R;
import programs.publicmodule.R2;
import programs.publicmodule.core.appconstant.ProgramsApplication;
import programs.publicmodule.core.base.CompatActBase;
import programs.publicmodule.core.db.tables.TableTask;
import programs.publicmodule.dagger2.components.DaggerPublicMsgShowComponent;
import programs.publicmodule.dagger2.components.PublicMsgShowComponent;
import programs.publicmodule.dagger2.modules.PublicMsgShowModule;
import programs.publicmodule.mvp.presenter.PublicMsgShowPresenter;
import programs.publicmodule.mvp.view.interfaces.IPublicMsgShowView;

public class PublicMsgShowAct extends CompatActBase implements IPublicMsgShowView {

    @BindView(R2.id.tv_public_msg_show)
    TextView tvPublicMsgShow;

    private Unbinder unbinder;

    @Inject
    PublicMsgShowPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_msg_show);
        unbinder = ButterKnife.bind(this);
        setComponent();
        initData();
    }

    private void setComponent(){
        DaggerPublicMsgShowComponent.builder()
                .appComponent(ProgramsApplication.getInstant().getAppComponent())
                .publicMsgShowModule(new PublicMsgShowModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != unbinder){
            unbinder.unbind();
        }
    }

    private void initData() {
        presenter.getAndShowTableOrders(this);
    }

    @Override
    public void showOrderList(List<TableTask> orders) {
        StringBuffer sb = new StringBuffer();
        for (TableTask order : orders){
            sb.append("TASK_VALUE="+order.getTASK_VALUE()+"\n");
        }
        tvPublicMsgShow.setText(sb.toString());
    }
}
