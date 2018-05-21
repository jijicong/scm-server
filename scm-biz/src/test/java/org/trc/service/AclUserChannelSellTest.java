package org.trc.service;


import com.alibaba.fastjson.JSON;
import com.tairanchina.csp.foundation.common.sdk.CommonConfig;
import com.tairanchina.csp.foundation.sdk.CSPKernelSDK;
import com.tairanchina.csp.foundation.sdk.dto.TokenDeliverDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.trc.biz.impower.IAclUserAccreditInfoBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclUserChannelSell;
import org.trc.service.System.IChannelSellChannelService;
import org.trc.service.System.IChannelService;
import org.trc.service.System.ISellChannelService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.impower.IAclUserChannelSellService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonConfigUtil;
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
    @Value("${apply.id}")
    private String applyId;

    @Value("${apply.secret}")
    private String applySecret;

    @Value("${apply.uri}")
    private String applyUri;
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
        CSPKernelSDK sdk = CommonConfigUtil.getCSPKernelSDK(applyUri,applyId,applySecret);
        try {
//            AssertUtil.isTrue(sdk.user.findPhoneExists("15757195796"), "该手机号未在泰然城注册");
            System.out.printf(JSON.toJSONString(sdk.user.logoutByUnionId("d6732e5d73b74a4f9bba0c9d55e65a9a")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void  tenantValidate(){
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkNjczMmU1ZDczYjc0YTRmOWJiYTBjOWQ1NWU2NWE5YSIsImF1ZCI6WyJ1YzFkNmQ2ZGFjNDAzOThmMTkiLCJ1Y2VudGVyIl0sImlfdiI6MTUyNjQ0MDQyMzAwMCwiaWRlbnRfaWQiOjE3ODgxMjEsIm5iZiI6MTUyNjQ1MTE2NywicF92IjoxNTI2NDQwNDIzMDAwLCJpc3MiOiJ1Y2VudGVyIiwiZXhwIjoxNTI2NTM3NTY3LCJ0eXBlIjoxLCJpYXQiOjE1MjY0NTExNjcsImp0aSI6IjEwNDEyIn0.Ft2b82OivLoTPZNNGGCfQXRH3UVCwWdwqXaXlZBfFW-3HX5Pl-ROPLBCxqo8S8MriuMCvebU4nnIuyXWXfBvU85PesY6lIwvSakGT8e2NfnLEDE82L19H8-lY3PzcI8hI9N9ua04r52qg6PQQ6qjwhXzk7_xjpMsBjubDO6jbFo";
        CommonConfig config = new CommonConfig();
        CommonConfig.Basic basicConfig = config.getBasic();
        basicConfig.setUrl(applyUri);
        basicConfig.setAppId(applyId);
        basicConfig.setAppSecret(applySecret);
        CSPKernelSDK sdk = CSPKernelSDK.instance(config);
        TokenDeliverDTO tokenInfo = sdk.user.tenantValidate(token, "", config).getBody();
        System.out.println(JSON.toJSONString(tokenInfo));
    }

}
