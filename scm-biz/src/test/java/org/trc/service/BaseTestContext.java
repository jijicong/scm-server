package org.trc.service;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.trc.domain.impower.AclUserAccreditInfo;

/**
 * Created by sone21 on 2017/8/18.
 */
public abstract class BaseTestContext extends BaseTest {

    @Override
    protected void prepareData(IDatabaseConnection conn, String fileName) throws Exception{
        //读取xml文件中的数据信息
        ReplacementDataSet createDataSet = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
        //INSERT TEST DATA
        DatabaseOperation.INSERT.execute(conn, createDataSet);
    };

    protected AclUserAccreditInfo createAclUserAccreditInfo(){
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
