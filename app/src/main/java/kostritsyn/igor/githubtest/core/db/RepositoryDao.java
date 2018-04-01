package kostritsyn.igor.githubtest.core.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.util.Log;
import android.util.SparseIntArray;

import java.util.Collections;
import java.util.List;

import kostritsyn.igor.githubtest.core.entity.Repository;
import kostritsyn.igor.githubtest.core.entity.RepositoryQueryResult;

@Dao
public abstract class RepositoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Repository... repositories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertRepos(List<Repository> repositories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(RepositoryQueryResult result);

    @Query("SELECT * FROM RepositoryQueryResult WHERE query = :query")
    public abstract LiveData<RepositoryQueryResult> search(String query);

    @Query("SELECT * FROM Repository WHERE id in (:ids)")
    public abstract LiveData<List<Repository>> loadRepos(List<Integer> ids);

    public LiveData<List<Repository>> loadOrderedRepos(List<Integer> ids) {

        SparseIntArray order = new SparseIntArray();
        int index = 0;
        for (Integer id : ids) {
            order.put(id, index++);
        }

        return Transformations.map(loadRepos(ids), repositories -> {
            Collections.sort(repositories,
                    (r1, r2) -> order.get(r1.getId()) - order.get(r2.getId()));
            return repositories;
        });
    }

    @Query("SELECT * FROM RepositoryQueryResult WHERE query = :query")
    public abstract RepositoryQueryResult findRepositoryQueryResult(String query);
}
