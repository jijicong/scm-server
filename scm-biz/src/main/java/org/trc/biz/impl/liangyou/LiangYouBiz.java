package org.trc.biz.impl.liangyou;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.trc.biz.liangyou.ILiangYouBiz;
import org.trc.constant.LiangYouConstant;
import org.trc.domain.config.Common;
import org.trc.domain.config.LiangYouSkuList;
import org.trc.domain.config.SkuListForm;
import org.trc.domain.order.OrderItem;
import org.trc.domain.order.SupplierOrderInfo;
import org.trc.enums.ExceptionEnum;
import org.trc.form.external.BalanceDetailDTO;
import org.trc.form.liangyou.*;
import org.trc.service.ILiangYouService;
import org.trc.service.config.ISkusListService;
import org.trc.service.impl.order.OrderItemService;
import org.trc.service.impl.order.SupplierOrderInfoService;
import org.trc.service.jingdong.ICommonService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by hzwyz on 2017/6/13 0013.
 */
@Service("liangYouBiz")
public class LiangYouBiz implements ILiangYouBiz {

    private Logger log = LoggerFactory.getLogger(LiangYouBiz.class);

    @Resource(name = "LiangYouService")
    private ILiangYouService liangYouService;

    @Autowired
    ICommonService commonService;

    @Autowired
    JingDongUtil jingDongUtil;

    @Resource
    ISkusListService skusListService;

    @Resource
    SupplierOrderInfoService supplierOrderInfoService;

    @Resource
    OrderItemService orderItemService;

    //粮油供应商编码
    public final static String SUPPLIER_LY_CODE = "LY";

    //粮油供应商编码
    public final static String SUCCESS = "200";

    //错误信息
    public final static String BAR = "-";

    //错误信息
    public final static String EXCEL = ".xls";

    @Override
    public String getAccessToken() throws Exception {

        String token = null;
        Common acc = new Common();
        try{
            //查询redis中是否有accessToken
            token = (String) RedisUtil.getObject("Ly_access_token");
            if (StringUtils.isBlank(token)){
                return createToken();
            }
            return token;
        }catch(RedisConnectionFailureException e){
            //当redis无法连接从数据库中去accessToken
            acc.setCode("accessToken");
            acc.setType(LiangYouConstant.LIANGYOU_TYPE);
            acc = commonService.selectOne(acc);
            if (null != acc) {
                //验证accessToken是否失效，失效创建新的token
                String time = acc.getDeadTime();
                if (jingDongUtil.validatToken(time)) {
                    return createToken();
                }
                return acc.getValue();
            }
            return createToken();
        }
    }

