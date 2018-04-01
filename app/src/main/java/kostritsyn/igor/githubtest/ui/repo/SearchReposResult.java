package kostritsyn.igor.githubtest.ui.repo;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import kostritsyn.igor.githubtest.core.entity.Repository;

public class SearchReposResult {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_SUCCESS, STATE_FAILED, STATE_IN_PROGRESS})
    public @interface State {}
    public static final int STATE_SUCCESS = 0;
    public static final int STATE_FAILED = 1;
    public static final int STATE_IN_PROGRESS = 2;

    private @State int state;

    private String errorMessage;

    private List<Repository> repositories;

    private Integer nextPage;

    public static SearchReposResult getSuccess(List<Repository> repositories, Integer nextPage) {
        return new SearchReposResult(STATE_SUCCESS, null, repositories, nextPage);
    }

    public static SearchReposResult getFailed(String errorMessage) {
        return new SearchReposResult(STATE_FAILED, errorMessage, new ArrayList<>(0), null);
    }

    public static SearchReposResult getFailed(String errorMessage, List<Repository> repositories) {
        return new SearchReposResult(STATE_FAILED, errorMessage, repositories, null);
    }

    public static SearchReposResult getInProgress(List<Repository> repositories) {
        return new SearchReposResult(STATE_IN_PROGRESS, null,
                repositories == null ? new ArrayList<>(0) : repositories, null);
    }

    private SearchReposResult(@State int state, String errorMessage, List<Repository> repositories,
                              Integer nextPage) {

        this.state = state;
        this.errorMessage = errorMessage;
        this.repositories = repositories;
        this.nextPage = nextPage;
    }

    public @State int getState() {
        return state;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public static SearchReposResult merge(SearchReposResult originalData,
                                          SearchReposResult nextPageData) {

        List<Repository> originalRepos = originalData.getRepositories();
        List<Repository> nextPageRepos = nextPageData.getRepositories();

        int repoCount = 0;
        if (originalRepos != null) {
            repoCount += originalRepos.size();
        }

        if (nextPageRepos != null) {
            repoCount += nextPageRepos.size();
        }

        List<Repository> repositories = new ArrayList<>(repoCount);

        if (originalRepos != null) {
            repositories.addAll(originalRepos);
        }

        if (nextPageRepos != null) {
            repositories.addAll(nextPageRepos);
        }

        return SearchReposResult.getSuccess(repositories, nextPageData.getNextPage());
    }

    @Override
    public String toString() {
        return "SearchReposResult{" +
                "state=" + state +
                ", errorMessage='" + errorMessage + '\'' +
                ", nextPage=" + nextPage +
                ", repositories=" + repositories +
                '}';
    }
}

