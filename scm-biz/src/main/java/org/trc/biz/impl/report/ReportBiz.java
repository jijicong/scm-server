package org.trc.biz.impl.report;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.biz.report.IReportBiz;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.report.ReportEntryDetail;
import org.trc.domain.report.ReportInventory;
import org.trc.domain.report.ReportOutboundDetail;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.enums.ItemTypeEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.enums.report.StockOperationTypeEnum;
import org.trc.enums.report.StockTypeEnum;
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
import org.trc.util.Pagenation;
import org.trc.util.QueryModel;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Description〈报表统计〉
 *
 * @author hzliuwei
 * @create 2018/9/10
 */
@Service("reportBiz")
public class ReportBiz implements IReportBiz {

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

    /**
     * 每日统计前一天明细报表数据
     */
    @Override
    public void statisticalDetailReport() {
        //获取前一天日期
        LocalDate localDate = LocalDate.now().minusDays(1);
        //仓库信息管理中“SKU数量”大于0且“货主仓库状态”为“通知成功”的所有仓库
        List<WarehouseInfo> warehouseInfos = reportInventoryService.selectWarehouseInfoList();
        if (!CollectionUtils.isEmpty(warehouseInfos)) {
            //查询当前仓库中入库时间在当前统计时间范围内的所有入库单据
            insertEntryDetailReport(warehouseInfos, localDate);
            //查询当前仓库中出库时间在当前统计时间范围内的所有出库单据
            insertOutboundDetailReport(warehouseInfos, localDate);
        }
    }

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
     * @return
     */
    @Override
    public Pagenation getReportPageList(ReportInventoryForm form, Pagenation page) {

        //总库存查询
        if (StringUtils.equals(form.getReportType(), ZeroToNineEnum.ONE.getCode())) {
            return getReportInventoryList(form, (Pagenation<ReportInventory>) page);
        }
        //入库明细查询
        else if (StringUtils.equals(form.getReportType(), ZeroToNineEnum.TWO.getCode())) {
            return getReportEntryDetailList(form, (Pagenation<ReportEntryDetail>) page);
        }
        //出库明细查询
        else if (StringUtils.equals(form.getReportType(), ZeroToNineEnum.THREE.getCode())) {
            return getReportOutboundDetailList(form, (Pagenation<ReportOutboundDetail>) page);
        }

        return new Pagenation<>();
    }

