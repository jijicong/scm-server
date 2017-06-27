package org.trc.service;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.category.ICategoryBiz;
import org.trc.domain.category.Category;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.TestException;

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
    @Override
    protected void prepareData(IDatabaseConnection conn, String fileName) throws Exception {
        //读取xml文件中的数据信息
        ReplacementDataSet createDataSet = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
        //INSERT TEST DATA
        DatabaseOperation.INSERT.execute(conn, createDataSet);
    }


    /**
     * 测试插入操作
     * @throws Exception
     */
    @Test
    public void testInsert() throws Exception {
        //删除原数据
        execSql(conn,"delete from category");
        execSql(conn,"delete from serial");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "category/preInsertCategoryData.xml");
        Category category=createCategory();
        iCategoryBiz.saveCategory(category,null);
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
    public void testUpdateIsValid()throws Exception{
        //删除原数据
        execSql(conn,"delete from category");
        execSql(conn,"delete from serial");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "category/preUpdateStatusData.xml");
        Category category=new Category();
        category.setId(2l);
        category.setIsValid(ValidEnum.VALID.getCode());
        try{
            iCategoryBiz.updateState(category);
            throw new TestException("测试异常");
        }catch (CategoryException e){
            if(e.getExceptionEnum().equals(ExceptionEnum.CATEGORY_CATEGORY_UPDATE_EXCEPTION)){
                System.out.println("----------update category status pass test---------");
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
        tableNameList.add("category");
        exportData(tableNameList, "");
    }

    /**
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
}
