package kostritsyn.igor.githubtest.di.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import kostritsyn.igor.githubtest.ui.repo.RepoListFragment;

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    public abstract RepoListFragment contributeSearchFragment();
}
