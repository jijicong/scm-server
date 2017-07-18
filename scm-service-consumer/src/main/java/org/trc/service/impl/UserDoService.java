package org.trc.service.impl;

import com.alibaba.fastjson.JSON;
import com.tairanchina.md.account.user.model.UserDO;
import com.tairanchina.md.account.user.service.UserService;
import com.tairanchina.md.api.QueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.UserCenterException;
import org.trc.service.IUserDoService;

/**
 * Created by hzszy on 2017/6/10.
 */
@Service("userDoService")
public class UserDoService implements IUserDoService {
    private Logger LOGGER = LoggerFactory.getLogger(UserDoService.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDO getUserDo(String phone) {
        UserDO userDO;
        try {
            userDO = userService.getUserDO(QueryType.Phone, phone);
        } catch (Exception e) {
            String msg = String.format("根据手机号%s查询失败", JSON.toJSONString(phone));
            LOGGER.error(msg);
            throw new UserCenterException(ExceptionEnum.USER_CENTER_QUERY_EXCEPTION, msg);
        }

        return userDO;
    }
}
