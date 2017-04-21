package org.trc.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import org.trc.util.PageResult;
import org.trc.util.Pagination;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by george on 2017/3/23.
 */
@Service
public interface IBaseService<T,PK> {

    PageResult pagination(Example example, int page, int limit);

    int insert(T record);

    int insertSelective(T record);

    int deleteByPrimaryKey(PK key);

    int updateByPrimaryKey(T record);

    int updateByPrimaryKeySelective(T record);

    T selectOne(T record);

    List<T> select(T record);

    List<T> selectByExample(Object example);

}
