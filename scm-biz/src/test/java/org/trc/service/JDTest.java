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
import org.trc.biz.impl.jingdong.util.JingDongUtil;
import org.trc.biz.impl.jingdong.util.Model.AddressDO;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.constants.SupplyConstants;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.JDModel.OrderDO;
import org.trc.form.JDModel.SellPriceDO;
import org.trc.jingdong.JingDongSkuList;
import org.trc.mapper.jingdong.IJingDongMapper;
import org.trc.util.RedisUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/*
 * Created by hzwyz on 2017/5/22 0022.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration(locations = {"classpath:config/dev/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class JDTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    IJingDongBiz iJingDongBiz;
    @Autowired
    JingDongUtil jingDongUtil;
    @Autowired
    IJDService ijdService;

    /* @Autowired
     ITableMappingMapper a;*/
    @Test
    public void testGetToken() {
        try {
            String accessToken = iJingDongBiz.getAccessToken();
            System.out.print(accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJD() {
        try {
            String accessToken = iJingDongBiz.getAccessToken();
           /* JSONObject obj = new JSONObject();
            obj.put("province","浙江");
            obj.put("city","杭州市");
            obj.put("county","滨江区");
            obj.put("town","西兴街道");*/

            /*String str = {{"province":"浙江省","city":"杭州市","county":"县/区","town":"街道"}};*/
            /*String tmp = jingDongUtil.getAddress(obj.toJSONString());*/
            /*"2350848,2374973*/
            /*iJingDongBiz.getStockById("2350848",obj.toJSONString());*/
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }

    }

    //@Test
    public void testTime() {
        try {
            RedisUtil.setObject("泰然城", "trc", 50);
            RedisUtil.getObject("泰然城");
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
            String province = ijdService.getProvince(token);
            JSONObject json = JSONObject.parseObject(province);
            JSONObject list=json.getJSONObject("result");
            Set<String> it = list.keySet();

            for (String str:it){
                TableMappingDO address =new TableMappingDO();
                address.setProvince(str);
                address.setJdCode(String.valueOf(list.get(str)));
                a.insert(address);
                String city = ijdService.getCity(token, String.valueOf(list.get(str)));
                JSONObject json01 = JSONObject.parseObject(city);
                JSONObject list01=json01.getJSONObject("result");
                Set<String> it01 = list01.keySet();
                for (String str01:it01) {
                    TableMappingDO address01 =new TableMappingDO();
                    address01.setProvince(str);
                    address01.setCity(str01);
                    address01.setJdCode(String.valueOf(list01.get(str01)));
                    a.insert(address01);
                    String county = ijdService.getCounty(token, String.valueOf(list01.get(str01)));
                    JSONObject json02 = JSONObject.parseObject(county);
                    JSONObject list02=json02.getJSONObject("result");
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

    @Test
    public void test01() {
        try {
            String token = iJingDongBiz.getAccessToken();
            String province = ijdService.getProvince(token);
            System.out.println("province:" + province);
            String city = ijdService.getCity(token, "1");
            System.out.println("city:" + city);
            /*String city = ijdService.getCity(token, String.valueOf(list.get(str)));*/
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }

    }

    @Test
    public void testGetAddress() {
        try {
            String str = iJingDongBiz.getAddress("520112", "520115", "520102");
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    @Test
    public void testStock() {
        try {
            AddressDO addressDO = new AddressDO();
            addressDO.setProvince("520112");
            addressDO.setCity("520115");
            addressDO.setCounty("520102");
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

    @Test
    public void testSellPrice() {
        try {
            iJingDongBiz.getSellPrice("2350848");
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    @Test
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
            JSONArray sku = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("skuId", "2350848");
            obj.put("num", 200);
            obj.put("bNeedAnnex", true);
            obj.put("bNeedGift", true);
            obj.put("price", 100);
            JSONArray tem = new JSONArray();
            JSONObject obj1 = new JSONObject();
            obj1.put("skuId", "2350848");
            tem.add(obj1);
            obj.put("yanbao", tem);
            sku.add(obj);
            orderDO.setSku(sku.toJSONString());
            orderDO.setName("wyz");
            orderDO.setProvince(24);
            orderDO.setCity(2144);
            orderDO.setCounty(21037);
            orderDO.setTown(0);
            orderDO.setAddress("浙江省杭州市");
            orderDO.setMobile("13725684578");
            orderDO.setEmail("550670854@qq.com");
            orderDO.setInvoiceState(1);
            orderDO.setInvoiceType(1);
            orderDO.setSelectedInvoiceTitle(5);
            orderDO.setCompanyName("小泰科技");
            orderDO.setInvoiceContent(3);
            orderDO.setPaymentType(1);
            orderDO.setIsUseBalance(0);
            orderDO.setSubmitState(1);
            JSONArray jsonArray = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("price", 21.30);
            json.put("skuId", 2350848);
            jsonArray.add(json);
            orderDO.setOrderPriceSnap(jsonArray.toJSONString());
            ijdService.submitOrder(token, orderDO);
            System.out.print("结束");
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    @Test
    public void getPage() {
        try {
            String token = iJingDongBiz.getAccessToken();
            String pageNum = ijdService.getPageNum(token);
            JSONObject object = JSON.parseObject(pageNum);
            JSONArray array = JSONArray.parseArray(object.getString("result"));
            System.out.println("商品池数量:" + array.size());
            for (int i = 0; i < array.size(); i++) {

                JSONObject object1 = array.getJSONObject(i);
                String code = object1.getString("page_num").toString();
                String skus = ijdService.getSkuByPage(token, code, "1");
                JSONObject skuobject = JSON.parseObject(skus);
                if (skuobject.getString("success").equals("false")) {
                    System.out.println((i + 1) + ":" + object1.getString("name") + ":" + "pageNum不存在");

                } else {
                    JSONObject result = JSONObject.parseObject(skuobject.getString("result"));
                    int pageCount = Integer.parseInt(result.getString("pageCount"));
                    Long skuCount = 0L;
                    for (int j = 1; j <= pageCount; j++) {
                        String skuStrings = ijdService.getSkuByPage(token, code, j + "");
                        if (skuobject.getString("success").equals("true")) {
                            JSONObject skuArray = JSON.parseObject(skuStrings);
                            JSONObject result1 = JSONObject.parseObject(skuArray.getString("result"));
                            String skuId = result1.getString("skuIds");
                            String[] skuArray1 = skuId.split(SupplyConstants.Symbol.COMMA);
                            List<String> skuArrayList = resolveSkuArray(skuId, skuArray1);
                            System.out.println(skuArrayList.get(0));
                        }
                    }
                    System.out.println("序号:" + (i + 1) + ",商品池名称:" + object1.getString("name") + ",商品池编号:" + code + ",品类池页数:" + result.getString("pageCount") + ",sku数量:" + skuCount);
//                    String[] skuArray = skuobject.getString("result").split(SupplyConstants.Symbol.COMMA);
//                    for (int j = 34; j < skuArray.length; j++) {
//                        String skuDetailArray = ijdService.getDetail(token, skuArray[j], false);
//                        JSONObject skuDetailObject = JSON.parseObject(skuDetailArray);
//                        if (StringUtils.equals(skuDetailObject.getString("success"), "true")) {
//                            JingDongSkuList jingDongSkuList = new JingDongSkuList();
//                            jingDongSkuList.setName(skuDetailObject.getString("name"));
//                            jingDongSkuList.setSku(skuDetailObject.getString("sku"));
//                            jingDongSkuList.setPageNum("655");
//                            jingDongSkuList.setUpc(skuDetailObject.getString("upc"));
//                            jingDongSkuList.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
//                            jingDongSkuList.setIsValid(ZeroToNineEnum.ONE.getCode());
//                            jingDongSkuList.setCreateOperator("test");
//                            jingDongSkuList.setCreateTime(Calendar.getInstance().getTime());
//                            jingDongSkuList.setUpdateTime(Calendar.getInstance().getTime());
////                            JSONObject priceObject = JSONObject.parseObject(ijdService.getSellPrice(skuDetailObject.getString("sku")).toString());
////                            if (StringUtils.equals(priceObject.getString("success"), "true")) {
////                                JSONArray detail1 = JSON.parseArray(priceObject.getString("result"));
////                                jingDongSkuList.setPurchasePrice(detail1.getJSONObject(0).getString("price"));
////                                jingDongSkuList.setMarketPrice(detail1.getJSONObject(0).getString("jdPrice"));
////                            }
//
//                        }
//
//                    }
                }
            }

        } catch (Exception e) {
            System.out.print(e);
        }

    }

    @Test
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
        String pageNum = ijdService.getPageNum(token);
        JSONObject object = JSON.parseObject(pageNum);
        //array解析出来的商品池集合
        JSONArray array = JSONArray.parseArray(object.getString("result"));
        //查询手机品类池
        JSONObject skuByPage = JSON.parseObject(ijdService.getSkuByPage(token, "655", "1"));
        if (skuByPage.getString("success").equals("false")) {
            System.out.println("pageNum不存在");
        } else {
            JSONObject skuResult = JSON.parseObject(skuByPage.getString("result"));
            int pageCount = Integer.parseInt(skuResult.getString("pageCount"));//品类池的页数
            String skuAll = "";
            for (int i = 1; i <= pageCount; i++) {
                JSONObject skuPageList = JSON.parseObject(ijdService.getSkuByPage(token, "655", i + ""));
                if (skuPageList.getString("success").equals("true")) {
                    JSONObject skuPageItem = JSON.parseObject(skuPageList.getString("result"));
                    skuAll = skuAll.replace("[", "");
                    skuAll = skuAll.replace("]", "");
                    skuAll = skuAll + (skuPageItem.getString("skuIds")) + ",";
                }
            }
            //上架可用的sku,返回的是所有的sku,并未分页
            String skuIds = screenSkuState(token, checkSku(token, skuAll));
            //筛选出来的Id分组100个一组
            Object[] arry = splitAry(skuIds.split(SupplyConstants.Symbol.COMMA), 100);

        }
    }

    //验证可用sku
    private String checkSku(String token, String skuAll) throws Exception {

        //将所有的sku 100一组分组
        Object[] arry = splitAry(skuAll.split(SupplyConstants.Symbol.COMMA), 100);
        String okSkuArray = "";
        for (int i = 0; i < 1; i++) {
            String a = Arrays.toString((String[]) arry[i]);
            a = a.replace("[", "");
            a = a.replace("]", "");
            a = a.replace(" ", "");
            //验证每一组内sku的可用状态
            JSONObject okSku = JSON.parseObject(ijdService.checkSku(token, a));
            if (StringUtils.equals(okSku.getString("success"), "true")) {
                JSONArray okSkuResult = okSku.getJSONArray("result");
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
            String a = Arrays.toString((String[]) arry[i]);
            a = a.replace("[", "");
            a = a.replace("]", "");
            a = a.replace(" ", "");
            JSONArray screenSku = JSON.parseArray(ijdService.skuState(token, a));
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

    //商品池100条为一组,进行分解
    public static List<String> resolveSkuArray(String sku, String[] skuArray) {
        int num = appearNumber(sku, ",");
        List<String> skuStr = new ArrayList<>();
        String s = "";
        if (num < 100) {
            skuStr.add(sku);
        } else if (num >= 100 && num < 200) {
            s = "";
            for (int i = 0; i < 100; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);
            s = "";
            for (int i = 100; i < skuArray.length; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);
        } else if (num >= 200 && num < 300) {
            s = "";
            for (int i = 0; i < 100; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);

            s = "";
            for (int i = 100; i < 200; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);

            s = "";
            for (int i = 200; i < skuArray.length; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);

        } else if (num >= 300 && num < 400) {
            s = "";
            for (int i = 0; i < 100; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);
            s = "";
            for (int i = 100; i < 200; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);
            s = "";
            for (int i = 200; i < 300; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);
            s = "";
            for (int i = 300; i < skuArray.length; i++) {
                s += skuArray[i] + ",";
            }
        } else if (num > 400) {
            s = "";
            for (int i = 0; i < 100; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);
            s = "";
            for (int i = 100; i < 200; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);
            s = "";
            for (int i = 200; i < 300; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);
            s = "";
            for (int i = 300; i < 400; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);
            s = "";
            for (int i = 400; i < skuArray.length; i++) {
                s += skuArray[i] + ",";
            }
            skuStr.add(s);
        }

        return skuStr;
    }
}