    private Pagenation getReportOutboundDetailList(ReportInventoryForm form, Pagenation<ReportOutboundDetail> page) {
        Example example = new Example(ReportEntryDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("stockType", form.getStockType());
        criteria.andEqualTo("warehouseCode", form.getWarehouseCode());
        criteria.andCondition("and TO_DAYS(`create_time`) = TO_DAYS(" + form.getDate() + ")");
        //criteria.andLike("createTime", form.getDate() + "%");
        Pagenation<ReportOutboundDetail> pagination = reportOutboundDetailService.pagination(example, page, new QueryModel());
        List<ReportOutboundDetail> result = pagination.getResult();
        setOutboundResultDetail(result, form);
        return pagination;
    }

    private void setOutboundResultDetail(List<ReportOutboundDetail> result, ReportInventoryForm form) {
        for (ReportOutboundDetail reportOutboundDetail : result) {

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
            //sellChannelService.selectSellByCode(reportOutboundDetail.getSellCode());
        }
    }

    private Pagenation getReportEntryDetailList(ReportInventoryForm form, Pagenation<ReportEntryDetail> page) {
        Example example = new Example(ReportEntryDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("stockType", form.getStockType());
        criteria.andEqualTo("warehouseCode", form.getWarehouseCode());
        criteria.andCondition("and TO_DAYS(`create_time`) = TO_DAYS(" + form.getDate() + ")");
        //criteria.andLike("createTime", form.getDate() + "%");
        Pagenation<ReportEntryDetail> pagination = reportEntryDetailService.pagination(example, page, new QueryModel());
        List<ReportEntryDetail> result = pagination.getResult();
        setEntryResultDetail(result, form);
        return pagination;
    }

    private void setEntryResultDetail(List<ReportEntryDetail> result, ReportInventoryForm form) {

        for (ReportEntryDetail reportEntryDetail : result) {
            //采购入库
            if (StringUtils.equals(reportEntryDetail.getOperationType(), StockOperationTypeEnum.PURCHASE.getCode())) {
                if (StringUtils.equals(form.getStockType(), StockTypeEnum.SUBSTANDARD.getCode())) {
                    reportEntryDetail.setStockType(StockTypeEnum.SUBSTANDARD.getCode());
                    reportEntryDetail.setEntryQuantity(0L);
                    reportEntryDetail.setPrice(new BigDecimal(0));
                    reportEntryDetail.setRealQuantity(reportEntryDetail.getDefectiveQuantity());
                    reportEntryDetail.setResidualQuantity(reportEntryDetail.getEntryQuantity() - reportEntryDetail.getDefectiveQuantity());
                    reportEntryDetail.setRemark("正品入库：" + reportEntryDetail.getNormalQuantity());
                } else {
                    reportEntryDetail.setRealQuantity(reportEntryDetail.getNormalQuantity());
                    reportEntryDetail.setRemark("残品入库：" + reportEntryDetail.getDefectiveQuantity());
                    reportEntryDetail.setResidualQuantity(reportEntryDetail.getEntryQuantity() - reportEntryDetail.getNormalQuantity());
                }
                //供应商名称
                Supplier supplier = supplierService.selectSupplierByCode(reportEntryDetail.getSupplierCode());
                reportEntryDetail.setSupplierName(supplier.getSupplierName());
            } else {
                if (StringUtils.equals(form.getStockType(), StockTypeEnum.SUBSTANDARD.getCode())) {
                    reportEntryDetail.setStockType(StockTypeEnum.SUBSTANDARD.getCode());
                    reportEntryDetail.setRealQuantity(reportEntryDetail.getDefectiveQuantity());
                    reportEntryDetail.setResidualQuantity(reportEntryDetail.getEntryQuantity() - reportEntryDetail.getDefectiveQuantity());
                    reportEntryDetail.setRemark("正品入库：" + reportEntryDetail.getNormalQuantity());
                } else {
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

    }


    private Pagenation getReportInventoryList(ReportInventoryForm form, Pagenation<ReportInventory> page) {

        Example example = new Example(ReportInventory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("stockType", form.getStockType());
        criteria.andEqualTo("warehouseCode", form.getWarehouseCode());
        criteria.andCondition("and TO_DAYS(`create_time`) = TO_DAYS(" + form.getDate() + ")");
        //criteria.andLike("createTime", form.getDate() + "%");
        Pagenation<ReportInventory> pagination = reportInventoryService.pagination(example, page, new QueryModel());
        List<ReportInventory> result = pagination.getResult();
        setResultDetail(result);
        return pagination;
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
            String categoryName = categoryService.selectAllCategoryName(Long.valueOf(reportInventory.getCategoryId()));
            reportInventory.setCategoryName(categoryName);
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
            time = "-0" + i;
        } else {
            time = "-" + i;
        }
        return reportInventoryService.selectPageList(date + time);
    }

    private void insertOutboundDetailReport(List<WarehouseInfo> warehouseInfos, LocalDate localDate) {
    }

    private void insertEntryDetailReport(List<WarehouseInfo> warehouseInfos, LocalDate localDate) {
        /*//采购入库
        List<WarehouseNotice> warehouseNotices = reportInventoryService.selectWarehouseNoticeList(warehouseInfo.getCode(), localDate);
        //调拨入库
        List<AllocateInOrder> allocates = reportInventoryService.selectAllocateInList(warehouseInfo.getCode(), localDate);

        List<ReportEntryDetail> reportEntryDetails = new ArrayList<>();

        if (!CollectionUtils.isEmpty(warehouseNotices)) {
            for (WarehouseNotice warehouseNotice : warehouseNotices) {
                List<WarehouseNoticeDetails> warehouseNoticeDetails = reportInventoryService.selectWarehouseNoticeDetailsByWarehouseNoticeCode(warehouseNotice.getWarehouseNoticeCode());
                if (!CollectionUtils.isEmpty(warehouseNoticeDetails)) {
                    for (WarehouseNoticeDetails warehouseNoticeDetail : warehouseNoticeDetails) {
                        setReportEntryDetail(reportEntryDetails, warehouseNotice, warehouseNoticeDetail);
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(allocates)) {
            for (AllocateInOrder allocateInOrder : allocates) {
                List<AllocateSkuDetail> allocateSkuDetails = reportInventoryService.selectAllocateInDetailList(allocateInOrder.getAllocateOrderCode());
                if (!CollectionUtils.isEmpty(allocateSkuDetails)) {
                    for (AllocateSkuDetail allocateSkuDetail : allocateSkuDetails) {
                        //setReportEntryDetailByAllocateIn(reportEntryDetails, allocateInOrder, allocateSkuDetail);
                    }
                }
            }
        }*/
        // 查询JD仓入库单库存变动明细

        // 查询子仓库入库单库存变动明细
    }

    private void setReportEntryDetail(List<ReportEntryDetail> reportEntryDetails, WarehouseNotice warehouseNotice, WarehouseNoticeDetails warehouseNoticeDetail) {
        ReportEntryDetail reportEntryDetail = new ReportEntryDetail();
        reportEntryDetail.setWarehouseCode(warehouseNotice.getWarehouseCode());
        //默认正品
        reportEntryDetail.setStockType(StockTypeEnum.QUALITY.getCode());
        reportEntryDetail.setEntryTime(warehouseNoticeDetail.getStorageTime());
        reportEntryDetail.setOperationType(StockOperationTypeEnum.PURCHASE.getCode());
        reportEntryDetail.setSupplierCode(warehouseNotice.getSupplierCode());
        reportEntryDetail.setOrderCode(warehouseNotice.getWarehouseNoticeCode());
        reportEntryDetail.setWarehouseOrderCode(warehouseNotice.getEntryOrderId());
        reportEntryDetail.setSkuCode(warehouseNoticeDetail.getSkuCode());
        reportEntryDetail.setBarCode(warehouseNoticeDetail.getBarCode());
        reportEntryDetail.setGoodsType("");
        reportEntryDetail.setSpecInfo(warehouseNoticeDetail.getSpecInfo());
        reportEntryDetail.setEntryQuantity(warehouseNoticeDetail.getPurchasingQuantity());
        reportEntryDetail.setPrice(warehouseNoticeDetail.getPurchasePrice());
        reportEntryDetail.setTotalPrice(warehouseNoticeDetail.getPurchaseAmount());
        reportEntryDetail.setRealQuantity(warehouseNoticeDetail.getNormalStorageQuantity());
        reportEntryDetail.setResidualQuantity(warehouseNoticeDetail.getPurchasingQuantity() - warehouseNoticeDetail.getNormalStorageQuantity());
        reportEntryDetail.setDefectiveQuantity(warehouseNoticeDetail.getDefectiveStorageQuantity());
        reportEntryDetails.add(reportEntryDetail);
    }

    public static void main(String[] args) {

        LocalDate now = LocalDate.now();
        LocalDate localDate = LocalDate.now().minusDays(1);
        System.out.println(localDate);
        System.out.println(now);

        String str1 = "2018-09-06 00:00:00";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(str1, dtf);
        LocalDateTime localDateTime = parse.minusDays(1);
        System.out.println(localDateTime);

        for (int i = 1; i < 13; i++) {
            System.out.println(i);
        }

        int year = LocalDateTime.now().getYear();
        Month month = LocalDateTime.now().getMonth();
        System.out.println(month.getValue());
        System.out.println(year);
    }
}
