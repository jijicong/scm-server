package org.trc.biz;

import com.alibaba.fastjson.JSONObject;
import org.trc.domain.score.Dict;
import org.trc.domain.score.DictType;

import java.util.List;

/**
 * Created by hzwdx on 2017/4/19.
 */
public interface IConfigBiz {

    /**
     * 查询字典类型列表
     * @param
     * @return
     */
    public List<DictType> queryDictTypes() throws Exception;

    /**
     * 保存字典类型
     * @param json
     * @return
     */
    public int saveDictType(JSONObject json) throws Exception;

    /**
     *根据主键查询字典类型
     * @param id
     * @return
     */
    public DictType findDictTypeById(Long id) throws Exception;

    /**
     * 根据类型编号查询字典类型
     * @param typeNo
     * @return
     */
    public DictType findDictTypeByTypeNo(String typeNo) throws Exception;

    /**
     * 根据主键删除
     * @param id
     * @return
     * @throws Exception
     */
    public int deleteDictTypeById(Long id) throws Exception;

    /**
     * 查询字典列表
     * @param json
     * @return
     */
    public List<Dict> queryDicts(JSONObject json) throws Exception;

    /**
     * 保存字典
     * @param json
     * @return
     */
    public int saveDict(JSONObject json) throws Exception;

    /**
     * 根据类型编号查询字典
     * @param id
     * @return
     */
    public Dict findDictById(Long id) throws Exception;

    /**
     * 根据主键删除字典
     * @param id
     * @return
     * @throws Exception
     */
    public int deleteDictById(Long id) throws Exception;

}