    @Override
    public void ExportGoods() throws Exception{
        try {
            String token = getAccessToken();
            ResultType<JSONObject> result = liangYouService.exportGoods(token,"1");
            if (org.apache.commons.lang3.StringUtils.equals(result.getMessage(),"ok")){
                JSONObject object = result.getData();
                int count = (int)object.get("pagecount");
                int e=1;
                for (int i = 1;i<=count;i++){
                    System.out.println(i);
                    ResultType<JSONObject> result01 = liangYouService.exportGoods(token, String.valueOf(i));
                    JSONObject object01 = result01.getData();
                    JSONArray array = object01.getJSONArray("goodslist");
                    List<SkuListForm> liangYouList = new ArrayList<SkuListForm>();
                    List<LiangYouSkuList> list = JSONArray.parseArray(array.toJSONString(),LiangYouSkuList.class);
                    for (LiangYouSkuList skuList:list){
                        SkuListForm temp = new SkuListForm();
                        StringBuilder sb = new StringBuilder();
                        String tem = String.valueOf(e++);
                        if(tem.length()<7){
                            for(int t=0;t<7-tem.length();t++){
                                sb.append("0");
                            }
                            sb.append(tem);
                        }
                        temp.setSku("SP1"+ DateUtils.dateToString(new Date(),DateUtils.COMPACT_DATE_FORMAT)+sb.toString());
                        temp.setProviderName("粮油");
                        temp.setProviderSku(skuList.getOnly_sku());
                        temp.setSkuName(skuList.getGoods_name());
                        temp.setSupplyPrice(skuList.getGradeprice());
                        temp.setMarketPrice(skuList.getMarket_price());
                        temp.setWarehouseName(skuList.getDepot_name());
                        temp.setStock(Integer.parseInt(skuList.getStock()));
                        temp.setIfShow(skuList.getIf_show());
                        temp.setCreateTime(new Date(Long.parseLong(skuList.getAdd_time())));
                        temp.setUpdateTime(new Date());
                        liangYouList.add(temp);
                    }
                    skusListService.insertList(liangYouList);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public SkuListForm getSkuList() throws Exception {

        return null;
    }

    @Override
    public List<CheckStockDO> checkStock(String sku) throws Exception {
        AssertUtil.notBlank(sku, "sku不能为空");
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        ResultType<JSONArray> result = liangYouService.checkStock(token,sku);
        JSONArray object = (JSONArray)result.getData();
        List<CheckStockDO> list = object.toJavaList(CheckStockDO.class);
        return list;
    }

    @Override
    public String addOutOrder(LiangYouOrderDO orderDO) throws Exception {
        AssertUtil.notBlank(orderDO.getConsignee(), "Consignee不能为空");
        AssertUtil.notBlank(orderDO.getOrderSn(), "orderSn不能为空");
        AssertUtil.notBlank(orderDO.getOutOrderSn(), "outOrderSn不能为空");
        AssertUtil.notBlank(orderDO.getRealName(), "realName不能为空");
        AssertUtil.notBlank(orderDO.getImId(), "imId不能为空");
        AssertUtil.notBlank(orderDO.getPhoneMob(), "phoneMob不能为空");
        AssertUtil.notBlank(orderDO.getAddress(), "address不能为空");
        AssertUtil.notBlank(orderDO.getProvince(),"province不能为空");
        AssertUtil.notBlank(orderDO.getCity(),"city不能为空");
        AssertUtil.notBlank(orderDO.getCounty(),"county不能为空");
        AssertUtil.notNull(orderDO.getShippingId(),"shippingId不能为空");
        List<OutOrderGoods> list=orderDO.getOutOrderGoods();
        for (OutOrderGoods goods:list){
            AssertUtil.notBlank(goods.getGoodsName(),"goodsName不能为空");
            AssertUtil.notBlank(goods.getOnlySku(),"onlySku不能为空");
            //AssertUtil.notBlank(goods.getQuantity(),"quantity不能为空");
        }
        ResultType<JSONObject> result = liangYouService.addOutOrder(orderDO);
        return JSONObject.toJSONString(result.getData());
    }

    @Override
    public String addToutOrder(LiangYouTorderDO orderDO) throws Exception {

        AssertUtil.notBlank(orderDO.getConsignee(), "Consignee不能为空");
        AssertUtil.notBlank(orderDO.getOrderSn(), "orderSn不能为空");
        //AssertUtil.notBlank(orderDO.getRealName(), "realName不能为空");

        if (StringUtils.isEquals(orderDO.getPaymentId(),LiangYouConstant.WEIXIN)){
            AssertUtil.notBlank(orderDO.getAccountId(), "accountId不能为空");
        }
        AssertUtil.notBlank(orderDO.getImId(), "imId不能为空");
        AssertUtil.notBlank(orderDO.getPaymentId(), "paymentId不能为空");
        AssertUtil.notBlank(orderDO.getTradeNum(), "tradeNum不能为空");
        AssertUtil.notBlank(orderDO.getOutOrderSn(), "outOrderSn不能为空");
        AssertUtil.notBlank(orderDO.getOrderAmount(), "orderAmount不能为空");
        AssertUtil.notBlank(orderDO.getPhoneMob(), "phoneMob不能为空");
        AssertUtil.notBlank(orderDO.getAddress(), "address不能为空");
        AssertUtil.notBlank(orderDO.getProvince(),"province不能为空");
        AssertUtil.notBlank(orderDO.getCity(),"city不能为空");
        AssertUtil.notBlank(orderDO.getCounty(),"county不能为空");
        AssertUtil.notNull(orderDO.getShippingId(),"shippingId不能为空");
        AssertUtil.notNull(orderDO.getShippingFee(),"shippingFee不能为空");
        List<OutTorderGoods> list=orderDO.getOutTorderGoods();
        for (OutTorderGoods goods:list){
            AssertUtil.notBlank(goods.getGoodsName(),"goodsName不能为空");
            AssertUtil.notBlank(goods.getOnlySku(),"onlySku不能为空");
            //AssertUtil.notBlank(goods.getQuantity(),"quantity不能为空");
            AssertUtil.notNull(goods.getPrice(),"price不能为空");
        }
        ResultType<JSONObject> result = liangYouService.addToutOrder(orderDO);
        return JSONObject.toJSONString(result.getData());
    }

    @Override
    public String getOrderStatus(String orderSn) throws Exception {
        AssertUtil.notBlank(orderSn, "orderSn不能为空");
        ResultType<JSONObject> result = liangYouService.getOrderStatus(orderSn);
        return JSONObject.toJSONString(result.getData());
    }

    @Override
    public GoodsInfoDO getGoodsInfo(String sku) throws Exception {
        AssertUtil.notBlank(sku, "sku不能为空");
        String token = getAccessToken();
        AssertUtil.notBlank(token, "token不能为空");
        ResultType<JSONObject> result = liangYouService.getGoodsInfo(token,sku);
        JSONObject object = (JSONObject)result.getData();
        GoodsInfoDO goodsInfo = object.toJavaObject(GoodsInfoDO.class);
        return goodsInfo;
    }

    /**
     * 分页查询粮油报表
     *
     * @param form
     * @param page
     * @return
     * @throws Exception
     */
    @Override
    public Pagenation<LyStatement> LyStatementPage(LyStatementForm form, Pagenation<OrderItem> page) throws Exception {
        AssertUtil.notNull(page.getPageNo(),"分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(),"分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(),"分页查询参数start不能为空");
        //1.根据查询条件查处符合要求的粮油订单
        List<SupplierOrderInfo> supplierOrderInfoList = getLySuccessOrder(form);
        List<String> list = new ArrayList();
        for (SupplierOrderInfo supplierOrderInfo : supplierOrderInfoList){
            list.add(supplierOrderInfo.getWarehouseOrderCode());
        }
        //2.分页查询订单商品详情信息
        Pagenation<OrderItem> pagenation  = getOrderItemsByPage(list,form, page);
        Pagenation<LyStatement> newPage  = new Pagenation<>();
        List<OrderItem> orderItemList = pagenation.getResult();
        newPage.setStart(pagenation.getStart());
        newPage.setTotalCount(pagenation.getTotalCount());
        newPage.setPageSize(pagenation.getPageSize());
        newPage.setPageNo(pagenation.getPageNo());
        if (orderItemList.size()!= 0){
            List<OrderItem> orderItemList1 = packageData(supplierOrderInfoList, orderItemList);
            List<LyStatement> list1 = setParam(orderItemList1);
            newPage.setResult(list1);
        }
        return newPage;
    }

    private List<LyStatement> setParam(List<OrderItem> orderItemList){
        List<LyStatement> list = new ArrayList<>();
        for (OrderItem orderItem:orderItemList){
            LyStatement lyStatement = new LyStatement();
            lyStatement.setSupplierSkuCode(orderItem.getSupplierSkuCode());
            lyStatement.setSkuCode(orderItem.getSkuCode());
            lyStatement.setItemName(orderItem.getItemName());
            lyStatement.setNum(orderItem.getNum());
            lyStatement.setPlatformOrderCode(orderItem.getPlatformOrderCode());
            lyStatement.setShopOrderCode(orderItem.getShopOrderCode());
            lyStatement.setSupplierOrderCode(orderItem.getSupplierOrderCode());
            lyStatement.setPayment(orderItem.getPayment());
            lyStatement.setCreateTime(DateUtils.formatDateTime(orderItem.getCreateTime()));
            list.add(lyStatement);
        }
        return list;
    }

    private List<OrderItem> packageData(List<SupplierOrderInfo> supplierOrderInfoList, List<OrderItem> orderItemList) {
        //将要返回到前端的数据补全
        Map<String,Map<String,SupplierOrderInfo>> map = getSku(supplierOrderInfoList);
        for (OrderItem orderItem:orderItemList){
            Map<String,SupplierOrderInfo> temMap = map.get(orderItem.getWarehouseOrderCode());
            SupplierOrderInfo supplierOrderInfo = temMap.get(orderItem.getSupplierSkuCode());
            if (supplierOrderInfo==null){
                continue;
            }
            orderItem.setSupplierOrderCode(supplierOrderInfo.getSupplierOrderCode());
            orderItem.setSubmitTime(supplierOrderInfo.getCreateTime());
        }
        return orderItemList;
    }

    //取出sku
    private Map<String,Map<String,SupplierOrderInfo>> getSku(List<SupplierOrderInfo> supplierOrderInfoList){
        Map<String,Map<String,SupplierOrderInfo>> map = new HashMap<>();
        for (SupplierOrderInfo supplierOrderInfo : supplierOrderInfoList){
            String skus = supplierOrderInfo.getSkus();
            String warehouseOrderCode = supplierOrderInfo.getWarehouseOrderCode();
            if(StringUtils.isBlank(skus)){
                continue;
            }
            List<SkuForm> list = JSONArray.parseArray(skus,SkuForm.class);
            Map<String,SupplierOrderInfo> newMap = new HashMap<>();
            for (SkuForm skuForm:list){
                newMap.put(skuForm.getSkuCode(),supplierOrderInfo);
            }
            map.put(warehouseOrderCode,newMap);
        }
        return map;
    }

    private Pagenation<OrderItem> getOrderItemsByPage(List<String> list, LyStatementForm form, Pagenation<OrderItem> page){
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(form.getSupplierSkuCode())){
            criteria.andLike("supplierSkuCode","%"+form.getSupplierSkuCode()+"%");
        }
        if (!StringUtils.isBlank(form.getSkuCode())){
            criteria.andLike("skuCode","%"+form.getSkuCode()+"%");
        }
        if (!StringUtils.isBlank(form.getItemName())){
            criteria.andLike("itemName","%"+form.getItemName()+"%");
        }
        if (!StringUtils.isBlank(form.getPlatformOrderCode())){
            criteria.andLike("platformOrderCode","%"+form.getPlatformOrderCode()+"%");
        }
        if (!StringUtils.isBlank(form.getShopOrderCode())){
            criteria.andLike("shopOrderCode","%"+form.getShopOrderCode()+"%");
        }
        criteria.andIn("warehouseOrderCode",list);
        example.orderBy("submitTime").desc();
        Pagenation<OrderItem> pagenation = orderItemService.pagination(example,page,form);
        return pagenation;
    }

    private List<OrderItem> getOrderItems(List<String> list, LyStatementForm form){
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(form.getSupplierSkuCode())){
            criteria.andLike("supplierSkuCode","%"+form.getSupplierSkuCode()+"%");
        }
        if (!StringUtils.isBlank(form.getSkuCode())){
            criteria.andLike("skuCode","%"+form.getSkuCode()+"%");
        }
        if (!StringUtils.isBlank(form.getItemName())){
            criteria.andLike("itemName","%"+form.getItemName()+"%");
        }
        if (!StringUtils.isBlank(form.getPlatformOrderCode())){
            criteria.andLike("platformOrderCode","%"+form.getPlatformOrderCode()+"%");
        }
        if (!StringUtils.isBlank(form.getShopOrderCode())){
            criteria.andLike("shopOrderCode","%"+form.getShopOrderCode()+"%");
        }
        criteria.andIn("warehouseOrderCode",list);
        example.orderBy("submitTime").desc();
        List<OrderItem> result = orderItemService.selectByExample(example);
        return result;
    }

    private List<SupplierOrderInfo> getLySuccessOrder(LyStatementForm form) {
        Example example = new Example(SupplierOrderInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("supplierCode",SUPPLIER_LY_CODE);
        if (!StringUtils.isBlank(form.getStartDate())){
            criteria.andGreaterThanOrEqualTo("createTime",form.getStartDate());
        }
        if (!StringUtils.isBlank(form.getEndDate())){
            criteria.andLessThanOrEqualTo("createTime",form.getEndDate());
        }
        if (!StringUtils.isBlank(form.getSupplierOrderCode())){
            criteria.andLike("supplierOrderCode","%"+form.getSupplierOrderCode()+"%");
        }
        List<String> list = new ArrayList();
        list.add("3");
        list.add("4");
        list.add("6");
        criteria.andIn("supplierOrderStatus",list);
        criteria.andEqualTo("status",SUCCESS);
        List<SupplierOrderInfo> supplierInfo = supplierOrderInfoService.selectByExample(example);
        return supplierInfo;
    }

    /**
     * 粮油代发报表导出
     *
     * @param form
     * @return
     * @throws Exception
     */
    @Override
    public Response exportStatement(LyStatementForm form) throws Exception {
        try{
            List<LyStatement> result = queryOrderForExport(form);
            CellDefinition spuCode = new CellDefinition("skuCode", "商品SKU编号", CellDefinition.TEXT, 4000);
            CellDefinition skuCode = new CellDefinition("supplierSkuCode", "粮油商品SKU", CellDefinition.TEXT, 4000);
            CellDefinition itemName = new CellDefinition("itemName", "粮油商品名称", CellDefinition.TEXT, 8000);
            CellDefinition num = new CellDefinition("num", "交易数量", CellDefinition.NUM_0_00, 2000);
            CellDefinition platformOrderCode = new CellDefinition("platformOrderCode", "平台订单号", CellDefinition.TEXT, 4000);
            CellDefinition shopOrderCode = new CellDefinition("shopOrderCode", "店铺订单号", CellDefinition.TEXT, 4000);
            CellDefinition supplierOrderCode = new CellDefinition("supplierOrderCode", "粮油订单号", CellDefinition.TEXT, 4000);
            CellDefinition payment = new CellDefinition("payment", "买家实付商品金额", CellDefinition.NUM_0_00, 2000);
            CellDefinition createTime = new CellDefinition("createTime", "系统发送粮油时间", CellDefinition.TEXT, 8000);

            List<CellDefinition> cellDefinitionList = new ArrayList<>();
            cellDefinitionList.add(spuCode);
            cellDefinitionList.add(skuCode);
            cellDefinitionList.add(itemName);
            cellDefinitionList.add(num);
            cellDefinitionList.add(platformOrderCode);
            cellDefinitionList.add(shopOrderCode);
            cellDefinitionList.add(supplierOrderCode);
            cellDefinitionList.add(payment);
            cellDefinitionList.add(createTime);
            String sheetName = "粮油代发报表";
            String fileName = "粮油代发报表-" + form.getStartDate() + BAR + form.getEndDate() + EXCEL;
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
            log.error("粮油代发报表导出异常"+e.getMessage(),e);
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.LIANG_YOU_EXPORT_EXCEPTION.getCode()),ExceptionEnum.LIANG_YOU_EXPORT_EXCEPTION.getMessage());
        }
    }

    private List<LyStatement> queryOrderForExport(LyStatementForm form){
        //1.根据查询条件查处符合要求的粮油订单
        List<SupplierOrderInfo> supplierOrderInfoList = getLySuccessOrder(form);
        List<String> list = new ArrayList();
        for (SupplierOrderInfo supplierOrderInfo : supplierOrderInfoList){
            list.add(supplierOrderInfo.getWarehouseOrderCode());
        }
        List<OrderItem> orderItems = getOrderItems(list,form);
        //将要返回到前端的数据补全
        List<OrderItem> list1 = packageData(supplierOrderInfoList, orderItems);
        List<LyStatement> newList = setParam(list1);
        return newList;
    }

    private String createToken() throws Exception{
        ResultType<JSONObject> result = liangYouService.getToken();
        JSONObject object = (JSONObject)result.getData();
        if (StringUtils.isEquals(result.getMessage(),"ok")){
            String token = (String)object.get("access_token");
            Integer expires = (Integer)object.get("expires_in");
            //保存到数据库和redis中，并返回token
            RedisUtil.setObject("Ly_access_token", token, expires-60);
            Common tem = new Common();
            tem.setCode(LiangYouConstant.LIANGYOU_CODE);
            tem.setType(LiangYouConstant.LIANGYOU_TYPE);
            Common obj =commonService.selectOne(tem);
            Common common = new Common();
            common.setCode(LiangYouConstant.LIANGYOU_CODE);
            common.setValue(token);
            common.setType(LiangYouConstant.LIANGYOU_TYPE);
            String deadTime = jingDongUtil.expireToken(Calendar.getInstance().getTimeInMillis(),Integer.toString(expires-60));
            common.setDeadTime(deadTime);
            common.setDescription(LiangYouConstant.LIANGYOU_DESC);
            if (null!=obj){
                common.setId(obj.getId());
                common.setUpdateTime(new Date());
                commonService.updateByPrimaryKey(common);
                return token;
            }
            commonService.insert(common);
            return token;
        }
        return object.toJSONString();
    }


}
