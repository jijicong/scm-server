package org.trc.service;

import org.trc.domain.score.Auth;

import java.util.List;

/**
 * Created by george on 2017/3/31.
 */
public interface IAuthService extends IBaseService<Auth,Long>{

    List<Auth> selectByAuth(Auth country, int page, int rows);

}
