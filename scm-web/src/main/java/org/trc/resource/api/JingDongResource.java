package org.trc.resource.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.constants.SupplyConstants;
import org.trc.form.JDModel.BalanceDetailDO;
import org.trc.form.JDModel.JdBalanceDetail;
import org.trc.form.JDModel.OrderDO;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by hzwyz on 2017/6/1 0001.
 */
@Component
@Path(SupplyConstants.JingDongOrder.ROOT)
public class JingDongResource {
    @Autowired
    private IJingDongBiz iJingDongBiz;


    /**
     * 查询余额对账明细信息接口
     * @param queryModel
     * @param page
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.JingDongOrder.CHECK_BALANCE)
    @Produces("application/json;charset=utf-8")
    public Pagenation<JdBalanceDetail> checkBalanceDetail(@BeanParam BalanceDetailDO queryModel, @BeanParam Pagenation<JdBalanceDetail> page) throws Exception {
        return iJingDongBiz.checkBalanceDetail(queryModel,page);
    }

    /**
     * 查询所有京东业务类型接口
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.JingDongOrder.GET_ALL_TREAD_TYPE)
    @Produces("application/json;charset=utf-8")
    public AppResult<JSONArray> getAllTreadType() throws Exception {
        return ResultUtil.createSucssAppResult("业务类型查询成功", iJingDongBiz.getAllTreadType().getResult());
    }
}
