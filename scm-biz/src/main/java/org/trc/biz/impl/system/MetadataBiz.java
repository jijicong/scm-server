package org.trc.biz.impl.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.system.IMetadataBiz;
import org.trc.cache.Cacheable;
import org.trc.domain.dict.Dict;
import org.trc.domain.util.AreaTreeNode;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.service.IJDService;
import org.trc.service.config.IDictService;
import org.trc.service.util.ILocationUtilService;

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
    @Cacheable(expireTime = 14400)
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
    @Cacheable(expireTime = 14400)
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
    @Cacheable(expireTime = 14400)
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
}
