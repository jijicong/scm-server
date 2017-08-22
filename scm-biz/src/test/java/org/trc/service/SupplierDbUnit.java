package org.trc.service;

import com.alibaba.fastjson.JSONObject;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.supplier.ISupplierBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyz on 2017/8/18.
 * *  * 测试步骤：
 * 1.删除需要用到的表中的干扰数据（测试结束后会回滚）
 * 2.读取xml中的需要初始化的测试数据
 * 3.执行业务中的方法进行测试
 * 4.读取事先准备的预期数据xml并且进行比较
 */
public class SupplierDbUnit extends BaseTest {
    private Logger log = LoggerFactory.getLogger(SupplierDbUnit.class);

    @Autowired
    private ISupplierBiz supplierBiz;

    private static final String TABLE_PLATFORM_ORDER = "platform_order";

    private static final String TABLE_SERIAL = "serial";

    private static final String TABLE_SUPPLIER_CATEGORY = "supplier_category";

    private static final String TABLE_SUPPLIER_BRAND = "supplier_brand";

    private static final String TABLE_SUPPLIER_FINANCIAL_INFO = "supplier_financial_info";

    private static final String TABLE_SUPPLIER_AFTER_SALE_INFO = "supplier_after_sale_info";

    private static final String TABLE_SUPPLIER_CHANNEL_RELATION = "supplier_channel_relation";

    private static final String TABLE_LOG_INFORMATION = "log_information";

    private static final String TABLE_SUPPLIER = "supplier";

    private static final String TABLE_CERTIFICATE = "certificate";

    private static final String TABLE_USER_ACCREDIT_INFO = "acl_user_accredit_info";


    /**
     * 准备需要的初始数据
     * @param conn
     * @param fileName
     * @throws Exception
     */
    @Override
    protected void prepareData(IDatabaseConnection conn, String fileName) throws Exception {
        //读取xml文件中的数据信息
        ReplacementDataSet createDataSet = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
        //INSERT TEST DATA
        DatabaseOperation.INSERT.execute(conn, createDataSet);
    }

