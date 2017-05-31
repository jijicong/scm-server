package org.trc.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.impl.jingdong.util.JingDongUtil;
import org.trc.biz.impl.jingdong.util.Model.AddressDO;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.domain.config.TableMappingDO;
import org.trc.form.JDModel.OrderDO;
import org.trc.form.JDModel.OrderResultDO;
import org.trc.mapper.config.ITableMappingMapper;
import org.trc.util.RedisUtil;

import java.math.BigDecimal;
import java.util.Set;

/**
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
        try{
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
        }catch (Exception e){
            System.out.print(e);
        }

    }
    //@Test
    public void testTime() {
        try{
            RedisUtil.setObject("泰然城","trc",50);
            RedisUtil.getObject("泰然城");
            /*JingDongBizImpl.refreshToken();*/
            System.out.print("结束");
        }catch (Exception e){
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
        try{
            String token = iJingDongBiz.getAccessToken();
            String province = ijdService.getProvince(token);
            System.out.println("province:"+province);
            String city = ijdService.getCity(token, "1");
            System.out.println("city:"+city);
            /*String city = ijdService.getCity(token, String.valueOf(list.get(str)));*/
            System.out.print("结束");
        }catch (Exception e){
            System.out.print(e);
        }

    }
    @Test
    public void testGetAddress(){
        try{
            String str = iJingDongBiz.getAddress("520112","520115","520102");
            System.out.print("结束");
        }catch (Exception e){
            System.out.print(e);
        }
    }
    @Test
    public void testStock(){
        try{
            AddressDO addressDO = new AddressDO();
            addressDO.setProvince("520112");
            addressDO.setCity("520115");
            addressDO.setCounty("520102");
            iJingDongBiz.getStockById("2350848", addressDO);
            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("skuId","2350848");
            object.put("num","100");
            array.add(object);
            iJingDongBiz.getNewStockById(array, addressDO);
            System.out.print("结束");
        }catch (Exception e){
            System.out.print(e);
        }
    }
    @Test
    public void testSellPrice(){
        try{
            iJingDongBiz.getSellPrice("2350848");
            System.out.print("结束");
        }catch (Exception e){
            System.out.print(e);
        }
    }
    @Test
    public void testSkuState(){
        try{
            String token = iJingDongBiz.getAccessToken();
            ijdService.skuState(token,"2350848");
            System.out.print("结束");
        }catch (Exception e){
            System.out.print(e);
        }
    }
    @Test
    public void testOrder(){
        try{
            String token = iJingDongBiz.getAccessToken();
            OrderDO orderDO = new OrderDO();
            JSONArray sku = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("thirdOrder",124565);
            obj.put("skuId","3553567");
            obj.put("num",1);
            obj.put("bNeedAnnex",true);
            obj.put("bNeedGift",false);
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
            ijdService.getTown(token,"21037");
            orderDO.setTown(51695);
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
            json.put("skuId",Long.parseLong("3553567"));
            jsonArray.add(json);
            orderDO.setOrderPriceSnap(jsonArray.toJSONString());
            OrderResultDO tmp = iJingDongBiz.billOrder(orderDO);
            System.out.print(tmp.toString());
        }catch (Exception e){
            System.out.print(e);
        }
    }

    @Test
    public void testCancelOrder() {
        try {
            String token = iJingDongBiz.getAccessToken();
            ijdService.cancel(token, "57494109359");

        } catch (Exception e) {
            System.out.print(e);
        }
    }


}

