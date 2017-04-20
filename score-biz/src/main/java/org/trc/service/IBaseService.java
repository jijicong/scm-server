package org.trc.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by george on 2017/3/23.
 */
@Service
public interface IBaseService<T,PK> {

    int insert(T record);

    int insertSelective(T record);

    int deleteByPrimaryKey(PK key);

    int updateByPrimaryKey(T record);

    int updateByPrimaryKeySelective(T record);

    T selectOne(T record);

    List<T> select(T record);

    int selectCount(T record);

    T selectByPrimaryKey(PK key);

}
