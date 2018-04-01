package kostritsyn.igor.githubtest.ui.repo;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import kostritsyn.igor.githubtest.BR;
import kostritsyn.igor.githubtest.R;
import kostritsyn.igor.githubtest.core.entity.Repository;

public class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.BindingHolder> {

    private static final int VIEW_TYPE_REPOSITORY = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private List<Repository> repositories;
    private boolean hasMoreRepos;

    public void setRepositories(List<Repository> repositories) {

        this.repositories = repositories;
        notifyDataSetChanged();
    }

    public void setHasMore(boolean hasMore) {

        this.hasMoreRepos = hasMore;
        notifyDataSetChanged();
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        BindingHolder holder;
        if (viewType == VIEW_TYPE_LOADING) {
            holder = LoadingBindingHolder.newInstance(LayoutInflater.from(parent.getContext()),
                    R.layout.view_loading, parent, false);
        } else {
            holder = RepoBindingHolder.newInstance(LayoutInflater.from(parent.getContext()),
                    R.layout.view_repository, parent, false);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_REPOSITORY) {
            ((RepoBindingHolder<ViewDataBinding>)holder).setRepository(repositories.get(position));
        }
    }

    @Override
    public int getItemCount() {

        int count = 0;
        if (repositories != null) {
            count += repositories.size();
        }

        if (count != 0 && hasMoreRepos) {
            count += 1;
        }

        return count;
    }

    @Override
    public int getItemViewType(int position) {

        int viewType;
        if (position == getItemCount() - 1 && hasMoreRepos) {
            viewType = VIEW_TYPE_LOADING;
        } else {
            viewType = VIEW_TYPE_REPOSITORY;
        }

        return viewType;
    }

    public static abstract class BindingHolder<VB extends ViewDataBinding>
            extends RecyclerView.ViewHolder {

        protected VB binding;

        public BindingHolder(VB binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }

    public static class RepoBindingHolder<VB extends ViewDataBinding> extends BindingHolder<VB> {

        public static <VB extends ViewDataBinding> RepoBindingHolder newInstance(
                LayoutInflater inflater, @LayoutRes int layoutId, ViewGroup parent,
                boolean attachToParent) {
            final VB vb = DataBindingUtil.inflate(inflater, layoutId, parent, attachToParent);
            return new RepoBindingHolder<>(vb);
        }

        private RepoBindingHolder(VB vb) {
            super(vb);
        }

        public void setRepository(Repository repository) {

            binding.setVariable(BR.repository, repository);
            binding.executePendingBindings();
        }
    }

    public static class LoadingBindingHolder<VB extends ViewDataBinding> extends BindingHolder<VB> {

        public static <VB extends ViewDataBinding> LoadingBindingHolder newInstance(
                LayoutInflater inflater, @LayoutRes int layoutId, ViewGroup parent,
                boolean attachToParent) {
            final VB vb = DataBindingUtil.inflate(inflater, layoutId, parent, attachToParent);
            return new LoadingBindingHolder<>(vb);
        }

        private LoadingBindingHolder(VB vb) {
            super(vb);
        }
    }
}
