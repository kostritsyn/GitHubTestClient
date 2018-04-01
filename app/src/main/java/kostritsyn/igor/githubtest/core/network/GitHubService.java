package kostritsyn.igor.githubtest.core.network;

import android.arch.lifecycle.LiveData;

import com.google.gson.JsonObject;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GitHubService {

    String CLIENT_ID = "fcad31d2d5dc2bb39366";
    String CLIENT_SECRET = "e1da6397f7c143e2cd8e1ecd4b7b586e3e84a7ef";

    @POST("/login/oauth/access_token")
    @Headers("Accept: application/json")
    LiveData<ApiResponse<CreateTokenResult>> createToken(@Body JsonObject params);
}
