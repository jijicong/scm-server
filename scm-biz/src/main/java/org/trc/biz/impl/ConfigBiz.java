package org.trc.biz.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.IConfigBiz;
import org.trc.domain.score.Dict;
import org.trc.domain.score.DictType;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.form.DictForm;
import org.trc.form.DictTypeForm;
import org.trc.service.IDictService;
import org.trc.service.IDictTypeService;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import sun.security.krb5.internal.PAData;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by hzwdx on 2017/4/19.
 */
@Service("configBiz")
public class ConfigBiz implements IConfigBiz {

    @Autowired
    private IDictTypeService dictTypeService;
    @Autowired
    private IDictService dictService;

    @Override
    public Pagenation<DictType> dictTypePage(DictTypeForm queryModel, Pagenation<DictType> page) throws Exception {
        Example example = new Example(DictType.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(queryModel.getName())) {
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if(StringUtil.isNotEmpty(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("isValid").desc();
        //分页查询
        return dictTypeService.pagination(example, page, queryModel);
    }

    @Override
    public List<DictType> queryDictTypes() throws Exception {
        DictType dictType = new DictType();
        dictType.setIsValid(ZeroToNineEnum.ONE.getCode());
        dictType.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return dictTypeService.select(dictType);
    }

    @Override
    public int saveDictType(DictType dictType) throws Exception{
        int count = 0;
        if(null != dictType.getId()){
            //修改
            DictType tmp = new DictType();
            tmp.setId(dictType.getId());
            tmp = dictTypeService.selectOne(tmp);
            BeanUtils.copyProperties(dictType, tmp);
            tmp.setUpdateTime(new Date());
            count = dictTypeService.updateByPrimaryKey(tmp);
        }else{
            //新增
            CommonUtil.setBaseDO(dictType);
            count = dictTypeService.insert(dictType);
            if(count == 0)
                throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION,
                        CommonUtil.joinStr("保存字典类型",JSON.toJSONString(dictType),"到数据库失败").toString());
        }
        return count;
    }

    @Override
    public DictType findDictTypeById(Long id)throws Exception {
        DictType dictType = new DictType();
        dictType.setId(id);
        dictType = dictTypeService.selectOne(dictType);
        if(null == dictType)
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_QUERY_EXCEPTION,
                    CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]查询字典类型为空").toString());
        return dictType;
    }

    @Override
    public DictType findDictTypeByTypeNo(String typeNo) throws Exception{
        DictType dictType = new DictType();
        dictType.setIsValid(ZeroToNineEnum.ONE.getCode());
        dictType.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        dictType.setCode(typeNo);
        return dictTypeService.selectOne(dictType);
    }

    @Override
    public int deleteDictTypeById(Long id) throws Exception {
        DictType tmp = new DictType();
        tmp.setId(id);
        tmp = dictTypeService.selectOne(tmp);
        tmp.setIsValid(ZeroToNineEnum.ZERO.getCode());
        tmp.setUpdateTime(new Date());
        int count = dictTypeService.updateByPrimaryKey(tmp);
        if(count == 0)
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION,
                    CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]删除字典类型失败").toString());
        return count;
    }

    @Override
    public Pagenation<Dict> dictPage(DictForm queryModel, Pagenation<Dict> page) throws Exception {
        Example example = new Example(Dict.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(queryModel.getTypeNo())) {
            criteria.andEqualTo("typeNo", queryModel.getTypeNo());
        }
        if(StringUtil.isNotEmpty(queryModel.getName())) {
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if(StringUtil.isNotEmpty(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("typeNo").asc().orderBy("isValid").desc();
        //分页查询
        return dictService.pagination(example, page, queryModel);
    }

    @Override
    public List<Dict> queryDicts(Dict dict) throws Exception{
        if(StringUtils.isEmpty(dict.getIsValid()))
            dict.setIsValid(ZeroToNineEnum.ONE.getCode());
        return dictService.select(dict);
    }

    @Override
    public int saveDict(Dict dict) throws Exception{
        int count = 0;
        if(null != dict.getId()){
            //修改
            Dict tmp = new Dict();
            tmp.setId(dict.getId());
            tmp = dictService.selectOne(tmp);
            BeanUtils.copyProperties(dict, tmp);
            tmp.setUpdateTime(new Date());
            count = dictService.updateByPrimaryKey(tmp);
        }else{
            //新增
            CommonUtil.setBaseDO(dict);
            count = dictService.insert(dict);
            if(count == 0)
                throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION,
                        CommonUtil.joinStr("保存字典",JSON.toJSONString(dict),"到数据库失败").toString());
        }
        return count;
    }

    @Override
    public Dict findDictById(Long id) throws Exception {
        Dict dict = new Dict();
        dict.setId(id);
        dict = dictService.selectOne(dict);
        if(null == dict)
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_QUERY_EXCEPTION,
                    CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]查询字典为空").toString());
        return dict;
    }

    @Override
    public int deleteDictById(Long id) throws Exception {
        Dict tmp = new Dict();
        tmp.setId(id);
        tmp = dictService.selectOne(tmp);
        tmp.setIsValid(ZeroToNineEnum.ZERO.getCode());
        tmp.setUpdateTime(new Date());
        int count = dictService.updateByPrimaryKey(tmp);
        if(count == 0)
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION,
                    CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]删除字典失败").toString());
        return count;
    }

}
