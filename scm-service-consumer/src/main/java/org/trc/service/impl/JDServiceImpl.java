package org.trc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.JDModel.*;
import org.trc.service.IJDService;
import org.trc.util.*;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created by hzwyz on 2017/5/18 0018.
 */
@Service("jDService")
public class JDServiceImpl implements IJDService {
    private final static Logger log = LoggerFactory.getLogger(JDServiceImpl.class);
    @Autowired
    private JdBaseDO jdBaseDO;

    @Override
    public ReturnTypeDO createToken() throws Exception{
        try {
            String timestamp = DateUtils.formatDateTime(Calendar.getInstance().getTime());
            String sign = jdBaseDO.getClient_secret() + timestamp + jdBaseDO.getClient_id()
                    + jdBaseDO.getUsername() + jdBaseDO.getPassword() + jdBaseDO.getGrant_type() + jdBaseDO.getClient_secret();
            sign = EncryptionUtil.encryption(sign,"MD5","UTF-8").toUpperCase();
            String url = jdBaseDO.getJdurl() + JingDongConstant.ACCESS_TOKEN;
            String data =
                    "grant_type=access_token" +
                            "&client_id=" + jdBaseDO.getClient_id() +
                            "&username=" + URLEncoder.encode(jdBaseDO.getUsername(), "utf-8") +
                            "&password=" + jdBaseDO.getPassword() +
                            "&timestamp=" + timestamp +
                            "&sign=" + sign;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_TOKEN);
        }
    }

    /**
     * 使用Refresh Token刷新Access Token
     *
     * @param refreshToken
     * @return
     * @throws Exception
     */
    @Override
    public ReturnTypeDO freshAccessTokenByRefreshToken(String refreshToken) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.REFRESH_TOKEN;
            String data = "refresh_token=" + refreshToken +
                    "&client_id=" + jdBaseDO.getClient_id() +
                    "&client_secret=" + jdBaseDO.getClient_secret();
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_TOKEN);
        }
    }

    /**
     * 获取商品池编号
     *
     * @param token 授权时的access token
     * @return
     */
    public ReturnTypeDO getPageNum(String token) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_PAGE_NUM;
            String data = "token=" + token;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_PAGE_NUM);
        }
    }

    /**
     * 获取商品编号
     *
     * @param token   授权时的access token
     * @param pageNum 池子编号
     * @return
     */
    public ReturnTypeDO getSku(String token, String pageNum) throws Exception{
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_SKU;
            String data = "token=" + token + "&pageNum=" + pageNum;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            String result =(String) json.get("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_SKU);
        }
    }

    private ReturnTypeDO returnValue(Boolean success, String resultCode,String resultMessage,Object result ) {
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(success);
        returnTypeDO.setResultCode(resultCode);
        returnTypeDO.setResult(result);
        returnTypeDO.setResultMessage(resultMessage);
        return returnTypeDO;
    }


    /**
     * 获取商品的详细信息
     *
     * @param token  授权时的access token
     * @param sku    商品编号
     * @param isShow 查询商品基本信息
     * @return
     */
    public ReturnTypeDO getDetail(String token, String sku, Boolean isShow) throws Exception{
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_DETAIL;
            String data = "token=" + token + "&sku=" + sku + "&isShow=" + isShow;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_DETAIL);
        }
    }

    /**
     * 获取池内商品编号接口-品类商品池
     *
     * @param token
     * @param pageNum
     * @param pageNo
     * @return
     * @throws Exception
     */
    @Override
    public ReturnTypeDO getSkuByPage(String token, String pageNum, String pageNo) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_SKU_BY_PAGE;
            String data = "token=" + token + "&pageNum=" + pageNum + "&pageNo=" + pageNo;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_SKU_BY_PAGE);
        }
    }

    @Override
    public ReturnTypeDO checkSku(String token, String skuIds) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CHECK;
            String data = "token=" + token + "&skuIds=" + skuIds;
