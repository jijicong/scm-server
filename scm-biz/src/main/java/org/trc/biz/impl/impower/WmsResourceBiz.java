package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.trc.biz.impower.IWmsResourceBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclResource;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.WmsResource;
import org.trc.domain.impower.WmsResourceExt;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.CategoryException;
import org.trc.form.impower.JurisdictionTreeNode;
import org.trc.service.impower.IWmsResourceExtService;
import org.trc.service.impower.IWmsResourceService;
import org.trc.util.AssertUtil;
import org.trc.util.cache.UserCacheEvict;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service("wmsResourceBiz")
public class WmsResourceBiz implements IWmsResourceBiz {


    private Logger logger = LoggerFactory.getLogger(WmsResourceBiz.class);
    /**
     * 仓级资源所属
     */
    private final static Integer WMS_RESOURCE = 1;

    @Autowired
    private IWmsResourceService wmsResourceService;
    @Autowired
    private IWmsResourceExtService wmsResourceExtService;


    @Override
    public List<WmsResource> queryWmsResource() {
        WmsResource wmsResource = new WmsResource();
        wmsResource.setBelong(WMS_RESOURCE);
        List<WmsResource> wmsResourceList = wmsResourceService.select(wmsResource);
        AssertUtil.notEmpty(wmsResourceList,"查询所有仓级资源为空!");
        return wmsResourceList;
    }

    /**
     * 新增资源
     *
     * @param jurisdictionTreeNode
     * @throws Exception
     */
    @Override
    public void saveWmsResource(JurisdictionTreeNode jurisdictionTreeNode, AclUserAccreditInfo aclUserAccreditInfo) {
        String code = "";
        //生成code
        if (jurisdictionTreeNode.getParentId() != null) {
            code = jurisdictionTreeNode.getParentId().toString();
        }
        String parentMethod = code;
        if (code.length() == 5) {
            parentMethod = parentMethod + ZeroToNineEnum.ZERO.getCode() + jurisdictionTreeNode.getOperationType();
        }
        WmsResource wmsResource = new WmsResource();
        //2.查询到当前方法,当前父资源下最大的序号,如果存在加1,如果不存在,自行组合
        Example example = new Example(WmsResource.class);
        Example.Criteria criteria = example.createCriteria();
        if (parentMethod.length() == 3) {
            criteria.andLike("parentId", parentMethod);
            example.orderBy("code").desc();
        } else if (StringUtils.isBlank(code)) {
            criteria.andLessThanOrEqualTo("parentId", jurisdictionTreeNode.getBelong());
            criteria.andEqualTo("belong", jurisdictionTreeNode.getBelong());
            example.orderBy("code").desc();
        } else {
            criteria.andLike("code", parentMethod + "%");
            example.orderBy("code").desc();
        }
        List<WmsResource> wmsResourceList = wmsResourceService.selectByExample(example);
        if (!AssertUtil.collectionIsEmpty(wmsResourceList)) {
            //存在的情况
            wmsResource.setCode(wmsResourceList.get(0).getCode() + 1);
        } else {
            //不存在,手动组合,从一开始
            if (code.length() == 3) {
                code = code + ZeroToNineEnum.ZERO.getCode() + ZeroToNineEnum.ONE.getCode();
            } else {
                code = code + ZeroToNineEnum.ZERO.getCode() + jurisdictionTreeNode.getOperationType() + ZeroToNineEnum.ZERO.getCode() + ZeroToNineEnum.ONE.getCode();
            }
            wmsResource.setCode(Long.parseLong(code));
        }
        if (code.length() == 3) {
            String acl = String.valueOf(wmsResource.getCode());
            //判断是否长度大于等于4
            if (acl.length() >= 5) {
                acl = acl.substring(0, 5);
            }
            wmsResource.setCode(Long.parseLong(acl));
        }

        wmsResource.setBelong(jurisdictionTreeNode.getBelong());
        wmsResource.setMethod(jurisdictionTreeNode.getMethod());
        wmsResource.setName(jurisdictionTreeNode.getName());
        if (wmsResource.getCode().toString().length() == 3) {
            wmsResource.setParentId(Long.valueOf(jurisdictionTreeNode.getBelong()));
        } else {
            wmsResource.setParentId(jurisdictionTreeNode.getParentId());
        }
        wmsResource.setUrl(jurisdictionTreeNode.getUrl());
        if (StringUtils.equals(wmsResource.getUrl(), ZeroToNineEnum.ONE.getCode())) {
            wmsResource.setType(ZeroToNineEnum.ZERO.getCode());
        } else {
            wmsResource.setType(ZeroToNineEnum.ONE.getCode());
        }
        wmsResource.setCreateOperator("admin");
        wmsResource.setCreateTime(Calendar.getInstance().getTime());
        wmsResource.setUpdateTime(Calendar.getInstance().getTime());
        wmsResource.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        wmsResourceService.insertOne(wmsResource);
    }


