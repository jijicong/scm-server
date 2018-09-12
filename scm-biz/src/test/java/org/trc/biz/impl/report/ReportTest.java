package org.trc.biz.impl.report;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.report.IReportBiz;
import org.trc.form.report.ReportInventoryForm;
import org.trc.util.Pagenation;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/resource-context.xml"})
public class ReportTest {

    @Autowired
    private IReportBiz reportBiz;

    @Test
    public void reportTest(){
        ReportInventoryForm form = new ReportInventoryForm();
        form.setReportType("2");
        form.setDate("2018-08-08");
        form.setStockType("2");
        form.setWarehouseCode("CK00177");
        Object reportPageList = reportBiz.getReportPageList(form, new Pagenation(), true);
        System.out.println(JSON.toJSONString(reportPageList));
    }
}
