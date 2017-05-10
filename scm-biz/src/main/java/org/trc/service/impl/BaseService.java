package org.trc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.service.IBaseService;
import org.trc.util.Pagenation;
import org.trc.util.QueryModel;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by george on 2017/3/23.
 */
public class BaseService<T,PK> implements IBaseService<T,PK> {

    @Autowired
    private Mapper<T> mapper;
    @Autowired
    private MySqlMapper<T> mySqlMapper;

    @Override
    public Pagenation<T> pagination(Example example, Pagenation<T> pagenation, QueryModel queryModel) {
        if(StringUtil.isNotEmpty(queryModel.getOrderBy())) {
            for(int i=0; i<queryModel.getOrderBys().length; i++){
                setPageOrder(example, queryModel.getOrderBys()[i], queryModel.getOrders()[i]);
            }
        }
        int totalCount = mapper.selectCountByExample(example);
        PageHelper.startPage(pagenation.getPageNo(), pagenation.getPageSize());
        List<T> list = mapper.selectByExample(example);
        pagenation.setTotalCount(totalCount);
        pagenation.setResult(list);
        return pagenation;
    }

    private void setPageOrder(Example example, String orderBy, String order){
        if(StringUtils.equals("DESC", order.toUpperCase()))
            example.orderBy(orderBy).desc();
        else
            example.orderBy(orderBy).asc();
    }

    @Override
    public int insert(T record) {
        return mapper.insert(record);
    }

    @Override
    public int insertList(List<T> records) {
        return mySqlMapper.insertList(records);
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
    public int updateByExample(T t, Example example) {
        return mapper.updateByExample(t, example);
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
