package kostritsyn.igor.githubtest.core.network;

import com.google.gson.annotations.SerializedName;

public class CreateTokenResult {

    @SerializedName("access_token")
    private String accseeToken;

    public String getAccessToken() {
        return accseeToken;
    }
}
