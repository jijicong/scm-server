package org.trc.biz.impl.system;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.system.ISellChannelBiz;
import org.trc.cache.CacheEvit;
import org.trc.domain.System.SellChannel;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.SellChannelException;
import org.trc.form.system.SellChannelFrom;
import org.trc.service.System.ISellChannelService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import java.util.Calendar;
import java.util.List;

/**
 * @author hzszy
 */
@Service("sellChannelBiz")
public class SellChannelBiz implements ISellChannelBiz{
    private Logger logger = LoggerFactory.getLogger(SellChannelBiz.class);


    @Autowired
    private IUserNameUtilService userNameUtilService;
    @Autowired
    private ISellChannelService sellChannelService;
    @Autowired
    private ISerialUtilService serialUtilService;
    @Autowired
    private ILogInfoService logInfoService;

    /**
     * 销售渠道的编码头部规则为XSQD;
     */
    private final static String SERIALNAME = "XSQD";
    /**
     * 销售渠道的数字长度是3
     */
    private final static Integer LENGTH = 3;

    /**
     * 分页查询
     * @param form  销售渠道查询条件
     * @param page  分页信息
     * @return
     */
    @Override
    public Pagenation<SellChannel> sellChannelPage(SellChannelFrom form, Pagenation<SellChannel> page) {
        Example example = new Example(SellChannel.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(form.getSellName())) {
            criteria.andLike("sellName", "%" + form.getSellName() + "%");
        }
        if (!StringUtils.isBlank(form.getSellType())) {
            criteria.andEqualTo("sellType", form.getSellType());
        }
        if (!StringUtils.isBlank(form.getSellCode())) {
            criteria.andLike("sellCode", "%" +form.getSellCode() + "%");
        }
        example.orderBy("updateTime").desc();
        Pagenation<SellChannel> pagenation = sellChannelService.pagination(example, page, form);
        List<SellChannel> sellChannelList = pagenation.getResult();
        userNameUtilService.handleUserName(sellChannelList);
        return pagenation;
    }

    /**
     * 新增销售渠道
     * @param sellChannel
     * @param aclUserAccreditInfo
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvit
    public void saveSellChannel(SellChannel sellChannel, AclUserAccreditInfo aclUserAccreditInfo) {
        SellChannel tmp =selectSellChannelByName(sellChannel.getSellName());
        if (null!=tmp){
           throw new SellChannelException(ExceptionEnum.SYSTEM_SELL_CHANNEL_SAVE_EXCEPTION, "该销售渠道名称已存在!");
        }
        checkSaveSellChannel(sellChannel);
        SellChannel saveSellChannel = new SellChannel();
        saveSellChannel.setSellCode(serialUtilService.generateCode(LENGTH, SERIALNAME));
        saveSellChannel.setIsValid(ValidEnum.VALID.getCode());
        saveSellChannel.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        saveSellChannel.setCreateOperator(aclUserAccreditInfo.getUserId());
        saveSellChannel.setSellName(sellChannel.getSellName());
        saveSellChannel.setSellType(sellChannel.getSellType());
        saveSellChannel.setRemark(sellChannel.getRemark());
        int count = sellChannelService.insert(saveSellChannel);
        if (count == 0) {
            String msg = "销售渠道保存,数据库操作失败";
            logger.error(msg);
            throw new SellChannelException(ExceptionEnum.SYSTEM_SELL_CHANNEL_SAVE_EXCEPTION, msg);
        }
    }

    @Override
    @CacheEvit(key = {"#sellChannel.id"})
    public void updateSellChannel(SellChannel sellChannel, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(sellChannel.getId(), "修改销售渠道参数ID为空");
        SellChannel tmp = selectSellChannelByName(sellChannel.getSellName());
        if (null != tmp) {
            if (!tmp.getId().equals(sellChannel.getId())) {
                throw new SellChannelException(ExceptionEnum.SYSTEM_SELL_CHANNEL_UPDATE_EXCEPTION, "该销售渠道名称已存在");
            }
        }
        sellChannel.setUpdateTime(Calendar.getInstance().getTime());
        int count = sellChannelService.updateByPrimaryKeySelective(sellChannel);
        if (count == 0) {
            String msg = String.format("修改销售渠道%s数据库操作失败", JSON.toJSONString(sellChannel));
            logger.error(msg);
            throw new SellChannelException(ExceptionEnum.SYSTEM_SELL_CHANNEL_UPDATE_EXCEPTION, msg);
        }
        String userId = aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(sellChannel, sellChannel.getId().toString(), userId, LogOperationEnum.UPDATE.getMessage(), null, null);
    }

    /**
     * 根据销售渠道名称查询销售渠道
     * @param sellName
     * @return
     */
    @Override
    public SellChannel selectSellChannelByName(String sellName) {
        SellChannel sellChannel = new SellChannel();
        sellChannel.setSellName(sellName);
        sellChannel=  sellChannelService.selectOne(sellChannel);
        return sellChannel;
    }

    @Override
    public SellChannel selectSellChannelById(Long id) {
        AssertUtil.notNull(id, "根据Id查询销售渠道,参数Id不能为空");
        SellChannel sellChannel = sellChannelService.selectByPrimaryKey(id);
        AssertUtil.notNull(sellChannel, "根据Id"+id+"查询销售渠道结果为空!");
        return sellChannel;
    }


    private void checkSaveSellChannel(SellChannel sellChannel){
        AssertUtil.notBlank(sellChannel.getSellName(), "销售渠道参数sellName不能为空");
        AssertUtil.notBlank(sellChannel.getSellType(), "销售渠道参数sellType不能为空");
    }
}
