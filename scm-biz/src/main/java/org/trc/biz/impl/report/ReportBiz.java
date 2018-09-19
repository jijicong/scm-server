package org.trc.biz.impl.report;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.biz.report.IReportBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.SellChannel;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.report.*;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ItemTypeEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.enums.report.GoodsTypeEnum;
import org.trc.enums.report.StockOperationTypeEnum;
import org.trc.enums.report.StockTypeEnum;
import org.trc.exception.ParamValidException;
import org.trc.form.report.ReportInventoryForm;
import org.trc.service.System.ISellChannelService;
import org.trc.service.category.ICategoryService;
import org.trc.service.goods.IItemsService;
import org.trc.service.impl.goods.SkusService;
import org.trc.service.report.IReportEntryDetailService;
import org.trc.service.report.IReportInventoryService;
import org.trc.service.report.IReportOutboundDetailService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Description〈报表统计〉
 *
 * @author hzliuwei
 * @create 2018/9/10
 */
@Service("reportBiz")
public class ReportBiz implements IReportBiz {

    private Logger logger = LoggerFactory.getLogger(ReportBiz.class);

    @Autowired
    private IReportInventoryService reportInventoryService;

    @Autowired
    private IWarehouseInfoService warehouseInfoService;

    @Autowired
    private IReportEntryDetailService reportEntryDetailService;

    @Autowired
    private IReportOutboundDetailService reportOutboundDetailService;

    @Autowired
    private SkusService skusService;

    @Autowired
    private IItemsService itemsService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ISupplierService supplierService;

    @Autowired
    private ISellChannelService sellChannelService;

    public static String XLS = "xls";

    public static String ZIP = "zip";

    @Autowired
    private IWarehouseItemInfoService warehouseItemInfoService;

