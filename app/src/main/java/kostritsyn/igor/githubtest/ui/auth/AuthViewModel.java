package kostritsyn.igor.githubtest.ui.auth;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import kostritsyn.igor.githubtest.core.repository.AccessTokenRepository;

public class AuthViewModel extends ViewModel {

    private final MutableLiveData<String> codeLiveData = new MutableLiveData<>();

    private final LiveData<String> accessTokenLiveData;

    @Inject
    AccessTokenRepository accessTokenRepository;

    @Inject
    public AuthViewModel() {
        accessTokenLiveData = Transformations.switchMap(codeLiveData,
                code -> accessTokenRepository.createAccessTokenWithCode(code));
    }

    public LiveData<String> getAccessToken() {
        return accessTokenLiveData;
    }

    public void createAccessToken(String code) {
        codeLiveData.setValue(code);
    }

    public void logout() {
        accessTokenRepository.saveAccessToken(null);
    }
}
