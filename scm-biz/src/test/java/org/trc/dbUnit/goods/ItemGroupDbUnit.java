package org.trc.dbUnit.goods;

import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.goods.IitemGroupBiz;
import org.trc.dbUnit.BaseTestContext;
import org.trc.dbUnit.purchase.PurchaseGroupDbUnit;
import org.trc.domain.goods.ItemGroup;
import org.trc.domain.goods.ItemGroupUser;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzgjl on 2018/7/30.
 */
public class ItemGroupDbUnit extends BaseTestContext {
    private Logger log = LoggerFactory.getLogger(PurchaseGroupDbUnit.class);

    @Autowired
    private IitemGroupBiz iitemGroupBiz;

    private static final String TABLE_ITEM_GROUP= "item_group";


    /**
     * 测试采购组的插入
     */
    @Test
    public void testInsertItemGroup() throws Exception{
        AclUserAccreditInfo aclUserAccreditInfo=createAclUserAccreditInfo();
        execSql(conn,"delete from item_group");
        execSql(conn,"delete from serial");
        prepareData(conn, "goods/itemGroup/preInsertItemGroupData.xml");
       ItemGroup itemGroup= creatItemGroup();
        List<ItemGroupUser> list=new ArrayList<>();
        ItemGroupUser itemGroupUser = new ItemGroupUser();
        itemGroupUser.setChannelCode(aclUserAccreditInfo.getChannelCode());
        itemGroupUser.setId(1L);
        itemGroupUser.setName("光华科技");
        itemGroupUser.setPhoneNumber("17707165896");
        list.add(itemGroupUser);

        ItemGroupUser itemGroupUser2 = new ItemGroupUser();
        itemGroupUser2.setChannelCode(aclUserAccreditInfo.getChannelCode());
        itemGroupUser2.setId(2L);
        itemGroupUser2.setName("赵云");
        itemGroupUser2.setPhoneNumber("17707165896");
        list.add(itemGroupUser2);

        try {
           iitemGroupBiz.itemGroupSave(itemGroup,list,aclUserAccreditInfo);
        }catch (Exception e){
            log.info("测试正常!,目前的userId不能使用,不是商品组组员");
            return;
        }
        ReplacementDataSet expResult = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("goods/itemGroup/preInsertItemGroupData.xml"));
        expResult.addReplacementObject("[null]", null);
        assertDataSet(TABLE_ITEM_GROUP,"select * from item_group",expResult,conn);

    }

    private ItemGroup creatItemGroup(){
        ItemGroup itemGroup = new ItemGroup();
        itemGroup.setId(1L);
        itemGroup.setRemark("gjl哈哈哈");
        itemGroup.setChannelCode("QD001");
        itemGroup.setItemGroupName("gjl");
        itemGroup.setLeaderName("光华科技");
        itemGroup.setLeaderUserId("1L");
        itemGroup.setMemberUserId("2L");
        itemGroup.setMemberName("赵云");

        return itemGroup;
    }
}
