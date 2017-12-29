package programs.studyprogram.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import okhttp3.ResponseBody;
import programs.publicmodule.mvp.view.acts.PublicMsgShowAct;
import programs.publicmodule.retrofit2.apiservices.NetApiService;
import programs.publicmodule.retrofit2.responsepack.RequestResPack;
import programs.studyprogram.mvp.model.IStudyFunctionModel;
import programs.studyprogram.mvp.model.StudyFunctionModel;
import programs.studyprogram.mvp.view.acts.StudyFunctionAct;
import programs.studyprogram.mvp.view.interfaces.IStudyFunctionView;
import programs.studyprogram.retrofit2.apiservices.ConfigObtainService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class StudyFunctionPresenter {

    IStudyFunctionView view;
    IStudyFunctionModel model;

    NetApiService netApiService;
    ConfigObtainService configObtainService;

    public StudyFunctionPresenter(StudyFunctionAct act,
                                  StudyFunctionModel model,
                                  NetApiService netApiService,
                                  ConfigObtainService configObtainService){
        this.view = act;
        this.model = model;
        this.netApiService = netApiService;
        this.configObtainService = configObtainService;
    }

    public void requestNetService(){
        Call<List<RequestResPack>> call = netApiService.getSearchPack("square","retrofit");
        call.enqueue(new Callback<List<RequestResPack>>() {
            @Override
            public void onResponse(Call<List<RequestResPack>> call, Response<List<RequestResPack>> response) {
                Log.i("response.size=",response.body().size()+"");
                view.requestResponse(response.body());
            }

            @Override
            public void onFailure(Call<List<RequestResPack>> call, Throwable t) {
                view.requestFailure("onFailure");
            }
        });

        Call<ResponseBody> resCall = netApiService.getResponse("square","retrofit");
        resCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.i("resCall","response="+response.body().string().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("resCall","onFailure");
            }
        });
    }

    public void getConfigObtain(){
        Call<ResponseBody> call = configObtainService.getConfigPack("http://www.baidu.com");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.i("ConfigresCall","response="+response.body().string().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("ConfigresCall","onFailure");
            }
        });
    }

    public void myTest(){
        model.myTest();
        view.myTestView();
    }

    public void redirectPublicMsg(Context context){
        Intent it = new Intent(context, PublicMsgShowAct.class);
        context.startActivity(it);
    }
}
