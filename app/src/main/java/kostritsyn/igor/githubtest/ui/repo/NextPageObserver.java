package kostritsyn.igor.githubtest.ui.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import kostritsyn.igor.githubtest.core.repository.RepoRepository;

public class NextPageObserver implements Observer<DownloadNextPageResult> {

    private LiveData<DownloadNextPageResult> nextPageLiveData;

    private final MutableLiveData<LoadMoreState> loadMoreStateLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> hasMoreLiveData = new MutableLiveData<>();

    private String query;

    private boolean hasMore;

    private final RepoRepository repoRepository;

    public NextPageObserver(RepoRepository repoRepository) {
        this.repoRepository = repoRepository;
        hasMoreLiveData.setValue(true);
    }

    public LiveData<LoadMoreState> getLoadMoreState() {
        return loadMoreStateLiveData;
    }

    public LiveData<Boolean> getHasMore() {
        return hasMoreLiveData;
    }

    public void downloadNextPage(String query) {

        if (this.query != null && this.query.equals(query)) {
            return;
        }

        clear();
        this.query = query;

        nextPageLiveData = repoRepository.downloadNextPage(query);
        loadMoreStateLiveData.setValue(new LoadMoreState(true, null));

        nextPageLiveData.observeForever(this);
    }

    private void clear() {

        if (nextPageLiveData == null) {
            return;
        }

        nextPageLiveData.removeObserver(this);
        nextPageLiveData = null;

        if (hasMore) {
            query = null;
        }
    }

    public void reset() {

        clear();
        hasMore = true;
        loadMoreStateLiveData.setValue(new LoadMoreState(false, null));
    }

    @Override
    public void onChanged(@Nullable DownloadNextPageResult result) {

        if (result == null) {
            reset();
        } else {
            if (result.isSuccessful()) {
                hasMore = result.hasMore();
                clear();
                loadMoreStateLiveData.setValue(new LoadMoreState(false, null));
            } else {
                hasMore = true;
                clear();
                loadMoreStateLiveData.setValue(new LoadMoreState(false, result.getErrorMessage()));
            }
        }
    }

    public static class LoadMoreState {

        private final boolean running;
        private final String errorMessage;
        private boolean errorHandled = false;

        LoadMoreState(boolean running, String errorMessage) {

            this.running = running;
            this.errorMessage = errorMessage;
        }

        boolean isRunning() {
            return running;
        }

        String getErrorMessage() {
            return errorMessage;
        }

        String getErrorMessageIfNotHandled() {

            if (errorHandled) {
                return null;
            }

            errorHandled = true;
            return errorMessage;
        }
    }
}
