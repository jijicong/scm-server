package org.trc.dbUnit.category;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.category.ICategoryBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Category;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.TestException;
import org.trc.service.BaseTest;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.DateUtils;
import org.trc.util.HttpClientUtil;
import org.trc.util.ResponseAck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hzqph on 2017/6/21.
 *  * 测试步骤：
 * 1.删除需要用到的表中的干扰数据（测试结束后会回滚）
 * 2.读取xml中的需要初始化的测试数据
 * 3.执行业务中的方法进行测试
 * 4.读取事先准备的预期数据xml并且进行比较
 */
public class categoryDbUnit extends BaseTest {
    private Logger log = LoggerFactory.getLogger(categoryDbUnit.class);
    @Autowired
    private ICategoryBiz iCategoryBiz;

    private static final String TABLE_BRAND = "brand";

    private static final String TABLE_CATEGORY= "category";

    private static final String TABLE_PROPERTY= "property";

    /**
     * 准备需要的初始数据
     * @param conn
     * @param fileName
     * @throws Exception
     */
    /*@Override
    protected void prepareData(IDatabaseConnection conn, String fileName) throws Exception {
        //读取xml文件中的数据信息
        ReplacementDataSet createDataSet = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
        //INSERT TEST DATA
        DatabaseOperation.INSERT.execute(conn, createDataSet);
    }*/


    /**
     * 测试插入操作
     * @throws Exception
     */
    @Test
    public void testInsert() throws Exception {
        AclUserAccreditInfo aclUserAccreditInfo=createAclUserAccreditInfo();
        //删除原数据
        execSql(conn,"delete from category");
        execSql(conn,"delete from serial");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "category/preInsertCategoryData.xml");
        Category category=createCategory();
        iCategoryBiz.saveCategory(category,aclUserAccreditInfo);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("category/expInsertCategoryData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult.addReplacementObject("[null]", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_CATEGORY,"select * from category",expResult,conn);
    }

