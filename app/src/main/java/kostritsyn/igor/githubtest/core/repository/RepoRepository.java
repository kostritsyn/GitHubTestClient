package kostritsyn.igor.githubtest.core.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import kostritsyn.igor.githubtest.core.ExecutorManager;
import kostritsyn.igor.githubtest.core.db.AppDatabase;
import kostritsyn.igor.githubtest.core.db.RepositoryDao;
import kostritsyn.igor.githubtest.core.entity.Repository;
import kostritsyn.igor.githubtest.core.entity.RepositoryQueryResult;
import kostritsyn.igor.githubtest.core.network.ApiGitHubService;
import kostritsyn.igor.githubtest.core.network.ApiResponse;
import kostritsyn.igor.githubtest.core.network.SearchReposResponse;
import kostritsyn.igor.githubtest.ui.repo.DownloadNextPageResult;
import kostritsyn.igor.githubtest.ui.repo.SearchReposResult;

@Singleton
public class RepoRepository {

    private final AppDatabase appDatabase;

    private final RepositoryDao repositoryDao;

    private final ApiGitHubService apiGitHubService;

    private final ExecutorManager executorManager;

    @Inject
    public RepoRepository(AppDatabase appDatabase, RepositoryDao repositoryDao,
                          ApiGitHubService apiGitHubService, ExecutorManager executorManager) {

        this.appDatabase = appDatabase;
        this.repositoryDao = repositoryDao;
        this.apiGitHubService = apiGitHubService;
        this.executorManager = executorManager;
    }

    public LiveData<SearchReposResult> searchRepositories(String query) {

        MediatorLiveData<SearchReposResult> result = new MediatorLiveData<>();
        result.setValue(SearchReposResult.getInProgress(null));

        LiveData<List<Repository>> dbSource = loadFromDb(query);
        result.addSource(dbSource, repositories -> {
            result.removeSource(dbSource);

            LiveData<ApiResponse<SearchReposResponse>> networkSource = loadFromNetwork(query);
            result.addSource(dbSource, data ->
                    result.setValue(SearchReposResult.getInProgress(data)));
            result.addSource(networkSource, apiResponse -> {
                result.removeSource(networkSource);
                result.removeSource(dbSource);

                if (apiResponse.isSuccessful()) {
                    executorManager.getDiskExecutor().execute(() -> {

                        SearchReposResponse response = apiResponse.body;

                        List<Integer> repositoryIds = response.getRepositoryIds();
                        RepositoryQueryResult queryResult = new RepositoryQueryResult(query,
                                repositoryIds, response.getTotalCount(), apiResponse.getNextPage());
                        appDatabase.beginTransaction();
                        try {
                            repositoryDao.insertRepos(response.getRepositories());
                            repositoryDao.insert(queryResult);
                            appDatabase.setTransactionSuccessful();
                        } finally {
                            appDatabase.endTransaction();
                        }

                        executorManager.getMainThreadExecutor().execute(() ->
                                result.addSource(loadFromDb(query), data -> result.setValue(
                                        SearchReposResult.getSuccess(data, null))));

                    });
                } else {
                    result.addSource(dbSource, data -> result.setValue(SearchReposResult.getFailed(
                            apiResponse.errorMessage, data)));
                }
            });
        });

        return result;
    }

    private LiveData<List<Repository>> loadFromDb(String query) {

        return Transformations.switchMap(repositoryDao.search(query), searchQuery -> {
            if (searchQuery == null) {

                MutableLiveData<List<Repository>> repositoriesLiveData = new MutableLiveData<>();
                repositoriesLiveData.postValue(null);
                return repositoriesLiveData;
            } else {
                return repositoryDao.loadOrderedRepos(searchQuery.repositoryIds);
            }
        });
    }

    private LiveData<ApiResponse<SearchReposResponse>> loadFromNetwork(String query) {
        return apiGitHubService.searchRepos(query);
    }

    public LiveData<DownloadNextPageResult> downloadNextPage(String query) {
        DownloadNextSearchPageTask task = new DownloadNextSearchPageTask(query, apiGitHubService,
                appDatabase);
        executorManager.getNetworkExecutor().execute(task);
        return task.getResultLiveData();
    }
}
