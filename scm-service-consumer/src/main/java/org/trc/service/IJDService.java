package org.trc.service;


/**
 * Created by hzwyz on 2017/5/18 0018.
 */
public interface IJDService {

    /**
     *创建Token
     * @param
     * @return 返回Access_Token,RefreshToken
     * @throws Exception
     */
    public String createToken() throws Exception;

    /**
     * 使用RefreshToken刷新AccessToken
     * @param refreshToken
     * @return
     */
    public String freshAccessTokenByRefreshToken(String refreshToken) throws Exception;


}
