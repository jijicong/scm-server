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
public class JDServiceImpl implements IJDService {

    @Autowired
    private JdBaseDO jdBaseDO;

    @Override
    public String createToken() {
        try {
            String timestamp = DateUtils.formatDateTime(new Date());
            String sign = jdBaseDO.getClient_secret() + timestamp + jdBaseDO.getClient_id()
                    + jdBaseDO.getUsername() + jdBaseDO.getPassword() + jdBaseDO.getGrant_type() + jdBaseDO.getClient_secret();
            sign = MD5.encryption(sign).toUpperCase();
            String url = jdBaseDO.getJdurl() + JingDongConstant.ACCESS_TOKEN;
            String data =
                    "grant_type=access_token" +
                            "&client_id=" + jdBaseDO.getClient_id() +
                            "&username=" + URLEncoder.encode(jdBaseDO.getUsername(), "utf-8") +
                            "&password=" + jdBaseDO.getPassword() +
                            "&timestamp=" + timestamp +
                            "&sign=" + sign;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_TOKEN;
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
    public String freshAccessTokenByRefreshToken(String refreshToken) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.REFRESH_TOKEN;
            String data = "refresh_token=" + refreshToken +
                    "&client_id=" + jdBaseDO.getClient_id() +
                    "&client_secret=" + jdBaseDO.getClient_secret();
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_REFRESH_TOKEN;
        }
    }

    /**
     * 获取商品池编号
     *
     * @param token 授权时的access token
     * @return
     */
    public String getPageNum(String token) {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_PAGE_NUM;
            String data = "token=" + token;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_PAGE_NUM;
        }
    }

    /**
     * 获取商品编号
     *
     * @param token   授权时的access token
     * @param pageNum 池子编号
     * @return
     */
    public String getSku(String token, String pageNum) {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_SKU;
            String data = "token=" + token + "&pageNum=" + pageNum;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_SKU;
        }
    }


    /**
     * 获取商品的详细信息
     *
     * @param token  授权时的access token
     * @param sku    商品编号
     * @param isShow 查询商品基本信息
     * @return
     */
    public String getDetail(String token, String sku, Boolean isShow) {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_DETAIL;
            String data = "token=" + token + "&sku=" + sku + "&isShow=" + isShow;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_DETAIL;
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
    public String getSkuByPage(String token, String pageNum, String pageNo) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_SKU_BY_PAGE;
            String data = "token=" + token + "&pageNum=" + pageNum + "&pageNo=" + pageNo;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_SKU_BY_PAGE;
        }
    }

    @Override
    public String checkSku(String token, String skuIds) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CHECK;
            String data = "token=" + token + "&skuIds=" + skuIds;
//            data = data.substring(0,data.length()-1);
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
//            System.out.println("data:"+data);
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_CHECK_SKU;
        }
    }

    /**
     * 获取商品上下架状态
     *
     * @param token 授权时的access token
     * @param sku   商品编号 支持批量（最高100个）
     * @return
     */
    public String skuState(String token, String sku) {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SKU_STATE;
            String data = "token=" + token + "&sku=" + sku;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_SKU_STATE;
        }
    }

    /**
     * 获取商品图片信息
     *
     * @param token 授权时的access token
     * @param sku   商品编号 支持批量（最高100个）
     * @return
     */
    public String skuImage(String token, String sku) {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SKU_IMAGE;
            String data = "token=" + token + "&sku=" + sku;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_SKU_IMAGE;
        }
    }

    /**
     * 商品搜索
     * 关键字+分页+页码，使用价格区间、品牌首字母、分类作为筛选条件
     *
     * @param searchDO
     * @return
     */
    public String search(SearchDO searchDO) {
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
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_SEARCH;
        }
    }

    @Override
    public String getYanbaoSku(String token, String skuIds, int province, int city, int county, int town) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_YANBAO_SKU;
            String data = "token=" + token + "&skuIds=" + skuIds + "&province=" + province + "&city=" + city + "&county=" + county + "&town=" + town;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_YANBAO_SKU;
        }
    }


    @Override
    public String getProvince(String token) {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_PROVINCE;
            String data = "token=" + token;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_PROVINCE;
        }
    }

    @Override
    public String getCity(String token, String id) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_CITY;
            String data = "token=" + token + "&id=" + id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_CITY;
        }
    }

    @Override
    public String getCounty(String token, String id) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_COUNTY;
            String data = "token=" + token + "&id=" + id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_COUNTY;
        }
    }

    @Override
    public String getTown(String token, String id) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_TOWN;
            String data = "token=" + token + "&id=" + id;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_TOWN;
        }
    }

    @Override
    public String checkArea(String token, String provinceId, String cityId, String countyId, String townId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CHECK_AREA;
            String data = "token=" + token + "&provinceId=" + provinceId + "&cityId=" + cityId + "&countyId=" + countyId + "&townId=" + townId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_CHECK_AREA;
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
    public String getSellPrice(String token, String sku) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_SELL_PRICE;
            String data = "token=" + token + "&sku=" + sku;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_SELL_PRICE;
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
    public String getNewStockById(String token, String skuNums, String area) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_NEW_STOCK_BY_ID;
            String data = "token=" + token + "&skuNums=" + skuNums + "&area=" + area;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_NEW_STOCK_BY_ID;
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
    public String getStockById(String token, String sku, String area) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.GET_STOCK_BY_ID;
            String data = "token=" + token + "&sku=" + sku + "&area=" + area;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_GET_STOCK_BY_ID;
        }
    }

    @Override
    public String submitOrder(String token, OrderDO orderDO) throws Exception {
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
            if ("2".equals(String.valueOf(orderDO.getInvoiceType())) && "1".equals(String.valueOf(orderDO.getInvoiceState()))) {
                data = data + "&invoiceName=" + orderDO.getInvoiceName() + "&invoicePhone=" + orderDO.getInvoicePhone() + "&invoiceProvince=" + orderDO.getInvoiceProvice() + "&invoiceCity=" + orderDO.getInvoiceCity()
                        + "&invoiceCounty=" + orderDO.getInvoiceCounty() + "&invoiceAddress=" + orderDO.getInvoiceAddress();
            }
            data = data + "&doOrderPriceMode=" + 1 + "&orderPriceSnap=" + orderDO.getOrderPriceSnap();
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_SUBMIT_ORDER;
        }
    }

    @Override
    public String confirmOrder(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CONFIRM_ORDER;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_CONFIRM_ORDER;
        }
    }

    @Override
    public String cancel(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.CANCEL;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_CANCEL_ORDER;
        }
    }

    @Override
    public String doPay(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.DO_PAY;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_DO_PAY;
        }
    }

    @Override
    public String selectJdOrderIdByThirdOrder(String token, String thirdOrder) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SELECT_JDORDER_ID_BY_THIRD_ORDER;
            String data = "token=" + token + "&thirdOrder=" + thirdOrder;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_SELECT_JDORDERID_BY_THIRDORDER;
        }
    }


    @Override
    public String selectJdOrder(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.SELECT_JDORDER;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_SELECT_JDORDERID;
        }
    }

    @Override
    public String orderTrack(String token, String jdOrderId) throws Exception {
        try {
            String url = jdBaseDO.getJdurl() + JingDongConstant.ORDER_TRACK;
            String data = "token=" + token + "&jdOrderId=" + jdOrderId;
            String rev = HttpRequestUtil.sendHttpsPost(url, data, "utf-8");
            return rev;
        } catch (Exception e) {
            return JingDongConstant.ERROR_ORDER_TRACK;
        }
    }


}
