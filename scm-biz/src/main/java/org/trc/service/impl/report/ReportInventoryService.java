package org.trc.service.impl.report;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.report.ReportInventory;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.enums.ZeroToNineEnum;
import org.trc.mapper.report.IReportInventoryMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.report.IReportInventoryService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDate;
import java.util.List;

/**
 * Description〈总库存报表〉
 *
 * @author hzliuwei
 * @create 2018/9/5
 */
@Service("reportInventoryService")
public class ReportInventoryService extends BaseService<ReportInventory, Long> implements IReportInventoryService {

    @Autowired
    private IReportInventoryMapper reportInventoryMapper;

    @Autowired
    private IWarehouseInfoService warehouseInfoService;

    @Autowired
    private IWarehouseItemInfoService warehouseItemInfoService;

    /**
     * 仓库信息管理中“SKU数量”大于0且“货主仓库状态”为“通知成功”的所有仓库
     *
     * @return
     */
    @Override
    public List<WarehouseInfo> selectWarehouseInfoList() {
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ownerWarehouseState", ZeroToNineEnum.ONE.getCode());
        criteria.andGreaterThan("skuNum", ZeroToNineEnum.ZERO.getCode());
        return warehouseInfoService.selectByExample(example);
    }

    /**
     * 库获取当前仓库在【仓库信息管理-商品管理】中“通知仓库状态”为“通知成功”的所有SKU
     *
     * @param warehouseCode
     * @return
     */
    @Override
    public List<WarehouseItemInfo> selectSkusByWarehouseCode(String warehouseCode) {
        if (StringUtils.isNotBlank(warehouseCode)) {
            Example example = new Example(WarehouseItemInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("warehouseCode", warehouseCode);
            return warehouseItemInfoService.selectByExample(example);
        }
        return null;
    }

    /**
     * 查询当前仓库中入库时间在当前统计时间范围内的采购入库单
     *
     * @param code
     * @param localDate
     * @return
     */
    @Override
    public List<WarehouseNotice> selectWarehouseNoticeList(String code, LocalDate localDate) {
        return null;
    }

    /**
     * 查询当前仓库中入库时间在当前统计时间范围内的调拨入库单
     *
     * @param code
     * @param localDate
     * @return
     */
    @Override
    public List<AllocateInOrder> selectAllocateInList(String code, LocalDate localDate) {
        return null;
    }

    /**
     * 获取入库单详情
     *
     * @param warehouseNoticeCode
     * @return
     */
    @Override
    public List<WarehouseNoticeDetails> selectWarehouseNoticeDetailsByWarehouseNoticeCode(String warehouseNoticeCode) {
        return null;
    }

    /**
     * 获取调拨入库单详情
     *
     * @param allocateOrderCode
     * @return
     */
    @Override
    public List<AllocateSkuDetail> selectAllocateInDetailList(String allocateOrderCode) {
        return null;
    }

    /**
     * @param time
     */
    @Override
    public List<ReportInventory> selectPageList(String time) {
        return reportInventoryMapper.selectPageList(time);
    }

}
