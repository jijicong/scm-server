package org.trc.biz.impl.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.trc.biz.system.IMetadataBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.util.AreaTreeNode;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.service.IJDService;
import org.trc.service.config.IDictService;
import org.trc.service.util.ILocationUtilService;
import org.trc.util.AppResult;
import org.trc.util.ResultUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzwdx on 2017/8/7.
 */
@Service("metadataBiz")
public class MetadataBiz implements IMetadataBiz {

    private Logger logger = LoggerFactory.getLogger(MetadataBiz.class);

    @Autowired
    private IDictService dictService;
    @Autowired
    private ILocationUtilService locationUtilService;
    @Autowired
    private IJDService ijdService;

    @Override
    //@Cacheable(isList = true, expireTime = 14400)
    @Cacheable(value = SupplyConstants.Cache.DICT)
    public List<Dict> queryDict() {
        List<Dict> dictList = null;
        try{
            Dict dict = new Dict();
            dictList = dictService.select(dict);
        }catch (Exception e){
            logger.error("查询数据字典元数据异常", e);
            dictList = new ArrayList<Dict>();
        }
        return dictList;
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.ADDRESS)
    public List<AreaTreeNode> queryAddress() {
        List<AreaTreeNode> areaTreeNodes = null;
        try {
            areaTreeNodes = locationUtilService.getTreeNodeFromLocation();
        }catch (Exception e){
            logger.error("查询地址元数据异常", e);
            areaTreeNodes = new ArrayList<AreaTreeNode>();
        }
        return areaTreeNodes;
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.JD_ADDRESS)
    public List<AreaTreeNode> queryJDAddress() {
        List<AreaTreeNode> areaTreeNodes = null;
        try {
            ReturnTypeDO returnTypeDO = ijdService.getJingDongArea();
            if(returnTypeDO.getSuccess()){
                JSONArray jbo = JSONArray.parseArray(returnTypeDO.getResult().toString());
                areaTreeNodes = jbo.toJavaList(AreaTreeNode.class);
            }
        }catch (Exception e){
            logger.error("查询京东地址元数据异常", e);
            areaTreeNodes = new ArrayList<AreaTreeNode>();
        }
        return areaTreeNodes;
    }

    @Override
    @CachePut(value = SupplyConstants.Cache.JD_ADDRESS)
    public AppResult jDAddressUpdate() {
        try{
            //调用查询京东地址方法使缓存更新最新地址
            queryJDAddress();
        }catch (Exception e){
            logger.error("查询京东地址异常", e);
        }
        return ResultUtil.createSucssAppResult("通知更新京东地址成功", "");
    }
}
