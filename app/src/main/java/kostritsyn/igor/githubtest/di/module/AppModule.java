package kostritsyn.igor.githubtest.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kostritsyn.igor.githubtest.core.db.AccessTokenDao;
import kostritsyn.igor.githubtest.core.db.AppDatabase;
import kostritsyn.igor.githubtest.core.db.RepositoryDao;
import kostritsyn.igor.githubtest.core.network.ApiGitHubService;
import kostritsyn.igor.githubtest.core.network.GitHubService;
import kostritsyn.igor.githubtest.util.LiveDataCallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class AppModule {

    @Singleton
    @Provides
    ApiGitHubService provideApiGithubService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();

        return retrofit.create(ApiGitHubService.class);
    }

    @Singleton
    @Provides
    GitHubService provideGithubService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();

        return retrofit.create(GitHubService.class);
    }

    @Singleton
    @Provides
    AppDatabase provideAppDatabase(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, "githubtest.db").build();
    }

    @Singleton
    @Provides
    RepositoryDao provideRepositoryDao(AppDatabase appDatabase) {
        return appDatabase.repositoryDao();
    }

    @Singleton
    @Provides
    AccessTokenDao provideAccessTokenDao(AppDatabase appDatabase) {
        return appDatabase.accessTokenDao();
    }
}
