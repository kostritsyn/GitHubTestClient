package kostritsyn.igor.githubtest.core;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ExecutorManager {

    private final Executor diskExecutor;

    private final Executor netExecutor;

    private final Executor mainThreadExecutor;

    @Inject
    public ExecutorManager() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(1),
                new MainThreadExecutor());
    }

    public ExecutorManager(Executor diskExecutor, Executor netExecutor,
                           Executor mainThreadExecutor) {

        this.diskExecutor = diskExecutor;
        this.netExecutor = netExecutor;
        this.mainThreadExecutor = mainThreadExecutor;
    }

    public Executor getDiskExecutor() {
        return diskExecutor;
    }

    public Executor getNetworkExecutor() {
        return netExecutor;
    }

    public Executor getMainThreadExecutor() {
        return mainThreadExecutor;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            mainThreadHandler.post(runnable);
        }
    }
}
