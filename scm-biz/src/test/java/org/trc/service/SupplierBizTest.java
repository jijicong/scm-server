package org.trc.service;

import com.alibaba.fastjson.JSON;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.supplier.ISupplierBiz;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.util.DateUtils;

/**
 * Created by hzwdx on 2017/8/17.
 */
public class SupplierBizTest extends BaseTest{

    /**
     * 供应商表名
     */
    private static final String TABLE_SUPPLIER = "preInsertSupplierData.xml";

    @Autowired
    private ISupplierBiz supplierBiz;

    @Override
    protected void prepareData(IDatabaseConnection conn, String fileName) throws Exception {

    }


    public void test_supplierPage() throws Exception{
        //删除原数据
        execSql(conn,"delete from preInsertSupplierData.xml");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "category/preInsertSupplierData.xml");
        Supplier supplier = createSupplier();


        // 从xml文件读取期望结果
        ReplacementDataSet expResult = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("category/expInsertSupplierData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult.addReplacementObject("[null]", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_SUPPLIER,"select * from preInsertSupplierData.xml",expResult,conn);


    }

    @Test
    public void test(){
        System.out.println(JSON.toJSON(createSupplier()));
    }

    public Supplier createSupplier(){
        Supplier supplier = new Supplier();
        supplier.setSupplierCode("GYS000001");
        supplier.setSupplierName("sss");
        supplier.setSupplierKindCode("purchase");
        supplier.setSupplierTypeCode("internalSupplier");
        supplier.setContact("sss");
        supplier.setPhone("15068839416");
        //supplier.setMobile("13568495789");
        supplier.setProvince("120000");
        supplier.setCity("120102");
        //supplier.setArea("滨江区");
        supplier.setAddress("ssss");
        supplier.setCertificateTypeId("normalThreeCertificate");//普通三证
        supplier.setIsValid(ValidEnum.VALID.getCode());
        supplier.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        supplier.setRemark("sss");
        supplier.setCreateOperator("E2E4BDAD80354EFAB6E70120C271968C");
        supplier.setCreateTime(DateUtils.parseDateTime("2017-07-24 15:45:31"));
        supplier.setUpdateTime(DateUtils.parseDateTime("2017-07-24 15:45:31"));
        return supplier;
    }




}
