package programs.publicmodule.retrofit2.apiservices;

import java.util.List;

import okhttp3.ResponseBody;
import programs.publicmodule.retrofit2.responsepack.RequestResPack;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Administrator on 2017/2/12 0012.
 */

public interface NetApiService {
    @GET("repos/{owner}/{repo}/contributors")
    Call<List<RequestResPack>> getSearchPack(@Path("owner") String owner,
                                             @Path("repo") String repo);
    @GET("repos/{owner}/{repo}/contributors")
    Call<ResponseBody> getResponse(@Path("owner") String owner,
                                   @Path("repo") String repo);
}
