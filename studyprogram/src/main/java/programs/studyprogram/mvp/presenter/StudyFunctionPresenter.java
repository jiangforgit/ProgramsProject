package programs.studyprogram.mvp.presenter;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import okhttp3.ResponseBody;
import programs.publicmodule.retrofit2.responsepack.RequestResPack;
import programs.studyprogram.mvp.model.IStudyFunctionModel;
import programs.studyprogram.mvp.model.StudyFunctionModel;
import programs.studyprogram.mvp.view.acts.StudyFunctionAct;
import programs.studyprogram.mvp.view.interfaces.IStudyFunctionView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class StudyFunctionPresenter {

    IStudyFunctionView view;
    IStudyFunctionModel model;

    @Inject
    public StudyFunctionPresenter(StudyFunctionAct act, StudyFunctionModel model){
        this.view = act;
        this.model = model;
    }

    public void getValueAndShow(){
        view.showTextValue(model.getShowValue()+"--jnivaue="+model.getVersionCode());
    }

    public void requestNetService(){
        Call<List<RequestResPack>> call = model.getNetApiService().getSearchPack("square","retrofit");
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

        Call<ResponseBody> resCall = model.getNetApiService().getResponse("square","retrofit");
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
        Call<ResponseBody> call = model.getConfigObtainService().getConfigPack("http://www.baidu.com");
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
}
