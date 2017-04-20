package org.trc.service.impl;

import org.springframework.stereotype.Service;
import org.trc.domain.score.Auth;
import org.trc.service.IAuthService;

/**
 * Created by george on 2017/3/31.
 */
@Service("authService")
public class AuthService extends BaseService<Auth,Long> implements IAuthService {


}
