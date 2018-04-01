package kostritsyn.igor.githubtest.ui.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import kostritsyn.igor.githubtest.core.repository.RepoRepository;

public class RepoListViewModel extends ViewModel {

    private final MutableLiveData<String> queryLiveData = new MutableLiveData<>();

    private final LiveData<SearchReposResult> searchResultLiveData;

    private final NextPageObserver nextPageObserver;

    @Inject
    public RepoListViewModel(RepoRepository repoRepository) {

        nextPageObserver = new NextPageObserver(repoRepository);

        searchResultLiveData = Transformations.switchMap(queryLiveData, query -> {
           if (query == null || query.trim().isEmpty()) {
               MutableLiveData<SearchReposResult> resultLiveData = new MutableLiveData<>();
               resultLiveData.postValue(null);
               return resultLiveData;
           } else {
               return repoRepository.searchRepositories(query);
           }
        });
    }

    public LiveData<SearchReposResult> getSearchReposResult() {
        return searchResultLiveData;
    }

    public LiveData<NextPageObserver.LoadMoreState> getLoadMoreState() {
        return nextPageObserver.getLoadMoreState();
    }

    public LiveData<Boolean> getHasMore() {
        return nextPageObserver.getHasMore();
    }

    public boolean setQuery(@NonNull String query) {

        String lowerCaseQuery = query.trim().toLowerCase();
        if (!lowerCaseQuery.equals(queryLiveData.getValue())) {

            nextPageObserver.reset();
            queryLiveData.setValue(query);
            return true;
        }

        return false;
    }

    public void downloadNextPage() {

        String query = queryLiveData.getValue();
        if (query == null || query.isEmpty()) {
            return;
        }

       nextPageObserver.downloadNextPage(query);
    }
}
