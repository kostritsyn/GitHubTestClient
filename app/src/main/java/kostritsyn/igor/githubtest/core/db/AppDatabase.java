package kostritsyn.igor.githubtest.core.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import javax.inject.Singleton;

import kostritsyn.igor.githubtest.core.entity.AccessToken;
import kostritsyn.igor.githubtest.core.entity.Repository;
import kostritsyn.igor.githubtest.core.entity.RepositoryQueryResult;

@Singleton
@Database(entities = {
        Repository.class,
        RepositoryQueryResult.class,
        AccessToken.class
}, version = 10)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RepositoryDao repositoryDao();

    public abstract AccessTokenDao accessTokenDao();
}
