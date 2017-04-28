package org.trc.biz.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.IConfigBiz;
import org.trc.domain.dict.Dict;
import org.trc.domain.dict.DictType;
import org.trc.enums.CommonExceptionEnum;
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
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by hzwdx on 2017/4/19.
 */
@Service("configBiz")
public class ConfigBiz implements IConfigBiz {

    private final static Logger log = LoggerFactory.getLogger(ConfigBiz.class);

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
    public List<DictType> queryDictTypes(DictTypeForm dictTypeForm) throws Exception {
        DictType dictType = new DictType();
        if(StringUtils.isEmpty(dictTypeForm.getIsValid())){
            dictType.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        dictType.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return dictTypeService.select(dictType);
    }

    @Override
    public int saveDictType(DictType dictType) throws Exception{
        DictType tmp = findDictTypeByTypeNo(dictType.getCode());
        if(null != tmp){
            String msg = CommonUtil.joinStr("字典类型编码为[code=",dictType.getCode(),"]的数据已存在,请使用其他编码").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }
        int count = 0;
        ParamsUtil.setBaseDO(dictType);
        count = dictTypeService.insert(dictType);
        if(count == 0){
            String msg = CommonUtil.joinStr("保存字典类型",JSON.toJSONString(dictType),"数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }
        return count;
    }

    @Override
    public int updateDictType(DictType dictType, Long id) throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("修改字典类型参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        int count = 0;
        dictType.setId(id);
        dictType.setUpdateTime(new Date());
        count = dictTypeService.updateByPrimaryKeySelective(dictType);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改字典类型",JSON.toJSONString(dictType),"数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }
        return count;
    }

    @Override
    public DictType findDictTypeById(Long id)throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("根据ID查询字典类型参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        DictType dictType = new DictType();
        dictType.setId(id);
        dictType = dictTypeService.selectOne(dictType);
        if(null == dictType) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]查询字典类型为空").toString();
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_QUERY_EXCEPTION,msg);
        }
        return dictType;
    }

    @Override
    public DictType findDictTypeByTypeNo(String typeNo) throws Exception{
        if(StringUtils.isEmpty(typeNo)){
            String msg = CommonUtil.joinStr("根据类型编码查询字典类型参数typeNo为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        DictType dictType = new DictType();
        dictType.setIsValid(ZeroToNineEnum.ONE.getCode());
        dictType.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        dictType.setCode(typeNo);
        return dictTypeService.selectOne(dictType);
    }

    @Override
    public int deleteDictTypeById(Long id) throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("根据ID删除字典类型参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        DictType tmp = new DictType();
        tmp.setId(id);
        tmp = dictTypeService.selectOne(tmp);
        tmp.setIsValid(ZeroToNineEnum.ZERO.getCode());
        tmp.setUpdateTime(new Date());
        int count = dictTypeService.updateByPrimaryKey(tmp);
        if(count == 0) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]删除字典类型失败").toString();
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION,msg);
        }
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
    public List<Dict> queryDicts(DictForm dictForm) throws Exception{
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictForm,dict);
        if(StringUtils.isEmpty(dictForm.getIsValid())){
            dict.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        dict.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return dictService.select(dict);
    }

    @Override
    public int saveDict(Dict dict) throws Exception{
        int count = 0;
        if(null != dict.getId()){
            //修改
            dict.setUpdateTime(new Date());
            count = dictService.updateByPrimaryKeySelective(dict);
        }else{
            //新增
            ParamsUtil.setBaseDO(dict);
            count = dictService.insert(dict);
        }
        if(count == 0){
            String msg = CommonUtil.joinStr("保存字典",JSON.toJSONString(dict),"到数据库失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION,msg);
        }
        return count;
    }

    @Override
    public int updateDict(Dict dict, Long id) throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("修改字典参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        int count = 0;
        dict.setId(id);
        dict.setUpdateTime(new Date());
        count = dictService.updateByPrimaryKeySelective(dict);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改字典",JSON.toJSONString(dict),"数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }
        return count;
    }

    @Override
    public Dict findDictById(Long id) throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("根据ID查询字典参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Dict dict = new Dict();
        dict.setId(id);
        dict = dictService.selectOne(dict);
        if(null == dict){
            String msg = CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]查询字典为空").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_QUERY_EXCEPTION,msg);
        }
        return dict;
    }

    @Override
    public List<Dict> findDictsByTypeNo(String typeNo) throws Exception {
        if(StringUtils.isEmpty(typeNo)){
            String msg = CommonUtil.joinStr("根据类型编码查询字典参数typeNo为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Dict dict = new Dict();
        dict.setIsValid(ZeroToNineEnum.ONE.getCode());
        dict.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        dict.setTypeNo(typeNo);
        return dictService.select(dict);
    }

    @Override
    public int deleteDictById(Long id) throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("根据ID删除字典参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Dict tmp = new Dict();
        tmp.setId(id);
        tmp.setIsValid(ZeroToNineEnum.ZERO.getCode());
        tmp.setUpdateTime(new Date());
        int count = dictService.updateByPrimaryKeySelective(tmp);
        if(count == 0) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]删除字典失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }
        return count;
    }

}
