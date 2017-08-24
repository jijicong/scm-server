package org.trc.dbUnit.system;

import org.dbunit.dataset.ReplacementDataSet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.system.IChannelBiz;
import org.trc.domain.System.Channel;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.dbUnit.BaseTestContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sone21 on 2017/8/18.
 * 建立数据库连接-> 备份表 -> 调用Dao层接口 -> 从数据库取实际结果-> 事先准备的期望结果 -> 断言 -> 回滚数据库 -> 关闭数据库连接
 */
public class ChannelDbUnit extends BaseTestContext {

    private Logger log = LoggerFactory.getLogger(ChannelDbUnit.class);

    @Autowired
    private IChannelBiz channelBiz;

    private static final String TABLE_CHANNEL= "channel";


    /**
     * 测试插入操作
     * @throws Exception
     */
    @Test
    public void testInsert() throws Exception {
        AclUserAccreditInfo aclUserAccreditInfo=createAclUserAccreditInfo();
        //删除原数据
        execSql(conn,"delete from channel");
        execSql(conn,"delete from serial");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "system/channel/preInsertChannelData.xml");
        Channel channel = createChannel();
        channelBiz.saveChannel(channel,aclUserAccreditInfo);
        //从xml文件读取期望结果
        ReplacementDataSet expResult = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("system/channel/expInsertChannelData.xml"));
        //空元素的字段需要一个"[null]"占位符，然后用 replacementDataSet.addReplacementObject("[null]", null) 替换成null,占位符可以自定义
        expResult.addReplacementObject("[null]", null);
        //从数据库中查出数据与期望结果作比较
        assertDataSet(TABLE_CHANNEL,"select * from category",expResult,conn);
    }

    /**
     * 创建渠道
     * @return
     */
    private Channel createChannel(){

        Channel channel = new Channel();
        channel.setId(1L);
        channel.setCreateOperator("sone21");
        channel.setIsValid("1");
        channel.setIsDeleted("0");
        channel.setName("H");
        channel.setRemark("L");
        return channel;

    }

    /**
     * 从数据库中导出指定表数据到xml文件中
     * @throws Exception
     */
    @Test
    public void exportData() throws Exception {
        List<String> tableNameList = new ArrayList<>();
        tableNameList.add("channel");
        exportData(tableNameList, "preInsertChannelData.xml");
    }

}
