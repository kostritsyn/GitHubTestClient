package kostritsyn.igor.githubtest.ui.auth;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import kostritsyn.igor.githubtest.R;
import kostritsyn.igor.githubtest.core.network.GitHubService;
import kostritsyn.igor.githubtest.databinding.ActivityAuthBinding;
import kostritsyn.igor.githubtest.di.Injectable;
import kostritsyn.igor.githubtest.di.ViewModelFactory;

public class AuthActivity extends AppCompatActivity implements Injectable {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STAGE_AUTH, STAGE_ACCESS_TOKEN, STAGE_FAILED})
    public @interface Stage {}
    public static final int STAGE_AUTH = 0;
    public static final int STAGE_ACCESS_TOKEN = 1;
    public static final int STAGE_FAILED = 2;

    public static final int RESULT_CODE_SUCCESS = 1;
    public static final int RESULT_CODE_ERROR = 2;

    private static final String GITHUB_URL = "https://github.com/login/oauth/authorize";
    private static final String GITHUB_COMPLETE_URL = "kostritsyn://githubaac.complete";

    @Inject
    ViewModelFactory viewModelFactory;

    private AuthViewModel authViewModel;

    private ActivityAuthBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        authViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(AuthViewModel.class);
        authViewModel.getAccessToken().observe(this, accessToken -> {
            if (accessToken == null) {
                binding.setAuthStage(STAGE_FAILED);
            } else {
                finishActivity(accessToken);
            }
        });

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth);

        binding.authTryAgainButton.setOnClickListener(v -> authorize());

        binding.authWebview.getSettings().setJavaScriptEnabled(true);
        binding.authWebview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);

                if (!url.startsWith(GITHUB_COMPLETE_URL)) {
                    return false;
                }

                binding.setAuthStage(STAGE_ACCESS_TOKEN);
                authViewModel.createAccessToken(Uri.parse(url).getQueryParameter("code"));
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                binding.setAuthStage(STAGE_FAILED);
            }
        });

        authorize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding = null;
    }

    private void authorize() {

        binding.setAuthStage(STAGE_AUTH);

        StringBuilder urlBuilder = new StringBuilder(GITHUB_URL)
                .append("?client_id=")
                .append(GitHubService.CLIENT_ID);
        binding.authWebview.loadUrl(urlBuilder.toString());
    }

    private void finishActivity(String accessToken) {

        Intent intent = new Intent();
        intent.putExtra("accessToken", accessToken);

        setResult(accessToken != null ? RESULT_CODE_SUCCESS : RESULT_CODE_ERROR, intent);
        finish();
    }
}