    /**
     * 库存报表首页列表
     *
     * @param date 年份
     * @return
     */
    @Override
    public List<List<ReportInventory>> getPageList(String date) {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear(); //当前年份
        int month = now.getMonth().getValue();
        List<List<ReportInventory>> result = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            //当前月不显示
            if (StringUtils.equals(date, year + "")) {
                if (i < month) {
                    List<ReportInventory> warehouseCodes = getResult(i, date);
                    setWarehouseName(warehouseCodes);
                    result.add(warehouseCodes);
                }
            } else {
                List<ReportInventory> warehouseCodes = getResult(i, date);
                setWarehouseName(warehouseCodes);
                result.add(warehouseCodes);
            }
        }
        //Collections.reverse(result);
        return result;
    }

    /**
     * 具体类型报表列表
     *
     * @param form
     * @param page
     * @param flag 是否分页
     * @return
     */
    @Override
    public Object getReportPageList(ReportInventoryForm form, Pagenation page, boolean flag) {

        //总库存查询
        if (StringUtils.equals(form.getReportType(), ZeroToNineEnum.ONE.getCode())) {
            return getReportInventoryList(form, (Pagenation<ReportInventory>) page, flag);
        }
        //入库明细查询
        else if (StringUtils.equals(form.getReportType(), ZeroToNineEnum.TWO.getCode())) {
            return getReportEntryDetailList(form, (Pagenation<ReportEntryDetail>) page, flag);
        }
        //出库明细查询
        else if (StringUtils.equals(form.getReportType(), ZeroToNineEnum.THREE.getCode())) {
            return getReportOutboundDetailList(form, (Pagenation<ReportOutboundDetail>) page, flag);
        }

        if (flag) {
            return new Pagenation<>();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Response downloadAllForWarehouse(ReportInventoryForm form)throws Exception {
        String warehouseCode = form.getWarehouseCode();
        String date = form.getDate();
        String reportDate = DateUtils.getYM(date) + "";
        AssertUtil.notBlank(warehouseCode, "仓库编码不能为空");
        AssertUtil.notBlank(date, "查询周期不能为空");

        logger.info(String.format("开始下载仓库编码为%s,日期为%s的全报表!", warehouseCode, date));

        //仓库名称
        WarehouseInfo warehouseInfo = warehouseInfoService.selectOneByCode(warehouseCode);
        if (warehouseInfo == null) {
            String msg = String.format("不存在此%s仓库编码的仓库", warehouseCode);
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        //获取仓库名称
        String warehouseName = warehouseInfo.getWarehouseName();

        ZipOutputStream zipOutputStream = null;
        DataOutputStream dataOutputStream = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(stream));
            //设置压缩方式
            zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
            //获取信息
            List<ReportExcelDetail> reportExcelDetails = this.getReportExcelDetail(form, warehouseName, reportDate);
            //循环将文件写入压缩流
            for (ReportExcelDetail reportExcelDetail : reportExcelDetails) {
                zipOutputStream.putNextEntry(new ZipEntry(reportExcelDetail.getFileName() + SupplyConstants.Symbol.FILE_NAME_SPLIT + XLS));
                dataOutputStream = new DataOutputStream(zipOutputStream);
                byte[] bytes = reportExcelDetail.getStream().toByteArray();
                InputStream inputStream = new ByteArrayInputStream(bytes);
                IOUtils.copy(inputStream, dataOutputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String.format("下载用户合同信息异常,仓库编码为%s,日期为%s", warehouseCode, date);
            logger.error(msg);
            throw new RuntimeException(msg);
        } finally {
            try {
                dataOutputStream.flush();
                dataOutputStream.close();
                zipOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                String msg = String.format("下载用户合同信息异常,仓库编码为%s,日期为%s", warehouseCode, date);
                logger.error(msg);
                throw new RuntimeException(msg);
            }
        }

        String zipName = String.format("【%s】库存报表%s%s%s", warehouseName, date,
                SupplyConstants.Symbol.FILE_NAME_SPLIT, ZIP);

        logger.info(String.format("仓库编码为%s,日期为%s的全报表下载打包完成!", warehouseCode, date));
        return javax.ws.rs.core.Response.ok(stream.toByteArray()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(zipName,"UTF-8")).type(MediaType.APPLICATION_OCTET_STREAM)
                .header("Cache-Control", "no-cache").build();
    }

    @Override
    public Response downloadCurrentForWarehouse(ReportInventoryForm form, boolean isOtherReport) {
        String warehouseCode = form.getWarehouseCode();
        String date = form.getDate();
        String reportDate = DateUtils.getYM(date) + "";
        String reportType = form.getReportType();
        String stockType = form.getStockType();
        AssertUtil.notBlank(warehouseCode, "仓库编码不能为空");
        AssertUtil.notBlank(reportType, "报表类型不能为空");
        AssertUtil.notBlank(stockType, "库存类型不能为空");


        logger.info(String.format("开始下载仓库编码为%s,日期为%s,报表类型为%s,库存类型为%s的报表!",
                warehouseCode, date, form.getReportType(), form.getStockType()));

        //仓库名称
        WarehouseInfo warehouseInfo = warehouseInfoService.selectOneByCode(warehouseCode);
        if (warehouseInfo == null) {
            String msg = String.format("不存在此%s仓库编码的仓库", warehouseCode);
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        //获取仓库名称
        String warehouseName = warehouseInfo.getWarehouseName();

        try {
            String sheetName = this.getExcelName(form, warehouseName, reportDate);
            HSSFWorkbook hssfWorkbook = null;
            if (StringUtils.equals(ZeroToNineEnum.ONE.getCode(), reportType)) {
                hssfWorkbook = this.reportExcel(
                        (List<ReportInventory>) this.getReportPageList(form, null, false),
                        sheetName, reportType);
            } else if (StringUtils.equals(ZeroToNineEnum.TWO.getCode(), reportType)) {
                hssfWorkbook = this.reportExcel(
                        (List<ReportEntryDetail>) this.getReportPageList(form, null, false),
                        sheetName, reportType);
            } else {
                hssfWorkbook = this.reportExcel(
                        (List<ReportOutboundDetail>) this.getReportPageList(form, null, false),
                        sheetName, reportType);
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            hssfWorkbook.write(stream);

            String fileName = "";
            if(isOtherReport){
                fileName = "库存报表导出" + LocalDate.now().toString() + SupplyConstants.Symbol.FILE_NAME_SPLIT + XLS;
            }else{
                fileName = sheetName + SupplyConstants.Symbol.FILE_NAME_SPLIT + XLS;
            }

            logger.info(String.format("下载仓库编码为%s,日期为%s,报表类型为%s,库存类型为%s的报表完成!",
                    warehouseCode, date, form.getReportType(), form.getStockType()));

            return javax.ws.rs.core.Response.ok(stream.toByteArray()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8")).type(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Cache-Control", "no-cache").build();
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String.format("下载报表异常异常仓库编码为%s,日期为%s,报表类型为%s,库存类型为%s!",
                    warehouseCode, date, form.getReportType(), form.getStockType());
            logger.error(msg + e.getMessage(), e);
            return ResultUtil.createfailureResult(
                    Integer.parseInt(ExceptionEnum.FILE_DOWNLOAD_EXCEPTION.getCode()), msg);
        }
    }

    private String getExcelName(ReportInventoryForm form, String warehouseName, String date) {
        if (StringUtils.equals(form.getStockType(), StockTypeEnum.QUALITY.getCode())) {
            if (StringUtils.equals(form.getReportType(), ZeroToNineEnum.ONE.getCode())) {
                return String.format("【%s】正品总库存%s", warehouseName, date);
            } else if (StringUtils.equals(form.getReportType(), ZeroToNineEnum.TWO.getCode())) {
                return String.format("【%s】正品入库明细%s", warehouseName, date);
            } else {
                return String.format("【%s】正品出库明细%s", warehouseName, date);
            }
        } else {
            if (StringUtils.equals(form.getReportType(), ZeroToNineEnum.ONE.getCode())) {
                return String.format("【%s】残品总库存%s", warehouseName, date);
            } else if (StringUtils.equals(form.getReportType(), ZeroToNineEnum.TWO.getCode())) {
                return String.format("【%s】残品入库明细%s", warehouseName, date);
            } else {
                return String.format("【%s】残品出库明细%s", warehouseName, date);
            }
        }
    }

    /**
     * 获取全部信息
     *
     * @param form
     * @param warehouseName
     * @return
     */
    private List<ReportExcelDetail> getReportExcelDetail(ReportInventoryForm form, String warehouseName, String reportDate) throws Exception {
        List<ReportExcelDetail> reportExcelDetails = new ArrayList<>();
        String date = form.getDate();

        //正品总库存
        form.setStockType(StockTypeEnum.QUALITY.getCode());
        form.setReportType(ZeroToNineEnum.ONE.getCode());
        List<ReportInventory> reportInventoryList = (List<ReportInventory>) this.getReportPageList(form, null, false);
        String fileName = String.format("【%s】正品总库存%s", warehouseName, reportDate);
        ReportExcelDetail reportExcelDetail = new ReportExcelDetail();
        HSSFWorkbook hssfWorkbook = this.reportExcel(reportInventoryList, fileName, ZeroToNineEnum.ONE.getCode());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        hssfWorkbook.write(stream);
        reportExcelDetail.setStream(stream);
        reportExcelDetail.setFileName(fileName);
        reportExcelDetails.add(reportExcelDetail);

        //残品总库存
        form.setStockType(StockTypeEnum.SUBSTANDARD.getCode());
        form.setReportType(ZeroToNineEnum.ONE.getCode());
        reportInventoryList = (List<ReportInventory>) this.getReportPageList(form, null, false);
        fileName = String.format("【%s】残品总库存%s", warehouseName, reportDate);
        ReportExcelDetail reportExcelDetail2 = new ReportExcelDetail();
        hssfWorkbook = this.reportExcel(reportInventoryList, fileName, ZeroToNineEnum.ONE.getCode());
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        hssfWorkbook.write(stream2);
        reportExcelDetail2.setStream(stream2);
        reportExcelDetail2.setFileName(fileName);
        reportExcelDetails.add(reportExcelDetail2);

        //正品入库明细
        form.setStockType(StockTypeEnum.QUALITY.getCode());
        form.setReportType(ZeroToNineEnum.TWO.getCode());
        List<ReportEntryDetail> reportEntryDetailList = (List<ReportEntryDetail>) this.getReportPageList(form, null, false);
        fileName = String.format("【%s】正品入库明细%s", warehouseName, reportDate);
        ReportExcelDetail reportExcelDetail3 = new ReportExcelDetail();
        hssfWorkbook = this.reportExcel(reportEntryDetailList, fileName, ZeroToNineEnum.TWO.getCode());
        ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
        hssfWorkbook.write(stream3);
        reportExcelDetail3.setStream(stream3);
        reportExcelDetail3.setFileName(fileName);
        reportExcelDetails.add(reportExcelDetail3);

        //残品入库明细
        form.setStockType(StockTypeEnum.SUBSTANDARD.getCode());
        form.setReportType(ZeroToNineEnum.TWO.getCode());
        reportEntryDetailList = (List<ReportEntryDetail>) this.getReportPageList(form, null, false);
        fileName = String.format("【%s】残品入库明细%s", warehouseName, reportDate);
        ReportExcelDetail reportExcelDetail4 = new ReportExcelDetail();
        hssfWorkbook = this.reportExcel(reportEntryDetailList, fileName, ZeroToNineEnum.TWO.getCode());
        ByteArrayOutputStream stream4 = new ByteArrayOutputStream();
        hssfWorkbook.write(stream4);
        reportExcelDetail4.setStream(stream4);
        reportExcelDetail4.setFileName(fileName);
        reportExcelDetails.add(reportExcelDetail4);

        //正品出库明细
        form.setStockType(StockTypeEnum.QUALITY.getCode());
        form.setReportType(ZeroToNineEnum.THREE.getCode());
        List<ReportOutboundDetail> reportOutboundDetailList = (List<ReportOutboundDetail>) this.getReportPageList(form, null, false);
        fileName = String.format("【%s】正品出库明细%s", warehouseName, reportDate);
        ReportExcelDetail reportExcelDetail5 = new ReportExcelDetail();
        hssfWorkbook = this.reportExcel(reportOutboundDetailList, fileName, ZeroToNineEnum.THREE.getCode());
        ByteArrayOutputStream stream5 = new ByteArrayOutputStream();
        hssfWorkbook.write(stream5);
        reportExcelDetail5.setStream(stream5);
        reportExcelDetail5.setFileName(fileName);
        reportExcelDetails.add(reportExcelDetail5);

        //残品出库明细
        form.setStockType(StockTypeEnum.SUBSTANDARD.getCode());
        form.setReportType(ZeroToNineEnum.THREE.getCode());
        reportOutboundDetailList = (List<ReportOutboundDetail>) this.getReportPageList(form, null, false);
        fileName = String.format("【%s】残品出库明细%s", warehouseName, reportDate);
        ReportExcelDetail reportExcelDetail6 = new ReportExcelDetail();
        hssfWorkbook = this.reportExcel(reportOutboundDetailList, fileName, ZeroToNineEnum.THREE.getCode());
        ByteArrayOutputStream stream6 = new ByteArrayOutputStream();
        hssfWorkbook.write(stream6);
        reportExcelDetail6.setStream(stream6);
        reportExcelDetail6.setFileName(fileName);
        reportExcelDetails.add(reportExcelDetail6);

        return reportExcelDetails;
    }

    /**
     * 特殊查询报表列表
     *
     * @param form
     * @param page
     * @param flag 是否分页
     * @return
     */
    @Override
    public Object getReportDetailPageList(ReportInventoryForm form, Pagenation page, boolean flag) {

        if (StringUtils.isBlank(form.getDate()) && (StringUtils.isBlank(form.getStartDate()) && StringUtils.isBlank(form.getEndDate()))
                && !StringUtils.isBlank(form.getDate()) && (!StringUtils.isBlank(form.getStartDate()) && !StringUtils.isBlank(form.getEndDate()))) {
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "参数校验异常");
        }

        return getReportPageList(form, page, flag);
    }

    /**
     * 【仓库信息管理】中“SKU数量”大于0且“货主仓库状态”为“通知成功”的所有仓库
     *
     * @return
     */
    @Override
    public List<WarehouseInfo> getWarehouseList() {
        return reportInventoryService.selectWarehouseInfoList();
    }

    /**
     * @param form
     * @param page
     * @param flag 是否分页
     * @return
     */
    private Object getReportOutboundDetailList(ReportInventoryForm form, Pagenation<ReportOutboundDetail> page, boolean flag) {

        //校验时间范围
        checkParam(form);

        Example example = new Example(ReportOutboundDetail.class);
        Example.Criteria criteria = example.createCriteria();

        //设置查询条件
        setSelectExample(criteria, form);

        if (StringUtils.isNotBlank(form.getDate())) {
            criteria.andCondition("DATE_FORMAT( `outbound_time`, '%Y%m' ) = DATE_FORMAT( '" + form.getDate() + "' , '%Y%m' )");
        } else {
            criteria.andBetween("outboundTime", form.getStartDate(), form.getEndDate() + " 23:59:59");
        }

        if (StringUtils.isNotBlank(form.getOutboundOrderCode())) {
            criteria.andEqualTo("outboundOrderCode", form.getOutboundOrderCode());
        }

        if (StringUtils.isNotBlank(form.getWarehouseOutboundOrderCode())) {
            criteria.andEqualTo("warehouseOutboundOrderCode", form.getWarehouseOutboundOrderCode());
        }

        if (StringUtils.isNotBlank(form.getSellChannelCode())) {
            criteria.andEqualTo("sellChannelCode", form.getSellChannelCode());
        }

        criteria.andEqualTo("stockType", form.getStockType());

        example.setOrderByClause("outbound_time, outbound_order_code DESC");

        if (flag) {
            Pagenation<ReportOutboundDetail> pagination = reportOutboundDetailService.pagination(example, page, new QueryModel());

            if (CollectionUtils.isEmpty(pagination.getResult())) {
                return new Pagenation<>();
            }

            setOutboundResultDetail(pagination.getResult(), form);
            return pagination;
        } else {
            List<ReportOutboundDetail> result = reportOutboundDetailService.selectByExample(example);

            if (CollectionUtils.isEmpty(result)) {
                return new ArrayList<>();
            }
            setOutboundResultDetail(result, form);
            return result;
        }
    }

    private void setSelectExample(Example.Criteria criteria, ReportInventoryForm form) {

        criteria.andEqualTo("warehouseCode", form.getWarehouseCode());


        if (StringUtils.isNotBlank(form.getSkuCode())) {
            criteria.andIn("skuCode", Collections.singleton(form.getSkuCode()));
        }

        if (StringUtils.isNotBlank(form.getBarCode())) {
            List<String> barCodes = Arrays.asList(form.getBarCode().split(","));
            String conditionSql = setConditionSql(barCodes);
            criteria.andCondition(conditionSql);
        }

        if (StringUtils.isNotBlank(form.getSkuName())) {
            Example example = new Example(WarehouseItemInfo.class);
            Example.Criteria criteria1 = example.createCriteria();
            criteria1.andEqualTo("warehouseCode", form.getWarehouseCode());
            criteria1.andLike("itemName", "%" + form.getSkuName() + "%");
            List<WarehouseItemInfo> warehouseInfos = warehouseItemInfoService.selectByExample(example);
            if (!CollectionUtils.isEmpty(warehouseInfos)) {
                List<String> skuCodes = warehouseInfos.stream().map(WarehouseItemInfo::getSkuCode).collect(Collectors.toList());
                criteria.andIn("skuCode", skuCodes);
            }
        }


    }

    private void setOutboundResultDetail(List<ReportOutboundDetail> result, ReportInventoryForm form) {
        for (ReportOutboundDetail reportOutboundDetail : result) {

            //销售出库
            if (StringUtils.equals(reportOutboundDetail.getOperationType(), StockOperationTypeEnum.SALES_OF_OUTBOUND.getCode())) {
                if (StringUtils.equals(form.getStockType(), StockTypeEnum.SUBSTANDARD.getCode())) {
                    reportOutboundDetail.setStockType(StockTypeEnum.SUBSTANDARD.getCode());
                    reportOutboundDetail.setOutboundQuantity(0L);
                    reportOutboundDetail.setSalesPrice(new BigDecimal(0));
                    reportOutboundDetail.setPayment(new BigDecimal(0));
                }
            } else {
                if (StringUtils.equals(form.getStockType(), StockTypeEnum.SUBSTANDARD.getCode())) {
                    reportOutboundDetail.setStockType(StockTypeEnum.SUBSTANDARD.getCode());
                }
            }

            reportOutboundDetail.setResidualQuantity(reportOutboundDetail.getOutboundQuantity() - reportOutboundDetail.getRealQuantity());
            //仓库名称
            WarehouseInfo warehouseInfo = warehouseInfoService.selectOneByCode(reportOutboundDetail.getWarehouseCode());
            if (warehouseInfo != null) {
                reportOutboundDetail.setWarehouseName(warehouseInfo.getWarehouseName());
            }

            //sku名称
            Skus skus = skusService.selectSkuBySkuCode(reportOutboundDetail.getSkuCode());
            if (skus != null) {
                reportOutboundDetail.setSkuName(skus.getSkuName());
                Items item = itemsService.selectOneBySpuCode(skus.getSpuCode());
                if (item != null) {
                    //商品类别
                    if (StringUtils.equals(item.getItemType(), ItemTypeEnum.XIAOTAI.getCode())) {
                        reportOutboundDetail.setGoodsType(ZeroToNineEnum.ONE.getCode());
                    } else if (StringUtils.equals(item.getItemType(), ItemTypeEnum.NON_XIAOTAI.getCode())) {
                        reportOutboundDetail.setGoodsType(ZeroToNineEnum.TWO.getCode());
                    }
                }
            }

            //销售渠道
            if(StringUtils.isNotBlank(reportOutboundDetail.getSellCode())){
                SellChannel sellChannel = sellChannelService.selectSellByCode(reportOutboundDetail.getSellCode());
                if (sellChannel != null) {
                    reportOutboundDetail.setSellName(sellChannel.getSellName());
                }
            }
        }
    }

    /**
     * @param form
     * @param page
     * @param flag 是否分页
     * @return
     */
    private Object getReportEntryDetailList(ReportInventoryForm form, Pagenation<ReportEntryDetail> page, boolean flag) {

        //校验时间范围
        checkParam(form);

        Example example = new Example(ReportEntryDetail.class);
        Example.Criteria criteria = example.createCriteria();

        //设置查询条件
        setSelectExample(criteria, form);

        if (StringUtils.isNotBlank(form.getDate())) {
            criteria.andCondition("DATE_FORMAT( `entry_time`, '%Y%m' ) = DATE_FORMAT( '" + form.getDate() + "' , '%Y%m' )");
        } else {
            criteria.andBetween("entryTime", form.getStartDate(), form.getEndDate() + " 23:59:59");
        }

        if (StringUtils.isNotBlank(form.getPurchaseOrderCode())) {
            criteria.andEqualTo("orderCode", form.getPurchaseOrderCode());
        }

        if (StringUtils.isNotBlank(form.getWarehousePurchaseOrderCode())) {
            criteria.andEqualTo("warehouseOrderCode", form.getWarehousePurchaseOrderCode());
        }

        example.setOrderByClause("entry_time, order_code DESC");

        List<ReportEntryDetail> result = reportEntryDetailService.selectByExample(example);
        if (flag) {

            if (CollectionUtils.isEmpty(result)) {
                return new Pagenation<>();
            }

            List<ReportEntryDetail> filtrationList = setEntryResultDetail(result, form);
            return pagingResult(result, filtrationList, page);

        } else {

            if (CollectionUtils.isEmpty(result)) {
                return new ArrayList<>();
            }
            setEntryResultDetail(result, form);
            return result;
        }

    }

    private Pagenation<ReportEntryDetail> pagingResult(List<ReportEntryDetail> result, List<ReportEntryDetail> filtrationList, Pagenation<ReportEntryDetail> page) {
        if (!CollectionUtils.isEmpty(filtrationList)) {
            Iterator<ReportEntryDetail> iterator = result.iterator();
            while (iterator.hasNext()){
                ReportEntryDetail reportEntryDetail = iterator.next();
                for(ReportEntryDetail list : filtrationList){
                    if(StringUtils.equals(list.getSkuCode(), reportEntryDetail.getSkuCode())
                            && StringUtils.equals(list.getOrderCode(), reportEntryDetail.getOrderCode())){
                        iterator.remove();
                    }
                }
            }
            Map<String, Object> pagingResultMap = PagingResultMap.getPagingResultMap(result, page.getPageNo(), page.getPageSize());
            page.setResult((List<ReportEntryDetail>) pagingResultMap.get("result"));
            page.setTotalCount(Long.valueOf(pagingResultMap.get("totalRowNum").toString()));
            return page;
        } else {
            Map<String, Object> pagingResultMap = PagingResultMap.getPagingResultMap(result, page.getPageNo(), page.getPageSize());
            page.setResult((List<ReportEntryDetail>) pagingResultMap.get("result"));
            page.setTotalCount(Long.valueOf(pagingResultMap.get("totalRowNum").toString()));
            return page;
        }
    }

    private List<ReportEntryDetail> setEntryResultDetail(List<ReportEntryDetail> result, ReportInventoryForm form) {
        List<ReportEntryDetail> details = new ArrayList<>();
        for (ReportEntryDetail reportEntryDetail : result) {

            //查询库存类型与实际单据入库类型不一致，初始化数据
            if (!StringUtils.equals(form.getStockType(), reportEntryDetail.getStockType())) {
                reportEntryDetail.setEntryQuantity(0L);

                //采购入库
                if (StringUtils.equals(reportEntryDetail.getOperationType(), StockOperationTypeEnum.PURCHASE.getCode())) {
                    if (StringUtils.equals(form.getStockType(), StockTypeEnum.SUBSTANDARD.getCode())) {
                        reportEntryDetail.setPrice(new BigDecimal(0));
                        reportEntryDetail.setRealQuantity(reportEntryDetail.getDefectiveQuantity());
                        reportEntryDetail.setResidualQuantity(reportEntryDetail.getEntryQuantity() - reportEntryDetail.getDefectiveQuantity());
                        reportEntryDetail.setRemark("正品入库：" + reportEntryDetail.getNormalQuantity());
                        //计划入库和实际入库都为0，不生成报表记录
                        if (reportEntryDetail.getEntryQuantity() == reportEntryDetail.getRealQuantity()) {
                            details.add(reportEntryDetail);
                        }
                    } else {
                        reportEntryDetail.setRealQuantity(reportEntryDetail.getNormalQuantity());
                        reportEntryDetail.setRemark("残品入库：" + reportEntryDetail.getDefectiveQuantity());
                        reportEntryDetail.setResidualQuantity(reportEntryDetail.getEntryQuantity() - reportEntryDetail.getNormalQuantity());
                    }
                } else {
                    if (StringUtils.equals(form.getStockType(), StockTypeEnum.SUBSTANDARD.getCode())) {
                        reportEntryDetail.setStockType(StockTypeEnum.SUBSTANDARD.getCode());
                        reportEntryDetail.setRealQuantity(reportEntryDetail.getDefectiveQuantity());
                        reportEntryDetail.setResidualQuantity(reportEntryDetail.getEntryQuantity() - reportEntryDetail.getDefectiveQuantity());
                        reportEntryDetail.setRemark("正品入库：" + reportEntryDetail.getNormalQuantity());
                        //计划入库和实际入库都为0，不生成报表记录
                        if (reportEntryDetail.getEntryQuantity() == reportEntryDetail.getRealQuantity()) {
                            details.add(reportEntryDetail);
                        }
                    } else {
                        reportEntryDetail.setStockType(StockTypeEnum.QUALITY.getCode());
                        reportEntryDetail.setRealQuantity(reportEntryDetail.getNormalQuantity());
                        reportEntryDetail.setRemark("残品入库：" + reportEntryDetail.getDefectiveQuantity());
                        reportEntryDetail.setResidualQuantity(reportEntryDetail.getEntryQuantity() - reportEntryDetail.getNormalQuantity());
                        //计划入库和实际入库都为0，不生成报表记录
                        if (reportEntryDetail.getEntryQuantity() == reportEntryDetail.getRealQuantity()) {
                            details.add(reportEntryDetail);
                        }
                    }
                }
            } else {
                if (StringUtils.equals(form.getStockType(), StockTypeEnum.SUBSTANDARD.getCode())) {
                    reportEntryDetail.setStockType(StockTypeEnum.SUBSTANDARD.getCode());
                    reportEntryDetail.setRealQuantity(reportEntryDetail.getDefectiveQuantity());
                    reportEntryDetail.setResidualQuantity(reportEntryDetail.getEntryQuantity() - reportEntryDetail.getDefectiveQuantity());
                    reportEntryDetail.setRemark("正品入库：" + reportEntryDetail.getNormalQuantity());
                } else {
                    reportEntryDetail.setStockType(StockTypeEnum.QUALITY.getCode());
                    reportEntryDetail.setRealQuantity(reportEntryDetail.getNormalQuantity());
                    reportEntryDetail.setRemark("残品入库：" + reportEntryDetail.getDefectiveQuantity());
                    reportEntryDetail.setResidualQuantity(reportEntryDetail.getEntryQuantity() - reportEntryDetail.getNormalQuantity());
                }
            }

            //仓库名称
            WarehouseInfo warehouseInfo = warehouseInfoService.selectOneByCode(reportEntryDetail.getWarehouseCode());
            if (warehouseInfo != null) {
                reportEntryDetail.setWarehouseName(warehouseInfo.getWarehouseName());
            }
            //sku名称
            Skus skus = skusService.selectSkuBySkuCode(reportEntryDetail.getSkuCode());
            if (skus != null) {
                reportEntryDetail.setSkuName(skus.getSkuName());
                Items item = itemsService.selectOneBySpuCode(skus.getSpuCode());
                if (item != null) {
                    //商品类别
                    if (StringUtils.equals(item.getItemType(), ItemTypeEnum.XIAOTAI.getCode())) {
                        reportEntryDetail.setGoodsType(ZeroToNineEnum.ONE.getCode());
                    } else if (StringUtils.equals(item.getItemType(), ItemTypeEnum.NON_XIAOTAI.getCode())) {
                        reportEntryDetail.setGoodsType(ZeroToNineEnum.TWO.getCode());
                    }
                }
            }
        }
        return details;
    }

    /**
     * @param form
     * @param page
     * @param flag 是否分页
     * @return
     */
    private Object getReportInventoryList(ReportInventoryForm form, Pagenation<ReportInventory> page, boolean flag) {

        //校验时间范围
        checkParam(form);

        List<String> skuCodes = new ArrayList<>();
        if (StringUtils.isNotBlank(form.getSkuName())) {
            Example example = new Example(WarehouseItemInfo.class);
            Example.Criteria criteria1 = example.createCriteria();
            criteria1.andEqualTo("warehouseCode", form.getWarehouseCode());
            criteria1.andLike("itemName", "%" + form.getSkuName() + "%");
            List<WarehouseItemInfo> warehouseInfos = warehouseItemInfoService.selectByExample(example);
            if (!CollectionUtils.isEmpty(warehouseInfos)) {
                skuCodes = warehouseInfos.stream().map(WarehouseItemInfo::getSkuCode).collect(Collectors.toList());
            }
        }

        if (flag) {
            //分组查询数据
            Page pages = PageHelper.startPage(page.getPageNo(), page.getPageSize());
            reportInventoryService.selectReportInventoryLimit(form, skuCodes);

            page.setTotalCount(pages.getTotal());

            if (CollectionUtils.isEmpty(pages.getResult())) {
                return new Pagenation<>();
            }

            //统计数据
            List<ReportInventory> statisticsDate = getStatisticsDate(pages.getResult(), form);
            setResultDetail(statisticsDate);
            page.setResult(statisticsDate);
            return page;
        } else {
            List<ReportInventory> reportInventoryList = reportInventoryService.selectReportInventoryLimit(form, skuCodes);

            if (CollectionUtils.isEmpty(reportInventoryList)) {
                return new ArrayList<>();
            }

            //统计数据
            List<ReportInventory> statisticsDate = getStatisticsDate(reportInventoryList, form);
            setResultDetail(statisticsDate);
            return statisticsDate;
        }

    }

    private void checkParam(ReportInventoryForm form) {
        if (StringUtils.isNotBlank(form.getStartDate()) && StringUtils.isNotBlank(form.getEndDate())) {
            LocalDate startDate = LocalDate.parse(form.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endDate = LocalDate.parse(form.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Period between = Period.between(startDate, endDate);
            if (between.getDays() > 13) {
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "日期间隔不能大于两周");
            }
        }
    }

    private List<ReportInventory> getStatisticsDate(List<ReportInventory> result, ReportInventoryForm form) {

        Set<String> warehouseCodes = result.stream().map(ReportInventory::getWarehouseCode).collect(Collectors.toSet());
        Set<String> skuCodes = result.stream().map(ReportInventory::getSkuCode).collect(Collectors.toSet());
        Example example = new Example(ReportInventory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("stockType", form.getStockType());

        if (StringUtils.isNotBlank(form.getDate())) {
            criteria.andCondition("DATE_FORMAT( `periods`, '%Y%m' ) = DATE_FORMAT( '" + form.getDate() + "' , '%Y%m' )");
        } else {
            criteria.andBetween("periods", form.getStartDate(), form.getEndDate());
        }

        if (StringUtils.isNotBlank(form.getBarCode())) {
            List<String> barCodes = Arrays.asList(form.getBarCode().split(","));
            String conditionSql = setConditionSql(barCodes);
            criteria.andCondition(conditionSql);
            //criteria.andEqualTo("barCode", form.getBarCode());
        }
        criteria.andIn("warehouseCode", warehouseCodes);
        criteria.andIn("skuCode", skuCodes);
        example.setOrderByClause("periods ASC");
        List<ReportInventory> reportInventorys = reportInventoryService.selectByExample(example);

        for (ReportInventory reportInventory : result) {

            long outboundQuantity = 0;  //销售出库数量
            BigDecimal outboundTotalAmount = new BigDecimal(0); //销售出库实付总金额（元）
            BigDecimal purchaseTotalAmount = new BigDecimal(0); //含税采购总金额（元）
            BigDecimal suppliderReturnTotalAmount = new BigDecimal(0);  //退供应商出库金额（元）
            long salesReturnQuantity = 0;   //退货入库数量
            long purchaseQuantity = 0;  //采购入库数量
            long supplierReturnOutboundQuantity = 0;    //退供应商出库数量
            long allocateInQuantity = 0;    //调拨入库数量
            long allocateOutQuantity = 0;   //调拨出库数量
            long inventoryProfitQuantity = 0;   //盘盈入库数量
            long inventoryLossesQuantity = 0;   //盘亏出库数量
            long defectiveToNormal = 0;   //残品转正品数量
            long normalToDefective = 0;   //正品转残品数量
            long otherIn = 0;   //其他入库
            long otherOut = 0;   //其他出库

            long entryTotalQuantity = 0;    //本期入库总数量
            long outboundTotalQuantity = 0; //本期出库总数量
            long balanceTotalQuantity = 0; //期末结存数量
            long initialQuantity = 0;

            for (ReportInventory inventory : reportInventorys) {
                if (StringUtils.equals(reportInventory.getWarehouseCode(), inventory.getWarehouseCode())
                        && StringUtils.equals(reportInventory.getSkuCode(), inventory.getSkuCode())) {
                    outboundQuantity += inventory.getOutboundQuantity();
                    outboundTotalAmount = outboundTotalAmount.add(inventory.getOutboundTotalAmount());
                    purchaseTotalAmount = purchaseTotalAmount.add(inventory.getPurchaseTotalAmount());
                    suppliderReturnTotalAmount = suppliderReturnTotalAmount.add(inventory.getSuppliderReturnTotalAmount());
                    salesReturnQuantity += inventory.getSalesReturnQuantity();
                    purchaseQuantity += inventory.getPurchaseQuantity();
                    supplierReturnOutboundQuantity += inventory.getSupplierReturnOutboundQuantity();
                    allocateInQuantity += inventory.getAllocateInQuantity();
                    allocateOutQuantity += inventory.getAllocateOutQuantity();
                    inventoryProfitQuantity += inventory.getInventoryProfitQuantity();
                    inventoryLossesQuantity += inventory.getInventoryLossesQuantity();
                    defectiveToNormal += inventory.getDefectiveToNormal();
                    normalToDefective += inventory.getNormalToDefective();
                    otherIn += inventory.getOtherIn();
                    otherOut += inventory.getOtherOut();

                    initialQuantity = inventory.getInitialQuantity();
                    //默认最后一天的期末数量
                    balanceTotalQuantity = inventory.getBalanceTotalQuantity();

                    reportInventory.setGoodsType(inventory.getGoodsType());
                    reportInventory.setSpecInfo(inventory.getSpecInfo());
                    reportInventory.setBarCode(inventory.getBarCode());
                    reportInventory.setCategoryId(inventory.getCategoryId());
                }
            }
            reportInventory.setOutboundQuantity(outboundQuantity);
            reportInventory.setOutboundTotalAmount(outboundTotalAmount);
            reportInventory.setPurchaseTotalAmount(purchaseTotalAmount);
            reportInventory.setSuppliderReturnTotalAmount(suppliderReturnTotalAmount);
            reportInventory.setSalesReturnQuantity(salesReturnQuantity);
            reportInventory.setPurchaseQuantity(purchaseQuantity);
            reportInventory.setSupplierReturnOutboundQuantity(supplierReturnOutboundQuantity);
            reportInventory.setAllocateInQuantity(allocateInQuantity);
            reportInventory.setAllocateOutQuantity(allocateOutQuantity);
            reportInventory.setInventoryLossesQuantity(inventoryLossesQuantity);
            reportInventory.setInventoryProfitQuantity(inventoryProfitQuantity);
            reportInventory.setDefectiveToNormal(defectiveToNormal);
            reportInventory.setNormalToDefective(normalToDefective);
            reportInventory.setOtherIn(otherIn);
            reportInventory.setOtherOut(otherOut);

            if (StringUtils.equals(form.getStockType(), StockTypeEnum.QUALITY.getCode())) {
                reportInventory.setStockType(StockTypeEnum.QUALITY.getCode());
            } else {
                reportInventory.setStockType(StockTypeEnum.SUBSTANDARD.getCode());
            }

            //默认第一天的期初数量
            reportInventory.setInitialQuantity(initialQuantity);
            //默认最后一天的期末数量
            reportInventory.setBalanceTotalQuantity(balanceTotalQuantity);

            reportInventory.setEntryTotalQuantity(salesReturnQuantity + purchaseQuantity + allocateInQuantity + inventoryProfitQuantity);
            reportInventory.setOutboundTotalQuantity(outboundQuantity + supplierReturnOutboundQuantity + inventoryLossesQuantity);
        }

        return result;
    }

    /**
     * sql条件拼接
     *
     * @param barCodes
     * @return
     */
    private String setConditionSql(List<String> barCodes) {
        StringBuilder sql = new StringBuilder("(");
        for (String bc : barCodes) {
            sql.append("FIND_IN_SET('" + bc + "', `bar_code`) OR ");
        }
        String substring = sql.substring(0, sql.lastIndexOf(")") + 1);
        return substring + ")";
    }

    private void setResultDetail(List<ReportInventory> result) {
        for (ReportInventory reportInventory : result) {
            //仓库名称
            WarehouseInfo warehouseInfo = warehouseInfoService.selectOneByCode(reportInventory.getWarehouseCode());
            if (warehouseInfo != null) {
                reportInventory.setWarehouseName(warehouseInfo.getWarehouseName());
            }
            //sku名称
            Skus skus = skusService.selectSkuBySkuCode(reportInventory.getSkuCode());
            if (skus != null) {
                reportInventory.setSkuName(skus.getSkuName());
                Items item = itemsService.selectOneBySpuCode(skus.getSpuCode());
                if (item != null) {
                    //商品类别
                    if (StringUtils.equals(item.getItemType(), ItemTypeEnum.XIAOTAI.getCode())) {
                        reportInventory.setGoodsType(ZeroToNineEnum.ONE.getCode());
                    } else if (StringUtils.equals(item.getItemType(), ItemTypeEnum.NON_XIAOTAI.getCode())) {
                        reportInventory.setGoodsType(ZeroToNineEnum.TWO.getCode());
                    }
                }
            }
            //所属类目
            if (StringUtils.isNotBlank(reportInventory.getCategoryId())) {
                String categoryName = categoryService.selectAllCategoryName(Long.valueOf(reportInventory.getCategoryId()));
                reportInventory.setCategoryName(categoryName);
            }
        }
    }


    private void setWarehouseName(List<ReportInventory> warehouseCodes) {
        if (!CollectionUtils.isEmpty(warehouseCodes)) {
            for (ReportInventory reportInventory : warehouseCodes) {
                WarehouseInfo warehouseInfo = warehouseInfoService.selectOneByCode(reportInventory.getWarehouseCode());
                if (warehouseInfo != null) {
                    reportInventory.setWarehouseName(warehouseInfo.getWarehouseName());
                }
            }
        }
    }

    private List<ReportInventory> getResult(int i, String date) {
        String time = "";
        if (i < 10) {
            time = "-0" + i + "-01";
        } else {
            time = "-" + i + "-01";
        }
        return reportInventoryService.selectPageList(date + time);
    }

    private HSSFWorkbook reportExcel(List<?> info, String fileName, String type) {
        //组装信息
        List<CellDefinition> cellDefinitionList = new ArrayList<>();
        if (StringUtils.equals(type, ZeroToNineEnum.ONE.getCode())) {
            this.createCellDefinitionForAll(cellDefinitionList);
        } else if (StringUtils.equals(type, ZeroToNineEnum.TWO.getCode())) {
            this.createCellDefinitionForEntry(cellDefinitionList);
        } else {
            this.createCellDefinitionForOutbound(cellDefinitionList);
        }
        info.forEach(obj -> {
            if (obj instanceof ReportBase) {
                if (StringUtils.isNotBlank(((ReportBase) obj).getGoodsType())) {
                    ((ReportBase) obj).setGoodsType(GoodsTypeEnum.queryNameByCode(((ReportBase) obj).
                            getGoodsType()).getName());
                }
                if (StringUtils.isNotBlank(((ReportBase) obj).getStockType())) {
                    ((ReportBase) obj).setStockType(StockTypeEnum.queryNameByCode(((ReportBase) obj).
                            getStockType()).getName());
                }
            }
            if(obj instanceof ReportEntryDetail){
                ((ReportEntryDetail) obj).setEntryTimeValue(DateUtils.formatDateTime(((ReportEntryDetail) obj).getEntryTime()));
            }
            if(obj instanceof ReportOutboundDetail){
                ((ReportOutboundDetail) obj).setOutboundTimeValue(DateUtils.formatDateTime(((ReportOutboundDetail) obj).getOutboundTime()));
            }
        });
        return ExportExcel.generateExcel(info, cellDefinitionList, fileName);
    }

    private void createCellDefinitionForAll(List<CellDefinition> cellDefinitionList) {
        CellDefinition warehouseName = new CellDefinition("warehouseName", "仓库名称", CellDefinition.TEXT, null, 15000);
        CellDefinition stockType = new CellDefinition("stockType", "库存类型", CellDefinition.TEXT, null, 4000);
        CellDefinition skuCode = new CellDefinition("skuCode", "SKU编码", CellDefinition.TEXT, null, 10000);
        CellDefinition barCode = new CellDefinition("barCode", "条形码", CellDefinition.TEXT, null, 6000);
        CellDefinition skuName = new CellDefinition("skuName", "SKU名称", CellDefinition.TEXT, null, 15000);
        CellDefinition categoryName = new CellDefinition("categoryName", "所属类目", CellDefinition.TEXT, null, 15000);
        CellDefinition goodsType = new CellDefinition("goodsType", "商品类别", CellDefinition.TEXT, null, 5000);
        CellDefinition specInfo = new CellDefinition("specInfo", "规格", CellDefinition.TEXT, null, 13000);
        CellDefinition initialQuantity = new CellDefinition("initialQuantity", "期初数量", CellDefinition.TEXT, null, 6000);
        CellDefinition outboundQuantity = new CellDefinition("outboundQuantity", "销售出库数量", CellDefinition.TEXT, null, 6000);
        CellDefinition outboundTotalAmount = new CellDefinition("outboundTotalAmount", "销售出库实付总金额（元）", CellDefinition.TEXT, null, 12000);
        CellDefinition salesReturnQuantity = new CellDefinition("salesReturnQuantity", "退货入库数量", CellDefinition.TEXT, null, 6000);
        CellDefinition purchaseQuantity = new CellDefinition("purchaseQuantity", "采购入库数量", CellDefinition.TEXT, null, 4000);
        CellDefinition purchaseTotalAmount = new CellDefinition("purchaseTotalAmount", "含税采购总金额（元）", CellDefinition.TEXT, null, 10000);
        CellDefinition supplierReturnOutboundQuantity = new CellDefinition("supplierReturnOutboundQuantity", "退供应商出库数量", CellDefinition.TEXT, null, 8000);
        CellDefinition suppliderReturnTotalAmount = new CellDefinition("suppliderReturnTotalAmount", "退供应商出库金额（元）", CellDefinition.TEXT, null, 12000);
        CellDefinition allocateInQuantity = new CellDefinition("allocateInQuantity", "调拨入库数量", CellDefinition.TEXT, null, 4000);
        CellDefinition allocateOutQuantity = new CellDefinition("allocateOutQuantity", "调拨出库数量", CellDefinition.TEXT, null, 4000);
        CellDefinition entryTotalQuantity = new CellDefinition("entryTotalQuantity", "本期入库总数量", CellDefinition.TEXT, null, 4000);
        CellDefinition outboundTotalQuantity = new CellDefinition("outboundTotalQuantity", "本期出库总数量", CellDefinition.TEXT, null, 4000);
        CellDefinition balanceTotalQuantity = new CellDefinition("balanceTotalQuantity", "期末结存数量", CellDefinition.TEXT, null, 4000);

        cellDefinitionList.add(warehouseName);
        cellDefinitionList.add(stockType);
        cellDefinitionList.add(skuCode);
        cellDefinitionList.add(barCode);
        cellDefinitionList.add(skuName);
        cellDefinitionList.add(categoryName);
        cellDefinitionList.add(goodsType);
        cellDefinitionList.add(specInfo);
        cellDefinitionList.add(initialQuantity);
        cellDefinitionList.add(outboundQuantity);
        cellDefinitionList.add(outboundTotalAmount);
        cellDefinitionList.add(salesReturnQuantity);
        cellDefinitionList.add(purchaseQuantity);
        cellDefinitionList.add(purchaseTotalAmount);
        cellDefinitionList.add(supplierReturnOutboundQuantity);
        cellDefinitionList.add(suppliderReturnTotalAmount);
        cellDefinitionList.add(allocateInQuantity);
        cellDefinitionList.add(allocateOutQuantity);
        cellDefinitionList.add(entryTotalQuantity);
        cellDefinitionList.add(outboundTotalQuantity);
        cellDefinitionList.add(balanceTotalQuantity);
    }

    private void createCellDefinitionForEntry(List<CellDefinition> cellDefinitionList) {
        CellDefinition warehouseName = new CellDefinition("warehouseName", "入库仓库名称", CellDefinition.TEXT, null, 15000);
        CellDefinition stockType = new CellDefinition("stockType", "库存类型", CellDefinition.TEXT, null, 4000);
        CellDefinition entryTimeValue = new CellDefinition("entryTimeValue", "入库时间", CellDefinition.TEXT, null, 10000);
        CellDefinition operationType = new CellDefinition("operationType", "入库类型", CellDefinition.TEXT, null, 6000);
        CellDefinition orderCode = new CellDefinition("orderCode", "入库单编号", CellDefinition.TEXT, null, 6000);
        CellDefinition warehouseOrderCode = new CellDefinition("warehouseOrderCode", "仓库反馈入库单编号", CellDefinition.TEXT, null, 6000);
        CellDefinition supplierCode = new CellDefinition("supplierCode", "供应商编号", CellDefinition.TEXT, null, 6000);
        CellDefinition supplierName = new CellDefinition("supplierName", "供应商名称", CellDefinition.TEXT, null, 6000);
        CellDefinition skuCode = new CellDefinition("skuCode", "SKU编码", CellDefinition.TEXT, null, 10000);
        CellDefinition barCode = new CellDefinition("barCode", "条形码", CellDefinition.TEXT, null, 6000);
        CellDefinition skuName = new CellDefinition("skuName", "SKU名称", CellDefinition.TEXT, null, 15000);
        CellDefinition goodsType = new CellDefinition("goodsType", "商品类别", CellDefinition.TEXT, null, 5000);
        CellDefinition specInfo = new CellDefinition("specInfo", "规格", CellDefinition.TEXT, null, 13000);
        CellDefinition entryQuantity = new CellDefinition("entryQuantity", "计划入库数量", CellDefinition.TEXT, null, 6000);
        CellDefinition price = new CellDefinition("price", "含税采购单价（元）", CellDefinition.TEXT, null, 12000);
        CellDefinition totalPrice = new CellDefinition("totalPrice", "含税采购总金额（元）", CellDefinition.TEXT, null, 6000);
        CellDefinition realQuantity = new CellDefinition("realQuantity", "实际入库数量", CellDefinition.TEXT, null, 4000);
        CellDefinition residualQuantity = new CellDefinition("residualQuantity", "未入库数量", CellDefinition.TEXT, null, 10000);
        CellDefinition remark = new CellDefinition("remark", "系统备注", CellDefinition.TEXT, null, 8000);

        cellDefinitionList.add(warehouseName);
        cellDefinitionList.add(stockType);
        cellDefinitionList.add(entryTimeValue);
        cellDefinitionList.add(operationType);
        cellDefinitionList.add(orderCode);
        cellDefinitionList.add(warehouseOrderCode);
        cellDefinitionList.add(supplierCode);
        cellDefinitionList.add(supplierName);
        cellDefinitionList.add(skuCode);
        cellDefinitionList.add(barCode);
        cellDefinitionList.add(skuName);
        cellDefinitionList.add(goodsType);
        cellDefinitionList.add(specInfo);
        cellDefinitionList.add(entryQuantity);
        cellDefinitionList.add(price);
        cellDefinitionList.add(totalPrice);
        cellDefinitionList.add(realQuantity);
        cellDefinitionList.add(residualQuantity);
        cellDefinitionList.add(remark);
    }

    private void createCellDefinitionForOutbound(List<CellDefinition> cellDefinitionList) {
        CellDefinition warehouseName = new CellDefinition("warehouseName", "出库仓库名称", CellDefinition.TEXT, null, 15000);
        CellDefinition stockType = new CellDefinition("stockType", "库存类型", CellDefinition.TEXT, null, 4000);
        CellDefinition outboundTimeValue = new CellDefinition("outboundTimeValue", "出库时间", CellDefinition.TEXT, null, 4000);
        CellDefinition operationType = new CellDefinition("operationType", "出库类型", CellDefinition.TEXT, null, 4000);
        CellDefinition outboundOrderCode = new CellDefinition("outboundOrderCode", "出库单编号", CellDefinition.TEXT, null, 4000);
        CellDefinition warehouseOutboundOrderCode = new CellDefinition("warehouseOutboundOrderCode", "仓库反馈出库单号", CellDefinition.TEXT, null, 4000);
        CellDefinition platformOrderCode = new CellDefinition("platformOrderCode", "平台订单号", CellDefinition.TEXT, null, 4000);
        CellDefinition sellChannelCode = new CellDefinition("sellChannelCode", "销售渠道订单号", CellDefinition.TEXT, null, 4000);
        CellDefinition goodsOrderCode = new CellDefinition("goodsOrderCode", "商品订单号", CellDefinition.TEXT, null, 4000);
        CellDefinition sellName = new CellDefinition("sellName", "销售渠道", CellDefinition.TEXT, null, 4000);
        CellDefinition skuCode = new CellDefinition("skuCode", "SKU编码", CellDefinition.TEXT, null, 10000);
        CellDefinition barCode = new CellDefinition("barCode", "条形码", CellDefinition.TEXT, null, 6000);
        CellDefinition skuName = new CellDefinition("skuName", "SKU名称", CellDefinition.TEXT, null, 15000);
        CellDefinition goodsType = new CellDefinition("goodsType", "商品类别", CellDefinition.TEXT, null, 5000);
        CellDefinition specInfo = new CellDefinition("specInfo", "规格", CellDefinition.TEXT, null, 13000);
        CellDefinition outboundQuantity = new CellDefinition("outboundQuantity", "计划出库数量", CellDefinition.TEXT, null, 6000);
        CellDefinition salesPrice = new CellDefinition("salesPrice", "销售单价（元）", CellDefinition.TEXT, null, 12000);
        CellDefinition salesTotalAmount = new CellDefinition("salesTotalAmount", "销售总价（元）", CellDefinition.TEXT, null, 6000);
        CellDefinition payment = new CellDefinition("payment", "销售出库实付总金额（元）", CellDefinition.TEXT, null, 4000);
        CellDefinition realQuantity = new CellDefinition("realQuantity", "实际出库数量", CellDefinition.TEXT, null, 10000);
        CellDefinition residualQuantity = new CellDefinition("residualQuantity", "未出库数量", CellDefinition.TEXT, null, 8000);
        CellDefinition receiver = new CellDefinition("receiver", "收货人", CellDefinition.TEXT, null, 12000);
        CellDefinition mobile = new CellDefinition("mobile", "收货人手机", CellDefinition.TEXT, null, 4000);
        CellDefinition address = new CellDefinition("address", "收货地址", CellDefinition.TEXT, null, 4000);
        CellDefinition express = new CellDefinition("express", "快递公司", CellDefinition.TEXT, null, 4000);
        CellDefinition waybillNumber = new CellDefinition("waybillNumber", "快递单号", CellDefinition.TEXT, null, 4000);
        CellDefinition outboundSupplierAmount = new CellDefinition("outboundSupplierAmount", "退供应商出库金额（元）", CellDefinition.TEXT, null, 4000);

        cellDefinitionList.add(warehouseName);
        cellDefinitionList.add(stockType);
        cellDefinitionList.add(outboundTimeValue);
        cellDefinitionList.add(operationType);
        cellDefinitionList.add(outboundOrderCode);
        cellDefinitionList.add(warehouseOutboundOrderCode);
        cellDefinitionList.add(platformOrderCode);
        cellDefinitionList.add(sellChannelCode);
        cellDefinitionList.add(goodsOrderCode);
        cellDefinitionList.add(sellName);
        cellDefinitionList.add(skuCode);
        cellDefinitionList.add(barCode);
        cellDefinitionList.add(skuName);
        cellDefinitionList.add(goodsType);
        cellDefinitionList.add(specInfo);
        cellDefinitionList.add(outboundQuantity);
        cellDefinitionList.add(salesPrice);
        cellDefinitionList.add(salesTotalAmount);
        cellDefinitionList.add(payment);
        cellDefinitionList.add(realQuantity);
        cellDefinitionList.add(residualQuantity);
        cellDefinitionList.add(receiver);
        cellDefinitionList.add(mobile);
        cellDefinitionList.add(address);
        cellDefinitionList.add(express);
        cellDefinitionList.add(waybillNumber);
        cellDefinitionList.add(outboundSupplierAmount);
    }

}
