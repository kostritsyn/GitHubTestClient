package kostritsyn.igor.githubtest.core.network;

import android.arch.lifecycle.LiveData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiGitHubService {

    @GET("/search/repositories")
    LiveData<ApiResponse<SearchReposResponse>> searchRepos(@Query("q") String query);

    @GET("/search/repositories")
    Call<SearchReposResponse> searchRepos(@Query("q") String query,
                                          @Query("page") int page);
}
