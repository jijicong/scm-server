package org.trc.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.IConfigBiz;
import org.trc.domain.score.Auth;
import org.trc.domain.score.Dict;
import org.trc.domain.score.DictType;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.DBException;
import org.trc.form.DictForm;
import org.trc.form.DictTypeForm;
import org.trc.mapper.score.IDictTypeMapper;
import org.trc.service.IDictService;
import org.trc.service.IDictTypeService;
import org.trc.util.CommonUtil;
import org.trc.util.PageResult;
import org.trc.util.ValidateUtil;
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
    public PageResult dictTypePage(DictTypeForm form) throws Exception {
        Example example = new Example(DictType.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(form.getName())) {
            criteria.andLike("name", "%" + form.getName() + "%");
        }
        if(StringUtil.isNotEmpty(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        if(StringUtil.isNotEmpty(form.getField())) {
            example.setOrderByClause(form.getOrderBy());
        }
        //分页查询
        return dictTypeService.pagination(example, form.getPageIndex(), form.getLimit());
    }

    @Override
    public List<DictType> queryDictTypes() throws Exception {
        DictType dictType = new DictType();
        dictType.setIsValid(ZeroToNineEnum.ONE.getCode());
        dictType.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<DictType> dictTypes = dictTypeService.select(dictType);
        if(null == dictTypes)
            throw new DBException("查询有效的字典类型结果不能为空值null");
        return dictTypes;
    }

    @Override
    public int saveDictType(JSONObject json) throws Exception{
        ValidateUtil.jsonParamNullCheck(json, "code:类型编码","name:类型名称","isValid:是否有效");
        int count = 0;
        if(json.containsKey("id")){
            //修改
            /*DictType tmp = dictTypeService.selectByPrimaryKey(Long.parseLong(json.getString("id")));
            if(null == tmp)
                throw new DBException(CommonUtil.joinStr("根据主键ID[id=",json.getString("id"),"]查询字典类型为空").toString());
            CommonUtil.setBeanProperty(tmp, json);*/
            DictType tmp = new DictType();
            tmp.setId(Long.parseLong(json.getString("id")));
            tmp = dictTypeService.selectOne(tmp);
            setDictType(tmp, json);
            count = dictTypeService.updateByPrimaryKey(tmp);
        }else{
            //新增
            DictType dictType = JSON.toJavaObject(json, DictType.class);
            CommonUtil.setBaseDO(dictType);
            count = dictTypeService.insert(dictType);
            if(count == 0)
                throw new DBException(CommonUtil.joinStr("保存字典类型",JSON.toJSONString(dictType),"到数据库失败").toString());
        }
        return count;
    }

    private void setDictType(DictType dictType, JSONObject json){
        if(json.containsKey("code"))
            dictType.setCode(json.getString("code"));
        if(json.containsKey("name"))
            dictType.setName(json.getString("name"));
        if(json.containsKey("description"))
            dictType.setDescription(json.getString("description"));
        if(json.containsKey("isValid"))
            dictType.setIsValid(json.getString("isValid"));
        dictType.setUpdateTime(new Date());
    }


    @Override
    public DictType findDictTypeById(Long id)throws Exception {
        //DictType dictType = dictTypeService.selectByPrimaryKey(id);
        DictType dictType = new DictType();
        dictType.setId(id);
        dictType = dictTypeService.selectOne(dictType);
        if(null == dictType)
            throw new DBException(CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]查询字典类型为空").toString());
        return dictType;
    }

    @Override
    public DictType findDictTypeByTypeNo(String typeNo) throws Exception{
        DictType dictType = new DictType();
        dictType.setIsValid(ZeroToNineEnum.ONE.getCode());
        dictType.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        dictType.setCode(typeNo);
        dictType = dictTypeService.selectOne(dictType);
        if(null == dictType)
            throw new DBException(CommonUtil.joinStr("根据字典类型编号[typeNo=",typeNo,"]查询字典类型为空值null").toString());
        return dictType;
    }

    @Override
    public int deleteDictTypeById(Long id) throws Exception {
        /*int count = dictTypeService.deleteByPrimaryKey(id);
        if(count == 0)
            throw new DBException(CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]删除字典类型失败").toString());
        return count;*/
        DictType tmp = new DictType();
        tmp.setId(id);
        tmp = dictTypeService.selectOne(tmp);
        tmp.setIsDeleted(ZeroToNineEnum.ONE.getCode());
        tmp.setUpdateTime(new Date());
        int count = dictTypeService.updateByPrimaryKey(tmp);
        if(count == 0)
            throw new DBException(CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]删除字典类型失败").toString());
        return count;
    }

    @Override
    public PageResult dictPage(DictForm form) throws Exception {
        Example example = new Example(Dict.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(form.getTypeNo())) {
            criteria.andEqualTo("typeNo", form.getTypeNo());
        }
        if(StringUtil.isNotEmpty(form.getName())) {
            criteria.andLike("name", "%" + form.getName() + "%");
        }
        if(StringUtil.isNotEmpty(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        if(StringUtil.isNotEmpty(form.getField())) {
            example.setOrderByClause(form.getOrderBy());
        }
        //分页查询
        return dictTypeService.pagination(example, form.getPageIndex(), form.getLimit());
    }

    @Override
    public List<Dict> queryDicts(JSONObject json) throws Exception{
        Dict dict = new Dict();
        dict.setIsValid(ZeroToNineEnum.ONE.getCode());
        if(json.containsKey("typeNo"))
            dict.setTypeNo(json.getString("typeNo"));
        List<Dict> dicts = dictService.select(dict);
        if(null == dicts)
            throw new DBException(CommonUtil.joinStr("根据查询条件", CommonUtil.getDBOperateCondition(dict).toJSONString(), "的字典类型结果为空值null").toString());
        return dicts;
    }

    @Override
    public int saveDict(JSONObject json) throws Exception{
        ValidateUtil.jsonParamNullCheck(json, "typeNo:类型编码","name:属性名称","value:属性值","isValid:是否有效");
        int count = 0;
        if(json.containsKey("id")){
            //修改
            Dict tmp = new Dict();
            tmp.setId(Long.parseLong(json.getString("id")));
            tmp = dictService.selectOne(tmp);
            setDict(tmp, json);
            count = dictService.updateByPrimaryKey(tmp);
        }else{
            //新增
            Dict dict = JSON.toJavaObject(json, Dict.class);
            CommonUtil.setBaseDO(dict);
            count = dictService.insert(dict);
            if(count == 0)
                throw new DBException(CommonUtil.joinStr("保存字典",JSON.toJSONString(dict),"到数据库失败").toString());
        }
        return count;
    }

    private void setDict(Dict dict, JSONObject json){
        if(json.containsKey("typeNo"))
            dict.setTypeNo(json.getString("typeNo"));
        if(json.containsKey("name"))
            dict.setName(json.getString("name"));
        if(json.containsKey("value"))
            dict.setValue(json.getString("value"));
        if(json.containsKey("isValid"))
            dict.setIsValid(json.getString("isValid"));
        dict.setUpdateTime(new Date());
    }

    @Override
    public Dict findDictById(Long id) throws Exception {
        //Dict dict = dictService.selectByPrimaryKey(id);
        Dict dict = new Dict();
        dict.setId(id);
        dict = dictService.selectOne(dict);
        if(null == dict)
            throw new DBException(CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]查询字典为空").toString());
        return dict;
    }

    @Override
    public int deleteDictById(Long id) throws Exception {
        /*int count = dictService.deleteByPrimaryKey(id);
        if(count == 0)
            throw new DBException(CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]删除字典失败").toString());
        return count;*/
        Dict tmp = new Dict();
        tmp.setId(id);
        tmp = dictService.selectOne(tmp);
        tmp.setIsDeleted(ZeroToNineEnum.ONE.getCode());
        tmp.setUpdateTime(new Date());
        int count = dictService.updateByPrimaryKey(tmp);
        if(count == 0)
            throw new DBException(CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]删除字典失败").toString());
        return count;
    }
}
