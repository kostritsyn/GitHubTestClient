package kostritsyn.igor.githubtest.core.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import kostritsyn.igor.githubtest.core.entity.AccessToken;

@Dao
public abstract class AccessTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(AccessToken accessToken);

    @Query("SELECT * FROM AccessToken WHERE tokenId = :id")
    public abstract LiveData<AccessToken> getToken(int id);
}
