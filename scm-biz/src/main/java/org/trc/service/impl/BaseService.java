package org.trc.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.trc.service.IBaseService;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by george on 2017/3/23.
 */
public class BaseService<T,PK> implements IBaseService<T,PK> {

    @Autowired
    private Mapper<T> mapper;

    @Override
    public int insert(T record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(T record) {
        return mapper.insertSelective(record);
    }

    @Override
    public int deleteByPrimaryKey(PK key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int updateByPrimaryKey(T record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByPrimaryKeySelective(T record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public T selectOne(T record) {
        return mapper.selectOne(record);
    }

    @Override
    public List<T> select(T record) {
        return mapper.select(record);
    }

    @Override
    public List<T> selectByExample(Object example) {
        return mapper.selectByExample(example);
    }

}
