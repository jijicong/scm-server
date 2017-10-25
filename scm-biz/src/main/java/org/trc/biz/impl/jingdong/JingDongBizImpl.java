package org.trc.biz.impl.jingdong;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.OperateEnum;
import org.trc.form.JDModel.*;
import org.trc.form.external.*;
import org.trc.service.IJDService;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.jingdong.ITableMappingService;
import org.trc.util.*;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


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

    //错误信息
    public final static String BAR = "-";

    //错误信息
    public final static String EXCEL = ".xls";

    /*public Pagenation<JdBalanceDetail> checkBalanceDetail(BalanceDetailDO queryModel, Pagenation<JdBalanceDetail> page) throws Exception{
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
    }*/

    /**
     * 获取所有京东交易类型
     * @return
     */
    @Override
    public ReturnTypeDO getAllTreadType() throws Exception{
        return ijdService.getAllTreadType();
    }

    /**
     * 京东账户余额信息查询接口
     */
    @Override
    public Response queryBalanceInfo() throws Exception{
        try {
            ReturnTypeDO result = ijdService.queryBalanceInfo();
            return ResultUtil.createSuccessResult("京东账户余额信息查询成功",result.getResult());
        }catch (Exception e){
            log.error("京东账户余额信息查询异常"+e.getMessage(),e);
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.JING_DONG_BALANCE_EXPORT_EXCEPTION.getCode()),ExceptionEnum.JING_DONG_BALANCE_EXPORT_EXCEPTION.getMessage());
        }

    }

    @Override
    public Pagenation<OrderDetailDTO> orderDetailByPage(OrderDetailForm queryModel, Pagenation<OrderDetail> page) throws Exception{
        AssertUtil.notNull(page.getPageNo(), "分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(), "分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(), "分页查询参数start不能为空");

        ReturnTypeDO result = ijdService.orderDetailByPage(queryModel,page);
        JSONObject jbo = JSONObject.parseObject(String.valueOf(result.getResult()));
        Pagenation<OrderDetailDTO> appResult = null;
        try{
            log.info("订单明细分页查询："+jbo.toJSONString());
            appResult = jbo.toJavaObject(Pagenation.class);
        }catch (Exception e){
            log.info("转换成java对象失败");
            throw e;
        }
        return appResult;
    }



    @Override
    public Pagenation<BalanceDetailDTO> balanceDetailByPage(BalanceDetailDO queryModel, Pagenation<JdBalanceDetail> page) throws Exception{
        AssertUtil.notNull(page.getPageNo(), "分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(), "分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(), "分页查询参数start不能为空");
        ReturnTypeDO result = ijdService.balanceDetailByPage(queryModel,page);
        JSONObject jbo = JSONObject.parseObject(String.valueOf(result.getResult()));
        Pagenation<BalanceDetailDTO> appResult = null;
        try{
            log.info("余额明细分页查询"+jbo.toJSONString());
            appResult = jbo.toJavaObject(Pagenation.class);
        }catch (Exception e){
            log.info("转换成java对象失败");
            throw e;
        }
        return appResult;
    }

    @Override
    public Response exportBalanceDetail(BalanceDetailDO queryModel)throws Exception {
        try{
            List<BalanceDetailDTO> result = ijdService.exportBalanceDetail(queryModel);
            CellDefinition tradeNo = new CellDefinition("tradeNo", "业务号", CellDefinition.TEXT, 8000);
            CellDefinition pin = new CellDefinition("pin", "京东账号", CellDefinition.TEXT, 4000);
            CellDefinition orderId = new CellDefinition("orderId", "京东订单号", CellDefinition.TEXT, 8000);
            CellDefinition income = new CellDefinition("income", "收入", CellDefinition.NUM_0_00, 4000);
            CellDefinition outcome = new CellDefinition("outcome", "支出", CellDefinition.NUM_0_00, 6000);
            CellDefinition accountType = new CellDefinition("accountType", "账号类型", CellDefinition.TEXT, 6000);
            CellDefinition createdDate = new CellDefinition("createdDate", "余额变动时间", CellDefinition.TEXT, 8000);
            CellDefinition tradeTypeName = new CellDefinition("tradeTypeName", "业务类型", CellDefinition.TEXT, 8000);
            CellDefinition notePub = new CellDefinition("notePub", "备注", CellDefinition.TEXT, 10000);

            List<CellDefinition> cellDefinitionList = new ArrayList<>();
            cellDefinitionList.add(tradeNo);
            cellDefinitionList.add(pin);
            cellDefinitionList.add(orderId);
            cellDefinitionList.add(income);
            cellDefinitionList.add(outcome);
            cellDefinitionList.add(accountType);
            cellDefinitionList.add(createdDate);
            cellDefinitionList.add(tradeTypeName);
            cellDefinitionList.add(notePub);
            String sheetName = "余额变动明细";
            String fileName = "余额变动明细-" + queryModel.getStartUpdateTime() + BAR + queryModel.getEndUpdateTime() + EXCEL;
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            HSSFWorkbook hssfWorkbook = ExportExcel.generateExcel(result, cellDefinitionList, sheetName);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            hssfWorkbook.write(stream);
            return Response.ok(stream.toByteArray()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8'zh_cn'" + fileName).type(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Cache-Control", "no-cache").build();
        }catch (Exception e){
            log.error("余额明细导出异常"+e.getMessage(),e);
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.JING_DONG_BALANCE_EXPORT_EXCEPTION.getCode()),ExceptionEnum.JING_DONG_BALANCE_EXPORT_EXCEPTION.getMessage());
        }

    }

    @Override
    public Response exportOrderDetail(OrderDetailForm queryModel) throws Exception{
        try{
            List<OrderDetailDTO> result = ijdService.exportOrderDetail(queryModel);
            CellDefinition operate = new CellDefinition("operate", "操作", CellDefinition.TEXT, 4000);
            CellDefinition errMsg = new CellDefinition("errMsg", "异常说明", CellDefinition.TEXT, 4000);
            CellDefinition channelOrderSubmitTime = new CellDefinition("channelOrderSubmitTime", "渠道订单提交时间", CellDefinition.TEXT, 6000);
            CellDefinition jingdongOrderCreateTime = new CellDefinition("jingdongOrderCreateTime", "京东订单生成时间", CellDefinition.TEXT, 6000);
            CellDefinition channelPlatformOrder = new CellDefinition("channelPlatformOrder", "渠道平台订单号", CellDefinition.TEXT, 6000);
            CellDefinition parentOrderCode = new CellDefinition("parentOrderCode", "京东父订单编号", CellDefinition.TEXT, 4000);
            CellDefinition orderCode = new CellDefinition("orderCode", "京东子订单编号", CellDefinition.TEXT, 4000);
            CellDefinition itemSkuCode = new CellDefinition("itemSkuCode", "京东商品编号", CellDefinition.TEXT, 4000);

            CellDefinition itemSkuName = new CellDefinition("itemSkuName", "京东商品名称", CellDefinition.TEXT, 16000);
            CellDefinition firstClassify = new CellDefinition("firstClassify", "京东一级分类", CellDefinition.TEXT, 4000);
            CellDefinition secondClassify = new CellDefinition("secondClassify", "京东二级分类", CellDefinition.TEXT, 4000);
            CellDefinition thirdClassify = new CellDefinition("thirdClassify", "京东三级分类", CellDefinition.TEXT, 4000);
            CellDefinition channelItemsNum = new CellDefinition("channelItemsNum", "渠道商品数量", CellDefinition.NUM_0, 4000);
            CellDefinition jdItemsNum = new CellDefinition("jdItemsNum", "京东商品数量", CellDefinition.NUM_0, 4000);
            CellDefinition price = new CellDefinition("price", "京东商品单价", CellDefinition.NUM_0_00, 4000);

            CellDefinition totalPrice = new CellDefinition("totalPrice", "京东商品总计金额", CellDefinition.NUM_0_00, 4000);
            CellDefinition pay = new CellDefinition("pay", "买家实付商品金额", CellDefinition.NUM_0_00, 4000);
            CellDefinition freight = new CellDefinition("freight", "运费", CellDefinition.NUM_0_00, 4000);
            CellDefinition subTotalPrice = new CellDefinition("subTotalPrice", "京东子订单总计金额", CellDefinition.NUM_0_00, 4000);
            CellDefinition parentTotalPrice = new CellDefinition("parentTotalPrice", "京东父订单总计金额", CellDefinition.NUM_0_00, 4000);
            CellDefinition actualPay = new CellDefinition("actualPay", "账户实际支付金额", CellDefinition.NUM_0_00, 4000);
            CellDefinition refund = new CellDefinition("refund", "账户实际退款金额", CellDefinition.NUM_0_00, 4000);

            CellDefinition balanceCreateTime = new CellDefinition("balanceCreateTime", "余额变动时间", CellDefinition.TEXT, 8000);
            CellDefinition state = new CellDefinition("state", "订单状态", CellDefinition.TEXT, 4000);
            CellDefinition remark = new CellDefinition("remark", "备注", CellDefinition.TEXT, 8000);
            List<CellDefinition> cellDefinitionList = new ArrayList<>();
            cellDefinitionList.add(operate);
            cellDefinitionList.add(errMsg);
            cellDefinitionList.add(channelOrderSubmitTime);
            cellDefinitionList.add(jingdongOrderCreateTime);
            cellDefinitionList.add(channelPlatformOrder);
            cellDefinitionList.add(parentOrderCode);
            cellDefinitionList.add(orderCode);
            cellDefinitionList.add(itemSkuCode);

            cellDefinitionList.add(itemSkuName);
            cellDefinitionList.add(firstClassify);
            cellDefinitionList.add(secondClassify);
            cellDefinitionList.add(thirdClassify);
            cellDefinitionList.add(channelItemsNum);
            cellDefinitionList.add(jdItemsNum);
            cellDefinitionList.add(price);
            cellDefinitionList.add(totalPrice);

            cellDefinitionList.add(pay);
            cellDefinitionList.add(freight);
            cellDefinitionList.add(subTotalPrice);
            cellDefinitionList.add(parentTotalPrice);
            cellDefinitionList.add(actualPay);
            cellDefinitionList.add(refund);
            cellDefinitionList.add(balanceCreateTime);
            cellDefinitionList.add(state);
            cellDefinitionList.add(remark);
            String sheetName = "订单比对明细";
            String fileName = "订单比对明细-" + queryModel.getStartUpdateTime() + BAR + queryModel.getEndUpdateTime() + EXCEL;
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            HSSFWorkbook hssfWorkbook = ExportExcel.generateExcel(result, cellDefinitionList, sheetName);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            hssfWorkbook.write(stream);
            return Response.ok(stream.toByteArray()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8'zh_cn'" + fileName).type(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Cache-Control", "no-cache").build();
        }catch (Exception e){
            log.error("订单明细导出异常"+e.getMessage(),e);
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.JING_DONG_ORDER_EXPORT_EXCEPTION.getCode()),ExceptionEnum.JING_DONG_ORDER_EXPORT_EXCEPTION.getMessage());
        }
    }

    @Override
    public Response operateRecord(OperateForm orderDetail) throws Exception{
        try{
            if (orderDetail.getOperate().equals(OperateEnum.HANDLE.getCode())){
                if (StringUtils.isBlank(orderDetail.getRemark())) {
                    return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.JING_DONG_REMARK_NOT_NULL_EXCEPTION.getCode()), ExceptionEnum.JING_DONG_REMARK_NOT_NULL_EXCEPTION.getMessage());
                }
            }
            AssertUtil.notNull(orderDetail.getId(), "操作参数id不能为空");
            ReturnTypeDO result = ijdService.operateRecord(orderDetail);
            return ResultUtil.createSuccessResult("订单明细操作更新成功",result.getResult());
        }catch (Exception e){
            log.error("订单明细操作更新异常"+e.getMessage(),e);
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.JING_DONG_ORDER_UPDATE_OPERATE_STATE_EXCEPTION.getCode()),ExceptionEnum.JING_DONG_ORDER_UPDATE_OPERATE_STATE_EXCEPTION.getMessage());
        }
    }

    /**
     * 订单明操作查询接口
     */
    @Override
    public Response getOperateState(Long id)throws Exception{
        try{
            AssertUtil.notNull(id, "操作参数id不能为空");
            ReturnTypeDO result = ijdService.getOperateState(id);
            return ResultUtil.createSuccessResult("订单明操作查询成功",result.getResult());
        }catch (Exception e){
            log.error("京东获取订单明细操作状态异常"+e.getMessage(),e);
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.JING_DONG_ORDER_GET_OPERATE_STATE_EXCEPTION.getCode()),ExceptionEnum.JING_DONG_ORDER_GET_OPERATE_STATE_EXCEPTION.getMessage());
        }
    }

    @Override
    public Response statisticsRecord(BalanceDetailDO queryModel) throws Exception{
        try{
            ReturnTypeDO result = ijdService.statisticsRecord(queryModel);
            return ResultUtil.createSuccessResult("余额明细统计查询成功",result.getResult());
        }catch (Exception e){
            log.error("京东获取订单明细操作状态异常"+e.getMessage(),e);
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.JING_DONG_STATISTICS_EXCEPTION.getCode()),ExceptionEnum.JING_DONG_STATISTICS_EXCEPTION.getMessage());
        }
    }


}
