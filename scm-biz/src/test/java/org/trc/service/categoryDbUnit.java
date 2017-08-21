package org.trc.service;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.category.ICategoryBiz;
import org.trc.domain.category.Category;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.TestException;

import java.security.acl.Acl;
import java.util.ArrayList;
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
        tableNameList.add("supplier_category");
        exportData(tableNameList, "expInsertSupplierCategoryData.xml");
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
}
