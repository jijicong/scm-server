package org.trc.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.IConfigBiz;
import org.trc.domain.score.Dict;
import org.trc.domain.score.DictType;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.DBException;
import org.trc.form.DictForm;
import org.trc.form.DictTypeForm;
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
        example.orderBy("isValid").desc();
        if(StringUtil.isNotEmpty(form.getField())) {
            if(StringUtils.equals("DESC", form.getDirection().toUpperCase()))
                example.orderBy(form.getField()).desc();
            else
                example.orderBy(form.getField()).asc();
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
                throw new DBException(CommonUtil.joinStr("保存字典类型",JSON.toJSONString(dictType),"到数据库失败").toString());
        }
        return count;
    }


    @Override
    public DictType findDictTypeById(Long id)throws Exception {
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
        DictType tmp = new DictType();
        tmp.setId(id);
        tmp = dictTypeService.selectOne(tmp);
        tmp.setIsValid(ZeroToNineEnum.ZERO.getCode());
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
        example.orderBy("typeNo").asc().orderBy("isValid").desc();
        if(StringUtil.isNotEmpty(form.getField())) {
            if(StringUtils.equals("DESC", form.getDirection().toUpperCase()))
                example.orderBy(form.getField()).desc();
            else
                example.orderBy(form.getField()).asc();
        }
        //分页查询
        return dictService.pagination(example, form.getPageIndex(), form.getLimit());
    }

    @Override
    public List<Dict> queryDicts(Dict dict) throws Exception{
        if(StringUtils.isEmpty(dict.getIsValid()))
            dict.setIsValid(ZeroToNineEnum.ONE.getCode());
        List<Dict> dicts = dictService.select(dict);
        if(null == dicts)
            throw new DBException(CommonUtil.joinStr("根据查询条件", CommonUtil.getDBOperateCondition(dict).toJSONString(), "的字典类型结果为空值null").toString());
        return dicts;
    }

    @Override
    public int saveDict(Dict dict) throws Exception{
        //ValidateUtil.jsonParamNullCheck(json, "typeNo:类型编码","name:属性名称","value:属性值","isValid:是否有效");
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
                throw new DBException(CommonUtil.joinStr("保存字典",JSON.toJSONString(dict),"到数据库失败").toString());
        }
        return count;
    }

    @Override
    public Dict findDictById(Long id) throws Exception {
        Dict dict = new Dict();
        dict.setId(id);
        dict = dictService.selectOne(dict);
        if(null == dict)
            throw new DBException(CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]查询字典为空").toString());
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
            throw new DBException(CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]删除字典失败").toString());
        return count;
    }

  /*  public static void main(String[] args){
        DictType t1 = new DictType();
        t1.setId(1l);
        t1.setCode("1");
        t1.setName("n1");
        DictType t2 = new DictType();
        t2.setId(2l);
        t2.setCode("2");
        t2.setName("n2");
        BeanUtils.copyProperties(t1, t2);
        System.out.println(JSON.toJSON(t1));
        System.out.println(JSON.toJSON(t2));
    }*/

}
