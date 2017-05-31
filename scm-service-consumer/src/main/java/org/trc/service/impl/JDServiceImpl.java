package org.trc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.form.JDModel.*;
import org.trc.service.IJDService;
import org.trc.util.BeanToMapUtil;
import org.trc.util.DateUtils;
import org.trc.util.HttpRequestUtil;
import org.trc.util.MD5;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created by hzwyz on 2017/5/18 0018.
 */
@Service("jDService")
public class JDServiceImpl implements IJDService{

    @Autowired
    private JdBaseDO jdBaseDO;

    @Override
    public String createToken(){
        try {
            String timestamp = DateUtils.formatDateTime(new Date());
            String sign = jdBaseDO.getClient_secret() + timestamp + jdBaseDO.getClient_id()
                    + jdBaseDO.getUsername() + jdBaseDO.getPassword() + jdBaseDO.getGrant_type() + jdBaseDO.getClient_secret();
            sign = MD5.encryption(sign).toUpperCase();
            String url = jdBaseDO.getJdurl()+"/oauth2/accessToken";
            String data =
                    "grant_type=access_token" +
                            "&client_id=" +jdBaseDO.getClient_id()+
                            "&username=" + URLEncoder.encode(jdBaseDO.getUsername(), "utf-8") +
                            "&password=" + jdBaseDO.getPassword() +
                            "&timestamp=" + timestamp +
                            "&sign="+sign;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        }catch (Exception e){
            return "创建Token出错";
        }
    }

    /**
     * 使用Refresh Token刷新Access Token
     * @param refreshToken
     * @return
     * @throws Exception
     */
    @Override
    public String freshAccessTokenByRefreshToken(String refreshToken)throws Exception  {
        try{
            String url = jdBaseDO.getJdurl()+"/oauth2/refreshToken";
            String data ="refresh_token="+refreshToken +
                    "&client_id=" +jdBaseDO.getClient_id()+
                    "&client_secret=" + jdBaseDO.getClient_secret();
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "刷新失败";
        }catch (Exception e){
            return "刷新Access Token出错";
        }
    }

    /**
     * 获取商品池编号
     * @param token 授权时的access token
     * @return
     */
    public String getPageNum(String token){
        try{
            String url = jdBaseDO.getJdurl()+"/api/product/getPageNum";
            String data ="token="+token;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "获取商品池失败";
        }catch (Exception e){
            return "获取商品池异常";
        }
    }

    /**
     * 获取商品编号
     * @param token 授权时的access token
     * @param pageNum 池子编号
     * @return
     */
    public String getSku(String token, String pageNum){
        try{
            String url = jdBaseDO.getJdurl()+"/api/product/getSku";
            String data ="token="+token+"&pageNum="+pageNum;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "获取商品池内商品编号失败";
        }catch (Exception e){
            return "获取商品池内商品编号异常";
        }
    }


    /**
     * 获取商品的详细信息
     * @param token 授权时的access token
     * @param sku 商品编号
     * @param isShow 查询商品基本信息
     * @return
     */
    public String getDetail(String token,String sku, Boolean isShow){
        try{
            String url = jdBaseDO.getJdurl()+"/api/product/getDetail";
            String data ="token="+token+"&sku="+sku+"&isShow="+isShow;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "获取商品详细信息失败";
        }catch (Exception e){
            return "获取商品详细信息异常";
        }
    }

    /**
     * 获取池内商品编号接口-品类商品池
     * @param token
     * @param pageNum
     * @param pageNo
     * @return
     * @throws Exception
     */
    @Override
    public String getSkuByPage(String token, String pageNum, String pageNo) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/product/getSkuByPage";
            String data ="token="+token+"&pageNum="+pageNum+"&pageNo="+pageNo;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return rev;
        }catch (Exception e){
            return "获取品类池信息异常";
        }
    }

    @Override
    public String checkSku(String token, String skuIds) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/product/check";
            String data ="token="+token+"&skuIds="+skuIds;