//            data = data.substring(0,data.length()-1);
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
//            System.out.println("data:"+data);
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_CHECK_SKU);
        }
    }

    /**
     * 获取商品上下架状态
     *
     * @param token 授权时的access token
     * @param sku   商品编号 支持批量（最高100个）
     * @return
     */
    public ReturnTypeDO skuState(String token, String sku) throws Exception{
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SKU_STATE;
            String data = "token=" + token + "&sku=" + sku;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_SKU_STATE);
        }
    }

    /**
     * 获取商品图片信息
     *
     * @param token 授权时的access token
     * @param sku   商品编号 支持批量（最高100个）
     * @return
     */
    public ReturnTypeDO skuImage(String token, String sku) throws Exception{
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SKU_IMAGE;
            String data = "token=" + token + "&sku=" + sku;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_SKU_IMAGE);
        }
    }

    /**
     * 商品搜索
     * 关键字+分页+页码，使用价格区间、品牌首字母、分类作为筛选条件
     *
     * @param searchDO
     * @return
     */
    public ReturnTypeDO search(SearchDO searchDO) throws Exception{
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SEARCH;
            String data = "token=" + searchDO.getToken();
            if (StringUtils.isNotBlank(searchDO.getKeyword())) {
                data = data + "&keyword=" + searchDO.getKeyword();
            }
            if (StringUtils.isNotBlank(searchDO.getCatId())) {
                data = data + "&catId=" + searchDO.getCatId();
            }
            if (StringUtils.isNotBlank(searchDO.getPageIndex())) {
                data = data + "&pageIndex=" + searchDO.getPageIndex();
            }
            if (StringUtils.isNotBlank(searchDO.getPageSize())) {
                data = data + "&pageSize=" + searchDO.getPageSize();
            }
            if (StringUtils.isNotBlank(searchDO.getMin())) {
                data = data + "&min=" + searchDO.getMin();
            }
            if (StringUtils.isNotBlank(searchDO.getMax())) {
                data = data + "&max=" + searchDO.getMax();
            }
            if (StringUtils.isNotBlank(searchDO.getBrands())) {
                data = data + "&brands=" + searchDO.getBrands();
            }
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_SEARCH);
        }
    }

    @Override
    public ReturnTypeDO getYanbaoSku(String token, String skuIds, int province, int city, int county, int town) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_YANBAO_SKU;
            String data = "token=" + token + "&skuIds=" + skuIds + "&province=" + province + "&city=" + city + "&county=" + county + "&town=" + town;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_YANBAO_SKU);
        }
    }


    @Override
    public ReturnTypeDO getProvince(String token) throws Exception{
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_PROVINCE;
            String data = "token=" + token;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_PROVINCE);
        }
    }

    @Override
    public ReturnTypeDO getCity(String token, String id) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_CITY;
            String data = "token=" + token + "&id=" + id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_CITY);
        }
    }

    @Override
    public ReturnTypeDO getCounty(String token, String id) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_COUNTY;
            String data = "token=" + token + "&id=" + id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_COUNTY);
        }
    }

    @Override
    public ReturnTypeDO getTown(String token, String id) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_TOWN;
            String data = "token=" + token + "&id=" + id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_TOWN);
        }
    }

    @Override
    public ReturnTypeDO checkArea(String token, String provinceId, String cityId, String countyId, String townId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CHECK_AREA;
            String data = "token=" + token + "&provinceId=" + provinceId + "&cityId=" + cityId + "&countyId=" + countyId + "&townId=" + townId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            String result = (String) json.get("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_CHECK_AREA);
        }
    }

    /**
     * 批量查询商品价格
     *
     * @param token 授权时的access token
     * @param sku   商品编号 支持批量（最高100个）请以，(英文逗号)分割。
     * @return
     * @throws Exception
     */
    @Override
    public ReturnTypeDO getSellPrice(String token, String sku) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_SELL_PRICE;
            String data = "token=" + token + "&sku=" + sku;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_SELL_PRICE);
        }
    }

    /**
     * 批量获取库存接口
     *
     * @param token   授权时的access token
     * @param skuNums 商品和数量
     * @param area    地址
     * @return
     * @throws Exception
     */
    @Override
    public ReturnTypeDO getNewStockById(String token, String skuNums, String area) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_NEW_STOCK_BY_ID;
            String data = "token=" + token + "&skuNums=" + skuNums + "&area=" + area;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_NEW_STOCK_BY_ID);
        }
    }


    /**
     * 批量获取库存接口
     *
     * @param token 授权时的access token
     * @param sku   商品编号
     * @param area  地址
     * @return
     * @throws Exception
     */
    @Override
    public ReturnTypeDO getStockById(String token, String sku, String area) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_STOCK_BY_ID;
            String data = "token=" + token + "&sku=" + sku + "&area=" + area;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_GET_STOCK_BY_ID);
        }
    }

    @Override
    public ReturnTypeDO submitOrder(String token, OrderDO orderDO) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SUBMIT_ORDER;
            String data = "token=" + token + "&thirdOrder=" + orderDO.getThirdOrder() + "&sku=" + orderDO.getSku() + "&name=" + orderDO.getName() + "&province=" + orderDO.getProvince() + "&city=" + orderDO.getCity()
                    + "&county=" + orderDO.getCounty() + "&town=" + orderDO.getTown() + "&address=" + orderDO.getAddress() + "&mobile=" + orderDO.getMobile() + "&email=" + orderDO.getEmail()
                    + "&invoiceState=" + orderDO.getInvoiceState() + "&invoiceType=" + orderDO.getInvoiceType() + "&selectedInvoiceTitle=" + orderDO.getSelectedInvoiceTitle()
                    + "&companyName=" + orderDO.getCompanyName() + "&invoiceContent=" + orderDO.getInvoiceContent() + "&paymentType=" + orderDO.getPaymentType();
            if ("4".equals(String.valueOf(orderDO.getPaymentType()))) {
                data = data + "&isUseBalance=" + 1;
            } else {
                data = data + "&isUseBalance=" + 0;
            }
            data = data + "&submitState=" + orderDO.getSubmitState();
            if (ZeroToNineEnum.TWO.getCode().equals(String.valueOf(orderDO.getInvoiceType())) && ZeroToNineEnum.ONE.getCode().equals(String.valueOf(orderDO.getInvoiceState()))) {
                data = data + "&invoiceName=" + orderDO.getInvoiceName() + "&invoicePhone=" + orderDO.getInvoicePhone() + "&invoiceProvince=" + orderDO.getInvoiceProvice() + "&invoiceCity=" + orderDO.getInvoiceCity()
                        + "&invoiceCounty=" + orderDO.getInvoiceCounty() + "&invoiceAddress=" + orderDO.getInvoiceAddress();
            }
            data = data + "&doOrderPriceMode=" + 1 + "&orderPriceSnap=" + orderDO.getOrderPriceSnap();
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_SUBMIT_ORDER);
        }
    }

    @Override
    public ReturnTypeDO confirmOrder(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CONFIRM_ORDER;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            Boolean result =(Boolean) json.get("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_CONFIRM_ORDER);
        }
    }

    @Override
    public ReturnTypeDO cancel(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CANCEL;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            Boolean result =(Boolean) json.get("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_CANCEL_ORDER);
        }
    }

    @Override
    public ReturnTypeDO doPay(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.DO_PAY;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            Boolean result =(Boolean) json.get("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_DO_PAY);
        }
    }

    @Override
    public ReturnTypeDO selectJdOrderIdByThirdOrder(String token, String thirdOrder) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SELECT_JDORDER_ID_BY_THIRD_ORDER;
            String data = "token=" + token + "&thirdOrder=" + thirdOrder;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            String result =(String) json.get("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_SELECT_JDORDERID_BY_THIRDORDER);
        }
    }


    @Override
    public ReturnTypeDO selectJdOrder(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SELECT_JDORDER;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_SELECT_JDORDERID);
        }
    }

    @Override
    public ReturnTypeDO orderTrack(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.ORDER_TRACK;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean)json.get("success");
            String resultCode = (String)json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result =json.getJSONObject("result");
            return returnValue(success,resultCode,resultMessage,result);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(JingDongConstant.ERROR_ORDER_TRACK);
        }
    }


}