    /**
     * 测试更新分类启停用状态异常状态
     * ！！！！当准备数据集中第一条数据如果有字段值为空，但是第二条数据该字段值不为空时，
     * 需要将字段多的值往前移动，否则会导致该字段查询结果全都为空值。
     */
    @Test
    public void  testUpdateIsValid()throws Exception{
        //删除原数据
        execSql(conn,"delete from category");
        execSql(conn,"delete from serial");
        execSql(conn,"delete from supplier_category");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "category/preUpdateStatusData.xml");
        //异常流程测试
        Category category=new Category();
        category.setId(1l);
        category.setIsValid(ValidEnum.VALID.getCode());
        AclUserAccreditInfo aclUserAccreditInfo =createAclUserAccreditInfo();
        try{
            iCategoryBiz.updateState(category,aclUserAccreditInfo);
            throw new TestException("测试异常");
        }catch (CategoryException e){
            if(e.getExceptionEnum().equals(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION)){
                log.info("----------分类启停用异常流程测试通过---------");
            }
        }
        //正常流程测试
        category.setId(2l);
        category.setIsValid(ValidEnum.VALID.getCode());
        iCategoryBiz.updateState(category,aclUserAccreditInfo);
        IDataSet expDataSet=getXmlDataSet("category/expUpdateStatusData.xml");
        assertItable(TABLE_CATEGORY,"select * from category where id=2",  expDataSet.getTable("category"),conn);
        assertItable("supplier_category","select * from supplier_category where id=2", expDataSet.getTable("supplier_category"),conn);
        log.info("----------分类启停用正常流程测试通过---------");
        //测试3级启用2级是否会启用
                category.setId(3l);
        category.setIsValid(ValidEnum.NOVALID.getCode());
        iCategoryBiz.updateState(category,aclUserAccreditInfo);
        ReplacementDataSet expResult2 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("category/expUpdateStatusData2.xml"));
        expResult2.addReplacementObject("[null]", null);
        assertDataSet(TABLE_CATEGORY,"select * from category",expResult2,conn);
        log.info("----------分类23级停用，启用第三级，第二级也启用正常流程测试通过---------");
    }

    /**
     * 测试分类关联品牌的正常流程和异常流程
     */
    @Test
    public void testLinkBrand()throws Exception{
        AclUserAccreditInfo aclUserAccreditInfo=createAclUserAccreditInfo();
        //删除原数据
        execSql(conn,"delete from category");
        execSql(conn,"delete from brand");
        execSql(conn,"delete from category_brand");
        prepareData(conn, "category/preLinkBrandData.xml");
        //测试正常流程
        iCategoryBiz.linkCategoryBrands(3l,"2",null,aclUserAccreditInfo);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("category/expLinkBrandData.xml"));
        //测试异常流程假如id为1的brand 停用，这个时候关联会失败
        //从数据库中查出数据与期望结果作比较
        assertDataSet("category_brand","select * from category_brand",expResult,conn);
        log.info("----------分类关联品牌正常流程测试通过---------");
        try{
            iCategoryBiz.linkCategoryBrands(3l,"1",null,aclUserAccreditInfo);
            throw new TestException("测试异常");
        }catch (CategoryException e){
            if(e.getExceptionEnum().equals(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION)){
                 log.info("----------分类关联品牌异常流程测试通过---------");
            }
        }
    }



    /**
     * 从数据库中导出指定表数据到xml文件中
     * @throws Exception
     */
    @Test
    public void exportData() throws Exception {
        List<String> tableNameList = new ArrayList<>();
        tableNameList.add("sku_stock");
        exportData(tableNameList, "preInsertSkuStock.xml");
    }

    /**，
     * init category
     * @return
     */
    private Category createCategory(){
        Category category=new Category();
        category.setName("描述");
        category.setSort(1);
        category.setClassifyDescribe("描述");
        category.setLevel(1);
        category.setIsValid("1");
        category.setCreateOperator("michael");
        return category;
    }

    private AclUserAccreditInfo createAclUserAccreditInfo(){
        AclUserAccreditInfo aclUserAccreditInfo=new AclUserAccreditInfo();
        aclUserAccreditInfo.setChannelCode("QD001");
        aclUserAccreditInfo.setChannelId(1L);
        aclUserAccreditInfo.setChannelName("aaa");
        aclUserAccreditInfo.setId(1L);
        aclUserAccreditInfo.setName("admin");
        aclUserAccreditInfo.setPhone("15757195796");
        aclUserAccreditInfo.setUserId("E2E4BDAD80354EFAB6E70120C271968C");
        aclUserAccreditInfo.setUserType("mixtureUser");
        aclUserAccreditInfo.setIsValid("1");
        aclUserAccreditInfo.setIsDeleted("0");
        return aclUserAccreditInfo;
    }

    @Autowired
    private ISerialUtilService serialUtilService;


    @Test
    public void testRedisLock() throws Exception{
        for(int i=0; i<400; i++){
            /*if(i == 50 || i == 100 || i == 150 || i == 200 || i == 250 || i == 300 || i == 350){
                Thread.sleep(1000L);
            }*/
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            String scmShopOrderCode = serialUtilService.generateCode(SupplyConstants.Serial.SYSTEM_ORDER_LENGTH, SupplyConstants.Serial.SYSTEM_ORDER_CODE, DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
                            System.out.println("订单号："+scmShopOrderCode);
                        }
                    }
            ).start();
        }
        System.in.read();
    }

    @Test
    public void testPushOrder() throws Exception{
        for(int i=950; i<1000; i++){
            String platformOrderCode = "wdx807261128293165009-"+i;
            String shopOrderCode = "wdx1807261128293175009-"+i;
            String orderInfo = "{\n" +
                    "    \"noticeNum\":\"54d9c6f246f69226cd32b649f545781d\",\n" +
                    "    \"operateTime\":1532575708,\n" +
                    "    \"sign\":\"12c87d62b0b7803f1e68a2e23ce1ee5494ceefd3e4391c9b17587cc2c5941c52\",\n" +
                    "    \"check\":\"54d9c6f246f69226cd32b649f545781d|1532575708|P1807261128293165009|S1807261128293175009|575009|15990141234|2|36.000|36.000|0.000|0.000|WAIT_SELLER_SEND|342901199001010808|15990142185\",\n" +
                    "    \"platformOrder\":{\n" +
                    "        \"adjustFee\":\"36.00\",\n" +
                    "        \"anony\":0,\n" +
                    "        \"buyerArea\":\"110100/110103/全区\",\n" +
                    "        \"cancelReason\":null,\n" +
                    "        \"cancelStatus\":\"NO_APPLY_CANCEL\",\n" +
                    "        \"couponCode\":\"\",\n" +
                    "        \"createTime\":"+"\""+DateUtils.dateToNormalFullString(new Date())+"\""+",\n" +
                    "        \"discountCouponPlatform\":0,\n" +
                    "        \"discountCouponShop\":0,\n" +
                    "        \"discountFee\":\"0.00\",\n" +
                    "        \"discountPromotion\":\"0.000\",\n" +
                    "        \"endTime\":null,\n" +
                    "        \"ip\":null,\n" +
                    "        \"isClearing\":0,\n" +
                    "        \"isDeleted\":0,\n" +
                    "        \"isVirtual\":0,\n" +
                    "        \"itemNum\":2,\n" +
                    "        \"needInvoice\":0,\n" +
                    "        \"payTime\":\"2018-07-26 11:28:33\",\n" +
                    "        \"payType\":\"online\",\n" +
                    "        \"payment\":\"36.000\",\n" +
                    "        \"platformOrderCode\":"+"\""+platformOrderCode+"\""+",\n" +
                    "        \"platformType\":\"PC\",\n" +
                    "        \"pointsFee\":0,\n" +
                    "        \"postageFee\":\"0.000\",\n" +
                    "        \"receiveTime\":null,\n" +
                    "        \"receiverAddress\":\"默认地址测试杭州滨江长河地区\",\n" +
                    "        \"receiverCity\":\"崇文区\",\n" +
                    "        \"receiverDistrict\":\"\",\n" +
                    "        \"receiverProvince\":\"北京市\",\n" +
                    "        \"receiverMobile\":\"15990142185\",\n" +
                    "        \"receiverName\":\"吴寿诞\",\n" +
                    "        \"receiverPhone\":\"\",\n" +
                    "        \"receiverEmail\":\"595287573@qq.com\",\n" +
                    "        \"receiverIdCard\":\"342901199001010808\",\n" +
                    "        \"shippingType\":\"express\",\n" +
                    "        \"status\":\"WAIT_SELLER_SEND\",\n" +
                    "        \"totalFee\":\"36.000\",\n" +
                    "        \"totalTax\":\"0.000\",\n" +
                    "        \"type\":0,\n" +
                    "        \"userId\":575009,\n" +
                    "        \"userName\":\"15990141234\"\n" +
                    "    },\n" +
                    "    \"shopOrders\":[\n" +
                    "        {\n" +
                    "            \"shopOrder\":{\n" +
                    "        \"platformOrderCode\":"+"\""+platformOrderCode+"\""+",\n" +
                    "                \"shopOrderCode\":"+"\""+shopOrderCode+"\""+",\n" +
                    "                \"sellCode\":\"XSQD001\",\n" +
                    "                \"channelCode\":\"YWX001\",\n" +
                    "                \"adjustFee\":\"36.000\",\n" +
                    "                \"consignTime\":null,\n" +
                    "        \"createTime\":"+"\""+DateUtils.dateToNormalFullString(new Date())+"\""+",\n" +
                    "                \"discountFee\":\"0.00\",\n" +
                    "                \"discountPromotion\":\"0.000\",\n" +
                    "                \"isDeleted\":0,\n" +
                    "                \"isPartConsign\":0,\n" +
                    "                \"itemNum\":2,\n" +
                    "                \"payment\":\"36.000\",\n" +
                    "                \"postageFee\":\"0.000\",\n" +
                    "                \"totalFee\":\"36.000\",\n" +
                    "                \"totalTax\":\"0.000\",\n" +
                    "                \"totalWeight\":\"1.800\",\n" +
                    "                \"shopId\":366,\n" +
                    "                \"shopName\":\"泰然易购泰然城（自营店铺）\",\n" +
                    "                \"status\":\"WAIT_SELLER_SEND\",\n" +
                    "                \"title\":\"荷兰乳牛 中老年配方奶粉900克罐装 进口奶源 添加高钙高维生素D 不含蔗糖\",\n" +
                    "                \"tradeMemo\":\"\",\n" +
                    "                \"userId\":575009\n" +
                    "            },\n" +
                    "            \"orderItems\":[\n" +
                    "                {\n" +
                    "                \"shopOrderCode\":"+"\""+shopOrderCode+"\""+",\n" +
                    "        \"platformOrderCode\":"+"\""+platformOrderCode+"\""+",\n" +
                    "                    \"itemNo\":\"SP1201710110000916\",\n" +
                    "        \"createTime\":"+"\""+DateUtils.dateToNormalFullString(new Date())+"\""+",\n" +
                    "                    \"payTime\":\"2018-07-26 11:28:33\",\n" +
                    "                    \"itemName\":\"荷兰乳牛 中老年配方奶粉900克罐装 进口奶源 添加高钙高维生素D 不含蔗糖\",\n" +
                    "                    \"category\":296,\n" +
                    "                    \"adjustFee\":\"36.00\",\n" +
                    "                    \"barCode\":\"SP1201710110000916\",\n" +
                    "                    \"complaintsStatus\":\"NOT_COMPLAINTS\",\n" +
                    "                    \"consignTime\":null,\n" +
                    "                    \"endTime\":null,\n" +
                    "                    \"customsPrice\":\"128.00\",\n" +
                    "                    \"marketPrice\":\"228.00\",\n" +
                    "                    \"price\":\"18.00\",\n" +
                    "                    \"promotionPrice\":\"0.00\",\n" +
                    "                    \"discountFee\":\"0.00\",\n" +
                    "                    \"discountPromotion\":0,\n" +
                    "                    \"num\":2,\n" +
                    "                    \"skuCode\":128377,\n" +
                    "                    \"outerSkuId\":\"SP1201710110000916\",\n" +
                    "                    \"payment\":\"36.00\",\n" +
                    "                    \"postDiscount\":\"0.00\",\n" +
                    "                    \"totalFee\":\"36.00\",\n" +
                    "                    \"priceTax\":\"0.00\",\n" +
                    "                    \"picPath\":\"http://image.tairanmall.com/FhmK84I-Q-qJV687768O29WMqWDx\",\n" +
                    "                    \"shopId\":366,\n" +
                    "                    \"shopName\":\"泰然易购泰然城（自营店铺）\",\n" +
                    "                    \"status\":\"WAIT_SELLER_SEND\",\n" +
                    "                    \"subStock\":0,\n" +
                    "                    \"taxRate\":\"0.000\",\n" +
                    "                    \"totalWeight\":\"0.900\",\n" +
                    "                    \"transactionPrice\":\"18.00\",\n" +
                    "                    \"type\":0,\n" +
                    "                    \"userId\":575009\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";


            String orderInfo2 = "{\n" +
                    "    \"noticeNum\":\"784809f2464afd2f6018e1e80c0e1591\",\n" +
                    "    \"operateTime\":1533777187,\n" +
                    "    \"sign\":\"6fee3574497c0c55e8915eda8da1e2337b3d005d75a74717024b553afe65613b\",\n" +
                    "    \"check\":\"784809f2464afd2f6018e1e80c0e1591|1533777187|P1808090909482194567|S1808090909482204567|94567|cally|1|7.000|5.000|2.000|0.000|WAIT_SELLER_SEND|342901199001010808|13666665057\",\n" +
                    "    \"platformOrder\":{\n" +
                    "        \"adjustFee\":\"7.00\",\n" +
                    "        \"anony\":0,\n" +
                    "        \"buyerArea\":\"330000/330100/330106\",\n" +
                    "        \"cancelReason\":null,\n" +
                    "        \"cancelStatus\":\"NO_APPLY_CANCEL\",\n" +
                    "        \"couponCode\":\"\",\n" +
                    "        \"createTime\":\"2018-08-09 09:09:48\",\n" +
                    "        \"discountCouponPlatform\":0,\n" +
                    "        \"discountCouponShop\":0,\n" +
                    "        \"discountFee\":\"0.00\",\n" +
                    "        \"discountPromotion\":\"0.000\",\n" +
                    "        \"endTime\":null,\n" +
                    "        \"ip\":null,\n" +
                    "        \"isClearing\":0,\n" +
                    "        \"isDeleted\":0,\n" +
                    "        \"isVirtual\":0,\n" +
                    "        \"itemNum\":1,\n" +
                    "        \"needInvoice\":0,\n" +
                    "        \"payTime\":\"2018-08-09 09:10:06\",\n" +
                    "        \"payType\":\"online\",\n" +
                    "        \"payment\":\"7.000\",\n" +
                    "        \"platformOrderCode\":"+"\""+platformOrderCode+"\""+",\n" +
                    "        \"platformType\":\"WX\",\n" +
                    "        \"pointsFee\":0,\n" +
                    "        \"postageFee\":\"2.000\",\n" +
                    "        \"receiveTime\":null,\n" +
                    "        \"receiverAddress\":\"墩莳家园9-2103\",\n" +
                    "        \"receiverCity\":\"杭州市\",\n" +
                    "        \"receiverDistrict\":\"西湖区\",\n" +
                    "        \"receiverProvince\":\"浙江省\",\n" +
                    "        \"receiverMobile\":\"13666665057\",\n" +
                    "        \"receiverName\":\"叶永周\",\n" +
                    "        \"receiverPhone\":\"\",\n" +
                    "        \"receiverEmail\":\"595287573@qq.com\",\n" +
                    "        \"receiverIdCard\":\"342901199001010808\",\n" +
                    "        \"shippingType\":\"express\",\n" +
                    "        \"status\":\"WAIT_SELLER_SEND\",\n" +
                    "        \"totalFee\":\"5.000\",\n" +
                    "        \"totalTax\":\"0.000\",\n" +
                    "        \"type\":0,\n" +
                    "        \"userId\":94567,\n" +
                    "        \"userName\":\"cally\"\n" +
                    "    },\n" +
                    "    \"shopOrders\":[\n" +
                    "        {\n" +
                    "            \"shopOrder\":{\n" +
                    "        \"platformOrderCode\":"+"\""+platformOrderCode+"\""+",\n" +
                    "                \"shopOrderCode\":"+"\""+shopOrderCode+"\""+",\n" +
                    "                \"sellCode\":\"XSQD001\",\n" +
                    "                \"channelCode\":\"YWX001\",\n" +
                    "                \"adjustFee\":\"7.000\",\n" +
                    "                \"consignTime\":null,\n" +
                    "                \"createTime\":\"2018-08-09 09:09:48\",\n" +
                    "                \"discountFee\":\"0.00\",\n" +
                    "                \"discountPromotion\":\"0.000\",\n" +
                    "                \"isDeleted\":0,\n" +
                    "                \"isPartConsign\":0,\n" +
                    "                \"itemNum\":1,\n" +
                    "                \"payment\":\"7.000\",\n" +
                    "                \"postageFee\":\"2.000\",\n" +
                    "                \"totalFee\":\"5.000\",\n" +
                    "                \"totalTax\":\"0.000\",\n" +
                    "                \"totalWeight\":\"1.950\",\n" +
                    "                \"shopId\":169,\n" +
                    "                \"shopName\":\"京东（自营店铺）\",\n" +
                    "                \"status\":\"WAIT_SELLER_SEND\",\n" +
                    "                \"title\":\"花王（Merries）纸尿裤 新生儿NB90【5kg以下】（日本原装进口）\",\n" +
                    "                \"tradeMemo\":\"\",\n" +
                    "                \"userId\":94567\n" +
                    "            },\n" +
                    "            \"orderItems\":[\n" +
                    "                {\n" +
                    "                \"shopOrderCode\":"+"\""+shopOrderCode+"\""+",\n" +
                    "        \"platformOrderCode\":"+"\""+platformOrderCode+"\""+",\n" +
                    "                    \"itemNo\":\"SP1201710100000637\",\n" +
                    "                    \"createTime\":\"2018-08-09 09:09:48\",\n" +
                    "                    \"payTime\":\"2018-08-09 09:10:06\",\n" +
                    "                    \"itemName\":\"花王（Merries）纸尿裤 新生儿NB90【5kg以下】（日本原装进口）\",\n" +
                    "                    \"category\":296,\n" +
                    "                    \"adjustFee\":\"7.00\",\n" +
                    "                    \"barCode\":\"SP1201710100000637\",\n" +
                    "                    \"complaintsStatus\":\"NOT_COMPLAINTS\",\n" +
                    "                    \"consignTime\":null,\n" +
                    "                    \"endTime\":null,\n" +
                    "                    \"customsPrice\":\"9000.00\",\n" +
                    "                    \"marketPrice\":\"177.00\",\n" +
                    "                    \"price\":\"5.00\",\n" +
                    "                    \"promotionPrice\":\"5.00\",\n" +
                    "                    \"discountFee\":\"0.00\",\n" +
                    "                    \"discountPromotion\":0,\n" +
                    "                    \"num\":1,\n" +
                    "                    \"skuCode\":128220,\n" +
                    "                    \"outerSkuId\":\"SP1201710100000637\",\n" +
                    "                    \"payment\":\"7.00\",\n" +
                    "                    \"postDiscount\":\"2.00\",\n" +
                    "                    \"totalFee\":\"5.00\",\n" +
                    "                    \"priceTax\":\"0.00\",\n" +
                    "                    \"picPath\":\"http://image.tairanmall.com/FszL9-QVATA8T0E7E0E4n90cdO1T\",\n" +
                    "                    \"shopId\":169,\n" +
                    "                    \"shopName\":\"京东（自营店铺）\",\n" +
                    "                    \"status\":\"WAIT_SELLER_SEND\",\n" +
                    "                    \"subStock\":0,\n" +
                    "                    \"taxRate\":\"0.000\",\n" +
                    "                    \"totalWeight\":\"1.950\",\n" +
                    "                    \"transactionPrice\":\"7.00\",\n" +
                    "                    \"type\":0,\n" +
                    "                    \"userId\":94567\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";



            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            pushOrder(orderInfo2);
                        }
                    }
            ).start();
        }
        System.in.read();
    }


    private void pushOrder(String params){
        ResponseAck responseAck = null;
        /*log.debug("开始推送订单, 参数：" + params + ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));*/
        long startL = System.nanoTime();
        log.debug("开始推送订单, 参数： 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        String response = null;
        try{
            HttpPost httpPost = new HttpPost("http://10.200.146.8/scm-web/tairan/orderProcessing");
            httpPost.addHeader(HTTP.CONTENT_TYPE,"text/plain; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            response = HttpClientUtil.httpPostJsonRequest("http://10.200.146.8/scm-web/tairan/orderProcessing", params, httpPost, 200000);
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
        Date end = new Date();
        long endL = System.nanoTime();
        log.debug("结束推送订单, 返回结果：" + response + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT) + ", 耗时" + DateUtils.getMilliSecondBetween(startL, endL) + "毫秒");
    }



}
