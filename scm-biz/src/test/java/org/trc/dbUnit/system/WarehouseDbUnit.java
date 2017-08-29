package org.trc.dbUnit.system;

import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.system.IChannelBiz;
import org.trc.biz.system.IWarehouseBiz;
import org.trc.dbUnit.BaseTestContext;
import org.trc.domain.System.Channel;
import org.trc.domain.System.Warehouse;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.ZeroToNineEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sone on 2017/8/25.
 */

public class WarehouseDbUnit extends BaseTestContext {

    private Logger log = LoggerFactory.getLogger(WarehouseDbUnit.class);

    @Autowired
    private IWarehouseBiz warehouseBiz;

    private static final String TABLE_WAREHOUSE= "warehouse";

    /**
     * 测试插入
     */
    @Test
    public  void testInsertWarehouse() throws Exception{

        AclUserAccreditInfo aclUserAccreditInfo=createAclUserAccreditInfo();
        //删除原数据
        execSql(conn,"delete from warehouse");
        execSql(conn,"delete from serial");

        //从xml文件读取数据并插入数据库中
        prepareData(conn, "system/warehouse/preInsertWarehouseData.xml");

        Warehouse warehouse = createWarehouse();
        warehouseBiz.saveWarehouse(warehouse,aclUserAccreditInfo);
        //从xml文件读取期望结果
        ReplacementDataSet expResult = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("system/warehouse/expInsertWarehouseData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult.addReplacementObject("[null]", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_WAREHOUSE,"select * from warehouse",expResult,conn);

    }

    /**
     * 创建仓库
     * @return
     */
    private Warehouse createWarehouse(){

        Warehouse warehouse = new Warehouse();
        warehouse.setCreateOperator("E2E4BDAD80354EFAB6E70120C271968C");
        warehouse.setIsValid("1");
        warehouse.setIsDeleted("0");
        warehouse.setAddress("111");
        warehouse.setCity("210300");
        warehouse.setProvince("210000");
        warehouse.setArea("210301");
        warehouse.setName("zhangsan");
        warehouse.setWarehouseTypeCode("bondedWarehouse");
        warehouse.setIsCustomsClearance(1);
        warehouse.setRemark("11");
        return warehouse;
    }



    /**
     * 从数据库中导出指定表数据到xml文件中
     * @throws Exception
     */
    @Test
    public void exportData() throws Exception {
        List<String> tableNameList = new ArrayList<>();
        tableNameList.add("warehouse");
        exportData(tableNameList, "preInsertWarehouseData.xml");
    }

}
