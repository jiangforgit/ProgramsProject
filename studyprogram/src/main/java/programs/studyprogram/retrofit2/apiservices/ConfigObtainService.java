package programs.studyprogram.retrofit2.apiservices;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public interface ConfigObtainService {
    @GET
    Call<ResponseBody> getConfigPack(@Url String url);
}
