package org.trc.service.impl;

import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.trc.domain.score.Auth;
import org.trc.service.IAuthService;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.List;

/**
 * Created by george on 2017/3/31.
 */
@Service("authService")
public class AuthService extends BaseService<Auth,Long> implements IAuthService {

    @Override
    public List<Auth> selectByAuth(Auth country, int page, int rows) {
        Example example = new Example(Auth.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(country.getChannelCode())) {
            criteria.andLike("channelCode", "%" + country.getChannelCode() + "%");
        }
        if (country.getId() != null) {
            criteria.andEqualTo("id", country.getId());
        }
        example.setOrderByClause("channelCode asc,createTime asc");
        //分页查询
        PageHelper.startPage(page, rows);
        return selectByExample(example);
    }
}
