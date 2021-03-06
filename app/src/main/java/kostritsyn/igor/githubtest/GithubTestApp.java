package kostritsyn.igor.githubtest;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.AndroidSupportInjection;
import kostritsyn.igor.githubtest.di.DaggerAppComponent;
import kostritsyn.igor.githubtest.di.Injectable;

public class GithubTestApp extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> mDispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);

        registerActivityLifecycleCallbacks(new Callbacks());
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return mDispatchingAndroidInjector;
    }

    private static class Callbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            if (activity instanceof Injectable) {
                AndroidInjection.inject(activity);
            }

            if (activity instanceof FragmentActivity) {

                ((FragmentActivity) activity).getSupportFragmentManager()
                        .registerFragmentLifecycleCallbacks(
                                new FragmentManager.FragmentLifecycleCallbacks() {
                                    @Override
                                    public void onFragmentCreated(FragmentManager fm, Fragment f,
                                                                  Bundle savedInstanceState) {
                                        if (f instanceof Injectable) {
                                            AndroidSupportInjection.inject(f);
                                        }
                                    }
                                }, true);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
