package org.trc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.JingDongEnum;
import org.trc.enums.SuccessFailureEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.GoodsException;
import org.trc.form.JDModel.*;
import org.trc.form.SupplyItemsExt;
import org.trc.form.liangyou.LiangYouOrder;
import org.trc.service.IJDService;
import org.trc.util.*;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
    @Autowired
    private ExternalSupplierConfig externalSupplierConfig;
    //接口调用超时时间
    public final static Integer TIME_OUT = 10000;
    //接口重试次数
    public final static Integer RETRY_TIMES = 3;

    @Override
    public ReturnTypeDO createToken() throws Exception {
        try {
            String timestamp = DateUtils.formatDateTime(Calendar.getInstance().getTime());
            String sign = jdBaseDO.getClient_secret() + timestamp + jdBaseDO.getClient_id()
                    + jdBaseDO.getUsername() + jdBaseDO.getPassword() + jdBaseDO.getGrant_type() + jdBaseDO.getClient_secret();
            sign = EncryptionUtil.encryption(sign, "MD5", "UTF-8").toUpperCase();
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
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_TOKEN.getCode(), JingDongEnum.ERROR_TOKEN.getMessage(), null);
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
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_REFRESH_TOKEN.getCode(), JingDongEnum.ERROR_REFRESH_TOKEN.getMessage(), null);
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
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_PAGE_NUM.getCode(), JingDongEnum.ERROR_GET_PAGE_NUM.getMessage(), null);
        }
    }

    /**
     * 获取商品编号
     *
     * @param token   授权时的access token
     * @param pageNum 池子编号
     * @return
     */
    public ReturnTypeDO getSku(String token, String pageNum) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_SKU;
            String data = "token=" + token + "&pageNum=" + pageNum;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            String result = (String) json.get("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_SKU.getCode(), JingDongEnum.ERROR_GET_SKU.getMessage(), null);
        }
    }

    private ReturnTypeDO returnValue(Boolean success, String resultCode, String resultMessage, Object result) {
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
    public ReturnTypeDO getDetail(String token, String sku, Boolean isShow) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_DETAIL;
            String data = "token=" + token + "&sku=" + sku + "&isShow=" + isShow;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_DETAIL.getCode(), JingDongEnum.ERROR_GET_DETAIL.getMessage(), null);
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
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_SKU_BY_PAGE.getCode(), JingDongEnum.ERROR_GET_SKU_BY_PAGE.getMessage(), null);
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
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_CHECK_SKU.getCode(), JingDongEnum.ERROR_CHECK_SKU.getMessage(), null);
        }
    }

    /**
     * 获取商品上下架状态
     *
     * @param token 授权时的access token
     * @param sku   商品编号 支持批量（最高100个）
     * @return
     */
    public ReturnTypeDO skuState(String token, String sku) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SKU_STATE;
            String data = "token=" + token + "&sku=" + sku;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_SKU_STATE.getCode(), JingDongEnum.ERROR_SKU_STATE.getMessage(), null);
        }
    }

    /**
     * 获取商品图片信息
     *
     * @param token 授权时的access token
     * @param sku   商品编号 支持批量（最高100个）
     * @return
     */
    @Override
    public ReturnTypeDO skuImage(String token, String sku) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SKU_IMAGE;
            String data = "token=" + token + "&sku=" + sku;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_SKU_IMAGE.getCode(), JingDongEnum.ERROR_SKU_IMAGE.getMessage(), null);
        }
    }

    /**
     * 商品区域购买限制查询
     *
     * @param token    access token
     * @param skuIds   商品编号
     * @param province 京东一级地址编号
     * @param city     京东二级地址编号
     * @param county   京东三级地址编号
     * @param town     京东四级地址编号
     * @return
     * @throws Exception
     */
    @Override
    public ReturnTypeDO checkAreaLimit(String token, String skuIds, String province, String city, String county, String town) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CHECK_AREA_LIMIT;
            String data = "token=" + token + "&skuIds=" + skuIds + "&province=" + province + "&city=" + city + "&county=" + county + "&town=" + town;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_CHECK_LIMIT.getCode(), JingDongEnum.ERROR_CHECK_LIMIT.getMessage(), null);
        }
    }

    /**
     * 商品搜索
     * 关键字+分页+页码，使用价格区间、品牌首字母、分类作为筛选条件
     *
     * @param searchDO
     * @return
     */
    public ReturnTypeDO search(SearchDO searchDO) throws Exception {
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
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_SEARCH.getCode(), JingDongEnum.ERROR_SEARCH.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO getYanbaoSku(String token, String skuIds, int province, int city, int county, int town) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_YANBAO_SKU;
            String data = "token=" + token + "&skuIds=" + skuIds + "&province=" + province + "&city=" + city + "&county=" + county + "&town=" + town;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_YANBAO_SKU.getCode(), JingDongEnum.ERROR_GET_YANBAO_SKU.getMessage(), null);
        }
    }


    @Override
    public ReturnTypeDO getProvince(String token) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_PROVINCE;
            String data = "token=" + token;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_PROVINCE.getCode(), JingDongEnum.ERROR_GET_PROVINCE.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO getCity(String token, String id) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_CITY;
            String data = "token=" + token + "&id=" + id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_CITY.getCode(), JingDongEnum.ERROR_GET_CITY.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO getCounty(String token, String id) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_COUNTY;
            String data = "token=" + token + "&id=" + id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_COUNTY.getCode(), JingDongEnum.ERROR_GET_COUNTY.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO getTown(String token, String id) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_TOWN;
            String data = "token=" + token + "&id=" + id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_TOWN.getCode(), JingDongEnum.ERROR_GET_TOWN.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO checkArea(String token, String provinceId, String cityId, String countyId, String townId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CHECK_AREA;
            String data = "token=" + token + "&provinceId=" + provinceId + "&cityId=" + cityId + "&countyId=" + countyId + "&townId=" + townId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            String result = (String) json.get("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_CHECK_AREA.getCode(), JingDongEnum.ERROR_CHECK_AREA.getMessage(), null);
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
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_SELL_PRICE.getCode(), JingDongEnum.ERROR_GET_SELL_PRICE.getMessage(), null);
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
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_NEW_STOCK_BY_ID.getCode(), JingDongEnum.ERROR_GET_NEW_STOCK_BY_ID.getMessage(), null);
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
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_GET_STOCK_BY_ID.getCode(), JingDongEnum.ERROR_GET_STOCK_BY_ID.getMessage(), null);
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
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_ORDER_BILL.getCode(), JingDongEnum.ERROR_ORDER_BILL.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO confirmOrder(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CONFIRM_ORDER;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            Boolean result = (Boolean) json.get("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_ORDER_CONFIRM.getCode(), JingDongEnum.ERROR_ORDER_CONFIRM.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO cancel(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CANCEL;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            Boolean result = (Boolean) json.get("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_ORDER_CANCEL.getCode(), JingDongEnum.ERROR_ORDER_CANCEL.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO doPay(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.DO_PAY;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            Boolean result = (Boolean) json.get("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_DO_PAY.getCode(), JingDongEnum.ERROR_DO_PAY.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO selectJdOrderIdByThirdOrder(String token, String thirdOrder) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SELECT_JDORDER_ID_BY_THIRD_ORDER;
            String data = "token=" + token + "&thirdOrder=" + thirdOrder;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            String result = (String) json.get("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_SELECT_JDORDERID_BY_THIRDORDER.getCode(), JingDongEnum.ERROR_SELECT_JDORDERID_BY_THIRDORDER.getMessage(), null);
        }
    }


    @Override
    public ReturnTypeDO selectJdOrder(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SELECT_JDORDER;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_SELECT_JDORDER.getCode(), JingDongEnum.ERROR_SELECT_JDORDER.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO orderTrack(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.ORDER_TRACK;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONObject result = json.getJSONObject("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_ORDER_TRACK.getCode(), JingDongEnum.ERROR_ORDER_TRACK.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO get(String token, String type) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.MESSAGE_GET;
            String data = null;
            if (StringUtils.isBlank(type)) {
                data = "token=" + token;
            } else {
                data = "token=" + token + "&type=" + type;
            }
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            JSONArray result = json.getJSONArray("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_MESSAGE_GET.getCode(), JingDongEnum.ERROR_MESSAGE_GET.getMessage(), null);
        }
    }

    @Override
    public ReturnTypeDO del(String token, String id) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.MESSAGE_DEL;
            String data = "token=" + token + "&id=" + id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            JSONObject json = JSONObject.parseObject(rev);
            Boolean success = (Boolean) json.get("success");
            String resultCode = (String) json.get("resultCode");
            String resultMessage = (String) json.get("resultMessage");
            Boolean result = (Boolean)json.get("result");
            return returnValue(success, resultCode, resultMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return returnValue(false, JingDongEnum.ERROR_MESSAGE_DEL.getCode(), JingDongEnum.ERROR_MESSAGE_DEL.getMessage(), null);
        }
    }


    @Override
    public ReturnTypeDO<Pagenation<SupplyItemsExt>> skuPage(SupplyItemsExt form, Pagenation<SupplyItemsExt> page) throws Exception {
        AssertUtil.notNull(page.getPageNo(), "分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(), "分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(), "分页查询参数start不能为空");
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(page);
        map.putAll(BeanToMapUtil.convertBeanToMap(form));
        ReturnTypeDO<Pagenation<SupplyItemsExt>> returnTypeDO = new ReturnTypeDO<Pagenation<SupplyItemsExt>>();
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getSkuPageUrl();
            response = HttpClientUtil.httpGetRequest(url, map);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    page = jbo.getJSONObject("result").toJavaObject(Pagenation.class);
                    List<SupplyItemsExt> supplyItemsExtList = new ArrayList<SupplyItemsExt>();
                    for(Object obj: page.getResult()){
                        JSONObject bo = (JSONObject)obj;
                        SupplyItemsExt supplyItemsExt = (SupplyItemsExt)bo.toJavaObject(SupplyItemsExt.class);
                        supplyItemsExt.setSupplierPrice(new Double(CommonUtil.getMoneyYuan(supplyItemsExt.getSupplierPrice())));
                        supplyItemsExt.setSupplyPrice(new Double(CommonUtil.getMoneyYuan(supplyItemsExt.getSupplyPrice())));
                        supplyItemsExt.setMarketPrice(new Double(CommonUtil.getMoneyYuan(supplyItemsExt.getMarketPrice())));
                        supplyItemsExt.setSkuName(bo.getString("name"));
                        supplyItemsExt.setBrand(bo.getString("brandName"));
                        supplyItemsExtList.add(supplyItemsExt);
                    }
                    page.setResult(supplyItemsExtList);
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(page);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用外部供应商商品查询接口返回结果为空");
            }
        }catch (IOException e){
            String msg = String.format("调用京东商品查询服务网络超时,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage("调用京东商品查询服务网络超时");
        } catch (Exception e){
            String msg = String.format("调用外部供应商商品查询接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }

    @Override
    public ReturnTypeDO noticeUpdateSkuUsedStatus(List<SkuDO> skuDOList) {
        AssertUtil.notEmpty(skuDOList, "调用外部供应商商品使用状态更新参数skuDOList不能为空");
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("skus", JSONArray.toJSON(skuDOList));
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getSkuAddNotice();
            response = HttpClientUtil.httpPostRequest(url, map, TIME_OUT);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    returnTypeDO.setSuccess(true);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用外部供应商品使用状态更新接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用外部供应商商品使用状态更新接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }

    @Override
    public ResponseAck submitJingDongOrder(JingDongOrder jingDongOrder) {
        AssertUtil.notNull(jingDongOrder, "提交京东订单参数不能为空");
        String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdSubmitOrderUrl();
        return invokeSubmitOrder(url, JSON.toJSON(jingDongOrder).toString());
    }

    @Override
    public ResponseAck submitLiangYouOrder(LiangYouOrder liangYouOrder) {
        AssertUtil.notNull(liangYouOrder, "提交粮油订单参数不能为空");
        String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getLySubmitOrderUrl();
        return invokeSubmitOrder(url, JSON.toJSON(liangYouOrder).toString());
    }

    @Override
    public ReturnTypeDO getLogisticsInfo(String warehouseOrderCode, String flag) {
        AssertUtil.notBlank(warehouseOrderCode, "查询代发供应商订单物流信息参数仓库订单编码不能为空");
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(false);
        String url = "";
        String response = null;
        try{
            url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getOrderLogisticsUrl()+"/"+warehouseOrderCode+"/"+flag;
            log.debug("开始调用物流查询" + url + ", 参数：warehouseOrderCode=" + warehouseOrderCode + "flag="+flag+". 开始时间" +
                    DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
            response = HttpClientUtil.httpGetRequest(url);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(appResult.getResult());
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用物流查询服务返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用物流查询服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        log.debug("结束调用物流查询" + url + ", 返回结果：" + JSONObject.toJSON(returnTypeDO) + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));

        return returnTypeDO;
    }

    @Override
    public ReturnTypeDO getSellPrice(String skus) {
        String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdSkuPriceUrl();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("skus", skus);
        log.debug("开始调用京东sku价格查询服务" + url + ", 参数：" + map + ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            response = HttpClientUtil.httpPostRequest(url, map, TIME_OUT);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(appResult.getResult());
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用京东sku价格查询服务返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用京东sku价格查询服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        log.debug("结束调用京东sku价格查询服务" + url + ", 返回结果：" + JSONObject.toJSON(returnTypeDO) + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        return returnTypeDO;
    }


    private ResponseAck invokeSubmitOrder(String url, String jsonParams){
        ResponseAck responseAck = null;
        log.debug("开始调用提交订单服务" + url + ", 参数：" + jsonParams + ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        String response = null;
        try{
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE,"text/plain; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            response = HttpClientUtil.httpPostJsonRequest(url, jsonParams, httpPost, TIME_OUT);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                responseAck = jbo.toJavaObject(ResponseAck.class);
            }else {
                responseAck = new ResponseAck(ExceptionEnum.SYSTEM_BUSY, "");
            }
        }catch (IOException e){
            String msg = String.format("调用提交订单服务网络超时,错误信息:%s", e.getMessage());
            log.error(msg, e);
            responseAck = new ResponseAck(ExceptionEnum.REMOTE_INVOKE_TIMEOUT_EXCEPTION, "");
        }catch (Exception e){
            String msg = String.format("调用提交订单服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            responseAck = new ResponseAck(ExceptionEnum.SYSTEM_EXCEPTION, "");
        }
        log.debug("结束调用提交订单服务" + url + ", 返回结果：" + JSONObject.toJSON(responseAck) + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        return responseAck;
    }

    public ReturnTypeDO checkBalanceDetail(BalanceDetailDO queryModel, Pagenation<JdBalanceDetail> page){
        AssertUtil.notNull(page.getPageNo(), "分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(), "分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(), "分页查询参数start不能为空");
        Map<String, Object> map = BeanToMapUtil.convertBeanToMap(page);
        map.putAll(BeanToMapUtil.convertBeanToMap(queryModel));
        ReturnTypeDO<Pagenation<JdBalanceDetail>> returnTypeDO = new ReturnTypeDO<Pagenation<JdBalanceDetail>>();
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getCheckOrderDetailUrl();
            response = HttpClientUtil.httpGetRequest(url, map);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    page = jbo.getJSONObject("result").toJavaObject(Pagenation.class);
                    List<JdBalanceDetail> balanceDetails = new ArrayList<JdBalanceDetail>();
                    for(Object obj: page.getResult()){
                        JSONObject bo = (JSONObject)obj;
                        JdBalanceDetail detail = (JdBalanceDetail)bo.toJavaObject(JdBalanceDetail.class);
                        detail.setAmount(new Double(CommonUtil.getMoneyYuan(detail.getAmount())));
                        balanceDetails.add(detail);
                    }
                    page.setResult(balanceDetails);
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(page);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用对账明细接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用对账明细服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }

    public ReturnTypeDO getAllTreadType(){
        ReturnTypeDO<List> returnTypeDO = new ReturnTypeDO<List>();
        returnTypeDO.setSuccess(false);
        String response = null;
        try{
            String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getTreadTypeUrl();
            response = HttpClientUtil.httpGetRequest(url);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    JSONArray page = jbo.getJSONArray("result");
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(page);
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用查询业务类型接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用查询业务类型接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        return returnTypeDO;
    }

    @Override
    public ReturnTypeDO getJingDongArea() {
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(false);
        String url = externalSupplierConfig.getScmExternalUrl()+externalSupplierConfig.getJdAddressUrl();
        log.debug("开始调用京东区域查询服务" + url + ", 参数：无. 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        String response = null;
        try{
            response = HttpClientUtil.httpGetRequest(url);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                if(StringUtils.equals(jbo.getString("appcode"), ZeroToNineEnum.ONE.getCode())){
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(jbo.getString("result"));
                }
                returnTypeDO.setResultMessage(jbo.getString("databuffer"));
                returnTypeDO.setResultCode(jbo.getString("resultCode"));
            }else {
                returnTypeDO.setResultMessage("调用京东区域查询服务返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用京东区域查询服务异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        log.debug("结束调用京东区域查询服务" + url + ", 返回结果：" + JSONObject.toJSON(returnTypeDO) + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        return returnTypeDO;
    }

}
