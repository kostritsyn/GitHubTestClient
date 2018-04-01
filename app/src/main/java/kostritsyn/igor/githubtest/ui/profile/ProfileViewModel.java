package kostritsyn.igor.githubtest.ui.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import kostritsyn.igor.githubtest.core.entity.AccessToken;
import kostritsyn.igor.githubtest.core.repository.AccessTokenRepository;

public class ProfileViewModel extends ViewModel {

    private final LiveData<AccessToken> accessTokenLiveData;

    @Inject
    ProfileViewModel(AccessTokenRepository accessTokenRepository) {
        accessTokenLiveData = accessTokenRepository.getAccessToken();
    }

    public LiveData<AccessToken> getAccessToken() {
        return accessTokenLiveData;
    }
}
