package org.trc.form.jingdong;

/**
 * Created by hzwyz on 2017/5/19 0019.
 */
public class JingDongDO {
    private String uid;

    private String refreshTokenExpires;

    private String time;

    private String expiresIn;

    private String refreshToken;

    private String accessToken;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRefreshTokenExpires() {
        return refreshTokenExpires;
    }

    public void setRefreshTokenExpires(String refreshTokenExpires) {
        this.refreshTokenExpires = refreshTokenExpires;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
