package kostritsyn.igor.githubtest.core.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kostritsyn.igor.githubtest.core.db.AppDatabase;
import kostritsyn.igor.githubtest.core.db.RepositoryDao;
import kostritsyn.igor.githubtest.core.entity.RepositoryQueryResult;
import kostritsyn.igor.githubtest.core.network.ApiGitHubService;
import kostritsyn.igor.githubtest.core.network.ApiResponse;
import kostritsyn.igor.githubtest.core.network.SearchReposResponse;
import kostritsyn.igor.githubtest.ui.repo.DownloadNextPageResult;
import retrofit2.Response;

public class DownloadNextSearchPageTask implements Runnable {

    private final MutableLiveData<DownloadNextPageResult> resultLiveData = new MutableLiveData<>();
    private final String query;
    private final ApiGitHubService gitHubService;
    private final AppDatabase appDatabase;

    DownloadNextSearchPageTask(String query, ApiGitHubService gitHubService,
                               AppDatabase appDatabase) {
        this.query = query;
        this.gitHubService = gitHubService;
        this.appDatabase = appDatabase;
    }

    @Override
    public void run() {

        final RepositoryDao repositoryDao = appDatabase.repositoryDao();

        RepositoryQueryResult current = repositoryDao.findRepositoryQueryResult(query);
        if(current == null) {
            resultLiveData.postValue(null);
            return;
        }

        final Integer nextPage = current.next;
        if (nextPage == null) {
            resultLiveData.postValue(DownloadNextPageResult.getSuccess(false));
            return;
        }

        try {
            Response<SearchReposResponse> response
                    = gitHubService.searchRepos(query, nextPage).execute();
            ApiResponse<SearchReposResponse> apiResponse = new ApiResponse<>(response);
            if (apiResponse.isSuccessful()) {

                List<Integer> ids = new ArrayList<>();
                ids.addAll(current.repositoryIds);

                ids.addAll(apiResponse.body.getRepositoryIds());
                RepositoryQueryResult merged = new RepositoryQueryResult(query, ids,
                        apiResponse.body.getTotalCount(), apiResponse.getNextPage());
                try {
                    appDatabase.beginTransaction();
                    repositoryDao.insert(merged);
                    repositoryDao.insertRepos(apiResponse.body.getRepositories());
                    appDatabase.setTransactionSuccessful();
                } finally {
                    appDatabase.endTransaction();
                }
                resultLiveData.postValue(
                        DownloadNextPageResult.getSuccess(apiResponse.getNextPage() != null));
            } else {
                resultLiveData.postValue(DownloadNextPageResult.getFailed(apiResponse.errorMessage));
            }
        } catch (IOException e) {
            resultLiveData.postValue(DownloadNextPageResult.getFailed(e.getMessage()));
        }
    }

    LiveData<DownloadNextPageResult> getResultLiveData() {
        return resultLiveData;
    }
}
