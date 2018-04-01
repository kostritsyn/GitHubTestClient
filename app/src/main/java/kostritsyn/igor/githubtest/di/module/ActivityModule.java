package kostritsyn.igor.githubtest.di.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import kostritsyn.igor.githubtest.MainActivity;
import kostritsyn.igor.githubtest.ui.auth.AuthActivity;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract AuthActivity contributeAuthActivity();
}

