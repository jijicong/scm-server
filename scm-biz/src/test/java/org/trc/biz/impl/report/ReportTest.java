package org.trc.biz.impl.report;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.biz.report.IReportBiz;
import org.trc.domain.report.ReportInventory;
import org.trc.form.report.ReportInventoryForm;
import org.trc.service.report.IReportInventoryService;
import org.trc.util.Pagenation;

import java.time.LocalDate;
import java.util.List;

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
    @Autowired
    private IReportInventoryService reportInventoryService;

    @Test
    public void reportTest(){
        ReportInventoryForm form = new ReportInventoryForm();
        form.setReportType("2");
        form.setStartDate("2018-07-19");
        form.setEndDate("2018-07-25");
        form.setStockType("2");
        form.setWarehouseCode("CK00184");
        Object reportPageList = reportBiz.getReportPageList(form, new Pagenation(), false);
        System.out.println(JSON.toJSONString(reportPageList));
    }

    @Test
    public void getReportPageListTest(){
        ReportInventoryForm form = new ReportInventoryForm();
        form.setReportType("1");
        form.setStartDate("2018-07-20");
        form.setEndDate("2018-07-29");
        form.setStockType("1");
        form.setWarehouseCode("CK00184");
        //form.setSkuName("床上用品尺寸");
        //form.setBarCode("38");
        //form.setSkuCode("SP0201708140000159");
        Object reportPageList = reportBiz.getReportDetailPageList(form, new Pagenation(), false);
        System.out.println(JSON.toJSONString(reportPageList));
    }

    @Test
    public void getReportEntryDetailListTest(){
        ReportInventoryForm form = new ReportInventoryForm();
        form.setReportType("1");
        form.setDate("2018-08-01");
        form.setEndDate("2018-09-13");
        form.setStockType("1");
        form.setWarehouseCode("CK00238");
        form.setSkuName("三件套");
        form.setBarCode("38");
        //form.setSkuCode("SP0201708140000159");
        Object reportPageList = reportBiz.getReportDetailPageList(form, new Pagenation(), false);
        System.out.println(JSON.toJSONString(reportPageList));
    }

    @Test
    public void getReportInventoryByWarehouseCodeAndTimeTest(){

        List<ReportInventory> ck00137 = reportInventoryService.getReportInventoryByWarehouseCodeAndTime("CK00137", LocalDate.of(2018, 8, 1), "1");
        System.out.println(JSON.toJSONString(ck00137));
    }
}
