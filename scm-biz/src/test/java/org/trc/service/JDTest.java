package org.trc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.form.JDModel.*;
import org.trc.form.jingdong.MessageDO;
import org.trc.mapper.config.ITableMappingMapper;
import org.trc.util.JingDongUtil;
import org.trc.form.jingdong.AddressDO;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.config.JingDongSku;
import org.trc.domain.config.JingDongSkuList;
import org.trc.mapper.jingdong.IJingDongMapper;
import org.trc.mapper.jingdong.IJingDongTestMapper;
import org.trc.util.BeanToMapUtil;
import org.trc.util.Pagenation;
import org.trc.util.RedisUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by hzwyz on 2017/5/22 0022.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration(locations = {"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class JDTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    IJingDongBiz iJingDongBiz;
    @Autowired
    JingDongUtil jingDongUtil;
    @Autowired
    IJDService ijdService;
    @Autowired
    private IJingDongMapper jingDongMapper;//商品sku
    @Autowired
    private IJingDongTestMapper jingDongTestMapper;//商品sku
    @Autowired
    ITableMappingMapper a;

    @Test
    public void testGetToken() {
        try {
            String accessToken = iJingDongBiz.getAccessToken();
            System.out.print(accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //@Test
    public void testTime() {
        try {
            RedisUtil.setObject("泰然城", "tairan", 50);
            RedisUtil.getObject("泰然城");
            /*JingDongBizImpl.refreshToken();*/
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }
    }
    //@Test
    public void testSku() {
        try {
            String token = iJingDongBiz.getAccessToken();
            ReturnTypeDO a = ijdService.getPageNum(token);
            Boolean A =a.getSuccess();
            a.getResult();
            /*JingDongBizImpl.refreshToken();*/
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }
    }
    @Test
    public void testOrderTrack() {
        try {
            iJingDongBiz.orderTrack("123456");
            /*JingDongBizImpl.refreshToken();*/
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    /*@Test
    //将京东地址编号导入到本地
    public void testMapping() {
        try{
            String token = iJingDongBiz.getAccessToken();
            ReturnTypeDO province = ijdService.getProvince(token);
            JSONObject list = (JSONObject) province.getResult();
            Set<String> it = list.keySet();

            for (String str:it){
                TableMappingDO address = new TableMappingDO();
                address.setProvince(str);
                address.setJdCode(String.valueOf(list.get(str)));
                a.insert(address);
                ReturnTypeDO city = ijdService.getCity(token, String.valueOf(list.get(str)));
                JSONObject list01 = (JSONObject) city.getResult();
                Set<String> it01 = list01.keySet();
                for (String str01:it01) {
                    TableMappingDO address01 =new TableMappingDO();
                    address01.setProvince(str);
                    address01.setCity(str01);
                    address01.setJdCode(String.valueOf(list01.get(str01)));
                    a.insert(address01);
                    ReturnTypeDO county = ijdService.getCounty(token, String.valueOf(list01.get(str01)));
                    JSONObject list02 = (JSONObject) county.getResult();
                    Set<String> it02 = list02.keySet();
                    for (String str02:it02) {
                        TableMappingDO address02 =new TableMappingDO();
                        address02.setProvince(str);
                        address02.setCity(str01);
                        address02.setDistrict(str02);
                        address02.setJdCode(String.valueOf(list02.get(str02)));
                        a.insert(address02);
                    }
                }
            }
            System.out.print("结束");
        }catch (Exception e){
            System.out.print(e);
        }

    }*/

    //@Test
    public void test01() {
        try {
            String token = iJingDongBiz.getAccessToken();
            ReturnTypeDO province = ijdService.getProvince(token);
            System.out.println("province:" + province);
            ReturnTypeDO city = ijdService.getCity(token, "1");
            System.out.println("city:" + city);
            /*String city = ijdService.getCity(token, String.valueOf(list.get(str)));*/
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }

    }
    @Test
    public void testGetMessage() {
        try {
            List<MessageDO> message = iJingDongBiz.getMessage(null);
            System.out.println("message:" + message);
            for (MessageDO list:message) {
                String id = list.getId();
                Boolean result = iJingDongBiz.delMessage(id);
                System.out.println("result:" + result);
            }
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }

    }
    @Test
    public void testDelMessage() {
        try {
                Boolean result = iJingDongBiz.delMessage("12456");
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }

    }

    //@Test
    public void testGetAddress() {
        try {
            String str = iJingDongBiz.getAddress("520112", "520115", "520102",null);
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    //@Test
    public void testStock() {
        try {
            AddressDO addressDO = new AddressDO();
            addressDO.setProvince("370000");
            addressDO.setCity("371600");
            addressDO.setCounty("371625");
            iJingDongBiz.getStockById("2350848", addressDO);
            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("skuId", "2350848");
            object.put("num", "100");
            array.add(object);
            iJingDongBiz.getNewStockById(array, addressDO);
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    //@Test
    public void testSellPrice() {
        try {
            iJingDongBiz.getSellPrice("2350848");
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    //@Test
    public void testSkuState() {
        try {
            String token = iJingDongBiz.getAccessToken();
            ijdService.skuState(token, "2350848");
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    @Test
    public void testOrder() {
        try {
            String token = iJingDongBiz.getAccessToken();
            OrderDO orderDO = new OrderDO();
            orderDO.setThirdOrder("124565");
            JSONArray sku = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("skuId", "3553567");
            obj.put("num", 1);
            obj.put("bNeedAnnex", true);
            obj.put("bNeedGift", false);
            /*obj.put("price",100);*/
            /*JSONArray tem = new JSONArray();
            JSONObject obj1 = new JSONObject();
            obj1.put("skuId","853342");
            tem.add(obj1);
            obj.put("yanbao",tem);*/
            sku.add(obj);
            orderDO.setSku(sku.toJSONString());
            orderDO.setName("wyz");
            orderDO.setProvince(24);
            orderDO.setCity(2144);
            orderDO.setCounty(21037);
            ijdService.getTown(token, "21037");
            //orderDO.setTown(51695);
            orderDO.setTown(0);
            orderDO.setAddress("浙江省杭州市");
            orderDO.setMobile("13725684578");
            orderDO.setEmail("550670854@qq.com");
            orderDO.setInvoiceState(2);
            orderDO.setInvoiceType(2);
            orderDO.setSelectedInvoiceTitle(5);
            orderDO.setCompanyName("小泰科技");
            orderDO.setInvoiceContent(3);
            orderDO.setPaymentType(1);
            orderDO.setIsUseBalance(0);
            orderDO.setSubmitState(0);
            JSONArray jsonArray = new JSONArray();
            JSONObject json = new JSONObject();
            iJingDongBiz.getSellPrice("3553567");
            json.put("price", BigDecimal.valueOf(599.00));
            json.put("skuId", Long.parseLong("3553567"));
            jsonArray.add(json);
            orderDO.setOrderPriceSnap(jsonArray.toJSONString());
            Map map = BeanToMapUtil.convertBeanToMap(orderDO);
            map.toString();
            String tmp = iJingDongBiz.billOrder(orderDO);

            System.out.print(tmp.toString());
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    //@Test
    public void testCancelOrder() {
        try {
            String token = iJingDongBiz.getAccessToken();
            ijdService.cancel(token, "57494109359");

        } catch (Exception e) {
            System.out.print(e);
        }

    }

    //@Test
    public void getDetail() {
        //获取商品详情
        try {
            List<SellPriceDO> sellPriceDO = iJingDongBiz.getSellPrice("2839521");
            System.out.println(sellPriceDO);

        } catch (Exception e) {
            System.out.print(e);
        }
    }

    @Test
    public void getSkuList() throws Exception {
        //获取Token
        //获取商品池
        String token = iJingDongBiz.getAccessToken();
        //获取商品池,array为商品池集合
        String pageNum = JSONObject.toJSONString(ijdService.getPageNum(token).getResult());
        JSONObject object = JSON.parseObject(pageNum);
        //array解析出来的商品池集合
        JSONArray array = JSONArray.parseArray(object.getString("result"));
        //查询手机品类池
        for (int l = 0; l < array.size(); l++) {


            JSONObject pageNumItemObj = JSONObject.parseObject(array.get(l).toString());
            String pageNumItem = pageNumItemObj.getString("page_num");
            JSONObject skuByPage = (JSONObject) ijdService.getSkuByPage(token, pageNumItem, "1").getResult();
            if (skuByPage.getString("success").equals("false")) {
                System.out.println("pageNum不存在");
            } else {
                JSONObject skuResult = JSON.parseObject(skuByPage.getString("result"));
                int pageCount = Integer.parseInt(skuResult.getString("pageCount"));//品类池的页数
                String skuAll = "";
                for (int i = 1; i <= pageCount; i++) {
                    JSONObject skuPageList = (JSONObject) ijdService.getSkuByPage(token, pageNumItem, i + "").getResult();
                    if (skuPageList.getString("success").equals("true")) {
                        JSONObject skuPageItem = JSON.parseObject(skuPageList.getString("result"));
                        skuAll = removeString(skuAll);
                        skuAll = skuAll + (skuPageItem.getString("skuIds")) + ",";
                    }
                }
                //上架可用的sku,返回的是所有的sku,并未分页
                String skuIds = screenSkuState(token, checkSku(token, skuAll));
                String[] skuid = skuIds.split(SupplyConstants.Symbol.COMMA);
                //筛选出来的Id分组100个一组
                Object[] arry = splitAry(skuid, 100);
                for (int i = 0; i < arry.length; i++) {
                    String a = removeString(Arrays.toString((String[]) arry[i]));
                    List<JingDongSkuList> jingDongSkuLists = new ArrayList<>();
                    List<SellPriceDO> sellPriceDOList = iJingDongBiz.getSellPrice(a);
                    for (SellPriceDO s : sellPriceDOList) {
                        try {
                            JingDongSkuList jingDongSkuList = new JingDongSkuList();
                            jingDongSkuList.setUpdateTime(Calendar.getInstance().getTime());
                            jingDongSkuList.setCreateTime(Calendar.getInstance().getTime());
                            jingDongSkuList.setIsDeleted("0");
                            jingDongSkuList.setIsValid("1");
                            jingDongSkuList.setSku(s.getSkuId());
                            jingDongSkuList.setPurchasePrice(s.getPrice());
                            jingDongSkuList.setMarketPrice(s.getJdPrice());
                            jingDongSkuList.setCreateOperator("隔壁老王");
                            JSONObject skuObj = (JSONObject) ijdService.getDetail(token, s.getSkuId(), false).getResult();
                            JSONObject skuDetail = skuObj.getJSONObject("result");
                            jingDongSkuList.setPageNum(pageNumItem);
                            jingDongSkuList.setName(skuDetail.getString("name"));
                            jingDongSkuList.setUpc(skuDetail.getString("upc"));
                            jingDongSkuLists.add(jingDongSkuList);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                    jingDongMapper.insertList(jingDongSkuLists);
                }
            }


        }
    }


    @Test
    public void getSkuListexecutor() throws Exception {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(6);
        //获取Token
        //获取商品池
        String token = iJingDongBiz.getAccessToken();
        //获取商品池,array为商品池集合
        //String pageNum = ijdService.getPageNum(token);
        //JSONObject object = JSON.parseObject(pageNum);
        //array解析出来的商品池集合
        JSONArray array = (JSONArray) ijdService.getPageNum(token).getResult();
        //查询手机品类池

        for (int l = 0; l < array.size(); l++) {
            int finalL = l;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject pageNumItemObj = JSONObject.parseObject(array.get(finalL).toString());
                        String pageNumItem = pageNumItemObj.getString("page_num");
                        JSONObject skuByPage = (JSONObject) ijdService.getSkuByPage(token, pageNumItem, "1").getResult();
                        if (skuByPage.getString("success").equals("false")) {
                            System.out.println("pageNum不存在");
                        } else {
                            JSONObject skuResult = JSON.parseObject(skuByPage.getString("result"));
                            int pageCount = Integer.parseInt(skuResult.getString("pageCount"));//品类池的页数
                            String skuAll = "";
                            for (int i = 1; i <= pageCount; i++) {
                                JSONObject skuPageList = (JSONObject) ijdService.getSkuByPage(token, pageNumItem, i + "").getResult();
                                if (skuPageList.getString("success").equals("true")) {
                                    JSONObject skuPageItem = JSON.parseObject(skuPageList.getString("result"));
                                    skuAll = removeString(skuAll);
                                    skuAll = skuAll + (skuPageItem.getString("skuIds")) + ",";
                                }
                            }
                            //上架可用的sku,返回的是所有的sku,并未分页
                            String skuIds = screenSkuState(token, checkSku(token, skuAll));
                            String[] skuid = skuIds.split(SupplyConstants.Symbol.COMMA);
                            //筛选出来的Id分组100个一组
                            Object[] arry = splitAry(skuid, 100);
                            for (int i = 0; i < arry.length; i++) {
                                String a = removeString(Arrays.toString((String[]) arry[i]));
                                List<JingDongSku> jingDongSkuLists = new ArrayList<>();
                                List<SellPriceDO> sellPriceDOList = iJingDongBiz.getSellPrice(a);
                                for (SellPriceDO s : sellPriceDOList) {
                                    try {
                                        JingDongSku jingDongSkuList = new JingDongSku();
                                        jingDongSkuList.setUpdateTime(Calendar.getInstance().getTime());
                                        jingDongSkuList.setCreateTime(Calendar.getInstance().getTime());
                                        jingDongSkuList.setIsDeleted("0");
                                        jingDongSkuList.setIsValid("1");
                                        jingDongSkuList.setSku(s.getSkuId());
                                        jingDongSkuList.setPurchasePrice(s.getPrice());
                                        jingDongSkuList.setMarketPrice(s.getJdPrice());
                                        jingDongSkuList.setCreateOperator("隔壁老王");
                                        JSONObject skuObj = (JSONObject)ijdService.getDetail(token, s.getSkuId(), false).getResult();
                                        JSONObject skuDetail = skuObj.getJSONObject("result");
                                        jingDongSkuList.setPageNum(pageNumItem);
                                        jingDongSkuList.setName(skuDetail.getString("name"));
                                        jingDongSkuList.setUpc(skuDetail.getString("upc"));
                                        jingDongSkuLists.add(jingDongSkuList);
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                }
                                jingDongTestMapper.insertList(jingDongSkuLists);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            });
        }
        System.in.read();//加入该代码，让主线程不挂掉
    }
    //验证可用sku
    private String checkSku(String token, String skuAll) throws Exception {

        //将所有的sku 100一组分组
        Object[] arry = splitAry(skuAll.split(SupplyConstants.Symbol.COMMA), 100);
        String okSkuArray = "";
        for (int i = 0; i < arry.length; i++) {
            String a = removeString(Arrays.toString((String[]) arry[i]));
            //验证每一组内sku的可用状态
            ReturnTypeDO okSku = ijdService.checkSku(token, a);
            if (StringUtils.equals(JSONObject.toJSONString(okSku.getSuccess()), "true")) {
                JSONArray okSkuResult = (JSONArray)okSku.getResult();
                for (int j = 0; j < okSkuResult.size(); j++) {
                    JSONObject skuItem = okSkuResult.getJSONObject(j);
                    if (skuItem.getString("saleState").equals("1")) {
                        String okSkuArray2 = "";
                        okSkuArray2 = skuItem.getString("skuId");
                        //返回出所有可用的sku
                        okSkuArray += okSkuArray2 + ",";
                    }
                }

            }
        }
        return okSkuArray;
    }

    //筛选出上架的sku
    private String screenSkuState(String token, String okSkuArray) throws Exception {
        String screenSkuIds = "";
        Object[] arry = splitAry(okSkuArray.split(SupplyConstants.Symbol.COMMA), 100);
        for (int i = 0; i < arry.length; i++) {
            String a = removeString(Arrays.toString((String[]) arry[i]));
            JSONArray screenSku = (JSONArray) ijdService.skuState(token, a).getResult();
            for (int j = 0; j < screenSku.size(); j++) {
                JSONObject skuItem = screenSku.getJSONObject(j);
                if (skuItem.getString("state").equals("1")) {
                    String okSkuArray2 = "";
                    okSkuArray2 = skuItem.getString("sku");
                    //返回出所有可用的sku
                    screenSkuIds += okSkuArray2 + ",";
                }
            }
        }
        return screenSkuIds.substring(0, screenSkuIds.length() - 1);
    }

    //根据筛选出的sku查询详情

    private static Object[] splitAry(String[] ary, int subSize) {
        int count = ary.length % subSize == 0 ? ary.length / subSize : ary.length / subSize + 1;

        List<List<String>> subAryList = new ArrayList<List<String>>();

        for (int i = 0; i < count; i++) {
            int index = i * subSize;

            List<String> list = new ArrayList<String>();
            int j = 0;
            while (j < subSize && index < ary.length) {
                list.add(ary[index++]);
                j++;
            }
            subAryList.add(list);
        }
        Object[] subAry = new Object[subAryList.size()];
        for (int i = 0; i < subAryList.size(); i++) {
            List<String> subList = subAryList.get(i);
            String[] subAryItem = new String[subList.size()];
            for (int j = 0; j < subList.size(); j++) {
                subAryItem[j] = subList.get(j);
            }
            subAry[i] = subAryItem;
        }

        return subAry;

    }

    /**
     * public int indexOf(int ch, int fromIndex)
     * 返回在此字符串中第一次出现指定字符处的索引，从指定的索引开始搜索
     *
     * @param srcText
     * @param findText
     * @return
     */
    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }

    public String removeString(String a) {
        if (a.indexOf("[") >= 0) {
            a = a.replace("[", "");
        }
        if (a.indexOf("]") >= 0) {
            a = a.replace("]", "");
        }
        if (a.indexOf(" ") >= 0) {
            a = a.replace(" ", "");
        }
        return a;
    }

    @Test
    public void testCheckOrder(){
        BalanceDetailDO queryModel = new BalanceDetailDO();
        queryModel.setStartUpdateTime("2017-07-27");
        queryModel.setEndUpdateTime("2017-08-01");
        Pagenation<JdBalanceDetail > page = new Pagenation<>();
        page.setPageNo(1);
        page.setPageSize(10);
        try {
            iJingDongBiz.checkBalanceDetail(queryModel,page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