    /**
     * 测试保存供应商订单操作
     * @throws Exception
     */
    @Test
    public void testSaveSupplier() throws Exception {
        //删除原数据
        execSql(conn,"delete from platform_order");
        execSql(conn,"delete from supplier_category");
        execSql(conn,"delete from supplier_brand");
        execSql(conn,"delete from supplier_financial_info");
        execSql(conn,"delete from supplier_after_sale_info");
        execSql(conn,"delete from supplier_channel_relation");
        //execSql(conn,"delete from log_information");
        execSql(conn,"delete from supplier");
        execSql(conn,"delete from certificate");
        execSql(conn,"delete from acl_user_accredit_info");
        execSql(conn,"delete from serial");
        execSql(conn,"delete from channel");
        execSql(conn,"delete from category");
        execSql(conn,"delete from brand");
        execSql(conn,"delete from category_brand");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "supplier/preInsertSerialData.xml");
        prepareData(conn, "supplier/preInsertBrandData.xml");
        prepareData(conn, "supplier/preInsertCategoryData.xml");
        prepareData(conn, "supplier/preInsertChannelData.xml");
        prepareData(conn, "supplier/preInsertCategoryBrandData.xml");
        Supplier supplier = createSupplier();
        Certificate certificate = createCertificate();
        SupplierCategory supplierCategory = createSupplierCategory();
        SupplierBrand supplierBrand = createSupplierBrand();
        SupplierFinancialInfo supplierFinancialInfo = createSupplierFinancialInfo();
        SupplierAfterSaleInfo supplierAfterSaleInfo = createSupplierAfterSaleInfo();
        AclUserAccreditInfo aclUserAccreditInfo = createAclUserAccreditInfo();
        supplierBiz.saveSupplier(supplier,certificate,supplierCategory,supplierBrand,supplierFinancialInfo,supplierAfterSaleInfo,aclUserAccreditInfo);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult01 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertPlatformOrderData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult01.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult02 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertSupplierCategoryData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult02.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult03 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertSupplierBrandData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult03.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult04 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertSupplierFinancialInfoData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult04.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult05 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertSupplierAfterSaleInfoData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult05.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult06 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertSupplierChannelRelationData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult06.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        //ReplacementDataSet expResult07 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertLogInformationData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        //expResult07.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult08 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertSupplierData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult08.addReplacementObject("[null]", null);
        ReplacementDataSet expResult09 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertCertificateData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult09.addReplacementObject("[null]", null);
        ReplacementDataSet expResult10 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("order/expInsertAclUserAccreditInfoData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult10.addReplacementObject("[null]", null);

        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_PLATFORM_ORDER,"select * from platform_order",expResult01,conn);
        assertDataSet(TABLE_SUPPLIER_CATEGORY,"select * from supplier_category",expResult02,conn);
        assertDataSet(TABLE_SUPPLIER_BRAND,"select * from supplier_brand",expResult03,conn);
        assertDataSet(TABLE_SUPPLIER_FINANCIAL_INFO,"select * from supplier_financial_info",expResult04,conn);
        assertDataSet(TABLE_SUPPLIER_AFTER_SALE_INFO,"select * from supplier_after_sale_info",expResult05,conn);
        assertDataSet(TABLE_SUPPLIER_CHANNEL_RELATION,"select * from supplier_channel_relation",expResult06,conn);
        //assertDataSet(TABLE_LOG_INFORMATION,"select * from log_information",expResult07,conn);
        assertDataSet(TABLE_SUPPLIER,"select * from supplier",expResult08,conn);
        assertDataSet(TABLE_CERTIFICATE,"select * from certificate",expResult09,conn);
        assertDataSet(TABLE_USER_ACCREDIT_INFO,"select * from acl_user_accredit_info",expResult10,conn);
    }

    private Supplier createSupplier(){
        Supplier supplier = new Supplier();
        //supplier.setSupplierCode("GYS000001");
        supplier.setSupplierName("亚马逊专供");
        supplier.setSupplierKindCode("purchase");
        supplier.setSupplierTypeCode("internalSupplier");
        supplier.setContact("杰夫·贝佐斯");
        supplier.setPhone("0012145784");
        supplier.setProvince("330000");
        supplier.setCity("330100");
        supplier.setAddress("浙江省杭州市西湖区香格里拉别墅区5栋");
        supplier.setCertificateTypeId("normalThreeCertificate");
        supplier.setRemark("测试供应商");
        supplier.setIsValid("1");
        supplier.setArea("330106");
        supplier.setChannel("1-QD001,2-QD002,3-QD003,5-QD005,7-QD007,8-QD008,11-QD011,12-QD012,13-QD013,14-QD014");
        supplier.setCreateOperator("E2E4BDAD80354EFAB6E70120C271968C");
        return supplier;
    }

    private Certificate createCertificate(){
        Certificate certificate = new Certificate();
        //certificate.setSupplierId(22L);
        //certificate.setSupplierCode("GYS000001");
        certificate.setBusinessLicence("1245784545454");
        certificate.setBusinessLicencePic("supply/437646141248602.jpg");
        certificate.setOrganRegistraCodeCertificate("12457841254124");
        certificate.setOrganRegistraCodeCertificatePic("supply/437652407989615.jpg");
        certificate.setTaxRegistrationCertificate("12457854788254");
        certificate.setTaxRegistrationCertificatePic("supply/437659640787382.jpg");
        certificate.setLegalPersonIdCard("321478189910253158");
        certificate.setLegalPersonIdCardPic1("supply/437666386680509.jpg");
        certificate.setLegalPersonIdCardPic2("supply/437670751540979.jpg");
        certificate.setMultiCertificateCombinePic("");
        certificate.setBusinessLicenceStartDate("2017-08-01");
        certificate.setBusinessLicenceEndDate("2017-08-23");
        certificate.setOrganRegistraStartDate("2017-08-01");
        certificate.setOrganRegistraEndDate("2017-08-30");
        certificate.setTaxRegistrationStartDate("2017-08-01");
        certificate.setTaxRegistrationEndDate("2017-08-29");
        certificate.setIdCardStartDate("2017-08-07");
        certificate.setIdCardEndDate("2017-08-08");
        return certificate;
    }

    private SupplierCategory createSupplierCategory(){
        SupplierCategory supplierCategory = new SupplierCategory();
        SupplierCategory supplierCategory01 = new SupplierCategory();
        supplierCategory.setSupplierCetegory("[{\"categoryId\":\"90\",\"categoryCode\":\"FL090\",\"categoryName\":\"美妆个护/面部护肤/眉笔/粉、膏\"},{\"categoryId\":\"89\",\"categoryCode\":\"FL089\",\"categoryName\":\"美妆个护/面部护肤/男士护肤\"},{\"categoryId\":\"88\",\"categoryCode\":\"FL088\",\"categoryName\":\"美妆个护/面部护肤/美甲\"}]");
        supplierCategory.setIsValid("1");
        return supplierCategory;
    }

    private SupplierBrand createSupplierBrand(){
        SupplierBrand supplierBrand = new SupplierBrand();
        supplierBrand.setIsValid("1");
        supplierBrand.setSupplierBrand("[{\"brandId\":39,\"brandCode\":\"PP2017081400039\",\"brandName\":\"love\",\"categoryCode\":\"FL090\",\"isValid\":\"0\",\"categoryId\":90,\"categoryName\":\"美妆个护/面部护肤/眉笔/粉、膏\",\"source\":1,\"status\":1,\"sortStatus\":0,\"proxyAptitudeId\":\"firstAgent\",\"proxyAptitudeStartDate\":1501516800000,\"proxyAptitudeEndDate\":1511366400000,\"aptitudePic\":\"supply/437773324715137.png\"}]");
        return supplierBrand;
    }

    private SupplierFinancialInfo createSupplierFinancialInfo(){
        SupplierFinancialInfo supplierFinancialInfo = new SupplierFinancialInfo();
        supplierFinancialInfo.setDepositBank("中国银行");
        supplierFinancialInfo.setBankAccount("3245879874544112454");
        return supplierFinancialInfo;
    }

    private SupplierAfterSaleInfo createSupplierAfterSaleInfo(){
        SupplierAfterSaleInfo supplierAfterSaleInfo = new SupplierAfterSaleInfo();
        supplierAfterSaleInfo.setGoodsReturnContactPerson("奥马马");
        supplierAfterSaleInfo.setGoodsReturnAddress("浙江省杭州市滨江区上峰电商园");
        supplierAfterSaleInfo.setGoodsReturnPhone("17765487544");
        supplierAfterSaleInfo.setGoodsReturnStrategy("七天无理由退换");
        return supplierAfterSaleInfo;
    }

    private AclUserAccreditInfo createAclUserAccreditInfo(){
        AclUserAccreditInfo aclUserAccreditInfo = new AclUserAccreditInfo();
        aclUserAccreditInfo.setId(1L);
        aclUserAccreditInfo.setChannelId(2L);
        aclUserAccreditInfo.setChannelName("小泰乐活");
        aclUserAccreditInfo.setUserId("E2E4BDAD80354EFAB6E70120C271968C");
        aclUserAccreditInfo.setPhone("15757195796");
        aclUserAccreditInfo.setName("admin");
        aclUserAccreditInfo.setUserType("mixtureUser");
        aclUserAccreditInfo.setChannelCode("QD002");
        aclUserAccreditInfo.setRemark("admin");
        aclUserAccreditInfo.setIsValid("1");
        aclUserAccreditInfo.setIsDeleted("0");
        aclUserAccreditInfo.setCreateOperator("E2E4BDAD80354EFAB6E70120C271968C");
        return aclUserAccreditInfo;
    }
    /**
     * 从数据库中导出指定表数据到xml文件中
     * @throws Exception
     */
    @Test
    public void exportData() throws Exception {
        List<String> tableNameList = new ArrayList<>();
        tableNameList.add("category_brand");
        exportData(tableNameList, "preInsertCategoryBrandData.xml");
    }

}
