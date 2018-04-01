package kostritsyn.igor.githubtest.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import kostritsyn.igor.githubtest.di.ViewModelFactory;
import kostritsyn.igor.githubtest.di.ViewModelKey;
import kostritsyn.igor.githubtest.ui.auth.AuthViewModel;
import kostritsyn.igor.githubtest.ui.profile.ProfileViewModel;
import kostritsyn.igor.githubtest.ui.repo.RepoListViewModel;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RepoListViewModel.class)
    abstract ViewModel bindSearchViewModel(RepoListViewModel repoListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel.class)
    abstract ViewModel bindAuthViewModel(AuthViewModel authViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel bindProfileViewModel(ProfileViewModel profileViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
