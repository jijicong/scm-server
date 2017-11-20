package org.trc.biz.system;

import org.trc.domain.System.SellChannel;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.system.SellChannelFrom;
import org.trc.util.Pagenation;

/**
 * @author hzszy
 */
public interface ISellChannelBiz {

    /**
     * 分页查询销售渠道信息
     * @param form  销售渠道查询条件
     * @param page  分页信息
     * @return  分页信息及当前页面的数据
     */
    Pagenation<SellChannel> sellChannelPage(SellChannelFrom form, Pagenation<SellChannel> page);


    /**
     * 新增销售渠道
     * @param sellChannel
     * @param aclUserAccreditInfo
     */
     void saveSellChannel(SellChannel sellChannel, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 修改销售渠道
     * @param sellChannel
     * @param aclUserAccreditInfo
     */
    void updateSellChannel(SellChannel sellChannel, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 根据名称查询销售渠道,用于校验
     * @param sellName
     * @return
     */
    SellChannel selectSellChannelByName(String sellName);

    /**
     * 根据主键id查询销售渠道
     * @param id
     * @return
     */
    SellChannel selectSellChannelById(Long id);
}
