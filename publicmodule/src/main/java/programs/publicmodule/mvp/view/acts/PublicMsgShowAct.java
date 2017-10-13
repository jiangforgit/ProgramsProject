package programs.publicmodule.mvp.view.acts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import programs.publicmodule.R;

public class PublicMsgShowAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_msg_show);
        initData();
    }

    private void initData() {

    }
}
