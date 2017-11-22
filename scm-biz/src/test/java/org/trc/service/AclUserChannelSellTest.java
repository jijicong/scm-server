package org.trc.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.trc.biz.impl.impower.AclUserAccreditInfoBiz;
import org.trc.biz.impower.IAclUserAccreditInfoBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclUserChannelSell;
import org.trc.service.System.IChannelSellChannelService;
import org.trc.service.System.IChannelService;
import org.trc.service.System.ISellChannelService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.impower.IAclUserChannelSellService;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
@TestExecutionListeners(TransactionalTestExecutionListener.class)
//@Transactional
public class AclUserChannelSellTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private IAclUserChannelSellService aclUserChannelSellService;

    @Autowired
    private IAclUserAccreditInfoService aclUserAccreditInfoService;
    @Autowired
    private IChannelService channelService;
    @Autowired
    private IChannelSellChannelService channelSellChannelService;
    @Autowired
    private ISellChannelService sellChannelService;
    @Autowired
    private IAclUserAccreditInfoBiz userAccreditInfoBiz;
    @Test
    public void linkUserChannelSell(){
        //user-channel-sellChannel 关联
        Example example = new Example(AclUserAccreditInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("channelCode", "YWX001");
        List<AclUserAccreditInfo> userAccreditInfoList = aclUserAccreditInfoService.selectByExample(example);
        List<AclUserChannelSell> aclUserChannelSellList = new ArrayList<>();
        for (AclUserAccreditInfo aclUserAccreditInfo:userAccreditInfoList ) {
            AclUserChannelSell aclUserChannelSell = new AclUserChannelSell();
            aclUserChannelSell.setUserId(aclUserAccreditInfo.getUserId());
            aclUserChannelSell.setUserAccreditId(aclUserAccreditInfo.getId());
            aclUserChannelSell.setChannelCode("YWX001");
            aclUserChannelSell.setSellChannelCode("XSQD001");
            aclUserChannelSellList.add(aclUserChannelSell);
        }
        aclUserChannelSellService.insertList(aclUserChannelSellList);
    }
    @Test
    public void UserChannelSell(){
        userAccreditInfoBiz.findUserAccreditInfoById(25L);
    }
    @Test
    public void UserSelect(){
        List<AclUserAccreditInfo> aclUserAccreditInfoList =  aclUserAccreditInfoService.selectUserListByUserId2("E2E4BDAD80354EFAB6E70120C271968C");
        System.out.println(aclUserAccreditInfoList.size());
    }
}
