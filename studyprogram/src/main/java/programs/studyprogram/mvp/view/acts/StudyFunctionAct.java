package programs.studyprogram.mvp.view.acts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import programs.publicmodule.core.appconstant.ProgramsApplication;
import programs.publicmodule.core.services.CoreMainService;
import programs.publicmodule.core.services.RemoteProcessService;
import programs.publicmodule.retrofit2.apiservices.NetApiService;
import programs.publicmodule.retrofit2.responsepack.RequestResPack;
import programs.studyprogram.R;
import programs.studyprogram.dagger2.components.DaggerStudyFunctionComponent;
import programs.studyprogram.dagger2.components.StudyFunctionComponent;
import programs.studyprogram.dagger2.modules.StudyFunctionModule;
import programs.studyprogram.mvp.presenter.StudyFunctionPresenter;
import programs.studyprogram.mvp.view.interfaces.IStudyFunctionView;
import programs.studyprogram.retrofit2.apiservices.ConfigObtainService;

public class StudyFunctionAct extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IStudyFunctionView {

    @BindView(R.id.tv_function_content_desc)
    TextView tvFunctionContentDesc;

    @Inject
    StudyFunctionPresenter presenter;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_function);
        unbinder = ButterKnife.bind(this);
        setComponent();
        initView();
        initData();
    }

    private void setComponent() {
        DaggerStudyFunctionComponent.builder()
                .appComponent(ProgramsApplication.getInstant().getAppComponent())
                .studyFunctionModule(new StudyFunctionModule(this))
                .build()
                .inject(this);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                presenter.requestNetService();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initData() {
        this.startService(new Intent(this, CoreMainService.class));
        this.startService(new Intent(this, RemoteProcessService.class));
//        presenter.getValueAndShow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.study_function, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                presenter.myTest();
                break;
            case R.id.action_publicmsg:
                presenter.redirectPublicMsg(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            presenter.getConfigObtain();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void showTextValue(String value) {
        tvFunctionContentDesc.setText(value);
    }

    @Override
    public void requestResponse(List<RequestResPack> res) {
        tvFunctionContentDesc.setText(res.get(0).getLogin());
    }

    @Override
    public void requestFailure(String failure) {
        tvFunctionContentDesc.setText(failure);
    }

    @Override
    public void myTestView() {
        Toast.makeText(this,"myTestView",Toast.LENGTH_SHORT).show();
    }
}
