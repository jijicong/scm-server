package org.trc.dbUnit.purchase;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.purchase.IPurchaseGroupBiz;
import org.trc.biz.system.IChannelBiz;
import org.trc.dbUnit.BaseTestContext;
import org.trc.domain.System.Channel;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ChannelException;
import org.trc.exception.PurchaseGroupException;
import org.trc.exception.TestException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sone21 on 2017/8/29.
 */
public class PurchaseGroupDbUnit extends BaseTestContext {

    private Logger log = LoggerFactory.getLogger(PurchaseGroupDbUnit.class);

    @Autowired
    private IPurchaseGroupBiz purchaseGroupBiz;

    private static final String TABLE_PURCHASE_GROUP= "purchase_group";

    /**
     * 测试采购组的插入
     */
    @Test
    public void testInsertPurchaseGroup() throws Exception{
        AclUserAccreditInfo aclUserAccreditInfo=createAclUserAccreditInfo();
        //删除原数据
        execSql(conn,"delete from purchase_group");
        execSql(conn,"delete from serial");
        prepareData(conn, "purchase/purchaseGroup/preInsertPruchaseGroupData.xml");
        PurchaseGroup purchaseGroup = createPurchaseGroup();
        try {
            purchaseGroupBiz.savePurchaseGroup(purchaseGroup,aclUserAccreditInfo);
        }catch (Exception e){
            log.info("测试正常!,目前的userId不能使用,不是采购组员");
            return;
        }
        ReplacementDataSet expResult = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("purchase/purchaseGroup/expInsertPurchaseGroupData.xml"));
        expResult.addReplacementObject("[null]", null);
        assertDataSet(TABLE_PURCHASE_GROUP,"select * from purchase_group",expResult,conn);
    }

    @Test
    public void testUpdatePurchaseGroupStatus() throws Exception {

        AclUserAccreditInfo aclUserAccreditInfo = createAclUserAccreditInfo();
        execSql(conn,"delete from purchase_group");
        prepareData(conn, "purchase/purchaseGroup/preUpdatePruchaseGroupStatusData.xml");
        PurchaseGroup purchaseGroup = createPurchaseGroup();
        purchaseGroupBiz.updatePurchaseStatus(purchaseGroup,aclUserAccreditInfo);
        ReplacementDataSet expResult = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("purchase/purchaseGroup/preUpdatePruchaseGroupStatusData.xml"));
        expResult.addReplacementObject("[null]", null);
        assertDataSet(TABLE_PURCHASE_GROUP,"select * from purchase_group",expResult,conn);

    }

    @Test
    public void testUpdatePurchaseGroup()throws Exception{

        AclUserAccreditInfo aclUserAccreditInfo = createAclUserAccreditInfo();
        execSql(conn,"delete from purchase_group");
        prepareData(conn, "purchase/purchaseGroup/preUpdatePruchaseGroupData.xml");
        PurchaseGroup purchaseGroup = createPurchaseGroup();
        try {
            //测试异常流程
            purchaseGroupBiz.updatePurchaseGroup(purchaseGroup,aclUserAccreditInfo);
            throw new RuntimeException("流程异常!");
        }catch (PurchaseGroupException e){
            if(e.getExceptionEnum().equals(ExceptionEnum.PURCHASE_PURCHASEGROUP_UPDATE_EXCEPTION)){
                log.info("----------采购组的更新正常---------");
            }
        }
        //测试正常流程
        purchaseGroup.setName("hbTest");
        try{
            purchaseGroupBiz.updatePurchaseGroup(purchaseGroup,aclUserAccreditInfo);
        }catch (PurchaseGroupException e){
            if(e.getExceptionEnum().equals(ExceptionEnum.PURCHASE_PURCHASEGROUP_SAVE_EXCEPTION)){
                log.info("部分采购员被取消采购角色,请重新添加");
                return;
            }
        }

        purchaseGroupBiz.updatePurchaseGroup(purchaseGroup,aclUserAccreditInfo);

        IDataSet expDataSet=getXmlDataSet("purchase/purchaseGroup/expUpdatePurchaseGroupData.xml");

        assertItable(TABLE_PURCHASE_GROUP,"select * from purchase_group where id=1",  expDataSet.getTable("purchase_group"),conn);


    }


    private PurchaseGroup createPurchaseGroup(){
        PurchaseGroup purchaseGroup = new PurchaseGroup();
        purchaseGroup.setId(1L);
        purchaseGroup.setName("sone");
        purchaseGroup.setLeaderName("9117");
        purchaseGroup.setLeaderUserId("42168B235B3C44A8AFF154B6F37DA989");
        purchaseGroup.setIsValid("1");
        purchaseGroup.setIsDeleted("0");
        purchaseGroup.setChannelCode("QD001");
        purchaseGroup.setCreateOperator("42168B235B3C44A8AFF154B6F37DA989");
        purchaseGroup.setMemberName("11");
        purchaseGroup.setMemberUserId("11");
        purchaseGroup.setRemark("chilema");
        return purchaseGroup;
    }
    /**
     * 从数据库中导出指定表数据到xml文件中
     * @throws Exception
     */
    @Test
    public void exportData() throws Exception {
        List<String> tableNameList = new ArrayList<>();
        tableNameList.add("purchase_group");
        exportData(tableNameList, "expInsertPurchaseGroupData.xml");
    }


}
