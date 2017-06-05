package org.trc.biz.impl.config;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.config.IConfigBiz;
import org.trc.domain.dict.Dict;
import org.trc.domain.dict.DictType;
import org.trc.domain.util.AreaTreeNode;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.ConfigException;
import org.trc.form.config.DictForm;
import org.trc.form.config.DictTypeForm;
import org.trc.service.config.IDictService;
import org.trc.service.config.IDictTypeService;
import org.trc.service.util.ILocationUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.Calendar;
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
    @Resource
    private ILocationUtilService locationUtilService;

    @Override
    public Pagenation<DictType> dictTypePage(DictTypeForm queryModel, Pagenation<DictType> page) throws Exception {
        Example example = new Example(DictType.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(queryModel.getName())) {
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        /*if(StringUtil.isNotEmpty(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }*/
        example.orderBy("updateTime").desc();
        example.orderBy("isValid").desc();
        //分页查询
        return dictTypeService.pagination(example, page, queryModel);
    }

    @Override
    public List<DictType> queryDictTypes(DictTypeForm dictTypeForm) throws Exception {
        DictType dictType = new DictType();
        BeanUtils.copyProperties(dictTypeForm, dictType);
        /*if(StringUtils.isEmpty(dictTypeForm.getIsValid())){
            dictType.setIsValid(ZeroToNineEnum.ONE.getCode());
        }*/
        dictType.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return dictTypeService.select(dictType);
    }

    @Override
    public void saveDictType(DictType dictType) throws Exception{
        dictTypeCheck(dictType);
        DictType tmp = findDictTypeByTypeNo(dictType.getCode());
        if(null != tmp){
            String msg = CommonUtil.joinStr("字典类型编码为[code=",dictType.getCode(),"]的数据已存在,请使用其他编码").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }
        ParamsUtil.setBaseDO(dictType);
        int count = dictTypeService.insert(dictType);
        if(count == 0){
            String msg = CommonUtil.joinStr("保存字典类型",JSON.toJSONString(dictType),"数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_SAVE_EXCEPTION, msg);
        }
    }

    @Override
    public void updateDictType(DictType dictType) throws Exception {
        AssertUtil.notNull(dictType.getId(), "字典类型ID不能为空");
        dictTypeCheck(dictType);
        dictType.setUpdateTime(Calendar.getInstance().getTime());
        int count = dictTypeService.updateByPrimaryKeySelective(dictType);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改字典类型",JSON.toJSONString(dictType),"数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }
    }

    /**
     * 字典参数校验
     * @param dictType
     */
    private void dictTypeCheck(DictType dictType){
        AssertUtil.notBlank(dictType.getCode(), "新增字典类型的字典类型编码不能为空");
        AssertUtil.notBlank(dictType.getName(), "新增字典类型的字典名称不能为空");
        //AssertUtil.notBlank(dictType.getIsValid(), "新增字典类型的是否启用不能为空");
    }



    @Override
    public DictType findDictTypeById(Long id)throws Exception {
        AssertUtil.notNull(id, "根据ID查询字典类型参数ID为空");
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
    public DictType findDictTypeByTypeNo(String typeCode) throws Exception{
        AssertUtil.notBlank(typeCode, "根据类型编码查询字典类型参数typeNo为空");
        DictType dictType = new DictType();
        //dictType.setIsValid(ZeroToNineEnum.ONE.getCode());
        dictType.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        dictType.setCode(typeCode);
        return dictTypeService.selectOne(dictType);
    }

    @Override
    public void deleteDictTypeById(Long id) throws Exception {
        AssertUtil.notNull(id, "字典类型ID不能为空");
        DictType tmp = new DictType();
        tmp.setId(id);
        tmp = dictTypeService.selectOne(tmp);
        //tmp.setIsValid(ZeroToNineEnum.ZERO.getCode());
        tmp.setUpdateTime(Calendar.getInstance().getTime());
        int count = dictTypeService.updateByPrimaryKey(tmp);
        if(count == 0) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]删除字典类型失败").toString();
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION,msg);
        }
    }

    @Override
    public Pagenation<Dict> dictPage(DictForm queryModel, Pagenation<Dict> page) throws Exception {
        Example example = new Example(Dict.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(queryModel.getTypeCode())) {
            criteria.andEqualTo("typeCode", queryModel.getTypeCode());
        }
        if(StringUtil.isNotEmpty(queryModel.getName())) {
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        /*if(StringUtil.isNotEmpty(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }*/
        example.orderBy("typeCode").asc().orderBy("isValid").desc();
        //分页查询
        return dictService.pagination(example, page, queryModel);
    }

    @Override
    public List<Dict> queryDicts(DictForm dictForm) throws Exception{
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictForm,dict);
        /*if(StringUtils.isEmpty(dictForm.getIsValid())){
            dict.setIsValid(ZeroToNineEnum.ONE.getCode());
        }*/
        dict.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return dictService.select(dict);
    }

    @Override
    public void saveDict(Dict dict) throws Exception{
        dictCheck(dict);
        ParamsUtil.setBaseDO(dict);
        int count = dictService.insert(dict);
        if(count == 0){
            String msg = CommonUtil.joinStr("保存字典",JSON.toJSONString(dict),"到数据库失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_SAVE_EXCEPTION,msg);
        }
    }

    @Override
    public void updateDict(Dict dict) throws Exception {
        AssertUtil.notNull(dict.getId(), "字典ID不能为空");
        dictCheck(dict);
        dict.setUpdateTime(Calendar.getInstance().getTime());
        int count = dictService.updateByPrimaryKeySelective(dict);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改字典",JSON.toJSONString(dict),"数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }
    }

    /**
     * 字典参数校验
     * @param dict
     */
    private void dictCheck(Dict dict){
        AssertUtil.notBlank(dict.getTypeCode(), "新增字典的字典类型编码不能为空");
        AssertUtil.notBlank(dict.getName(), "新增字典的字典名称不能为空");
        AssertUtil.notBlank(dict.getValue(), "新增字典的字典值编码不能为空");
        //AssertUtil.notBlank(dict.getIsValid(), "新增字典的是否启用不能为空");
    }

    @Override
    public Dict findDictById(Long id) throws Exception {
        AssertUtil.notNull(id, "根据ID查询字典参数ID不能为空");
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
    public List<Dict> findDictsByTypeNo(String typeCode) throws Exception {
        AssertUtil.notBlank(typeCode, "根据类型编码查询字典参数typeCode为空");
        Dict dict = new Dict();
        //dict.setIsValid(ZeroToNineEnum.ONE.getCode());
        dict.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        dict.setTypeCode(typeCode);
        return dictService.select(dict);
    }

    @Override
    public void deleteDictById(Long id) throws Exception {
        AssertUtil.notNull(id, "字典ID不能为空");
        Dict tmp = new Dict();
        tmp.setId(id);
        //tmp.setIsValid(ZeroToNineEnum.ZERO.getCode());
        tmp.setUpdateTime(Calendar.getInstance().getTime());
        int count = dictService.updateByPrimaryKeySelective(tmp);
        if(count == 0) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]删除字典失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.CONFIG_DICT_UPDATE_EXCEPTION, msg);
        }
    }

    @Override
    public List<AreaTreeNode> findProvinceCity()  throws Exception {
        return locationUtilService.getTreeNodeFromLocation();
    }

}