    /**
     * 编辑资源
     *
     * @param jurisdictionTreeNode
     * @return
     * @throws Exception
     */
    @Override
    public void updateWmsResource(JurisdictionTreeNode jurisdictionTreeNode,AclUserAccreditInfo accreditInfo) {
        WmsResourceExt wmsResource = JSONObject.parseObject(JSON.toJSONString(jurisdictionTreeNode), WmsResourceExt.class);
        WmsResourceExt resourceExt = new WmsResourceExt();
        resourceExt.setCode(wmsResource.getId());
        resourceExt = wmsResourceExtService.selectOne(resourceExt);
        wmsResource.setId(resourceExt.getId());
        wmsResource.setMethod(jurisdictionTreeNode.getOperationType());
        wmsResource.setUpdateTime(Calendar.getInstance().getTime());
        int count = wmsResourceExtService.updateByPrimaryKeySelective(wmsResource);
        if (count == 0) {
            String msg = "更新资源" + JSON.toJSONString(wmsResource.getName()) + "操作失败";
            logger.error(msg);
            throw new CategoryException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }
    }



    @Override
    public List<JurisdictionTreeNode> getWmsNodes(Long parentId, boolean isRecursive) {
        Example example = new Example(WmsResource.class);
        Example.Criteria criteria = example.createCriteria();
        if (null == parentId) {
            List<Long> parentIdList = new ArrayList<>();
            parentIdList.add(1l);
            parentIdList.add(2l);
            criteria.andIn("parentId", parentIdList);
        } else {
            criteria.andEqualTo("parentId", parentId);
        }
        List<WmsResource> childCategoryList = wmsResourceService.selectByExample(example);
        List<JurisdictionTreeNode> childNodeList = new ArrayList<>();
        for (WmsResource wmsResource : childCategoryList) {
            JurisdictionTreeNode treeNode = new JurisdictionTreeNode();
            treeNode.setCode(wmsResource.getCode());
            treeNode.setName(wmsResource.getName());
            treeNode.setUrl(wmsResource.getUrl());
            treeNode.setMethod(wmsResource.getMethod());
            treeNode.setParentId(wmsResource.getParentId());
            treeNode.setBelong(wmsResource.getBelong());
            treeNode.setId(wmsResource.getId());
            treeNode.setCreateOperator(wmsResource.getCreateOperator());
            childNodeList.add(treeNode);
        }
        if (childNodeList.size() == 0) {
            return childNodeList;
        }
        if (isRecursive == true) {
            for (JurisdictionTreeNode childNode : childNodeList) {
                List<JurisdictionTreeNode> nextChildJurisdictionList = getWmsNodes(childNode.getCode(), isRecursive);
                if (nextChildJurisdictionList.size() > 0) {
                    childNode.setChildren(nextChildJurisdictionList);
                }
            }
        }
        return childNodeList;
    }
}
