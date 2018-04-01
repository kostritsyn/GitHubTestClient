package kostritsyn.igor.githubtest.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import kostritsyn.igor.githubtest.GithubTestApp;
import kostritsyn.igor.githubtest.di.module.ActivityModule;
import kostritsyn.igor.githubtest.di.module.AppModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        ActivityModule.class
})
public interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(GithubTestApp githubTestApp);
}
