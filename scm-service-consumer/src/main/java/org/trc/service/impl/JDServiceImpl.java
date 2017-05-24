package org.trc.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.form.JDModel.JdBaseDO;
import org.trc.form.JDModel.OrderDO;
import org.trc.form.JDModel.SearchDO;
import org.trc.service.IJDService;
import org.trc.util.DateUtils;
import org.trc.util.HttpRequestUtil;
import org.trc.util.MD5;

import java.net.URLEncoder;
import java.util.Date;

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
            if (result){
                return rev;
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
    public String getProvince(String token) throws Exception {
        return null;
    }

    @Override
    public String getCity(String token, String id) throws Exception {
        return null;
    }

    @Override
    public String getCounty(String token, String id) throws Exception {
        return null;
    }

    @Override
    public String getTown(String token, String id) throws Exception {
        return null;
    }

    @Override
    public String checkArea(String token, String provinceId, String cityId, String countyId, String townId) throws Exception {
        return null;
    }

    @Override
    public String getSellPrice(String token, String sku) throws Exception {
        return null;
    }

    @Override
    public String getNewStockById(String token, String skuNums, String area) throws Exception {
        return null;
    }

    @Override
    public String getStockById(String token, String sku, String area) throws Exception {
        return null;
    }

    @Override
    public String submitOrder(OrderDO orderDO) throws Exception {
        return null;
    }

    @Override
    public String confirmOrder(String token, String jdOrderId) throws Exception {
        return null;
    }

    @Override
    public String cancel(String token, String jdOrderId) throws Exception {
        return null;
    }

    @Override
    public String doPay(String token, String jdOrderId) throws Exception {
        return null;
    }

    @Override
    public String selectJdOrder(String token, String jdOrderId) throws Exception {
        return null;
    }

    @Override
    public String orderTrack(String token, String jdOrderId) throws Exception {
        return null;
    }


}
