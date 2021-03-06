package org.trc.enums;

/**
 * Created by wangyz on 2017/11/21.
 * 分布式锁枚举
 */
public enum DistributeLockEnum {
    WAREHOSE_NOTICE_STOCK("stock_","库存更新"),
    DELIVERY_ORDER_CREATE("deliveryOrderCreate_","发货通知单"),
    SUBMIT_JINGDONG_ORDERßßß("submitJingdongOrder_","提交京东订单"),
    PURCHASE_ORDER("purchaseOrder", "采购单"),
    PURCHASE_OUTBOUND_ORDER("purchaseOutboundOrder", "采购退货单"),
    WAREHOUSE_NOTICE_CREATE("warehouseNoticeCreate_","入库通知"),
    ALLOCATE_OUT_ORDER("allocateOutOrder", "调拨出库"),
    ALLOCATE_IN_ORDER("allocateInOrder", "调拨入库"),
    PURCHASE_OUTBOUND_NOTICE("purchaseOutboundNotice", "采购退货出库"),
    ALLOCATE_ORDER_AUDIT("allocateOrderAudit", "调拨单审核"),
    SERIAL_GENERATE("serialGenerate", "序列号生成"),
    ITEM_GROUP_SAVE("item_group_save","商品组新增"),
    ITEM_GROUP_EDIT("item_group_edit","商品组编辑"),
    BAND_SAVE("bandSave","品牌保存");

    private String code;
    private String name;
    DistributeLockEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
