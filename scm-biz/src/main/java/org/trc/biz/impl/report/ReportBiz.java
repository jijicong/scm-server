package org.trc.biz.impl.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.biz.report.IReportBiz;
import org.trc.domain.report.ReportEntryDetail;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.enums.report.StockOperationTypeEnum;
import org.trc.enums.report.StockTypeEnum;
import org.trc.service.report.IReportInventoryService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Description〈报表统计〉
 *
 * @author hzliuwei
 * @create 2018/9/5
 */
@Service("reportBiz")
public class ReportBiz implements IReportBiz {

    @Autowired
    private IReportInventoryService reportInventoryService;

    @Autowired
    private IWarehouseInfoService warehouseInfoService;

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

        String str1="2018-09-06 00:00:00";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(str1, dtf);
        LocalDateTime localDateTime = parse.minusDays(1);
        System.out.println(localDateTime);

    }
}
