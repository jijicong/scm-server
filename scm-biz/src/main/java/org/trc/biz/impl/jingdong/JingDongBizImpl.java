package org.trc.biz.impl.jingdong;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.constant.RequestFlowConstant;
import org.trc.domain.config.Common;
import org.trc.domain.config.RequestFlow;
import org.trc.enums.JingDongEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.JingDongException;
import org.trc.form.JDModel.*;
import org.trc.form.jingdong.AddressDO;
import org.trc.form.jingdong.MessageDO;
import org.trc.form.jingdong.NewStockDO;
import org.trc.service.IJDService;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.jingdong.ITableMappingService;
import org.trc.util.*;

import java.util.Calendar;
import java.util.List;
import java.util.Map;


/**
 * Created by hzwyz on 2017/5/19 0019.
 */
@Service("iJingDongBiz")
public class JingDongBizImpl implements IJingDongBiz {
    private Logger log = LoggerFactory.getLogger(JingDongBizImpl.class);
    @Autowired
    IJDService ijdService;

    @Autowired
    ICommonService commonService;

    @Autowired
    ITableMappingService tableMappingService;

    @Autowired
    JingDongUtil jingDongUtil;

    @Autowired
    IRequestFlowService requestFlowService;

    public Pagenation<JdBalanceDetail> checkBalanceDetail(BalanceDetailDO queryModel, Pagenation<JdBalanceDetail> page) throws Exception{
        AssertUtil.notNull(page.getPageNo(), "分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(), "分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(), "分页查询参数start不能为空");
        ReturnTypeDO<Pagenation<JdBalanceDetail>> returnTypeDO = ijdService.checkBalanceDetail(queryModel, page);
        if(!returnTypeDO.getSuccess()){
            log.error(returnTypeDO.getResultMessage());
            throw new Exception("对账明细查询异常："+returnTypeDO.getResultMessage());
        }
        page = returnTypeDO.getResult();
        return page;
    }

    /**
     * 获取所有京东交易类型
     * @return
     */
    public ReturnTypeDO getAllTreadType() throws Exception{
        return ijdService.getAllTreadType();
    }
}