//            data = data.substring(0,data.length()-1);
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
//            System.out.println("data:"+data);
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return rev;
        }catch (Exception e){
            return "获取商品是否可用异常";
        }
    }

    /**
     * 获取商品上下架状态
     * @param token 授权时的access token
     * @param sku 商品编号 支持批量（最高100个）
     * @return
     */
    public String skuState(String token,String sku){
        try{
            String url = jdBaseDO.getJdurl()+"/api/product/skuState";
            String data ="token="+token+"&sku="+sku;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");

            JSONArray array = json.getJSONArray("result");
            if (result){
                return array.toString();
            }
            return "获取商品上下架状态失败";
        }catch (Exception e){
            return "获取商品上下架状态异常";
        }
    }

    /**
     * 获取商品图片信息
     * @param token 授权时的access token
     * @param sku 商品编号 支持批量（最高100个）
     * @return
     */
    public String skuImage(String token,String sku){
        try{
            String url = jdBaseDO.getJdurl()+"/api/product/skuImage";
            String data ="token="+token+"&sku="+sku;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "获取商品图片信息失败";
        }catch (Exception e){
            return "获取商品图片信息异常";
        }
    }

    /**
     * 商品搜索
     * 关键字+分页+页码，使用价格区间、品牌首字母、分类作为筛选条件
     * @param searchDO
     * @return
     */
    public String search(SearchDO searchDO){
        try{
            String url = jdBaseDO.getJdurl()+"/api/search/search";
            String data ="token="+searchDO.getToken();
            if (StringUtils.isNotBlank(searchDO.getKeyword())){
                data =data+"&keyword="+searchDO.getKeyword();
            }
            if (StringUtils.isNotBlank(searchDO.getCatId())){
                data =data+"&catId="+searchDO.getCatId();
            }
            if (StringUtils.isNotBlank(searchDO.getPageIndex())){
                data =data+"&pageIndex="+searchDO.getPageIndex();
            }
            if (StringUtils.isNotBlank(searchDO.getPageSize())){
                data =data+"&pageSize="+searchDO.getPageSize();
            }
            if (StringUtils.isNotBlank(searchDO.getMin())){
                data =data+"&min="+searchDO.getMin();
            }
            if (StringUtils.isNotBlank(searchDO.getMax())){
                data =data+"&max="+searchDO.getMax();
            }
            if (StringUtils.isNotBlank(searchDO.getBrands())){
                data =data+"&brands="+searchDO.getBrands();
            }
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "商品搜索失败";
        }catch (Exception e){
            return "商品搜索异常";
        }
    }

    @Override
    public String getYanbaoSku(String token, String skuIds, int province, int city, int county, int town) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/product/getYanbaoSku";
            String data ="token="+token+"&skuIds="+skuIds+"&province="+province+"&city="+city+"&county="+county+"&town="+town;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "查询商品延保失败";
        }catch (Exception e){
            return "查询商品延保异常";
        }
    }


    @Override
    public String getProvince(String token){
        try{
            String url = jdBaseDO.getJdurl()+"/api/area/getProvince";
            String data ="token="+token;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "获取一级地址失败";
        }catch (Exception e){
            return "获取一级地址异常";
        }
    }

    @Override
    public String getCity(String token, String id) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/area/getCity";
            String data ="token="+token+"&id="+id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "获取二级地址失败";
        }catch (Exception e){
            return "获取二级地址异常";
        }
    }

    @Override
    public String getCounty(String token, String id) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/area/getCounty";
            String data ="token="+token+"&id="+id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "获取三级地址失败";
        }catch (Exception e){
            return "获取三级地址异常";
        }
    }

    @Override
    public String getTown(String token, String id) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/area/getTown";
            String data ="token="+token+"&id="+id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "获取四级地址失败";
        }catch (Exception e){
            return "获取四级地址异常";
        }
    }

    @Override
    public String checkArea(String token, String provinceId, String cityId, String countyId, String townId) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/area/checkArea";
            String data ="token="+token+"&provinceId="+provinceId+"&cityId="+cityId+"&countyId="+countyId+"&townId="+townId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean result = (Boolean) json.get("success");
            if (result){
                return rev;
            }
            return "检查四级地址失败";
        }catch (Exception e){
            return "检查四级地址异常";
        }
    }

    /**
     * 批量查询商品价格
     * @param token 授权时的access token
     * @param sku 商品编号 支持批量（最高100个）请以，(英文逗号)分割。
     * @return
     * @throws Exception
     */
    @Override
    public List<SellPriceDO> getSellPrice(String token, String sku) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/price/getSellPrice";
            String data ="token="+token+"&sku="+sku;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean state = (Boolean) json.get("success");
            if (!state){
                return null;
            }
            JSONArray result = json.getJSONArray("result");
            Iterator<Object> it = result.iterator();
            List<SellPriceDO> list=new ArrayList<SellPriceDO>();
            while (it.hasNext()) {
                JSONObject ob = (JSONObject) it.next();
                SellPriceDO model = new SellPriceDO();
                if (null!=ob.getString("skuId")){
                    model.setSkuId(ob.getString("skuId"));
                }
                if (null!=ob.getString("price")){
                    model.setPrice(ob.getString("price"));
                }
                if (null!=ob.getString("jdPrice")){
                    model.setJdPrice(ob.getString("jdPrice"));
                }
                if(model!=null){
                    list.add(model);
                }
            }
            return list;
        }catch (Exception e){
            throw new Exception("查询商品价格异常");
        }
    }

    /**
     *批量获取库存接口
     * @param token 授权时的access token
     * @param skuNums 商品和数量
     * @param area 地址
     * @return
     * @throws Exception
     */
    @Override
    public List<StockDO> getNewStockById(String token, String skuNums, String area) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/stock/getNewStockById";
            String data ="token="+token+"&skuNums="+skuNums+"&area="+area;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean state = (Boolean) json.get("success");
            if (!state){
                return null;
            }
            List<StockDO> stockState = getNewStockState(json);

            return stockState;
        }catch (Exception e){
            throw new Exception("查询库存异常");
        }
    }

    private List<StockDO> getNewStockState(JSONObject json) {
        JSONArray result = json.getJSONArray("result");
        Iterator<Object> it = result.iterator();
        List<StockDO> list=new ArrayList<StockDO>();
        while (it.hasNext()) {
            JSONObject ob = (JSONObject) it.next();
            StockDO model = new StockDO();
            if (null!=ob.getString("areaId")){
                model.setArea(ob.getString("areaId"));
            }
            if (null!=ob.getString("stockStateDesc")){
                model.setDesc(ob.getString("stockStateDesc"));
            }
            if (null!=ob.getString("skuId")){
                model.setSku(ob.getString("skuId"));
            }
            if (null!=ob.getString("stockStateId")){
                model.setState(ob.getString("stockStateId"));
            }
            if (null!=ob.getString("remainNum")){
                model.setRemainNum(ob.getString("remainNum"));
            }
            if(model!=null){
                list.add(model);
            }
        }
        return list;
    }

    private List<StockDO> getStockState(JSONObject json) {
        JSONArray result = json.getJSONArray("result");
        Iterator<Object> it = result.iterator();
        List<StockDO> list=new ArrayList<StockDO>();
        while (it.hasNext()) {
            JSONObject ob = (JSONObject) it.next();
            StockDO model = new StockDO();
            if (null!=ob.getString("area")){
                model.setArea(ob.getString("area"));
            }
            if (null!=ob.getString("desc")){
                model.setDesc(ob.getString("desc"));
            }
            if (null!=ob.getString("sku")){
                model.setSku(ob.getString("sku"));
            }
            if (null!=ob.getString("state")){
                model.setState(ob.getString("state"));
            }
            if(model!=null){
                list.add(model);
            }
        }
        return list;
    }

    /**
     * 批量获取库存接口
     * @param token 授权时的access token
     * @param sku 商品编号
     * @param area 地址
     * @return
     * @throws Exception
     */
    @Override
    public List<StockDO> getStockById(String token, String sku, String area) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/stock/getStockById";
            String data ="token="+token+"&sku="+sku+"&area="+area;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean state = (Boolean) json.get("success");
            if (!state){
                return null;
            }
            List<StockDO> stockState = getStockState(json);

            return stockState;
        }catch (Exception e){
            throw new Exception("查询库存异常");
        }
    }

    @Override
    public OrderResultDO submitOrder(String token,OrderDO orderDO) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/order/submitOrder";
            String data ="token="+token+"&thirdOrder="+orderDO.getThirdOrder()+"&sku="+orderDO.getSku()+"&name="+orderDO.getName()+"&province="+orderDO.getProvince()+"&city="+orderDO.getCity()
                    +"&county="+orderDO.getCounty()+"&town="+orderDO.getTown()+"&address="+orderDO.getAddress()+"&mobile="+orderDO.getMobile()+"&email="+orderDO.getEmail()
                    +"&invoiceState="+orderDO.getInvoiceState()+"&invoiceType="+orderDO.getInvoiceType()+"&selectedInvoiceTitle="+orderDO.getSelectedInvoiceTitle()
                    +"&companyName="+orderDO.getCompanyName()+"&invoiceContent="+orderDO.getInvoiceContent()+"&paymentType="+orderDO.getPaymentType();
            if ("4".equals(String.valueOf(orderDO.getPaymentType()))){
                data = data +"&isUseBalance="+1;
            }else {
                data = data +"&isUseBalance="+0;
            }
            data = data+"&submitState="+orderDO.getSubmitState();
            if ("2".equals(String.valueOf(orderDO.getInvoiceType())) && "1".equals(String.valueOf(orderDO.getInvoiceState()))){
                data = data + "&invoiceName="+orderDO.getInvoiceName()+"&invoicePhone="+orderDO.getInvoicePhone()+"&invoiceProvince="+orderDO.getInvoiceProvice()+"&invoiceCity="+orderDO.getInvoiceCity()
                        +"&invoiceCounty="+orderDO.getInvoiceCounty()+"&invoiceAddress="+orderDO.getInvoiceAddress();
            }
            data =data+"&doOrderPriceMode="+1+"&orderPriceSnap="+orderDO.getOrderPriceSnap();

            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean state = (Boolean) json.get("success");
            if (!state){
                return null;
            }
            JSONObject result = json.getJSONObject("result");
            OrderResultDO orderResultDO = new OrderResultDO();
            if (null != result.get("jdOrderId")){
                orderResultDO.setJdOrderId(String.valueOf(result.get("jdOrderId")));
            }
            if (null != result.get("freight")){
                orderResultDO.setFreight(String.valueOf(result.get("freight")));
            }
            JSONArray array = result.getJSONArray("sku");
            if (null != array){
                Iterator<Object> it = array.iterator();
                List<SellPriceDO> list=new ArrayList<SellPriceDO>();
                while (it.hasNext()) {
                    JSONObject ob = (JSONObject) it.next();
                    SellPriceDO model = new SellPriceDO();
                    if (null!=ob.getString("skuId")){
                        model.setSkuId(ob.getString("skuId"));
                    }
                    if (null!=ob.getString("num")){
                        model.setNum(ob.getString("num"));
                    }
                    if (null!=ob.getString("category")){
                        model.setCategory(ob.getString("category"));
                    }
                    if (null!=ob.getString("price")){
                        model.setPrice(ob.getString("price"));
                    }
                    if (null!=ob.getString("name")){
                        model.setName(ob.getString("name"));
                    }
                    if (null!=ob.getString("tax")){
                        model.setTax(ob.getString("tax"));
                    }
                    if (null!=ob.getString("taxPrice")){
                        model.setTaxPrice(ob.getString("taxPrice"));
                    }
                    if (null!=ob.getString("nakedPrice")){
                        model.setNakedPrice(ob.getString("nakedPrice"));
                    }
                    if (null!=ob.getString("type")){
                        model.setType(ob.getString("type"));
                    }
                    if (null!=ob.getString("oid")){
                        model.setOid(ob.getString("oid"));
                    }
                    if (null!=ob.getString("remoteRegionFreight")){
                        model.setRemoteRegionFreight(ob.getString("remoteRegionFreight"));
                    }
                    if(model!=null){
                        list.add(model);
                    }
                }
                orderResultDO.setSku(list);
            }
            if (null != result.get("orderPrice")){
                orderResultDO.setOrderPrice(String.valueOf(result.get("orderPrice")));
            }
            if (null != result.get("orderNakedPrice")){
                orderResultDO.setOrderNakedPrice(String.valueOf(result.get("orderNakedPrice")));
            }
            if (null != result.get("orderTaxPrice")){
                orderResultDO.setOrderTaxPrice(String.valueOf(result.get("orderTaxPrice")));
            }
            return orderResultDO;
        }catch (Exception e){
            throw new Exception("下单存在异常");
        }
    }

    @Override
    public String confirmOrder(String token, String jdOrderId) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/order/confirmOrder";
            String data ="token="+token+"&jdOrderId="+jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean state = (Boolean) json.get("result");
            if (!state){
                return "确认预占库存订单失败";
            }
            return "确认预占库存订单成功";
        }catch (Exception e){
            throw new Exception("取消订单异常");
        }
    }

    @Override
    public String cancel(String token, String jdOrderId) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/order/cancel";
            String data ="token="+token+"&jdOrderId="+jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean state = (Boolean) json.get("result");
            if (!state){
                return "取消订单失败";
            }
            return "取消订单成功";
        }catch (Exception e){
            throw new Exception("取消订单异常");
        }
    }

    @Override
    public String doPay(String token, String jdOrderId) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/order/doPay";
            String data ="token="+token+"&jdOrderId="+jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean state = (Boolean) json.get("result");
            if (!state){
                return "发起支付失败";
            }
            return "发起支付成功";
        }catch (Exception e){
            throw new Exception("发起支付异常");
        }
    }

    @Override
    public String selectJdOrderIdByThirdOrder(String token, String thirdOrder) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/order/selectJdOrderIdByThirdOrder";
            String data ="token="+token+"&thirdOrder="+thirdOrder;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean state = (Boolean) json.get("result");
            if (!state){
                return "订单反查失败";
            }
            return "订单反查成功";
        }catch (Exception e){
            throw new Exception("订单反查异常");
        }
    }


    @Override
    public String selectJdOrder(String token, String jdOrderId) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/order/selectJdOrderIdByThirdOrder";
            String data ="token="+token+"&jdOrderId="+jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean state = (Boolean) json.get("result");
            if (!state){
                return "查询京东订单信息失败";
            }
            return "查询京东订单信息成功";
        }catch (Exception e){
            throw new Exception("查询订单信息异常");
        }
    }

    @Override
    public String orderTrack(String token, String jdOrderId) throws Exception {
        try{
            String url = jdBaseDO.getJdurl()+"/api/order/orderTrack";
            String data ="token="+token+"&jdOrderId="+jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json=JSONObject.parseObject(rev);
            Boolean state = (Boolean) json.get("result");
            if (!state){
                return "查询京东订单信息失败";
            }
            return "查询京东订单信息成功";
        }catch (Exception e){
            throw new Exception("查询订单信息异常");
        }
    }




}
