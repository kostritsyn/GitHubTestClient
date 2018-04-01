package kostritsyn.igor.githubtest.ui.repo;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import java.util.List;

import javax.inject.Inject;

import kostritsyn.igor.githubtest.R;
import kostritsyn.igor.githubtest.core.entity.Repository;
import kostritsyn.igor.githubtest.databinding.FragmentRepoListBinding;
import kostritsyn.igor.githubtest.di.Injectable;
import kostritsyn.igor.githubtest.util.KeyboardUtils;

public class RepoListFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private RepoListViewModel repoListViewModel;

    private FragmentRepoListBinding binding;

    private RepoListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_repo_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        repoListViewModel = ViewModelProviders.of(getActivity(), viewModelFactory)
                .get(RepoListViewModel.class);

        adapter = new RepoListAdapter();
        binding.repoRecyclerView.setAdapter(adapter);

        binding.repoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager
                        = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
                    repoListViewModel.downloadNextPage();
                }
            }
        });

        repoListViewModel.getSearchReposResult().observe(this, searchReposResult -> {

            if (searchReposResult == null) {
                return;
            }

            @SearchReposResult.State int state = searchReposResult.getState();
            List<Repository> repositories = searchReposResult.getRepositories();

            binding.noReposFound
                    .setVisibility(state == SearchReposResult.STATE_SUCCESS && repositories.isEmpty() ?
                            View.VISIBLE : View.GONE);

            binding.searchProgressBar.setVisibility(state == SearchReposResult.STATE_IN_PROGRESS ?
                    View.VISIBLE : View.GONE);

            adapter.setRepositories(repositories);

            if (searchReposResult.getState() == SearchReposResult.STATE_FAILED) {

                Snackbar snackbar = Snackbar.make(binding.container,
                        searchReposResult.getErrorMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        repoListViewModel.getHasMore().observe(this, hasMore -> {
            adapter.setHasMore(hasMore == null || hasMore);
        });

        repoListViewModel.getLoadMoreState().observe(this, loadMoreState -> {

            if (loadMoreState == null) {
                return;
            }

            if (!loadMoreState.isRunning()) {

                String errorMessage = loadMoreState.getErrorMessage();
                if (errorMessage != null) {
                    Snackbar snackbar = Snackbar.make(binding.container, errorMessage,
                            Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        binding.searchReposTextInput.setOnEditorActionListener((view, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchRepos();
                return true;
            }

            return false;
        });

        binding.searchReposTextInput.setOnKeyListener((view, keyCode, event) -> {

            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                searchRepos();
                return true;
            }

            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        adapter = null;
    }

    public void searchRepos() {

        KeyboardUtils.hideKeyboard(getActivity());

        String query = binding.searchReposTextInput.getText().toString();
        binding.setQuery(query);
        repoListViewModel.setQuery(query);
    }
}
