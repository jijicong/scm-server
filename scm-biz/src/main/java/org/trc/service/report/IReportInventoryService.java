package org.trc.service.report;

import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.report.ReportInventory;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.form.report.ReportInventoryForm;
import org.trc.service.IBaseService;

import java.time.LocalDate;
import java.util.List;

public interface IReportInventoryService extends IBaseService<ReportInventory, Long> {

    /**
     * 仓库信息管理中“SKU数量”大于0且“货主仓库状态”为“通知成功”的所有仓库
     *
     * @return
     */
    List<WarehouseInfo> selectWarehouseInfoList();

    /**
     * 库获取当前仓库在【仓库信息管理-商品管理】中“通知仓库状态”为“通知成功”的所有SKU
     *
     * @param warehouseCode
     * @return
     */
    List<WarehouseItemInfo> selectSkusByWarehouseCode(String warehouseCode);

    /**
     * 查询当前仓库中入库时间在当前统计时间范围内的采购入库单
     *
     * @param code
     * @param localDate
     * @return
     */
    List<WarehouseNotice> selectWarehouseNoticeList(String code, LocalDate localDate);

    /**
     * 查询当前仓库中入库时间在当前统计时间范围内的调拨入库单
     *
     * @param code
     * @param localDate
     * @return
     */
    List<AllocateInOrder> selectAllocateInList(String code, LocalDate localDate);

    /**
     * 获取入库单详情
     *
     * @param warehouseNoticeCode
     * @return
     */
    List<WarehouseNoticeDetails> selectWarehouseNoticeDetailsByWarehouseNoticeCode(String warehouseNoticeCode);

    /**
     * 获取调拨入库单详情
     *
     * @param allocateOrderCode
     * @return
     */
    List<AllocateSkuDetail> selectAllocateInDetailList(String allocateOrderCode);

    /**
     *
     * @param time
     */
    List<ReportInventory> selectPageList(String time);

    /**
     * 获取当天所有记录
     * @param warehouseCode
     * @param with
     * @param stockType
     * @return
     */
    List<ReportInventory> getReportInventoryByWarehouseCodeAndTime(String warehouseCode, LocalDate with, String stockType);

    /**
     * 分组查询数据
     * @param form
     * @param skuCodes
     * @return
     */
    List<ReportInventory> selectReportInventoryLimit(ReportInventoryForm form, List<String> skuCodes);
}
