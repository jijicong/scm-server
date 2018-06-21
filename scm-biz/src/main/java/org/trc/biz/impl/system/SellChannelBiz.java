package org.trc.biz.impl.system;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.system.ISellChannelBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.SellChannel;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.*;
import org.trc.exception.SellChannelException;
import org.trc.form.system.SellChannelFrom;
import org.trc.service.System.ISellChannelService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.cache.SellChannelCacheEvict;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
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
    //@Cacheable(key="#form.toString()+#page.pageNo+#page.pageSize",isList=true)
    @Cacheable(value = SupplyConstants.Cache.SELL_CHANNEL)
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
    @SellChannelCacheEvict
    public void saveSellChannel(SellChannel sellChannel, AclUserAccreditInfo aclUserAccreditInfo) {
        SellChannel tmp =selectSellChannelByName(null,sellChannel.getSellName());
        if (null!=tmp){
           throw new SellChannelException(ExceptionEnum.SYSTEM_SELL_CHANNEL_SAVE_EXCEPTION, "该销售渠道名称已存在!");
        }

        checkSaveSellChannel(sellChannel);
        SellChannel saveSellChannel = new SellChannel();
        String sellType = sellChannel.getSellType();
        saveSellChannel.setSellCode(serialUtilService.generateCode(LENGTH, SERIALNAME));
        saveSellChannel.setIsValid(ValidEnum.VALID.getCode());
        saveSellChannel.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        saveSellChannel.setCreateOperator(aclUserAccreditInfo.getUserId());
        saveSellChannel.setSellName(sellChannel.getSellName());
        saveSellChannel.setSellType(sellType);
        if(StringUtils.equals(sellChannel.getSellType(), SellChannelTypeEnum.STORE.getCode().toString())){
            saveSellChannel.setStoreId(sellChannel.getStoreId());
            if(isValidStoreId(sellChannel.getStoreId(), null)){
                throw new SellChannelException(ExceptionEnum.SYSTEM_SELL_CHANNEL_SAVE_EXCEPTION, "该门店ID已存在!");
            }
        }
        saveSellChannel.setRemark(sellChannel.getRemark());
        int count = sellChannelService.insert(saveSellChannel);
        if (count == 0) {
            String msg = "销售渠道保存,数据库操作失败";
            logger.error(msg);
            throw new SellChannelException(ExceptionEnum.SYSTEM_SELL_CHANNEL_SAVE_EXCEPTION, msg);
        }
        String userId = aclUserAccreditInfo.getUserId();
        SellChannel sellChannelTemp = new SellChannel();
        sellChannelTemp.setSellName(sellChannel.getSellName());
        sellChannelTemp = sellChannelService.selectOne(sellChannelTemp);
        logInfoService.recordLog(sellChannel, String.valueOf(sellChannelTemp.getId()), userId, LogOperationEnum.ADD.getMessage(), "", null);

    }

    private boolean isValidStoreId(String storeId, Long id){
        Example example = new Example(SellChannel.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("storeId", storeId);
        if (id != null ) {
            List<Long> ids = new ArrayList<>();
            ids.add(id);
            criteria.andNotIn("id", ids);
        }
        List<SellChannel> sellChannels = sellChannelService.selectByExample(example);
        if(sellChannels != null && sellChannels.size() > 0){
            return true;
        }else{
            return false;
        }

    }

    @Override
    @SellChannelCacheEvict
    public void updateSellChannel(SellChannel sellChannel, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(sellChannel.getId(), "修改销售渠道参数ID为空");
        SellChannel tmp = selectSellChannelByName(null,sellChannel.getSellName());
        if (null != tmp) {
            if (!tmp.getId().equals(sellChannel.getId())) {
                throw new SellChannelException(ExceptionEnum.SYSTEM_SELL_CHANNEL_UPDATE_EXCEPTION, "该销售渠道名称已存在");
            }
        }
        checkSaveSellChannel(sellChannel);
        sellChannel.setUpdateTime(Calendar.getInstance().getTime());
        int count = sellChannelService.updateByPrimaryKeySelective(sellChannel);
        if (count == 0) {
            String msg = String.format("修改销售渠道%s数据库操作失败", JSON.toJSONString(sellChannel));
            logger.error(msg);
            throw new SellChannelException(ExceptionEnum.SYSTEM_SELL_CHANNEL_UPDATE_EXCEPTION, msg);
        }
        String userId = aclUserAccreditInfo.getUserId();
        sellChannel = sellChannelService.selectByPrimaryKey(sellChannel.getId());
        logInfoService.recordLog(sellChannel, sellChannel.getId().toString(), userId, LogOperationEnum.UPDATE.getMessage(), StringUtils.EMPTY, null);
    }

    /**
     * 根据销售渠道名称查询销售渠道
     * @param sellName
     * @return
     */
    @Override
    @Cacheable(value = SupplyConstants.Cache.SELL_CHANNEL)
    public SellChannel selectSellChannelByName(Long id,String sellName) {
        SellChannel sellChannel = new SellChannel();
        sellChannel.setSellName(sellName);
        sellChannel=  sellChannelService.selectOne(sellChannel);
        if (null!=id&&null!=sellChannel){
          if (id.equals(sellChannel.getId())){
              return null;
          }
        }
        return sellChannel;
    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.SELL_CHANNEL)
    public SellChannel selectSellChannelById(Long id) {
        AssertUtil.notNull(id, "根据Id查询销售渠道,参数Id不能为空");
        SellChannel sellChannel = sellChannelService.selectByPrimaryKey(id);
        AssertUtil.notNull(sellChannel, "根据Id"+id+"查询销售渠道结果为空!");
        return sellChannel;
    }

    @Override
    public SellChannel selectSellChannelByNameAndId(Long id, String name) {
        return null;
    }


    private void checkSaveSellChannel(SellChannel sellChannel){
        AssertUtil.notBlank(sellChannel.getSellName(), "销售渠道参数sellName不能为空");
        AssertUtil.notBlank(sellChannel.getSellType(), "销售渠道参数sellType不能为空");
        if(StringUtils.equals(sellChannel.getSellType(), SellChannelTypeEnum.STORE.getCode().toString())){
            AssertUtil.notBlank(sellChannel.getStoreId(), "销售渠道参数门店ID不能为空");
            if(isValidStoreId(sellChannel.getStoreId(), sellChannel.getId())){
                throw new SellChannelException(ExceptionEnum.SYSTEM_SELL_CHANNEL_SAVE_EXCEPTION, "该门店ID已存在!");
            }
        }
    }
}
