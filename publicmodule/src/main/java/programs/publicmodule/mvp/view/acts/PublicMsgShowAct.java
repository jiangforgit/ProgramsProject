package programs.publicmodule.mvp.view.acts;

import android.os.Bundle;
import android.widget.TextView;
import java.util.List;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import programs.publicmodule.R;
import programs.publicmodule.core.base.CompatActBase;
import programs.publicmodule.core.db.tables.TableOrder;
import programs.publicmodule.mvp.presenter.PublicMsgShowPresenter;
import programs.publicmodule.mvp.view.interfaces.IPublicMsgShowView;

public class PublicMsgShowAct extends CompatActBase implements IPublicMsgShowView {

    @BindView(R.id.tv_public_msg_show)
    TextView tvPublicMsgShow;

    private Unbinder unbinder;

    @Inject
    PublicMsgShowPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_msg_show);
        unbinder = ButterKnife.bind(this);
        initData();
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
    public void showOrderList(List<TableOrder> orders) {
        StringBuffer sb = new StringBuffer();
        for (TableOrder order : orders){
            sb.append("id="+order.getId()+",orderId="+order.getOrderId()+",orderType="+order.getOrderType()+",orderValue="+order.getOrderValue()+"\n");
        }
        tvPublicMsgShow.setText(sb.toString());
    }
}
