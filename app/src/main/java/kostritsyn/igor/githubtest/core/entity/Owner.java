package kostritsyn.igor.githubtest.core.entity;

import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

public class Owner {

    @SerializedName("login")
    @PrimaryKey
    private String login;

    @SerializedName("avatar_url")
    private String avatar;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "login='" + login + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Owner owner = (Owner) o;

        if (login != null ? !login.equals(owner.login) : owner.login != null) return false;
        return avatar != null ? avatar.equals(owner.avatar) : owner.avatar == null;
    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        return result;
    }
}
