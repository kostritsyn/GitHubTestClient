package kostritsyn.igor.githubtest.core.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import kostritsyn.igor.githubtest.core.entity.Repository;

public class SearchReposResponse {

    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("items")
    private List<Repository> repositories;

    private Integer nextPage;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }

    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public List<Integer> getRepositoryIds() {

        if (repositories == null) {
            return new ArrayList<>(0);
        }

        List<Integer> repositoryIds = new ArrayList<>(repositories.size());
        for (Repository repository : repositories) {
            repositoryIds.add(repository.getId());
        }

        return repositoryIds;
    }
}
