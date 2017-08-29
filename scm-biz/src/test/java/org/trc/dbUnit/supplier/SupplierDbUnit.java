package org.trc.dbUnit.supplier;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.category.IBrandBiz;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.supplier.ISupplierBiz;
import org.trc.domain.category.Brand;
import org.trc.domain.category.Category;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.*;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.GoodsException;
import org.trc.exception.ParamValidException;
import org.trc.exception.SupplierException;
import org.trc.exception.TestException;
import org.trc.service.BaseTest;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

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

    @Autowired
    private ICategoryBiz categoryBiz;

    @Autowired
    private IBrandBiz brandBiz;

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
        handleData();
        Supplier supplier = createSupplier();
        Certificate certificate = createCertificate();
        SupplierCategory supplierCategory = createSupplierCategory();
        SupplierBrand supplierBrand = createSupplierBrand();
        SupplierFinancialInfo supplierFinancialInfo = createSupplierFinancialInfo();
        SupplierAfterSaleInfo supplierAfterSaleInfo = createSupplierAfterSaleInfo();
        AclUserAccreditInfo aclUserAccreditInfo = createAclUserAccreditInfo();
        supplierBiz.saveSupplier(supplier,certificate,supplierCategory,supplierBrand,supplierFinancialInfo,supplierAfterSaleInfo,aclUserAccreditInfo);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult02 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierCategoryData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult02.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult03 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierBrandData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult03.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult04 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierFinancialInfoData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult04.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult05 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierAfterSaleInfoData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult05.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult06 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierChannelRelationData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult06.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        //ReplacementDataSet expResult07 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertLogInformationData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        //expResult07.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult08 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult08.addReplacementObject("[null]", null);
        ReplacementDataSet expResult09 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertCertificateData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult09.addReplacementObject("[null]", null);
        ReplacementDataSet expResult10 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertAclUserAccreditInfoData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult10.addReplacementObject("[null]", null);

        //从数据库中查出数据与期望结果作比较
        String[] str = new String[]{"id","supplier_id","create_operator","create_time","update_time"};
        assertDataSet(TABLE_SUPPLIER_CATEGORY,"select * from supplier_category",expResult02,conn,str);
        assertDataSet(TABLE_SUPPLIER_BRAND,"select * from supplier_brand",expResult03,conn,str);
        assertDataSet(TABLE_SUPPLIER_FINANCIAL_INFO,"select * from supplier_financial_info",expResult04,conn,str);
        assertDataSet(TABLE_SUPPLIER_AFTER_SALE_INFO,"select * from supplier_after_sale_info",expResult05,conn,str);
        assertDataSet(TABLE_SUPPLIER_CHANNEL_RELATION,"select * from supplier_channel_relation",expResult06,conn,str);
        //assertDataSet(TABLE_LOG_INFORMATION,"select * from log_information",expResult07,conn,str);
        assertDataSet(TABLE_SUPPLIER,"select * from supplier",expResult08,conn,str);
        assertDataSet(TABLE_CERTIFICATE,"select * from certificate",expResult09,conn,str);
        //分类停用异常
        testSaveSupplierCategoryError();
        //品牌停用异常
        testSaveSupplierBrandError();

    }

    /**
     * 测试保存供应商订单操作(异常情况)
     * @throws Exception
     */
    @Transactional(propagation = REQUIRES_NEW)
    private void testSaveSupplierCategoryError() throws Exception {
        //删除原数据
        //handleData();
        //异常情况
        Supplier supplier01 = createSupplier();
        Supplier supplier02 = createSupplier();
        Certificate certificate01 = createCertificate();
        SupplierCategory supplierCategory01 = createSupplierCategory();
        SupplierBrand supplierBrand01 = createSupplierBrand();
        SupplierFinancialInfo supplierFinancialInfo01 = createSupplierFinancialInfo();
        SupplierAfterSaleInfo supplierAfterSaleInfo01 = createSupplierAfterSaleInfo();
        AclUserAccreditInfo aclUserAccreditInfo01 = createAclUserAccreditInfo();
        //测试异常流程 参数校验异常
        try{
            supplier02.setCertificateTypeId("");
            supplierBiz.saveSupplier(supplier02,certificate01,supplierCategory01,supplierBrand01,supplierFinancialInfo01,supplierAfterSaleInfo01,aclUserAccreditInfo01);
            throw new TestException("测试异常");
        }catch (ParamValidException e){
            if(e.getExceptionEnum().equals(CommonExceptionEnum.PARAM_CHECK_EXCEPTION)){
                log.info("----------保存供应商参数校验异常流程测试通过---------");
            }
        }catch (IllegalArgumentException e){
            log.info("----------保存供应商参数校验异常流程测试通过---------");
        }

        //测试异常流程 分类停用异常
        try{
            Category category = new Category();
            category.setId(88L);
            category.setIsValid("1");
            categoryBiz.updateState(category,aclUserAccreditInfo01);
            supplierBiz.saveSupplier(supplier01,certificate01,supplierCategory01,supplierBrand01,supplierFinancialInfo01,supplierAfterSaleInfo01,aclUserAccreditInfo01);
            throw new TestException("测试异常");
        }catch (GoodsException e){
            if(e.getExceptionEnum().equals(ExceptionEnum.GOODS_DEPEND_DATA_INVALID)){
                log.info("----------保存供应商分类停用异常流程测试通过---------");
            }
        }

    }

    /**
     * 测试保存供应商订单操作(异常情况)
     * @throws Exception
     */
    @Transactional(propagation = REQUIRES_NEW)
    private void testSaveSupplierBrandError() throws Exception {
        //handleData();
        //异常情况
        Supplier supplier01 = createSupplier();
        Supplier supplier02 = createSupplier();
        Certificate certificate01 = createCertificate();
        SupplierCategory supplierCategory01 = createSupplierCategory();
        SupplierBrand supplierBrand01 = createSupplierBrand();
        SupplierFinancialInfo supplierFinancialInfo01 = createSupplierFinancialInfo();
        SupplierAfterSaleInfo supplierAfterSaleInfo01 = createSupplierAfterSaleInfo();
        AclUserAccreditInfo aclUserAccreditInfo01 = createAclUserAccreditInfo();

        //测试异常流程 品牌停用异常
        try{
            Brand brand = new Brand();
            brand.setId(39L);
            brand.setIsValid("1");
            brandBiz.updateBrandStatus(brand,aclUserAccreditInfo01);
            supplierBiz.saveSupplier(supplier01,certificate01,supplierCategory01,supplierBrand01,supplierFinancialInfo01,supplierAfterSaleInfo01,aclUserAccreditInfo01);
            throw new TestException("测试异常");
        }catch (GoodsException e){
            if(e.getExceptionEnum().equals(ExceptionEnum.GOODS_DEPEND_DATA_INVALID)){
                log.info("----------保存供应商品牌停用异常流程测试通过---------");
            }
        }
    }

    private void handleData() throws Exception {
        //删除原数据
        execSql(conn,"delete from platform_order");
        execSql(conn,"delete from supplier_category");
        execSql(conn,"delete from supplier_brand");
        execSql(conn,"delete from supplier_financial_info");
        execSql(conn,"delete from supplier_after_sale_info");
        execSql(conn,"delete from supplier_channel_relation");
        execSql(conn,"delete from log_information");
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
        //prepareData(conn, "supplier/preUpdateSupplierData.xml");
    }

    /**
     * 测试保存供应商订单操作
     * @throws Exception
     */
    @Test
    public void testUpdateSupplier() throws Exception {
        //删除原数据
        handleData();
        prepareData(conn, "supplier/preUpdateCertificateData.xml");
        prepareData(conn, "supplier/preUpdateSupplierAfterSaleInfoData.xml");
        prepareData(conn, "supplier/preUpdateSupplierBrandData.xml");
        prepareData(conn, "supplier/preUpdateSupplierCategoryData.xml");
        prepareData(conn, "supplier/preUpdateSupplierChannelRelationData.xml");
        prepareData(conn, "supplier/preUpdateSupplierData.xml");
        prepareData(conn, "supplier/preUpdateSupplierFinancialInfoData.xml");
        //异常情况
        Supplier supplier01 = createSupplier();
        supplier01.setId(1L);
        supplier01.setSupplierCode("GYS000065");
        Certificate certificate01 = createCertificate();
        certificate01.setId(109L);
        Certificate certificate02 = createCertificate();
        certificate02.setId(109L);
        SupplierCategory supplierCategory01 = createSupplierCategory();
        SupplierBrand supplierBrand01 = createSupplierBrand();
        supplierBrand01.setId(116L);
        SupplierFinancialInfo supplierFinancialInfo01 = createSupplierFinancialInfo();
        SupplierAfterSaleInfo supplierAfterSaleInfo01 = createSupplierAfterSaleInfo();
        supplierAfterSaleInfo01.setId(65L);
        AclUserAccreditInfo aclUserAccreditInfo01 = createAclUserAccreditInfo();
        //测试异常流程 参数校验异常
        try{
            certificate02.setIdCardStartDate("");
            supplierBiz.updateSupplier(supplier01,certificate02,supplierCategory01,supplierBrand01,supplierFinancialInfo01,supplierAfterSaleInfo01,aclUserAccreditInfo01);
            throw new TestException("测试异常");
        }catch (ParamValidException e){
            if(e.getExceptionEnum().equals(CommonExceptionEnum.PARAM_CHECK_EXCEPTION)){
                log.info("----------更新供应商参数校验异常流程测试通过---------");
            }
        }catch (IllegalArgumentException e){
            log.info("----------更新供应商参数校验异常流程测试通过---------");
        }

        Supplier supplier = createSupplier();
        supplier.setIsValid("1");
        supplier.setId(1L);
        supplier.setSupplierCode("GYS000065");
        Certificate certificate = createCertificate();
        certificate.setIdCardStartDate("2017-08-07");
        certificate.setId(109L);
        SupplierCategory supplierCategory = createSupplierCategory();
        supplierCategory.setIsValid("1");
        supplierCategory.setSupplierCetegory("[{\"id\":\"267\",\"categoryId\":\"90\",\"categoryCode\":\"FL090\",\"categoryName\":\"美妆个护/面部护肤/眉笔/粉、膏\"},{\"id\":\"268\",\"categoryId\":\"89\",\"categoryCode\":\"FL089\",\"categoryName\":\"美妆个护/面部护肤/男士护肤\"},{\"id\":\"269\",\"categoryId\":\"88\",\"categoryCode\":\"FL088\",\"categoryName\":\"美妆个护/面部护肤/美甲\"}]");
        SupplierBrand supplierBrand = createSupplierBrand();
        supplierBrand.setId(116L);
        supplierBrand.setSupplierBrand("[{\"id\":\"116\",\"brandId\":39,\"brandCode\":\"PP2017081400039\",\"brandName\":\"love\",\"categoryCode\":\"FL090\",\"isValid\":\"1\",\"categoryId\":90,\"categoryName\":\"美妆个护/面部护肤/眉笔/粉、膏\",\"source\":0,\"status\":2,\"sortStatus\":0,\"proxyAptitudeId\":\"firstAgent\",\"proxyAptitudeStartDate\":1501516800000,\"proxyAptitudeEndDate\":1511366400000,\"aptitudePic\":\"supply/437773324715137.png\"}]");
        SupplierFinancialInfo supplierFinancialInfo = createSupplierFinancialInfo();
        supplierFinancialInfo.setBankAccount("3245879874544112454");
        SupplierAfterSaleInfo supplierAfterSaleInfo = createSupplierAfterSaleInfo();
        supplierAfterSaleInfo.setGoodsReturnPhone("17765487544");
        supplierAfterSaleInfo.setId(65L);
        AclUserAccreditInfo aclUserAccreditInfo = createAclUserAccreditInfo();
        supplierBiz.updateSupplier(supplier,certificate,supplierCategory,supplierBrand,supplierFinancialInfo,supplierAfterSaleInfo,aclUserAccreditInfo);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult01 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierCategoryData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult01.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult02 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierBrandData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult02.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult03 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierFinancialInfoData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult03.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult04 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierAfterSaleInfoData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult04.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult05 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierChannelRelationData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult05.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        //ReplacementDataSet expResult06 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertLogInformationData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        //expResult06.addReplacementObject("[null]", null);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult07 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertSupplierData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        //expResult07.addReplacementObject("[null]", null);
        ReplacementDataSet expResult08 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expInsertCertificateData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult08.addReplacementObject("[null]", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_SUPPLIER_CATEGORY,"select * from supplier_category",expResult01,conn);
        assertDataSet(TABLE_SUPPLIER_BRAND,"select * from supplier_brand",expResult02,conn);
        assertDataSet(TABLE_SUPPLIER_FINANCIAL_INFO,"select * from supplier_financial_info",expResult03,conn);
        assertDataSet(TABLE_SUPPLIER_AFTER_SALE_INFO,"select * from supplier_after_sale_info",expResult04,conn);
        assertDataSet(TABLE_SUPPLIER_CHANNEL_RELATION,"select * from supplier_channel_relation",expResult05,conn);
        //assertDataSet(TABLE_LOG_INFORMATION,"select * from log_information",expResult06,conn);
        assertDataSet(TABLE_SUPPLIER,"select * from supplier",expResult07,conn);
        assertDataSet(TABLE_CERTIFICATE,"select * from certificate",expResult08,conn);
    }


    /**
     * 测试保存供应商订单操作(异常情况)
     * @throws Exception
     */
    @Test
    public void testUpdateSupplierCategoryError() throws Exception {
        //删除原数据
        handleData();
        prepareData(conn, "supplier/preUpdateSupplierData.xml");
        //异常情况
        Supplier supplier01 = createSupplier();
        supplier01.setId(1L);
        supplier01.setSupplierCode("GYS000065");
        Certificate certificate01 = createCertificate();
        SupplierCategory supplierCategory01 = createSupplierCategory();
        SupplierBrand supplierBrand01 = createSupplierBrand();
        SupplierFinancialInfo supplierFinancialInfo01 = createSupplierFinancialInfo();
        SupplierAfterSaleInfo supplierAfterSaleInfo01 = createSupplierAfterSaleInfo();
        AclUserAccreditInfo aclUserAccreditInfo01 = createAclUserAccreditInfo();

        //测试异常流程 分类停用异常
        try{
            Category category = new Category();
            category.setId(88L);
            category.setIsValid("1");
            categoryBiz.updateState(category,aclUserAccreditInfo01);
            supplierBiz.updateSupplier(supplier01,certificate01,supplierCategory01,supplierBrand01,supplierFinancialInfo01,supplierAfterSaleInfo01,aclUserAccreditInfo01);
            throw new TestException("测试异常");
        }catch (GoodsException e){
            if(e.getExceptionEnum().equals(ExceptionEnum.GOODS_DEPEND_DATA_INVALID)){
                log.info("----------保存供应商分类停用异常流程测试通过---------");
            }
        }

    }

    /**
     * 测试保存供应商订单操作(异常情况)
     * @throws Exception
     */
    @Test
    public void testUpdateSupplierBrandError() throws Exception {
        handleData();
        prepareData(conn, "supplier/preUpdateSupplierData.xml");
        //异常情况
        Supplier supplier01 = createSupplier();
        supplier01.setId(1L);
        supplier01.setSupplierCode("GYS000065");
        Certificate certificate01 = createCertificate();
        SupplierCategory supplierCategory01 = createSupplierCategory();
        SupplierBrand supplierBrand01 = createSupplierBrand();
        SupplierFinancialInfo supplierFinancialInfo01 = createSupplierFinancialInfo();
        SupplierAfterSaleInfo supplierAfterSaleInfo01 = createSupplierAfterSaleInfo();
        AclUserAccreditInfo aclUserAccreditInfo01 = createAclUserAccreditInfo();

        //测试异常流程 品牌停用异常
        try{
            Brand brand = new Brand();
            brand.setId(39L);
            brand.setIsValid("1");
            brandBiz.updateBrandStatus(brand,aclUserAccreditInfo01);
            supplierBiz.updateSupplier(supplier01,certificate01,supplierCategory01,supplierBrand01,supplierFinancialInfo01,supplierAfterSaleInfo01,aclUserAccreditInfo01);
            throw new TestException("测试异常");
        }catch (GoodsException e){
            if(e.getExceptionEnum().equals(ExceptionEnum.GOODS_DEPEND_DATA_INVALID)){
                log.info("----------保存供应商品牌停用异常流程测试通过---------");
            }
        }
    }

    /**
     * 测试启用/停用操作
     * @throws Exception
     */
    @Test
    public void testUpdateValid() throws Exception {
        //删除原数据
        execSql(conn,"delete from supplier; alter table supplier auto_increment=1;");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "supplier/preInsertAclUserAccreditInfoData.xml");
        prepareData(conn, "supplier/preInsertBrandData.xml");
        prepareData(conn, "supplier/preInsertCategoryData.xml");
        prepareData(conn, "supplier/preInsertChannelData.xml");
        prepareData(conn, "supplier/preInsertCategoryBrandData.xml");
        prepareData(conn, "supplier/preUpdateSupplierData.xml");
        prepareData(conn, "supplier/preInsertSupplierCategoryData01.xml");
        AclUserAccreditInfo aclUserAccreditInfo01 = createAclUserAccreditInfo();
        //测试异常流程
        try{
            supplierBiz.updateValid(121L,"1",aclUserAccreditInfo01);
            throw new TestException("测试异常");
        }catch (SupplierException e){
            if(e.getExceptionEnum().equals(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION)){
                log.info("----------分类启停用异常流程测试通过---------");
            }
        }

        AclUserAccreditInfo aclUserAccreditInfo = createAclUserAccreditInfo();
        supplierBiz.updateValid(1L,"0",aclUserAccreditInfo);
        // 从xml文件读取期望结果
        ReplacementDataSet expResult01 = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("supplier/expUpdateSupplierData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
      //  expResult01.addReplacementObject("[null]", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_SUPPLIER,"select * from supplier",expResult01,conn);
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

//    private <T> T converDateSet(ReplacementDataSet dataSet, Class<T> clazz){
//        try {
//            String[] tableNames = dataSet.getTableNames();
//            for (String tableName :tableNames) {
//                ITableMetaData date = dataSet.getTableMetaData(tableName);
//                System.out.print("");
//
//            }
//
//        } catch (DataSetException e) {
//            e.printStackTrace();
//        }
//        T a = (T) "";
//        return a;
//    }

}
