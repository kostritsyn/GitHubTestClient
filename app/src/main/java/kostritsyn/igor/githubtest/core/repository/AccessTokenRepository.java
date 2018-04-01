package kostritsyn.igor.githubtest.core.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.google.gson.JsonObject;

import javax.inject.Inject;
import javax.inject.Singleton;

import kostritsyn.igor.githubtest.core.ExecutorManager;
import kostritsyn.igor.githubtest.core.db.AccessTokenDao;
import kostritsyn.igor.githubtest.core.db.AppDatabase;
import kostritsyn.igor.githubtest.core.entity.AccessToken;
import kostritsyn.igor.githubtest.core.network.ApiResponse;
import kostritsyn.igor.githubtest.core.network.CreateTokenResult;
import kostritsyn.igor.githubtest.core.network.GitHubService;

@Singleton
public class AccessTokenRepository {

    private final AppDatabase appDatabase;

    private final AccessTokenDao accessTokenDao;

    private final GitHubService gitHubService;

    private final ExecutorManager executorManager;

    @Inject
    AccessTokenRepository(AppDatabase appDatabase, AccessTokenDao accessTokenDao,
                          GitHubService gitHubService, ExecutorManager executorManager) {

        this.appDatabase = appDatabase;
        this.accessTokenDao = accessTokenDao;
        this.gitHubService = gitHubService;
        this.executorManager = executorManager;
    }

    public MediatorLiveData<String> createAccessTokenWithCode(String code) {

        JsonObject params = new JsonObject();
        params.addProperty("client_id", GitHubService.CLIENT_ID);
        params.addProperty("client_secret", GitHubService.CLIENT_SECRET);
        params.addProperty("code", code);

        LiveData<ApiResponse<CreateTokenResult>> apiResponseLiveData
                = gitHubService.createToken(params);

        MediatorLiveData<String> result = new MediatorLiveData<>();
        result.addSource(apiResponseLiveData, response -> {
            result.removeSource(apiResponseLiveData);

            if (response == null) {
                result.setValue(null);
                return;
            }

            if (response.isSuccessful()) {

                CreateTokenResult createTokenResult = response.body;
                String token = createTokenResult != null ?
                        createTokenResult.getAccessToken() : null;
                saveAccessToken(token);

                result.setValue(token);
            }
        });

        return result;
    }

    public LiveData<AccessToken> getAccessToken() {
        return accessTokenDao.getToken(AccessToken.KEY);
    }

    public void saveAccessToken(String token) {

        executorManager.getDiskExecutor().execute(() -> {

            AccessToken accessToken = new AccessToken();
            accessToken.setToken(token);

            appDatabase.beginTransaction();
            try {
                accessTokenDao.insert(accessToken);
                appDatabase.setTransactionSuccessful();
            } finally {
                appDatabase.endTransaction();
            }
        });

    }
}
