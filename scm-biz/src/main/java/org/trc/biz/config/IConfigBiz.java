package org.trc.biz.config;

import org.trc.domain.dict.Dict;
import org.trc.domain.dict.DictType;
import org.trc.domain.util.TreeNode;
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
    public Pagenation<DictType> dictTypePage(DictTypeForm form, Pagenation<DictType> page) throws Exception;

    /**
     * 查询字典类型列表
     * @return
     * @throws Exception
     */
    public List<DictType> queryDictTypes(DictTypeForm dictTypeForm) throws Exception;

    /**
     * 保存字典类型
     * @param dictType
     * @return
     */
    public int saveDictType(DictType dictType) throws Exception;

    /**
     * 修改字典类型
     * @param dictType
     * @param id
     * @return
     * @throws Exception
     */
    public int updateDictType(DictType dictType, Long id) throws Exception;

    /**
     *根据主键查询字典类型
     * @param id
     * @return
     */
    public DictType findDictTypeById(Long id) throws Exception;

    /**
     * 根据类型编号查询字典类型
     * @param typeCode
     * @return
     */
    public DictType findDictTypeByTypeNo(String typeCode) throws Exception;

    /**
     * 根据主键删除
     * @param id
     * @return
     * @throws Exception
     */
    public int deleteDictTypeById(Long id) throws Exception;

    /**
     * 字典类型分页查询
     * @param form
     * @return
     * @throws Exception
     */
    public Pagenation<Dict> dictPage(DictForm form, Pagenation<Dict> page) throws Exception;
    /**
     * 查询字典列表
     * @param dictForm
     * @return
     */
    public List<Dict> queryDicts(DictForm dictForm) throws Exception;

    /**
     * 保存字典
     * @param dict
     * @return
     */
    public int saveDict(Dict dict) throws Exception;

    /**
     * 更新字典
     * @param dict
     * @param id
     * @return
     * @throws Exception
     */
    public int updateDict(Dict dict, Long id) throws Exception;

    /**
     * 根据类型编号查询字典
     * @param id
     * @return
     */
    public Dict findDictById(Long id) throws Exception;

    /**
     * 根据类型编号查询字典列表
     * @param typeCode
     * @return
     */
    public List<Dict> findDictsByTypeNo(String typeCode) throws Exception;

    /**
     * 根据主键删除字典
     * @param id
     * @return
     * @throws Exception
     */
    public int deleteDictById(Long id) throws Exception;

    /**
     * 查询省市的集合
     * @return
     */
    public List<TreeNode> findProvinceCity();
}
