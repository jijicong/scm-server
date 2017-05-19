package org.trc.biz.config;

import org.trc.domain.dict.Dict;
import org.trc.domain.dict.DictType;
import org.trc.domain.util.AreaTreeNode;
import org.trc.form.config.DictForm;
import org.trc.form.config.DictTypeForm;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by hzwdx on 2017/4/19.
 */
public interface IConfigBiz {

    /**
     * 字典类型分页查询
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<DictType> dictTypePage(DictTypeForm form, Pagenation<DictType> page) throws Exception;

    /**
     * 查询字典类型列表
     * @return
     * @throws Exception
     */
    List<DictType> queryDictTypes(DictTypeForm dictTypeForm) throws Exception;

    /**
     * 保存字典类型
     * @param dictType
     * @return
     */
    void saveDictType(DictType dictType) throws Exception;

    /**
     * 修改字典类型
     * @param dictType
     * @param
     * @return
     * @throws Exception
     */
    void updateDictType(DictType dictType) throws Exception;

    /**
     *根据主键查询字典类型
     * @param id
     * @return
     */
    DictType findDictTypeById(Long id) throws Exception;

    /**
     * 根据类型编号查询字典类型
     * @param typeCode
     * @return
     */
    DictType findDictTypeByTypeNo(String typeCode) throws Exception;

    /**
     * 根据主键删除
     * @param id
     * @return
     * @throws Exception
     */
    void deleteDictTypeById(Long id) throws Exception;

    /**
     * 字典类型分页查询
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<Dict> dictPage(DictForm form, Pagenation<Dict> page) throws Exception;
    /**
     * 查询字典列表
     * @param dictForm
     * @return
     */
    List<Dict> queryDicts(DictForm dictForm) throws Exception;

    /**
     * 保存字典
     * @param dict
     * @return
     */
    void saveDict(Dict dict) throws Exception;

    /**
     * 更新字典
     * @param dict
     * @param id
     * @return
     * @throws Exception
     */
     void updateDict(Dict dict) throws Exception;

    /**
     * 根据类型编号查询字典
     * @param id
     * @return
     */
    Dict findDictById(Long id) throws Exception;

    /**
     * 根据类型编号查询字典列表
     * @param typeCode
     * @return
     */
    List<Dict> findDictsByTypeNo(String typeCode) throws Exception;

    /**
     * 根据主键删除字典
     * @param id
     * @return
     * @throws Exception
     */
    void deleteDictById(Long id) throws Exception;

    /**
     * 查询省市的集合
     * @return
     */
    List<AreaTreeNode> findProvinceCity() throws Exception;
}
