package org.trc.biz.impl.Purchase;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.purchase.IPurchaseGroupBiz;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.exception.ConfigException;
import org.trc.form.purchase.PurchaseGroupForm;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sone on 2017/5/19.
 */
@Service("purchaseGroupBiz")
public class PurchaseGroupBiz implements IPurchaseGroupBiz{

    private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseGroupBiz.class);
    @Resource
    private IPurchaseGroupService purchaseGroupService;

    @Override
    public Pagenation<PurchaseGroup> purchaseGroupPage(PurchaseGroupForm form, Pagenation<PurchaseGroup> page) throws Exception {
        Example example = new Example(PurchaseGroup.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(form.getName())) {
            criteria.andLike("name", "%" + form.getName() + "%");
        }
        if (!StringUtils.isBlank(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        example.orderBy("updateTime").desc();
        return purchaseGroupService.pagination(example,page,form);
    }

    @Override
    public void updatePurchaseStatus(PurchaseGroup purchaseGroup) throws Exception {
        AssertUtil.notNull(purchaseGroup,"采购组信息为空，修改采购组状态失败");
        PurchaseGroup updatePurchaseGroup = new PurchaseGroup();
        updatePurchaseGroup.setId(purchaseGroup.getId());
        if (purchaseGroup.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updatePurchaseGroup.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updatePurchaseGroup.setIsValid(ValidEnum.VALID.getCode());
        }
        updatePurchaseGroup.setUpdateTime(Calendar.getInstance().getTime());
        int count = purchaseGroupService.updateByPrimaryKeySelective(updatePurchaseGroup);
        if(count<1){
            String msg = CommonUtil.joinStr("修改采购组状态", JSON.toJSONString(purchaseGroup), "数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.PURCHASE_PURCHASEGROUP_UPDATE_EXCEPTION, msg);
        }
    }
}
